SET search_path TO audit;

-- ===============================
-- Criação da tabela system_audit_log
-- ===============================
CREATE TABLE IF NOT EXISTS system_audit_log (
    id UUID PRIMARY KEY,

    action VARCHAR(100) NOT NULL,
    target_entity VARCHAR(100),
    target_id VARCHAR(100),

    performed_by VARCHAR(150),
    performed_by_id UUID,

    ip_address VARCHAR(50),
    user_agent TEXT,
    http_method VARCHAR(10),
    path TEXT,
    session_id VARCHAR(100),

    details TEXT,
    timestamp TIMESTAMP DEFAULT now()
);

-- ===============================
-- Índices para otimização de busca
-- ===============================
CREATE INDEX IF NOT EXISTS idx_auditlog_action ON system_audit_log(action);
CREATE INDEX IF NOT EXISTS idx_auditlog_target ON system_audit_log(target_entity, target_id);
CREATE INDEX IF NOT EXISTS idx_auditlog_performed_by ON system_audit_log(performed_by);
CREATE INDEX IF NOT EXISTS idx_auditlog_timestamp ON system_audit_log(timestamp);
