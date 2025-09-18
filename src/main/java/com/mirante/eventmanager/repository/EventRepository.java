package com.mirante.eventmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mirante.eventmanager.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByDeletedFalse(Pageable pageable);
}

