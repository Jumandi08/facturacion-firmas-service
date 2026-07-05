package org.ups.facturacionfirmas.adapters.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ups.facturacionfirmas.adapters.rest.dto.ClienteResponse;
import org.ups.facturacionfirmas.adapters.rest.dto.RegistrarClienteRequest;
import org.ups.facturacionfirmas.application.usecase.ConsultarCarteraService;
import org.ups.facturacionfirmas.application.usecase.RegistrarClienteService;
import org.ups.facturacionfirmas.domain.Cliente;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final RegistrarClienteService registrarClienteService;
    private final ConsultarCarteraService consultarCarteraService;
    private final ClienteMapper clienteMapper;

    public ClienteController(
            RegistrarClienteService registrarClienteService,
            ConsultarCarteraService consultarCarteraService,
            ClienteMapper clienteMapper) {
        this.registrarClienteService = registrarClienteService;
        this.consultarCarteraService = consultarCarteraService;
        this.clienteMapper = clienteMapper;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(@Valid @RequestBody RegistrarClienteRequest request) {
        Cliente cliente = registrarClienteService.registrar(request.nombre(), request.fechaVencimiento());
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toResponse(cliente));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> consultarCartera() {
        List<ClienteResponse> respuesta = consultarCarteraService.consultarTodos().stream()
                .map(clienteMapper::toResponse)
                .toList();
        return ResponseEntity.ok(respuesta);
    }
}
