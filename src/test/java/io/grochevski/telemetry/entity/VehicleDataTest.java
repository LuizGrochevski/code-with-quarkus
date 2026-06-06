package io.grochevski.telemetry.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleDataTest {

    private VehicleData buildData(String vehicleId, int speed) {
        VehicleData data = new VehicleData();
        data.vehicleId = vehicleId;
        data.speed = speed;
        data.latitude = -25.5030;
        data.longitude = -49.3060;
        return data;
    }

    @Test
    void deveria_retornar_true_quando_velocidade_acima_de_110() {
        VehicleData data = buildData("Hunter-350", 111);
        assertTrue(data.isSpeeding());
    }

    @Test
    void deveria_retornar_false_quando_velocidade_igual_a_110() {
        VehicleData data = buildData("Hunter-350", 110);
        assertFalse(data.isSpeeding());
    }

    @Test
    void deveria_retornar_false_quando_velocidade_abaixo_de_110() {
        VehicleData data = buildData("Hunter-350", 80);
        assertFalse(data.isSpeeding());
    }

    @Test
    void deveria_retornar_false_quando_velocidade_zero() {
        VehicleData data = buildData("Hunter-350", 0);
        assertFalse(data.isSpeeding());
    }
}

