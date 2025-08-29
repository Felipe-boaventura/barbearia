package me.dio.barbearia.service;

import me.dio.barbearia.domain.model.AvailableTime;
import me.dio.barbearia.domain.model.ServiceBarber;
import me.dio.barbearia.domain.repository.AvailableTimeRepository;
import me.dio.barbearia.util.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AvailableTimeService {

    private final AvailableTimeRepository availableTimeRepository;
    private final ServiceBarberService serviceBarberService;

    public AvailableTimeService(AvailableTimeRepository availableTimeRepository, ServiceBarberService serviceService) {
        this.availableTimeRepository = availableTimeRepository;
        this.serviceBarberService = serviceService;
    }

    @Transactional(readOnly = true)
    public List<AvailableTime> findAll() {
        return availableTimeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AvailableTime findById(Long id) {
        return availableTimeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Horário disponível não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<AvailableTime> findAvailableTimesByDate(LocalDate date) {
        return availableTimeRepository.findByDateAndIsBookedFalse(date);
    }

    @Transactional
    public AvailableTime create(AvailableTime availableTime) {
        ServiceBarber serviceBarber = serviceBarberService.findById(availableTime.getServiceBarber().getId());
        availableTime.setServiceBarber(serviceBarber);

        if (availableTimeRepository.findByDateAndStartTimeAndEndTimeAndIsBookedFalse(
                availableTime.getDate(), availableTime.getStartTime(), availableTime.getEndTime()).isPresent()) {
            throw new BusinessException("Já existe um horário disponível idêntico e não agendado para esta data e hora.");
        }

        if (availableTime.getStartTime().isAfter(availableTime.getEndTime()) || availableTime.getStartTime().equals(availableTime.getEndTime())) {
            throw new BusinessException("O horário de início deve ser anterior ao horário de término.");
        }

        return availableTimeRepository.save(availableTime);
    }

    @Transactional
    public AvailableTime update(Long id, AvailableTime availableTimeUpdate) {
        AvailableTime dbAvailableTime = availableTimeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Horário disponível não encontrado com ID: " + id));

        if (dbAvailableTime.isBooked()) {
            throw new BusinessException("Não é possível alterar um horário que já foi agendado.");
        }

        ServiceBarber serviceBarber = serviceBarberService.findById(availableTimeUpdate.getServiceBarber().getId());
        dbAvailableTime.setServiceBarber(serviceBarber);

        dbAvailableTime.setDate(availableTimeUpdate.getDate());
        dbAvailableTime.setStartTime(availableTimeUpdate.getStartTime());
        dbAvailableTime.setEndTime(availableTimeUpdate.getEndTime());
        dbAvailableTime.setBooked(availableTimeUpdate.isBooked());

        if (dbAvailableTime.getStartTime().isAfter(dbAvailableTime.getEndTime()) || dbAvailableTime.getStartTime().equals(dbAvailableTime.getEndTime())) {
            throw new BusinessException("O horário de início deve ser anterior ao horário de término.");
        }

        return availableTimeRepository.save(dbAvailableTime);
    }

    @Transactional
    public void delete(Long id) {
        AvailableTime availableTime = availableTimeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Horário disponível não encontrado com ID: " + id));
        if (availableTime.isBooked()) {
            throw new BusinessException("Não é possível excluir um horário que já foi agendado.");
        }
        availableTimeRepository.deleteById(id);
    }
}