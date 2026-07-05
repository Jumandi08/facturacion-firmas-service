package org.ups.facturacionfirmas.adapters.persistence;

import org.springframework.stereotype.Repository;
import org.ups.facturacionfirmas.application.port.ClienteRepository;
import org.ups.facturacionfirmas.domain.Cliente;

import java.util.List;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getFechaVencimiento());
        ClienteEntity guardado = jpaRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Cliente toDomain(ClienteEntity entity) {
        return new Cliente(entity.getId(), entity.getNombre(), entity.getFechaVencimiento());
    }
}
