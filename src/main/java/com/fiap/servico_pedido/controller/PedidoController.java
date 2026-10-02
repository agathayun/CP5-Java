package com.fiap.servico_pedido.controller;

import com.fiap.servico_pedido.dto.PedidoResponse;
import com.fiap.servico_pedido.service.PedidoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService){

        this.pedidoService = pedidoService;
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar (@PathVariable Long id){

        return pedidoService.processar(id);
    }

    @GetMapping("/lentos")
    public String lento(){

        return pedidoService.processarLento();
    }
}
