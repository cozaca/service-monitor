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

@Path("/api/services")
@Tag(name = "services")
public class ServiceResource
{
    Logger logger;
    DaoService daoService;

    public ServiceResource(Logger logger, DaoService daoService)
    {
        this.logger = logger;
        this.daoService = daoService;
    }

    @Operation(summary = "Returns all monitored services")
    @GET
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Service.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No Services")
    public Response getAllServices()
    {
        List<Service> allServices = daoService.getAllServices();
        return Response.ok(allServices).build();
    }

    @Operation(summary = "Returns a service for a given id")
    @GET
    @Path("/{id}")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Service.class)))
    @APIResponse(responseCode = "204", description = "The service is not found for a given identifier")
    public Response getService(@RestPath Long id)
    {
        Service service = daoService.getById(id);
        if (service != null)
        {
            return Response.ok(service).build();
        }
        return Response.noContent().build();
    }

    @Operation(summary = "Register a valid service")
    @POST
    @APIResponse(responseCode = "200", description = "Service registered with URI ", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    public Response registerService(@Valid Service service, @Context UriInfo uriInfo)
    {
        service = daoService.persist(service);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(service.id));
        logger.debug("New service registered with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    @Operation(summary = "Update an registered service")
    @PUT
    @APIResponse(responseCode = "200", description = "The updated service", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Schema.class)))
    public Response updateService(@Valid Service service)
    {
        service = daoService.update(service);
        logger.debugf("Service update with new values %s ", service);
        return Response.ok(service).build();
    }

    @Operation(summary = "Unregister/deletes a service")
    @DELETE
    @Path(("/{id}"))
    @APIResponse(responseCode = "200")
    public Response unregisterService(@RestPath Long id)
    {
        daoService.delete(id);
        logger.debugf("Service deleted with $d", id);
        return Response.noContent().build();
    }
}
