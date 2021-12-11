package com.acozac.service;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

@Path("/api/services")
@Tag(name = "services")
public class ServiceResource
{
    Logger logger;

    public ServiceResource(Logger logger)
    {
        this.logger = logger;
    }

    @Operation(summary = "Returns all monitored services")
    @GET
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Service.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No Services")
    public Uni<List<Service>> getAllServices()
    {
        return Service.listAll();
    }

    @Operation(summary = "Returns a service for a given id")
    @GET
    @Path("/{id}")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Service.class)))
    @APIResponse(responseCode = "204", description = "The service is not found for a given identifier")
    public Uni<Response> getService(@RestPath Long id)
    {
        return Service.findById(id)
            .map(ser -> {
                if(ser != null)
                {
                    return Response.ok(ser).build();
                }
                return Response.noContent().build();
            });
    }

    @Operation(summary = "Register a valid service")
    @POST
    @APIResponse(responseCode = "201", description = "Service registered with URI ", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @ReactiveTransactional
    public Uni<Response> registerService(@Valid Service service, @Context UriInfo uriInfo)
    {
        return service.<Service>persist()
            .map(s -> {
                UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(s.id));
                logger.debug("New service registered with URI " + builder.build().toString());
                return Response.created(builder.build()).build();
            });
    }

    @Operation(summary = "Update an registered service")
    @PUT
    @APIResponse(responseCode = "200", description = "The updated service", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Schema.class)))
    @ReactiveTransactional
    public Uni<Response> updateService(@Valid Service service)
    {
        return Service.<Service>findById(service.id)
            .map(s -> {
                s.name = service.name;
                s.url = service.url;
                s.status = service.status;
                s.creationTime = service.creationTime;
                return s;
            })
            .map(s -> {
                logger.debugf("Service update with new values %s ", s);
                return Response.ok(s).build();
            });
    }

    @Operation(summary = "Unregister/deletes a service")
    @DELETE
    @Path(("/{id}"))
    @APIResponse(responseCode = "204")
    @ReactiveTransactional
    public Uni<Response> unregisterService(@RestPath Long id)
    {
        return Service.deleteById(id)
            .invoke(() -> logger.debugf("Service deleted with $d", id))
            .replaceWith(Response.noContent().build());
    }
}
