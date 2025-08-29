package me.dio.barbearia.service;

import me.dio.barbearia.domain.model.Client;
import me.dio.barbearia.domain.repository.ClientRepository;
import me.dio.barbearia.util.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com ID: " + id));
    }

    @Transactional
    public Client create(Client clientToCreate) {
        if (clientRepository.findByCpf(clientToCreate.getCpf()).isPresent()) {
            throw new BusinessException("CPF já cadastrado para outro cliente.");
        }
        return clientRepository.save(clientToCreate);
    }

    @Transactional
    public Client update(Long id, Client clientToUpdate) {
        Client dbClient = clientRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado com ID: " + id));

        if (!dbClient.getCpf().equals(clientToUpdate.getCpf()) && clientRepository.findByCpf(clientToUpdate.getCpf()).isPresent()) {
            throw new BusinessException("CPF já cadastrado para outro cliente.");
        }

        dbClient.setName(clientToUpdate.getName());
        dbClient.setCpf(clientToUpdate.getCpf());
        dbClient.setPhone(clientToUpdate.getPhone());

        return clientRepository.save(dbClient);
    }

    @Transactional
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new NoSuchElementException("Cliente não encontrado com ID: " + id);
        }
        clientRepository.deleteById(id);
    }
}