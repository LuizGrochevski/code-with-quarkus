package org.acme.resource;

import io.smallrye.mutiny.Uni;
import io.quarkus.hibernate.reactive.panache.Panache;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.acme.entity.VehicleData;
import org.jboss.logging.Logger;

@Path("/telemetry")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleResource {

    private static final Logger LOG = Logger.getLogger(VehicleResource.class);
    
    @GET
    @Path("/{vehicleId}")
    public io.smallrye.mutiny.Uni<java.util.List<VehicleData>> getVehicleHistory(@PathParam("vehicleId") String vehicleId) {
        return VehicleData.findByVehicle(vehicleId);
    }

    @POST
    public Uni<Response> receiveTelemetry(VehicleData data) {
        if (data.timestamp == null) {
            data.timestamp = LocalDateTime.now();
        }

        if (data.isSpeeding()) {
            LOG.warnf("⚠️ ALERTA: Veículo %s atingiu %d km/h! Localização: %f, %f", 
                data.vehicleId, data.speed, data.latitude, data.longitude);
        } else {
            LOG.infof("Telemetria recebida para o veículo %s (%d km/h)", data.vehicleId, data.speed);
        }
        
        return Panache.withTransaction(data::persist)
                .replaceWith(Response.status(Response.Status.CREATED).entity(data).build());
    }
}