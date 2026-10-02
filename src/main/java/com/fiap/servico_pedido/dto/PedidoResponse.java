package com.fiap.servico_pedido.dto;

public record PedidoResponse(
        Long id,
        String cliente,
        double valorTotal,
        double frete,
        String transportadora,
        long tempoProcessamentoMs
) {}




