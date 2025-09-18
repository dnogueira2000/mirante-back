package com.mirante.eventmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mirante.eventmanager.dto.EventDTO;
import com.mirante.eventmanager.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
@ActiveProfiles("test")
public class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createEvent_shouldReturnCreatedEvent() throws Exception {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setTitle("Evento de Teste");
        eventDTO.setDescription("Descrição do Evento de Teste");
        eventDTO.setEventDateTime(LocalDateTime.now().plusDays(5));
        eventDTO.setLocation("Local de Teste");

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Evento de Teste"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void getAllEvents_shouldReturnPagedEvents() throws Exception {
        createEvent_shouldReturnCreatedEvent();

        mockMvc.perform(get("/api/events?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Evento de Teste"));
    }

    @Test
    void getEventById_shouldReturnEvent() throws Exception {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setTitle("Evento para Buscar");
        eventDTO.setDescription("Descrição do Evento para Buscar");
        eventDTO.setEventDateTime(LocalDateTime.now().plusDays(10));
        eventDTO.setLocation("Local para Buscar");

        String response = mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        EventDTO createdEvent = objectMapper.readValue(response, EventDTO.class);

        mockMvc.perform(get("/api/events/{id}", createdEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Evento para Buscar"));
    }

    @Test
    void deleteEvent_shouldPerformSoftDelete() throws Exception {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setTitle("Evento para Deletar");
        eventDTO.setDescription("Descrição do Evento para Deletar");
        eventDTO.setEventDateTime(LocalDateTime.now().plusDays(15));
        eventDTO.setLocation("Local para Deletar");

        String response = mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        EventDTO createdEvent = objectMapper.readValue(response, EventDTO.class);

        mockMvc.perform(delete("/api/events/{id}", createdEvent.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/events/{id}", createdEvent.getId()))
                .andExpect(status().isNotFound());
    }
}

