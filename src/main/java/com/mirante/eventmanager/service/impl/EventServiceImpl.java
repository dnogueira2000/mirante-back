package com.mirante.eventmanager.service.impl;

import com.mirante.eventmanager.dto.EventDTO;
import com.mirante.eventmanager.entity.Event;
import com.mirante.eventmanager.repository.EventRepository;
import com.mirante.eventmanager.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
	

    @Override
    public Page<EventDTO> findAll(Pageable pageable) {
        return eventRepository.findByDeletedFalse(pageable)
                .map(this::convertToDto);
    }

    @Override
    public Optional<EventDTO> findById(Long id) {
        return eventRepository.findById(id)
                .filter(event -> !event.isDeleted())
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public EventDTO create(EventDTO eventDTO) {
        Event event = convertToEntity(eventDTO);
        event.setDeleted(false);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
       
        return convertToDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventDTO update(Long id, EventDTO eventDTO) {
        return eventRepository.findById(id)
                .filter(event -> !event.isDeleted())
                .map(existingEvent -> {
                    existingEvent.setTitle(eventDTO.getTitle());
                    existingEvent.setDescription(eventDTO.getDescription());
                    existingEvent.setEventDateTime(eventDTO.getEventDateTime());
                    existingEvent.setLocation(eventDTO.getLocation());
                    existingEvent.setUpdatedAt(LocalDateTime.now());
                    return convertToDto(eventRepository.save(existingEvent));
                })
                .orElseThrow(() -> new RuntimeException("Evento não encontrado ou já excluído"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        eventRepository.findById(id)
                .filter(event -> !event.isDeleted())
                .ifPresentOrElse(event -> {
                    event.setDeleted(true);
                    event.setUpdatedAt(LocalDateTime.now());
                    eventRepository.save(event);
                }, () -> {
                    throw new RuntimeException("Evento não encontrado ou já excluído");
                });
    }

    private EventDTO convertToDto(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDateTime(event.getEventDateTime());
        dto.setLocation(event.getLocation());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
       
        return dto;
    }

    private Event convertToEntity(EventDTO dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventDateTime(dto.getEventDateTime());
        event.setLocation(dto.getLocation());
        
        return event;
    }
}

