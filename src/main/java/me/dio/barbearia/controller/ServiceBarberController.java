package me.dio.barbearia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.dio.barbearia.domain.model.ServiceBarber;
import me.dio.barbearia.domain.model.ServiceType;
import me.dio.barbearia.service.ServiceBarberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/services")
@Tag(name = "Serviços", description = "Operações relacionadas aos serviços da barbearia")
public class ServiceBarberController {

    private final ServiceBarberService serviceBarberService;

    public ServiceBarberController(ServiceBarberService serviceBarberService) {
        this.serviceBarberService = serviceBarberService;
    }

    @GetMapping
    @Operation(summary = "Obtém todos os serviços")
    public ResponseEntity<List<ServiceBarber>> findAll() {
        List<ServiceBarber> services = serviceBarberService.findAll();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém um serviço por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServiceBarber> findById(@PathVariable Long id) {
        ServiceBarber serviceBarber = serviceBarberService.findById(id);
        return ResponseEntity.ok(serviceBarber);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Obtém um serviço por tipo (BARBA, CABELO, COMBO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado para o tipo especificado")
    })
    public ResponseEntity<ServiceBarber> findByType(@PathVariable ServiceType type) {
        ServiceBarber serviceBarber = serviceBarberService.findByType(type);
        return ResponseEntity.ok(serviceBarber);
    }

    @PostMapping
    @Operation(summary = "Cria um novo serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou serviço já cadastrado para o tipo")
    })
    public ResponseEntity<ServiceBarber> create(@Valid @RequestBody ServiceBarber serviceBarberToCreat) {
        ServiceBarber createdServiceBarber = serviceBarberService.create(serviceBarberToCreat);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdServiceBarber.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdServiceBarber);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um serviço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou serviço já cadastrado para o tipo"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServiceBarber> update(@PathVariable Long id, @Valid @RequestBody ServiceBarber serviceBarberToUpdate) {
        ServiceBarber updatedServiceBarber = serviceBarberService.update(id, serviceBarberToUpdate);
        return ResponseEntity.ok(updatedServiceBarber);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceBarberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/initialize-defaults")
    @Operation(summary = "Inicializa os serviços padrão (Barba, Cabelo, Combo) se não existirem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviços padrão inicializados ou já existentes")
    })
    public ResponseEntity<String> initializeDefaultServices() {
        serviceBarberService.initializeDefaultServices();
        return ResponseEntity.ok("Serviços padrão inicializados ou já existentes.");
    }
}