package me.dio.barbearia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.dio.barbearia.domain.model.Appointment;
import me.dio.barbearia.domain.model.AvailableTime;
import me.dio.barbearia.service.AppointmentService;
import me.dio.barbearia.service.AvailableTimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Agendamentos", description = "Operações relacionadas a agendamentos na barbearia")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AvailableTimeService availableTimeService;

    public AppointmentController(AppointmentService appointmentService, AvailableTimeService availableTimeService) {
        this.appointmentService = appointmentService;
        this.availableTimeService = availableTimeService;
    }

    @GetMapping
    @Operation(summary = "Obtém todos os agendamentos")
    public ResponseEntity<List<Appointment>> findAll() {
        List<Appointment> appointments = appointmentService.findAll();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém um agendamento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<Appointment> findById(@PathVariable Long id) {
        Appointment appointment = appointmentService.findById(id);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/available-times")
    @Operation(summary = "Obtém horários disponíveis para agendamento em uma data específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horários disponíveis encontrados")
    })
    public ResponseEntity<List<AvailableTime>> findAvailableTimes(@RequestParam LocalDate date) {
        List<AvailableTime> availableTimes = availableTimeService.findAvailableTimesByDate(date);
        return ResponseEntity.ok(availableTimes);
    }

    @PostMapping("/available-times")
    @Operation(summary = "Cria um novo horário disponível para agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horário disponível criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou horário já existente")
    })
    public ResponseEntity<AvailableTime> createAvailableTime(@Valid @RequestBody AvailableTime availableTime) {
        AvailableTime createdAvailableTime = availableTimeService.create(availableTime);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAvailableTime.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdAvailableTime);
    }

    @PutMapping("/available-times/{id}")
    @Operation(summary = "Atualiza um horário disponível existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horário disponível atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou horário já agendado"),
            @ApiResponse(responseCode = "404", description = "Horário disponível não encontrado")
    })
    public ResponseEntity<AvailableTime> updateAvailableTime(@PathVariable Long id, @Valid @RequestBody AvailableTime availableTimeUpdate) {
        AvailableTime updatedAvailableTime = availableTimeService.update(id, availableTimeUpdate);
        return ResponseEntity.ok(updatedAvailableTime);
    }

    @DeleteMapping("/available-times/{id}")
    @Operation(summary = "Exclui um horário disponível")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horário disponível excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível excluir horário já agendado"),
            @ApiResponse(responseCode = "404", description = "Horário disponível não encontrado")
    })
    public ResponseEntity<Void> deleteAvailableTime(@PathVariable Long id) {
        availableTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Cria um novo agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, horário já agendado ou inconsistência de data/hora"),
            @ApiResponse(responseCode = "404", description = "Cliente, serviço ou horário disponível não encontrado")
    })
    public ResponseEntity<Appointment> create(@Valid @RequestBody Appointment appointmentToCreate) {
        Appointment createdAppointment = appointmentService.create(appointmentToCreate);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAppointment.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdAppointment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um agendamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, agendamento já ocorreu ou novo horário já agendado"),
            @ApiResponse(responseCode = "404", description = "Agendamento, cliente, serviço ou horário disponível não encontrado")
    })
    public ResponseEntity<Appointment> update(@PathVariable Long id, @Valid @RequestBody Appointment appointmentToUpdate) {
        Appointment updatedAppointment = appointmentService.update(id, appointmentToUpdate);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancela/Exclui um agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}