# Serviço de Pedido

## Integrantes

- Agatha Yie Won Yun - RM561507
- Samantha Faruolo Galdi - RM554794

## Objetivo

Projeto desenvolvido para o Checkpoint 5 de Java Avançado da FIAP.

O projeto simula um serviço de pedidos com recursos de observabilidade utilizando logs, métricas e traces distribuídos.

## Tecnologias

- Java
- Spring Boot
- Spring Actuator
- Micrometer
- Prometheus
- Zipkin
- Brave

## Endpoints

### Consultar pedido

```http
GET /pedidos/{id}
```

Consulta um pedido e realiza uma chamada ao serviço de frete.

Exemplo:

```http
GET http://localhost:8080/pedidos/42
```

### Processamento lento

```http
GET /pedidos/lentos
```

Simula um processamento com aproximadamente 2 segundos de duração.

### Métricas

```http
GET /actuator/prometheus
```

Expõe métricas da aplicação no formato Prometheus.

## Observabilidade

A aplicação possui:

- Logs com `traceId` e `spanId`
- Métricas customizadas de pedidos e chamadas de frete
- Percentis p50, p95 e p99
- Traces distribuídos utilizando Zipkin

O serviço é executado na porta:

```text
8080
```

O Zipkin pode ser acessado em:

```text
http://localhost:9411
```

## Execução

Execute o projeto e mantenha também o `servico-frete` rodando na porta `8081`.

## Serviço de Frete

Este projeto utiliza o `servico-frete` para realizar o cálculo do frete.

Repositório: [servico-frete](https://github.com/samyfg41/Cp5_java_frete.git)