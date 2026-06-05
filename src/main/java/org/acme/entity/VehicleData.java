package org.acme.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import io.smallrye.mutiny.Multi;

@Entity
public class VehicleData extends PanacheEntity {

    public String vehicleId;
    public double latitude;
    public double longitude;
    public int speed;
    public LocalDateTime timestamp;

    public static io.smallrye.mutiny.Uni<java.util.List<VehicleData>> findByVehicle(String vehicleId) {
        return list("vehicleId", vehicleId);
    }
}