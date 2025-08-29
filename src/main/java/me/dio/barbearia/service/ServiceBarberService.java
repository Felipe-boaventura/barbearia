package me.dio.barbearia.service;

import me.dio.barbearia.domain.model.ServiceBarber;
import me.dio.barbearia.domain.model.ServiceType;
import me.dio.barbearia.domain.repository.ServiceBarberRepository;
import me.dio.barbearia.util.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ServiceBarberService {

    private final ServiceBarberRepository serviceBarberRepository;

    public ServiceBarberService(ServiceBarberRepository serviceRepository) {
        this.serviceBarberRepository = serviceRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceBarber> findAll() {
        return serviceBarberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ServiceBarber findById(Long id) {
        return serviceBarberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Serviço não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public ServiceBarber findByType(ServiceType type) {
        return serviceBarberRepository.findByType(type).orElseThrow(() -> new NoSuchElementException("Serviço não encontrado para o tipo: " + type.getDescription()));
    }

    @Transactional
    public ServiceBarber create(ServiceBarber serviceToCreate) {
        if (serviceBarberRepository.findByType(serviceToCreate.getType()).isPresent()) {
            throw new BusinessException("Já existe um serviço cadastrado para o tipo: " + serviceToCreate.getType().getDescription());
        }
        return serviceBarberRepository.save(serviceToCreate);
    }

    @Transactional
    public ServiceBarber update(Long id, ServiceBarber serviceBarberToUpdate) {
        ServiceBarber dbService = serviceBarberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Serviço não encontrado com ID: " + id));

        if (!dbService.getType().equals(serviceBarberToUpdate.getType()) && serviceBarberRepository.findByType(serviceBarberToUpdate.getType()).isPresent()) {
            throw new BusinessException("Já existe um serviço cadastrado para o tipo: " + serviceBarberToUpdate.getType().getDescription());
        }

        dbService.setType(serviceBarberToUpdate.getType());
        dbService.setPrice(serviceBarberToUpdate.getPrice());

        return serviceBarberRepository.save(dbService);
    }

    @Transactional
    public void delete(Long id) {
        if (!serviceBarberRepository.existsById(id)) {
            throw new NoSuchElementException("Serviço não encontrado com ID: " + id);
        }
        serviceBarberRepository.deleteById(id);
    }


    @Transactional
    public void initializeDefaultServices() {
        Arrays.stream(ServiceType.values()).forEach(type -> {
            if (serviceBarberRepository.findByType(type).isEmpty()) {
                ServiceBarber newService = new ServiceBarber(type);
                serviceBarberRepository.save(newService);
                System.out.println("Serviço padrão criado: " + type.getDescription());
            }
        });
    }
}