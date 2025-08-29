package me.dio.barbearia.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class ServiceBarber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private ServiceType type;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;


    public ServiceBarber(ServiceType type) {
        this.type = type;
        this.price = BigDecimal.valueOf(type.getPrice());
    }

    public ServiceBarber() {
    }
}