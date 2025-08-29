package me.dio.barbearia.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data e hora do agendamento não podem ser nulas")
    @FutureOrPresent(message = "O agendamento deve ser para hoje ou no futuro")
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    @NotNull(message = "O agendamento deve estar associado a um serviço")
    private ServiceBarber serviceBarber;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @NotNull(message = "O agendamento deve estar associado a um cliente")
    private Client client;

    @OneToOne
    @JoinColumn(name = "available_time_id", unique = true, nullable = false)
    @NotNull(message = "O agendamento deve estar associado a um horário disponível")
    private AvailableTime availableTime;
}