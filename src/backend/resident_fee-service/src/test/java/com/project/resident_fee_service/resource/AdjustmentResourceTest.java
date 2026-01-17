package com.project.resident_fee_service.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AdjustmentResourceTest {

    /**
     * Helper: Create fee category for fee creation.
     */
    private Long createFeeCategoryAndFindId(Long feeTypeId, String name) {
        String body = String.format(
                "{\"fee_type_id\": %d, \"name\": \"%s\", \"description\": \"Helper category\"}",
                feeTypeId, name
        );

        given()
                .contentType("application/json")
                .body(body)
                .post("/api/v1/fee-categories")
                .then()
                .statusCode(201);

        Response response = given()
                .queryParam("fee_type_id", feeTypeId)
                .queryParam("page", 1)
                .queryParam("limit", 50)
                .get("/api/v1/fee-categories")
                .then()
                .statusCode(200)
                .extract().response();

        return response.jsonPath().getLong("data.'fee-categories'.find { it.name == '" + name + "' }.fee_category_id");
    }

    /**
     * Helper: Create fee for adjustment creation.
     */
    private Long createFeeAndFindId(Long feeTypeId, Long feeCategoryId, String feeName) {
        String body = String.format(
                "{\"fee_type_id\": %d, \"fee_category_id\": %d, \"fee_name\": \"%s\", \"fee_description\": \"Helper fee\", \"fee_amount\": 50000, \"applicable_month\": \"2025-10\", \"effective_date\": \"2025-10-01\", \"expiry_date\": \"2025-10-31\", \"status\": \"ACTIVE\"}",
                feeTypeId, feeCategoryId, feeName
        );

        given()
                .contentType("application/json")
                .body(body)
                .post("/api/v1/fees")
                .then()
                .statusCode(201);

        Response response = given()
                .queryParam("fee_name", feeName)
                .queryParam("page", 1)
                .queryParam("limit", 5)
                .get("/api/v1/fees")
                .then()
                .statusCode(200)
                .extract().response();

        return response.jsonPath().getLong("data.fees.find { it.fee_name == '" + feeName + "' }.fee_id");
    }

    /**
     * Helper: Create adjustment and find its id via listing API.
     */
    private Long createAdjustmentAndFindId(Long feeId, String reason) {
        String body = String.format(
                "{\"fee_id\": %d, \"adjustment_amount\": 10000, \"adjustment_type\": \"decrease\", \"reason\": \"%s\", \"effective_date\": \"2025-10-01\", \"expiry_date\": \"2025-10-31\"}",
                feeId, reason
        );

        given()
                .contentType("application/json")
                .body(body)
                .post("/api/v1/adjustments")
                .then()
                .statusCode(201);

        Response response = given()
                .queryParam("fee_id", feeId)
                .queryParam("page", 1)
                .queryParam("limit", 50)
                .get("/api/v1/adjustments")
                .then()
                .statusCode(200)
                .extract().response();

        return response.jsonPath().getLong("data.adjustments.find { it.reason == '" + reason + "' }.adjustment_id");
    }

    @Test
    void testCreateAdjustment() {
        Long feeCategoryId = createFeeCategoryAndFindId(1L, "AdjCat-Create");
        Long feeId = createFeeAndFindId(1L, feeCategoryId, "AdjFee-Create");

        given()
                .contentType("application/json")
                .body(String.format(
                        "{\"fee_id\": %d, \"adjustment_amount\": 5000, \"adjustment_type\": \"increase\", \"reason\": \"Test create\", \"effective_date\": \"2025-10-01\", \"expiry_date\": \"2025-10-31\"}",
                        feeId
                ))
                .when()
                .post("/api/v1/adjustments")
                .then()
                .statusCode(201)
                .body("success", is(true))
                .body("code", is(201));
    }

    @Test
    void testGetAllAdjustments() {
        Long feeCategoryId = createFeeCategoryAndFindId(1L, "AdjCat-List");
        Long feeId = createFeeAndFindId(1L, feeCategoryId, "AdjFee-List");
        createAdjustmentAndFindId(feeId, "Test list");

        given()
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get("/api/v1/adjustments")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.adjustments", is(not(empty())))
                .body("data.total_items", greaterThanOrEqualTo(1))
                .body("data.page", is(1));
    }

    @Test
    void testGetAdjustmentById() {
        Long feeCategoryId = createFeeCategoryAndFindId(1L, "AdjCat-Detail");
        Long feeId = createFeeAndFindId(1L, feeCategoryId, "AdjFee-Detail");
        Long adjustmentId = createAdjustmentAndFindId(feeId, "Test detail");

        given()
                .when()
                .get("/api/v1/adjustments/" + adjustmentId)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.adjustment_id", is(adjustmentId.intValue()));
    }

    @Test
    void testUpdateAdjustment() {
        Long feeCategoryId = createFeeCategoryAndFindId(1L, "AdjCat-Update");
        Long feeId = createFeeAndFindId(1L, feeCategoryId, "AdjFee-Update");
        Long adjustmentId = createAdjustmentAndFindId(feeId, "Test update");

        String updateBody = String.format(
                "{\"adjustment_id\": %d, \"fee_id\": %d, \"adjustment_amount\": 15000, \"adjustment_type\": \"increase\", \"reason\": \"Updated reason\", \"effective_date\": \"2025-10-05\", \"expiry_date\": \"2025-10-25\"}",
                adjustmentId, feeId
        );

        given()
                .contentType("application/json")
                .body(updateBody)
                .when()
                .put("/api/v1/adjustments/" + adjustmentId)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.adjustment_amount", is(15000))
                .body("data.adjustment_type", is("increase"))
                .body("data.reason", is("Updated reason"));
    }

    @Test
    void testDeleteAdjustment() {
        Long feeCategoryId = createFeeCategoryAndFindId(1L, "AdjCat-Delete");
        Long feeId = createFeeAndFindId(1L, feeCategoryId, "AdjFee-Delete");
        Long adjustmentId = createAdjustmentAndFindId(feeId, "Test delete");

        given()
                .when()
                .delete("/api/v1/adjustments/" + adjustmentId)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    void testGetApartmentSpecificAdjustments() {
        given()
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get("/api/v1/adjustments/apartment_specific_adjustments")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.page", is(1));
    }
}
