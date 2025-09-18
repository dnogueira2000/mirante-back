package com.mirante.eventmanager.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;

    @NotBlank(message = "O título não pode ser vazio")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    private String title;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String description;

    @NotNull(message = "A data e hora do evento não podem ser vazia")
    @FutureOrPresent(message = "A data e hora do evento devem ser no presente ou no futuro")
    private LocalDateTime eventDateTime;

    @Size(max = 200, message = "O local deve ter no máximo 200 caracteres")
    private String location;
}

