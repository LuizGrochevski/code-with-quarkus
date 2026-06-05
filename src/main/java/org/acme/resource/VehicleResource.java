package org.acme.resource;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.quarkus.hibernate.reactive.panache.Panache;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import org.acme.entity.VehicleData;

@Path("/telemetry")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleResource {

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
        
        return Panache.withTransaction(data::persist)
                .replaceWith(Response.status(Response.Status.CREATED).entity(data).build());
    }
}