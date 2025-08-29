package me.dio.barbearia.service;

import me.dio.barbearia.domain.model.Appointment;
import me.dio.barbearia.domain.model.AvailableTime;
import me.dio.barbearia.domain.model.Client;
import me.dio.barbearia.domain.model.ServiceBarber;
import me.dio.barbearia.domain.repository.AppointmentRepository;
import me.dio.barbearia.util.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClientService clientService;
    private final ServiceBarberService serviceBarberService;
    private final AvailableTimeService availableTimeService;

    public AppointmentService(AppointmentRepository appointmentRepository, ClientService clientService, ServiceBarberService serviceService, AvailableTimeService availableTimeService) {
        this.appointmentRepository = appointmentRepository;
        this.clientService = clientService;
        this.serviceBarberService = serviceService;
        this.availableTimeService = availableTimeService;
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado com ID: " + id));
    }

    @Transactional
    public Appointment create(Appointment appointmentToCreate) {
        Client client = clientService.findById(appointmentToCreate.getClient().getId());
        appointmentToCreate.setClient(client);

        ServiceBarber serviceBarber = serviceBarberService.findById(appointmentToCreate.getServiceBarber().getId());
        appointmentToCreate.setServiceBarber(serviceBarber);

        AvailableTime availableTime = availableTimeService.findById(appointmentToCreate.getAvailableTime().getId());

        if (availableTime.isBooked()) {
            throw new BusinessException("O horário selecionado já está agendado.");
        }

        LocalDateTime proposedDateTime = appointmentToCreate.getDateTime();
        LocalDateTime startOfAvailableTime = LocalDateTime.of(availableTime.getDate(), availableTime.getStartTime());
        LocalDateTime endOfAvailableTime = LocalDateTime.of(availableTime.getDate(), availableTime.getEndTime());

        if (!proposedDateTime.isEqual(startOfAvailableTime)) {
            throw new BusinessException("A data e hora do agendamento devem corresponder ao início do horário disponível selecionado.");
        }

        availableTime.setBooked(true);
        availableTimeService.update(availableTime.getId(), availableTime); // Atualiza o status no banco

        appointmentToCreate.setAvailableTime(availableTime);

        return appointmentRepository.save(appointmentToCreate);
    }

    @Transactional
    public Appointment update(Long id, Appointment appointmentToUpdate) {
        Appointment dbAppointment = appointmentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado com ID: " + id));

        if (dbAppointment.getDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível alterar um agendamento que já ocorreu.");
        }

        if (!dbAppointment.getAvailableTime().getId().equals(appointmentToUpdate.getAvailableTime().getId())) {
            AvailableTime oldAvailableTime = dbAppointment.getAvailableTime();
            oldAvailableTime.setBooked(false);
            availableTimeService.update(oldAvailableTime.getId(), oldAvailableTime);

            AvailableTime newAvailableTime = availableTimeService.findById(appointmentToUpdate.getAvailableTime().getId());
            if (newAvailableTime.isBooked()) {
                throw new BusinessException("O novo horário selecionado já está agendado.");
            }
            newAvailableTime.setBooked(true);
            availableTimeService.update(newAvailableTime.getId(), newAvailableTime);
            dbAppointment.setAvailableTime(newAvailableTime);
        }

        Client client = clientService.findById(appointmentToUpdate.getClient().getId());
        ServiceBarber serviceBarber = serviceBarberService.findById(appointmentToUpdate.getServiceBarber().getId());

        dbAppointment.setClient(client);
        dbAppointment.setServiceBarber(serviceBarber);
        dbAppointment.setDateTime(appointmentToUpdate.getDateTime());

        return appointmentRepository.save(dbAppointment);
    }

    @Transactional
    public void delete(Long id) {
        Appointment dbAppointment = appointmentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado com ID: " + id));

        AvailableTime availableTime = dbAppointment.getAvailableTime();
        availableTime.setBooked(false);
        availableTimeService.update(availableTime.getId(), availableTime);

        appointmentRepository.deleteById(id);
    }
}