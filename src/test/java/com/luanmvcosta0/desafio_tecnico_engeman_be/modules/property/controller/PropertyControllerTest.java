package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.dtos.PropertyDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.enums.Type;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.service.PropertyService;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.auth.config.SecurityConfig;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyController.class)
@Import(SecurityConfig.class)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PropertyService propertyService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private PropertyEntity entity;
    private PropertyDto dto;

    @BeforeEach
    void setUp() {
        entity = new PropertyEntity("id-1", "Casa Teste", 3, new BigDecimal("250000.00"), Type.HOUSE, true);
        dto = new PropertyDto("Casa Teste", 3, new BigDecimal("250000.00"), Type.HOUSE);
    }

    // ── POST /property/ ────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn201AndBody_whenAdminRole() throws Exception {
        when(propertyService.create(any())).thenReturn(entity);

        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.name").value("Casa Teste"))
                .andExpect(jsonPath("$.rooms").value(3))
                .andExpect(jsonPath("$.price").value(250000.00))
                .andExpect(jsonPath("$.type").value("HOUSE"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "BROKER")
    void create_shouldReturn201_whenBrokerRole() throws Exception {
        when(propertyService.create(any())).thenReturn(entity);

        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void create_shouldReturn403_whenCustomerRole() throws Exception {
        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn400_whenNameIsBlank() throws Exception {
        PropertyDto invalid = new PropertyDto("", 3, new BigDecimal("250000.00"), Type.HOUSE);

        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn400_whenRoomsIsZero() throws Exception {
        PropertyDto invalid = new PropertyDto("Casa", 0, new BigDecimal("250000.00"), Type.HOUSE);

        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn400_whenPriceIsZero() throws Exception {
        PropertyDto invalid = new PropertyDto("Casa", 3, BigDecimal.ZERO, Type.HOUSE);

        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn400_whenTypeIsNull() throws Exception {
        PropertyDto invalid = new PropertyDto("Casa", 3, new BigDecimal("250000.00"), null);

        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /property/ ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void findAll_shouldReturn200AndPage_whenCustomerRole() throws Exception {
        Page<PropertyEntity> page = new PageImpl<>(List.of(entity));
        when(propertyService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/property/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Casa Teste"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_shouldReturn200_whenAdminRole() throws Exception {
        when(propertyService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/property/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_shouldForwardPaginationParamsToService() throws Exception {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(propertyService.findAll(captor.capture())).thenReturn(new PageImpl<>(List.of(entity)));

        mockMvc.perform(get("/property/")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk());

        assertThat(captor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(captor.getValue().getPageSize()).isEqualTo(5);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_shouldReturnEmptyPage_whenNoPropertiesExist() throws Exception {
        when(propertyService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/property/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ── GET /property/search ───────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void findByName_shouldReturn200AndProperty_whenFound() throws Exception {
        when(propertyService.findByName("casa")).thenReturn(entity);

        mockMvc.perform(get("/property/search").param("name", "casa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa Teste"))
                .andExpect(jsonPath("$.type").value("HOUSE"));
    }

    @Test
    @WithMockUser(roles = "BROKER")
    void findByName_shouldReturn200_whenBrokerRole() throws Exception {
        when(propertyService.findByName("casa")).thenReturn(entity);

        mockMvc.perform(get("/property/search").param("name", "casa"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void findByName_shouldReturn404_whenPropertyNotFound() throws Exception {
        when(propertyService.findByName("inexistente"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/property/search").param("name", "inexistente"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /property/{id} ─────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturn200AndUpdatedBody_whenAdminRole() throws Exception {
        PropertyEntity updated = new PropertyEntity("id-1", "Casa Atualizada", 4, new BigDecimal("300000.00"), Type.CONDOMINIUM, true);
        when(propertyService.update(eq("id-1"), any())).thenReturn(updated);

        mockMvc.perform(put("/property/id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa Atualizada"))
                .andExpect(jsonPath("$.rooms").value(4))
                .andExpect(jsonPath("$.type").value("CONDOMINIUM"));
    }

    @Test
    @WithMockUser(roles = "BROKER")
    void update_shouldReturn200_whenBrokerRole() throws Exception {
        when(propertyService.update(eq("id-1"), any())).thenReturn(entity);

        mockMvc.perform(put("/property/id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void update_shouldReturn403_whenCustomerRole() throws Exception {
        mockMvc.perform(put("/property/id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturn404_whenPropertyNotFound() throws Exception {
        when(propertyService.update(eq("nao-existe"), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/property/nao-existe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturn400_whenBodyIsInvalid() throws Exception {
        PropertyDto invalid = new PropertyDto("", 0, BigDecimal.ZERO, null);

        mockMvc.perform(put("/property/id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /property/{id}/toggle-active ─────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void toggleActive_shouldReturn200AndToggledEntity_whenAdminRole() throws Exception {
        PropertyEntity toggled = new PropertyEntity("id-1", "Casa Teste", 3, new BigDecimal("250000.00"), Type.HOUSE, false);
        when(propertyService.toggleActive("id-1")).thenReturn(toggled);

        mockMvc.perform(patch("/property/id-1/toggle-active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser(roles = "BROKER")
    void toggleActive_shouldReturn200_whenBrokerRole() throws Exception {
        when(propertyService.toggleActive("id-1")).thenReturn(entity);

        mockMvc.perform(patch("/property/id-1/toggle-active"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void toggleActive_shouldReturn403_whenCustomerRole() throws Exception {
        mockMvc.perform(patch("/property/id-1/toggle-active"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void toggleActive_shouldReturn404_whenPropertyNotFound() throws Exception {
        when(propertyService.toggleActive("nao-existe"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(patch("/property/nao-existe/toggle-active"))
                .andExpect(status().isNotFound());
    }

    // ── Unauthenticated access ─────────────────────────────────────────────────

    @Test
    void create_shouldReturn403_whenUnauthenticated() throws Exception {
        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // ── Additional validation tests ────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn400_whenRoomsIsNegative() throws Exception {
        PropertyDto invalid = new PropertyDto("Casa", -1, new BigDecimal("250000.00"), Type.HOUSE);

        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturn400_whenPriceIsNegative() throws Exception {
        PropertyDto invalid = new PropertyDto("Casa", 3, new BigDecimal("-100.00"), Type.HOUSE);

        mockMvc.perform(post("/property/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── Additional role coverage ───────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "BROKER")
    void findAll_shouldReturn200_whenBrokerRole() throws Exception {
        when(propertyService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(entity)));

        mockMvc.perform(get("/property/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByName_shouldReturn200_whenAdminRole() throws Exception {
        when(propertyService.findByName("casa")).thenReturn(entity);

        mockMvc.perform(get("/property/search").param("name", "casa"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void findByName_shouldReturn400_whenNameParamIsMissing() throws Exception {
        mockMvc.perform(get("/property/search"))
                .andExpect(status().isBadRequest());
    }
}
