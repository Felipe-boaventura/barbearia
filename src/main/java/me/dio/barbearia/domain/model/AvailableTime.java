package me.dio.barbearia.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class AvailableTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data não pode ser nula")
    @FutureOrPresent(message = "A data deve ser hoje ou no futuro")
    private LocalDate date;

    @NotNull(message = "O horário de início não pode ser nulo")
    private LocalTime startTime;

    @NotNull(message = "O horário de término não pode ser nulo")
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean isBooked = false;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    @NotNull(message = "O horário disponível deve estar associado a um serviço")
    private ServiceBarber serviceBarber;
}