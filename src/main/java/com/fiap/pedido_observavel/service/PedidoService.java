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


    public PedidoService (RestClient restClient, MeterRegistry registry){
        this.restClient = restClient;
        this.registry = registry;

        this.pedidosDuracao = Timer.builder("pedidos_duracao")
                .description("Duração do processamento dos pedidos")
                .publishPercentiles(0.5,0.95,0.99)
                .register(registry);

        this.freteDuracao = Timer.builder("frete_duracao")
                .description("Duração das chamadas ao serviço de frete")
                .register(registry);
    }

    @Observed(name = "pedido.processar",
              contextualName = "processar-pedido",
              lowCardinalityKeyValues = {"camada","service"}
    )
    public PedidoResponse processar(Long id){
        long inicio = System.nanoTime();

        log.info("Iniciando processamento do pedido {}",id);

        try {
            log.info("Chamando serviço de frete para o pedido {}",id);

            long inicioFrete = System.nanoTime();


        Map<?,?> resposta = restClient.get()
                .uri("/frete/{cep}","01001000")
                .retrieve()
                .body(Map.class);//Converto o corpo do Map
        //Extrai a transportadora ou usa fallback
        String transportadora = resposta != null
                ? String.valueOf(resposta.get("url"))
                :"desconhecida";
        //Calcular a duração total
        long duracao = System.nanoTime() - inicio;
        //Registra a duração do timer
        pedidoTimer.record(duracao, TimeUnit.NANOSECONDS);
        //Log final com o tempo gasto
        log.info("Pedido {} processado em {} ns",id,duracao);
        //Retornar o DTO montado
        return new PedidoResponse(
                id,
                "PROCESSADO",
                transportadora,
                TimeUnit.NANOSECONDS.toMillis(duracao)
        );
    }

}
