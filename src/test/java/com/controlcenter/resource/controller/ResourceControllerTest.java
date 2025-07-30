package com.controlcenter.resource.controller;

import com.controlcenter.resource.dto.ResourceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListResourcesSuccessfully() throws Exception {
        mockMvc.perform(get("/api/resources")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test João"));
    }

    @Test
    void shouldFetchResourcesByStatusSuccessfully() throws Exception {
        mockMvc.perform(get("/api/resources")
                        .param("status", "AVAILABLE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldSearchResourcesSuccessfully() throws Exception {
        mockMvc.perform(get("/api/resources")
                        .param("q", "notebook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Notebook João"));
    }

    @Test
    void shouldFilterResourcesByStatusSuccessfully() throws Exception {
        mockMvc.perform(get("/api/resources")
                        .param("status", "DISPONIVEL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldCreateResourceSuccessfully() throws Exception {
        ResourceRequest request = new ResourceRequest();
        request.setName("Test Notebook");
        request.setSerialNumber("SN123456789");
        request.setAssetTag("TAG987654321");
        request.setModel("ThinkPad X1");
        request.setBrand("Lenovo");
        request.setDescription("Notebook de testes");
        request.setLocation("Sala 12");
        request.setPurchaseDate(LocalDate.now().minusMonths(6));
        request.setWarrantyEndDate(LocalDate.now().plusYears(1));
        request.setPrice(new BigDecimal("6500.00"));
        request.setAvailableForUse(true);
        request.setCompanyId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        request.setCurrentUserId(UUID.fromString("e6e28546-5601-4840-9804-f61ea2e55c4a"));
        request.setStatusId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        request.setResourceTypeId(UUID.fromString("33333333-3333-3333-3333-333333333333"));


        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Notebook"));
    }

    @Test
    void shouldUpdateResourceSuccessfully() throws Exception {
        UUID existingResourceId = UUID.fromString("55555555-5555-5555-5555-555555555555"); // ID real do recurso existente

        ResourceRequest request = new ResourceRequest();
        request.setName("Updated Notebook");
        request.setSerialNumber("SN123456789");
        request.setAssetTag("TAG987654321");
        request.setModel("ThinkPad X1");
        request.setBrand("Dell");
        request.setDescription("Notebook de testes");
        request.setLocation("Sala 12");
        request.setPurchaseDate(LocalDate.now().minusMonths(6));
        request.setWarrantyEndDate(LocalDate.now().plusYears(1));
        request.setPrice(new BigDecimal("6500.00"));
        request.setAvailableForUse(true);
        request.setCompanyId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        request.setCurrentUserId(UUID.fromString("e6e28546-5601-4840-9804-f61ea2e55c4a"));
        request.setStatusId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        request.setResourceTypeId(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        mockMvc.perform(put("/api/resources/{id}", existingResourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Notebook"))
                .andExpect(jsonPath("$.brand").value("Dell"));
    }

    @Test
    void shouldDeleteResourceSuccessfully() throws Exception {
        UUID existingResourceId = UUID.fromString("55555555-5555-5555-5555-555555555555"); // ID real do recurso existente

        mockMvc.perform(delete("/api/resources/{id}", existingResourceId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentResource() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/resources/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}