package org.acme.route;

import org.acme.entity.VehicleData;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.hibernate.reactive.mutiny.Mutiny;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VehicleRoute extends RouteBuilder {

@Inject
    Mutiny.SessionFactory sessionFactory;

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(2000)
            .handled(true) // Marca como tratado para não quebrar o loop do Kafka
            .logHandled(true)
            .log("❌ [ERRO CRÍTICO] Falha ao persistir telemetria no banco após retentativas. Mensagem descartada.");

        from("kafka:vehicle-telemetry?brokers={{kafka.bootstrap.servers}}")
            .unmarshal().json(JsonLibrary.Jackson, VehicleData.class)
            .process(exchange -> {
                VehicleData data = exchange.getIn().getBody(VehicleData.class);
                
                if (data.speed > 110) {
                    log.warn("🚨 [KAFKA] ALERTA DE VELOCIDADE: Veículo " + data.vehicleId + " a " + data.speed + " km/h!");
                } else {
                    log.info("📥 [KAFKA] Telemetria processada para o veículo " + data.vehicleId);
                }

                sessionFactory.openSession()
                    .flatMap(session -> session.persist(data)
                        .flatMap(v -> session.flush()) // Força a gravação no banco
                        .onTermination().call(session::close) // Garante o fechamento da sessão
                    )
                    .subscribe().with(
                        item -> log.info("💾 [BANCO] Telemetria de " + data.vehicleId + " gravada com sucesso total!"),
                        err -> log.error("❌ Erro fatal de persistência: " + err.getMessage())
                    );
            });
    }
}