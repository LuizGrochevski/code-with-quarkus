package org.acme.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class VehicleData extends PanacheEntity {

    @NotBlank(message = "O ID do veículo é obrigatório")
    public String vehicleId;

    @NotNull(message = "A latitude é obrigatória")
    public double latitude;

    @NotNull(message = "A longitude é obrigatória")
    public double longitude;

    @Min(value = 0, message = "A velocidade não pode ser negativa")
    public int speed;

    public LocalDateTime timestamp;

    public boolean isSpeeding() {
        return this.speed > 110;
    }

    public static io.smallrye.mutiny.Uni<java.util.List<VehicleData>> findByVehicle(String vehicleId) {
        return list("vehicleId", vehicleId);
    }
}