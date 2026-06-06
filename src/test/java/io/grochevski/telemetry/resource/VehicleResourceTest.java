package io.grochevski.telemetry.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class VehicleResourceTest {

    private static final String PAYLOAD_NORMAL =
        """
        {
            "vehicleId": "Hunter-350",
            "latitude": -25.5030,
            "longitude": -49.3060,
            "speed": 80
        }
        """;

    private static final String PAYLOAD_SPEEDING =
        """
        {
            "vehicleId": "Falcon-X",
            "latitude": -23.5505,
            "longitude": -46.6333,
            "speed": 150
        }
        """;

    private static final String PAYLOAD_SEM_VEHICLE_ID =
        """
        {
            "latitude": -25.5030,
            "longitude": -49.3060,
            "speed": 80
        }
        """;

    private static final String PAYLOAD_VELOCIDADE_NEGATIVA =
        """
        {
            "vehicleId": "Hunter-350",
            "latitude": -25.5030,
            "longitude": -49.3060,
            "speed": -10
        }
        """;

    // --- Testes do POST /telemetry ---

    @Test
    void post_deveria_retornar_201_com_telemetria_valida() {
        given()
            .contentType("application/json")
            .body(PAYLOAD_NORMAL)
        .when()
            .post("/telemetry")
        .then()
            .statusCode(201)
            .body("vehicleId", equalTo("Hunter-350"))
            .body("speed", equalTo(80))
            .body("timestamp", notNullValue());
    }

    @Test
    void post_deveria_retornar_201_com_veiculo_em_excesso_de_velocidade() {
        given()
            .contentType("application/json")
            .body(PAYLOAD_SPEEDING)
        .when()
            .post("/telemetry")
        .then()
            .statusCode(201)
            .body("vehicleId", equalTo("Falcon-X"))
            .body("speed", equalTo(150));
    }

    @Test
    void post_deveria_retornar_400_sem_vehicleId() {
        given()
            .contentType("application/json")
            .body(PAYLOAD_SEM_VEHICLE_ID)
        .when()
            .post("/telemetry")
        .then()
            .statusCode(400);
    }

    @Test
    void post_deveria_retornar_400_com_velocidade_negativa() {
        given()
            .contentType("application/json")
            .body(PAYLOAD_VELOCIDADE_NEGATIVA)
        .when()
            .post("/telemetry")
        .then()
            .statusCode(400);
    }

    // --- Testes do GET /telemetry ---

    @Test
    void get_deveria_retornar_200_e_lista() {
        // Garante que há ao menos um registro antes de listar
        given()
            .contentType("application/json")
            .body(PAYLOAD_NORMAL)
        .when()
            .post("/telemetry");

        given()
        .when()
            .get("/telemetry")
        .then()
            .statusCode(200)
            .body("$", not(empty()));
    }

    // --- Testes do GET /telemetry/{vehicleId} ---

    @Test
    void get_por_vehicleId_deveria_retornar_apenas_registros_do_veiculo() {
        given()
            .contentType("application/json")
            .body(PAYLOAD_NORMAL)
        .when()
            .post("/telemetry");

        given()
        .when()
            .get("/telemetry/Hunter-350")
        .then()
            .statusCode(200)
            .body("$", not(empty()))
            .body("vehicleId", everyItem(equalTo("Hunter-350")));
    }

    @Test
    void get_por_vehicleId_inexistente_deveria_retornar_lista_vazia() {
        given()
        .when()
            .get("/telemetry/VEICULO-INEXISTENTE-999")
        .then()
            .statusCode(200)
            .body("$", empty());
    }
}

