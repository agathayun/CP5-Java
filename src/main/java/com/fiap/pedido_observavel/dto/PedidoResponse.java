package com.fiap.pedido_observavel.dto;

import java.math.BigDecimal;

public record PedidoResponse(
        Long id,
        String cliente,
        double valorTotal,
        double frete,
        String transportadora,
        long tempoProcessamentoMs
) {}




