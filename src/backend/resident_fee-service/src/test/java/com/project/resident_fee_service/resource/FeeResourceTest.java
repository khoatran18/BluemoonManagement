package com.project.resident_fee_service.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.*;


@QuarkusTest
public class FeeResourceTest {

    /**
     * Helper: Create fee category to satisfy fee FK and return its id.
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
     * Helper: Create fee and fetch its id via filter by fee_name.
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
    void testCreateFee() {
	Long feeCategoryId = createFeeCategoryAndFindId(1L, "FeeCat-Create");

	given()
		.contentType("application/json")
		.body(String.format(
			"{\"fee_type_id\": 1, \"fee_category_id\": %d, \"fee_name\": \"Electricity Oct\", \"fee_description\": \"Create test fee\", \"fee_amount\": 123456.78, \"applicable_month\": \"2025-10\", \"effective_date\": \"2025-10-01\", \"expiry_date\": \"2025-10-31\", \"status\": \"ACTIVE\"}",
			feeCategoryId
		))
		.when()
		.post("/api/v1/fees")
		.then()
		.statusCode(201)
		.body("success", is(true))
		.body("code", is(201));
    }

    @Test
    void testGetAllFees() {
	Long feeCategoryId = createFeeCategoryAndFindId(1L, "FeeCat-List");
	createFeeAndFindId(1L, feeCategoryId, "Fee-List");

	given()
		.queryParam("page", 1)
		.queryParam("limit", 10)
		.when()
		.get("/api/v1/fees")
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.fees", is(not(empty())))
		.body("data.total_items", greaterThanOrEqualTo(1))
		.body("data.page", is(1));
    }

    @Test
    void testGetFeeById() {
	Long feeCategoryId = createFeeCategoryAndFindId(1L, "FeeCat-Detail");
	Long feeId = createFeeAndFindId(1L, feeCategoryId, "Fee-Detail");

	given()
		.when()
		.get("/api/v1/fees/" + feeId)
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.fee_id", is(feeId.intValue()));
    }

    @Test
    void testUpdateFee() {
	Long feeCategoryId = createFeeCategoryAndFindId(1L, "FeeCat-Update");
	Long feeId = createFeeAndFindId(1L, feeCategoryId, "Fee-Update");

	String updateBody = String.format(
		"{\"fee_id\": %d, \"fee_type_id\": 1, \"fee_category_id\": %d, \"fee_name\": \"Updated Fee\", \"fee_description\": \"Updated desc\", \"fee_amount\": 98765.43, \"applicable_month\": \"2025-11\", \"effective_date\": \"2025-11-01\", \"expiry_date\": \"2025-11-30\", \"status\": \"CLOSED\"}",
		feeId, feeCategoryId
	);

	given()
		.contentType("application/json")
		.body(updateBody)
		.when()
		.put("/api/v1/fees/" + feeId)
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.fee_name", is("Updated Fee"))
		.body("data.fee_amount", is(98765.43f))
		.body("data.status", is("CLOSED"));
    }

    @Test
    void testDeleteFee() {
	Long feeCategoryId = createFeeCategoryAndFindId(1L, "FeeCat-Delete");
	Long feeId = createFeeAndFindId(1L, feeCategoryId, "Fee-Delete");

	given()
		.when()
		.delete("/api/v1/fees/" + feeId)
		.then()
		.statusCode(200)
		.body("success", is(true));
    }

}
