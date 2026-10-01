package com.fiap.pedido_observavel.dto;

import java.math.BigDecimal;

public record PedidoResponse(
        Long id,
        String cliente,
        BigDecimal valorTotal,
        BigDecimal frete,
        String transportadora,
        long tempoProcessamentoMs
) {}




