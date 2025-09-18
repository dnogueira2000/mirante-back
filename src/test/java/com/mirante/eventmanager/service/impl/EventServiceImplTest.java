package com.mirante.eventmanager.service.impl;

import com.mirante.eventmanager.dto.EventDTO;
import com.mirante.eventmanager.entity.Event;
import com.mirante.eventmanager.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event1;
    private Event event2;
    private EventDTO eventDTO1;
    private EventDTO eventDTO2;

    @BeforeEach
    void setUp() {
    	LocalDateTime now = LocalDateTime.now();
    	
        event1 = new Event(1L, "Título Evento 1", "Descrição Evento 1", LocalDateTime.now().plusDays(1), "Local 1", false, now, now);
        event2 = new Event(2L, "Título Evento 2", "Descrição Evento 2", LocalDateTime.now().plusDays(2), "Local 2", false, now, now);

        eventDTO1 = new EventDTO();
        eventDTO1.setId(1L);
        eventDTO1.setTitle("Título Evento 1");
        eventDTO1.setDescription("Descrição Evento 1");
        eventDTO1.setEventDateTime(LocalDateTime.now().plusDays(1));
        eventDTO1.setLocation("Local 1");
        eventDTO1.setCreatedAt(now);
        eventDTO1.setUpdatedAt(now);

        eventDTO2 = new EventDTO();
        eventDTO2.setId(2L);
        eventDTO2.setTitle("Título Evento 2");
        eventDTO2.setDescription("Descrição Evento 2");
        eventDTO2.setEventDateTime(LocalDateTime.now().plusDays(2));
        eventDTO2.setLocation("Local 2");
        eventDTO2.setCreatedAt(now);
        eventDTO2.setUpdatedAt(now);
    }

    @Test
    void findAll_shouldReturnPageOfEventDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(Arrays.asList(event1, event2), pageable, 2);
        when(eventRepository.findByDeletedFalse(pageable)).thenReturn(eventPage);

        Page<EventDTO> result = eventService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(eventDTO1.getTitle(), result.getContent().get(0).getTitle());
        assertEquals(eventDTO2.getTitle(), result.getContent().get(1).getTitle());
        assertEquals(eventDTO1.getCreatedAt(), result.getContent().get(0).getCreatedAt());
        assertEquals(eventDTO2.getUpdatedAt(), result.getContent().get(1).getUpdatedAt());
    }

    @Test
    void findById_shouldReturnEventDTO_whenEventExistsAndIsNotDeleted() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));

        Optional<EventDTO> result = eventService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(eventDTO1.getTitle(), result.get().getTitle());
    }

    @Test
    void create_shouldReturnCreatedEventDTO() {
    	LocalDateTime now = LocalDateTime.now();
        EventDTO newEventDTO = new EventDTO();
        newEventDTO.setTitle("Novo Evento");
        newEventDTO.setDescription("Descrição Novo Evento");
        newEventDTO.setEventDateTime(LocalDateTime.now().plusDays(3));
        newEventDTO.setLocation("Novo Local");

        Event newEvent = new Event(3L, "Novo Evento", "Descrição Novo Evento", LocalDateTime.now().plusDays(3), "Novo Local", false, now, now);
        when(eventRepository.save(any(Event.class))).thenReturn(newEvent);

        EventDTO result = eventService.create(newEventDTO);

        assertNotNull(result);
        assertEquals(newEvent.getTitle(), result.getTitle());
        assertFalse(newEvent.isDeleted());
    }

    @Test
    void delete_shouldSetDeletedFlagToTrue() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));

        eventService.delete(1L);

        assertTrue(event1.isDeleted());
        verify(eventRepository, times(1)).save(event1);
    }

    @Test
    void delete_shouldThrowException_whenEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> eventService.delete(99L));
        assertEquals("Evento não encontrado ou já excluído", exception.getMessage());
    }
}

