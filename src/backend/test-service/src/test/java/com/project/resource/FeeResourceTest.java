package com.project.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@QuarkusTest
public class FeeResourceTest {

    @Test
    void testCreateFee() {
        given()
                .contentType("application/json")
                .body("{\"fee_name\": \"Internet\", \"fee_amount\": 150000}")
                .when().post("/fees")
                .then()
                .statusCode(201)
                .body("success", is(true));
    }

    @Test
    void testGetAllFees() {
        given()
                .when().get("/fees")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }
}
