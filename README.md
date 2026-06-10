# 🏍️ Vehicle Telemetry API — Quarkus Nativo, Apache Camel & Kafka

Uma API reativa de altíssima performance desenvolvida para monitoramento e processamento de telemetria de veículos (coordenadas e velocidade) em tempo real. O projeto demonstra como unir mensageria assíncrona, engines de integração e persistência não-bloqueante em um ecossistema totalmente compilado de forma nativa (AOT) e monitorado.

## Objetivo

A Vehicle Telemetry API simula um sistema de monitoramento de frota em tempo real.

O sistema recebe dados de telemetria de veículos, processa eventos de velocidade, persiste informações de localização e disponibiliza consultas históricas através de uma API REST reativa.

## Funcionalidades

- Recebimento de telemetria em tempo real
- Processamento assíncrono com Kafka
- Persistência reativa em PostgreSQL
- Detecção de excesso de velocidade
- Consulta histórica por veículo
- Métricas Prometheus
- Health Checks para ambientes Kubernetes

## Benefícios Técnicos

- Baixa latência
- Alto throughput
- Escalabilidade horizontal
- Observabilidade
- Compilação nativa com GraalVM

## 🛠️ Tecnologias e Ecossistema

- **Java 21** & **Quarkus 3.36** (Modo Reativo)
- **GraalVM / Mandrel** (Compilação Nativa Ahead-of-Time)
- **Apache Camel Quarkus** (Engine de Rotas e Integração)
- **Apache Kafka** (Broker de Mensageria Orientada a Eventos)
- **Hibernate Reactive com Panache** (Persistência Não-Bloqueante)
- **PostgreSQL** (Banco de Dados Relacional)
- **Micrometer & Prometheus Registry** (Coleta de Métricas Core)
- **SmallRye Health** (Métricas de Liveness/Readiness para Kubernetes)

---

## 🏗️ Arquitetura e Fluxo de Dados

A aplicação resolve a ponte entre pools de threads tradicionais de consumo (Apache Camel) e o ecossistema de Event Loop síncrono/reativo do banco de dados (Vert.x) sem causar bloqueios ou vazamento de contexto.

1. **Recepção de Telemetria:** O sistema suporta ingestão de dados tanto via API HTTP quanto através de eventos publicados no Kafka.
2. **Processamento de Eventos Kafka:** O Apache Camel realiza o unmarshal das mensagens recebidas, executa validações e aplica regras de negócio como a detecção de excesso de velocidade.
3. **Persistência Isolada:** Utiliza o padrão de abertura de sessão assíncrona (`Mutiny.SessionFactory`), garantindo escrita não-bloqueante de alto rendimento no PostgreSQL.
4. **Resiliência:** políticas de retentativa automática configuradas através do Apache Camel para falhas transitórias.
5. **Observabilidade (Prometheus/Grafana):** O motor do Micrometer expõe métricas nativas do ecossistema e do JVM/SO no endpoint `/q/metrics` que alimenta os dashboards.

### Fluxo Simplificado

```text
┌─────────────┐
│ Cliente API │
└──────┬──────┘
       │ HTTP POST
       ▼
┌─────────────┐
│   Quarkus   │
│ Vehicle API │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ PostgreSQL  │
└─────────────┘


┌─────────────┐
│    Kafka    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Apache Camel│
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ PostgreSQL  │
└─────────────┘

---

## 🚀 Como Executar o Ecossistema (Produção Nativa)

Com a compilação nativa concluída com sucesso, toda a infraestrutura e o binário otimizado da aplicação são orquestrados de forma isolada e integrada via Docker Compose.

### Pré-requisitos
- Docker e Docker Compose instalados.

### Inicialização Rápida
Na raiz do projeto, limpe eventuais conflitos residuais e suba toda a stack (Banco, Kafka e a API Nativa):

```bash
# Derrubar containers órfãos das portas locais
docker rm -f telemetry-db telemetry-kafka

# Subir a stack integrada
docker compose up --force-recreate
```

### Validação de Inicialização Nativa
Graças à compilação Ahead-of-Time (AOT), a aplicação reduz drasticamente o tempo de inicialização em comparação com uma JVM tradicional.

```plaintext
Quarkus 3.36.1 native (powered by GraalVM) started in 0.035s. Listening on: http://0.0.0.0:8080
```

## 🧪 Massa de Teste (Endpoints HTTP)

**Enviar Telemetria (POST):**
```bash
curl -X POST http://localhost:8080/telemetry \
  -H "Content-Type: application/json" \
  -d '{"vehicleId": "Hunter-350", "latitude": -25.5030, "longitude": -49.3060, "speed": 115}'
```
**Listar Histórico Geral Ordenado (GET):**
```bash
curl http://localhost:8080/telemetry
```


## 📊 Métricas & Observabilidade
- **Métricas Cruas (Prometheus format):** GET http://localhost:8080/q/metrics

- **Health Checks (Kubernetes/Pod status):** GET http://localhost:8080/q/health

- **Painel Prometheus:** http://localhost:9090 (Métrica alvo: http_server_requests_seconds_count)

## 🏁 Linha de Chegada do Projeto
- [x] **Fase 1 & 2:** Criação da API REST Reativa e validações de dados com Panache Active Record.**

- [x] **Fase 3:** Integração de Event-Driven Architecture com Apache Camel e Kafka.

- [x] **Fase 4:** Resiliência em Threads reativas usando Mutiny.SessionFactory.

- [x] **Fase 5:** Monitoramento contínuo com Prometheus e Grafana via Micrometer Core.

- [x] **Fase 6:** Compilação Nativa com GraalVM Mandrel Builder gerando imagens Docker ultra-leves e de boot instantâneo.


