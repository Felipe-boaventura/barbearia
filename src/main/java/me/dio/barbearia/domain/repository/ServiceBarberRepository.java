package me.dio.barbearia.domain.repository;


import me.dio.barbearia.domain.model.ServiceBarber;
import me.dio.barbearia.domain.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceBarberRepository extends JpaRepository<ServiceBarber, Long> {
    Optional<ServiceBarber> findByType(ServiceType type);
}