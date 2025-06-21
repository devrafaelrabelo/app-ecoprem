package com.ecoprem.core.audit.filter;

import com.ecoprem.entity.audit.RequestAuditLog;
import com.ecoprem.core.audit.repository.RequestAuditLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestAuditLogFilter extends OncePerRequestFilter {

    private final RequestAuditLogRepository auditLogRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if ("/api/health".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = (queryString == null) ? uri : uri + "?" + queryString;
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String username = (request.getUserPrincipal() != null)
                ? request.getUserPrincipal().getName()
                : "anonymous";

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            RequestAuditLog logEntry = new RequestAuditLog();
            logEntry.setMethod(method);
            logEntry.setPath(fullUrl);
            logEntry.setIpAddress(ip);
            logEntry.setStatusCode(status);
            logEntry.setUserAgent(userAgent);
            logEntry.setUsername(username);
            logEntry.setDurationMs((int) duration);
            logEntry.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(logEntry);

            log.info("AUDIT - [{}] {} - IP: {} - Status: {} - User: {} - Time: {} ms - At: {}",
                    method, fullUrl, ip, status, username, duration, logEntry.getTimestamp());
        }
    }
}
