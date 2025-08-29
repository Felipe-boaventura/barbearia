package me.dio.barbearia.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome não pode estar em branco")
    private String name;

    @NotBlank(message = "O CPF não pode estar em branco")
    @CPF
    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @NotBlank(message = "O telefone não pode estar em branco")
    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos numéricos")
    @Column(nullable = false, length = 11)
    private String phone;
}

