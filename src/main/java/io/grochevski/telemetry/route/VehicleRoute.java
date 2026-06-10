package io.grochevski.telemetry.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.hibernate.reactive.mutiny.Mutiny;

import io.grochevski.telemetry.entity.VehicleData;
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
            .handled(true)
            .logHandled(true)
            .log("❌ [ERRO CRÍTICO] Falha ao persistir telemetria no banco após retentativas. Mensagem descartada.");

        from("kafka:vehicle-telemetry?brokers={{kafka.bootstrap.servers}}")
            .unmarshal().json(JsonLibrary.Jackson, VehicleData.class)
            .process(exchange -> {
                VehicleData data = exchange.getIn().getBody(VehicleData.class);
                
                if (data.isSpeeding()) {
                    log.warn("🚨 [KAFKA] ALERTA DE VELOCIDADE: Veículo " + data.vehicleId + " a " + data.speed + " km/h!");
                } else {
                    log.info("📥 [KAFKA] Telemetria processada para o veículo " + data.vehicleId);
                }

                sessionFactory.openSession()
                    .flatMap(session -> session.persist(data)
                        .flatMap(v -> session.flush())
                        .onTermination().call(session::close)
                    )
                    .await().indefinitely();

                log.info("💾 [BANCO] Telemetria de " + data.vehicleId + " gravada com sucesso!");
            });
    }
}
