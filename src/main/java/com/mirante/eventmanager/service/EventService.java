package com.mirante.eventmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mirante.eventmanager.dto.EventDTO;

import java.util.Optional;

public interface EventService {
    Page<EventDTO> findAll(Pageable pageable);
    Optional<EventDTO> findById(Long id);
    EventDTO create(EventDTO eventDTO);
    EventDTO update(Long id, EventDTO eventDTO);
    void delete(Long id);
}

