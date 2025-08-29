package me.dio.barbearia.domain.repository;

import me.dio.barbearia.domain.model.AvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Long> {
    List<AvailableTime> findByDateAndIsBookedFalse(LocalDate date);
    Optional<AvailableTime> findByDateAndStartTimeAndEndTimeAndIsBookedFalse(LocalDate date, LocalTime startTime, LocalTime endTime);
}