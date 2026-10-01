package com.fiap.pedido_observavel.service;

import com.fiap.pedido_observavel.dto.PedidoResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final RestClient restClient;
    private final MeterRegistry registry;

    private final Timer pedidosDuracao;
    private final Timer freteDuracao;

    public PedidoService(RestClient restClient, MeterRegistry registry) {
        this.restClient = restClient;
        this.registry = registry;

        this.pedidosDuracao = Timer.builder("pedidos_duracao")
                .description("Duração do processamento dos pedidos")
                .publishPercentiles(0.50, 0.95, 0.99)
                .register(registry);

        this.freteDuracao = Timer.builder("frete_duracao")
                .description("Duração das chamadas ao serviço de frete")
                .register(registry);
    }

    @Observed(
            name = "pedido.processar",
            contextualName = "processar-pedido",
            lowCardinalityKeyValues = {"camada", "service"}
    )
    public PedidoResponse processar(Long id) {

        long inicio = System.nanoTime();

        log.info("Iniciando processamento do pedido {}", id);

        try {

            log.info("Chamando serviço de frete para o pedido {}", id);

            long inicioFrete = System.nanoTime();

            Map<?, ?> resposta = restClient.get()
                    .uri("/frete/{cep}", "01001000")
                    .retrieve()
                    .body(Map.class);

            long duracaoFrete = System.nanoTime() - inicioFrete;

            freteDuracao.record(
                    duracaoFrete,
                    TimeUnit.NANOSECONDS
            );

            log.info(
                    "Serviço de frete respondeu em {} ms",
                    TimeUnit.NANOSECONDS.toMillis(duracaoFrete)
            );

            String transportadora = "Desconhecida";
            double valorFrete = 0.0;

            if (resposta != null) {

                Object transportadoraResposta = resposta.get("transportadora");
                Object valorResposta = resposta.get("valor");

                if (transportadoraResposta != null) {
                    transportadora = String.valueOf(transportadoraResposta);
                }

                if (valorResposta != null) {
                    valorFrete = Double.parseDouble(
                            String.valueOf(valorResposta)
                    );
                }
            }

            long duracao = System.nanoTime() - inicio;

            pedidosDuracao.record(
                    duracao,
                    TimeUnit.NANOSECONDS
            );

            Counter.builder("pedidos_consultados")
                    .description("Total de pedidos consultados")
                    .tag("status", "sucesso")
                    .register(registry)
                    .increment();

            log.info(
                    "Pedido {} processado com sucesso em {} ms",
                    id,
                    TimeUnit.NANOSECONDS.toMillis(duracao)
            );

            return new PedidoResponse(
                    id,
                    "Maria Silva",
                    250.00,
                    valorFrete,
                    transportadora,
                    TimeUnit.NANOSECONDS.toMillis(duracao)
            );

        } catch (Exception e) {

            Counter.builder("pedidos_consultados")
                    .description("Total de pedidos consultados")
                    .tag("status", "erro")
                    .register(registry)
                    .increment();

            log.warn(
                    "Erro ao processar o pedido {}: {}",
                    id,
                    e.getMessage()
            );

            throw e;
        }
    }

    public String processarLento() {

        log.info("Iniciando processamento lento");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Processamento lento foi interrompido");
        }

        log.info("Processamento lento finalizado");

        return "Processamento lento concluído";
    }
}