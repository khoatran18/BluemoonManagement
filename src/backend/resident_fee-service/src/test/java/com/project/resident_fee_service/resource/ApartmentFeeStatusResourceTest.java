package com.project.resident_fee_service.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ApartmentFeeStatusResourceTest {

    /**
     * Helper: Create apartment and return its id.
     */
    private Long createApartment(String building, String room) {
        given()
                .contentType("application/json")
                .body(String.format("{\"building\": \"%s\", \"room_number\": \"%s\"}", building, room))
                .post("/api/v1/apartments")
                .then()
                .statusCode(201);

        Response response = given()
                .queryParam("building", building)
                .queryParam("room_number", room)
                .get("/api/v1/apartments")
                .then()
                .statusCode(200)
                .extract().response();

        return response.jsonPath().getLong("data.items.find { it.room_number == '" + room + "' }.apartment_id");
    }

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
     * Helper: Create fee for apartment fee status testing.
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

    @Test
    void testGetApartmentFeeStatus() {
        Long apartmentId = createApartment("AFS-GET", "101-G");

        given()
                .when()
                .get("/api/v1/apartment-fee-statuses/" + apartmentId)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.apartment_id", is(apartmentId.intValue()))
                .body("data", hasKey("unpaid_fees"))
                .body("data", hasKey("adjustments"))
                .body("data", hasKey("total_paid"))
                .body("data", hasKey("balance"));
    }

    @Test
    void testUpdateApartmentFeeStatus() {
        Long apartmentId = createApartment("AFS-UPD", "202-U");
        Long feeCategoryId = createFeeCategoryAndFindId(1L, "AFSCat-Update");
        Long feeId = createFeeAndFindId(1L, feeCategoryId, "AFSFee-Update");

        String updateBody = String.format(
                "{\"total_paid\": 50000, \"balance\": 0, \"paid_fees\": [{\"fee_id\": %d}], \"unpaid_fees\": []}",
                feeId
        );

        given()
                .contentType("application/json")
                .body(updateBody)
                .when()
                .put("/api/v1/apartment-fee-statuses/" + apartmentId)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    void testGetApartmentFeeStatusAfterUpdate() {
        Long apartmentId = createApartment("AFS-CHK", "303-C");
        Long feeCategoryId = createFeeCategoryAndFindId(1L, "AFSCat-Check");
        Long feeId = createFeeAndFindId(1L, feeCategoryId, "AFSFee-Check");

        // First read initial status to ensure the record exists
        given()
                .get("/api/v1/apartment-fee-statuses/" + apartmentId)
                .then()
                .statusCode(200);

        // Update by marking a fee as paid with proper balance calculation
        String updateBody = String.format(
                "{\"total_paid\": %d, \"balance\": %d, \"paid_fees\": [{\"fee_id\": %d}], \"unpaid_fees\": []}",
                50000, 0, feeId
        );

        given()
                .contentType("application/json")
                .body(updateBody)
                .put("/api/v1/apartment-fee-statuses/" + apartmentId)
                .then()
                .statusCode(200);

        // Finally verify the status
        given()
                .when()
                .get("/api/v1/apartment-fee-statuses/" + apartmentId)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.apartment_id", is(apartmentId.intValue()))
                .body("data.total_paid", notNullValue())
                .body("data.balance", notNullValue());
    }
}
