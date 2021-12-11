package com.acozac;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.jboss.resteasy.reactive.RestResponse.Status.CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import com.acozac.service.Service;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceResourceTest
{

    private static final int NB_SERVICES = 3;
    private static String serviceId;

    @Test
    void shouldPingOpenAPI()
    {
        given()
            .header(ACCEPT, APPLICATION_JSON)
            .when().get("/q/openapi")
            .then()
            .statusCode(OK.getStatusCode());
    }

    private static Stream<Arguments> service_validation_params()
    {
        return Stream.of(
            Arguments.of(null, "https://quarkus.io/"),
            Arguments.of("", "https://quarkus.io/"),
            Arguments.of("AA", "https://quarkus.io/"),
            Arguments.of("Quarkus Service", null),
            Arguments.of("Quarkus Service", "noAnURL")
        );
    }

    @ParameterizedTest
    @MethodSource("service_validation_params")
    void shouldNotAddInvalidItem(String name, String url)
    {
        Service service = new Service();
        service.name = name;
        service.url = url;
        service.status = "VALID";
        service.creationTime = "20211112";

        given()
            .body(service)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/services")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Order(1)
    void shouldGetInitialItems()
    {
        List<Service> services = get("/api/services").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getServiceTypeRef());
        assertEquals(NB_SERVICES, services.size());
    }

    @Test
    void shouldNotGetUnknownService()
    {
        given()
            .pathParam("id", 458)
            .when().get("/api/services/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(2)
    void shouldAddAnItem()
    {
        Service service = new Service();
        service.name = "My Service";
        service.url = "http://myservice:2020";
        service.status = "VALID";
        service.creationTime = "20211112";

        String location = given()
            .body(service)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/services")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().header("Location");
        assertTrue(location.contains("/api/services"));

        // Stores the id
        String[] segments = location.split("/");
        serviceId = segments[segments.length - 1];
        assertNotNull(serviceId);

        given()
            .pathParam("id", serviceId)
            .when().get("/api/services/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .body("name", Is.is("My Service"))
            .body("url", Is.is("http://myservice:2020"))
            .body("status", Is.is("VALID"))
            .body("creationTime", Is.is("20211112"));

        List<Service> heroes = get("/api/services").then()
            .statusCode(OK.getStatusCode())
            .extract().body().as(getServiceTypeRef());
        assertEquals(NB_SERVICES + 1, heroes.size());
    }

    @Test
    @Order(3)
    public void shouldUpdateAnItem()
    {
        Service service = new Service();
        service.id = Long.valueOf(serviceId);
        service.name = "New name";
        service.url = "http:mynewurl:8785";
        service.status = "invalid";
        service.creationTime = "21211111";

        given()
            .body(service)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .put("/api/services")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body("name", Is.is("New name"))
            .body("url", Is.is("http:mynewurl:8785"))
            .body("status", Is.is("invalid"))
            .body("creationTime", Is.is("21211111"));

        List<Service> services = get("/api/services").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getServiceTypeRef());
        assertEquals(NB_SERVICES + 1, services.size());
    }

    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
            .pathParam("id", serviceId)
            .when().delete("/api/services/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        List<Service> services = get("/api/services").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getServiceTypeRef());
        assertEquals(NB_SERVICES, services.size());
    }

    private TypeRef<List<Service>> getServiceTypeRef()
    {
        return new TypeRef<>()
        {
            // Kept empty on purpose
        };
    }
}