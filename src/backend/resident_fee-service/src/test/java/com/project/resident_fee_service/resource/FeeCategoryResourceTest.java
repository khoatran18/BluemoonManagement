package com.project.resident_fee_service.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class FeeCategoryResourceTest {

    /**
     * Helper: Create a fee category (needs existing fee_type_id) and fetch its generated id via list API.
     */
    private Long createFeeCategoryAndFindId(Long feeTypeId, String name, String description) {
	String body = String.format(
		"{\"fee_type_id\": %d, \"name\": \"%s\", \"description\": \"%s\"}",
		feeTypeId, name, description
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

    @Test
    void testCreateFeeCategory() {
	String name = "Electricity-Test";
	String description = "Category create test";

	given()
		.contentType("application/json")
		.body(String.format(
			"{\"fee_type_id\": 1, \"name\": \"%s\", \"description\": \"%s\"}",
			name, description
		))
		.when()
		.post("/api/v1/fee-categories")
		.then()
		.statusCode(201)
		.body("success", is(true))
		.body("code", is(201));
    }

    @Test
    void testGetAllFeeCategories() {
	createFeeCategoryAndFindId(1L, "List-Category", "List test");

	given()
		.queryParam("page", 1)
		.queryParam("limit", 20)
		.when()
		.get("/api/v1/fee-categories")
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.'fee-categories'", is(not(empty())))
		.body("data.total_items", greaterThanOrEqualTo(1))
		.body("data.page", is(1));
    }

    @Test
    void testGetFeeCategoryById() {
	Long id = createFeeCategoryAndFindId(1L, "Detail-Category", "Detail test");

	given()
		.when()
		.get("/api/v1/fee-categories/" + id)
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.fee_category_id", is(id.intValue()))
		.body("data.name", is("Detail-Category"));
    }

    @Test
    void testUpdateFeeCategory() {
	Long id = createFeeCategoryAndFindId(1L, "Update-Category", "Before update");

	String updateBody = String.format(
		"{\"fee_category_id\": %d, \"fee_type_id\": 1, \"name\": \"Updated Category\", \"description\": \"After update\"}",
		id
	);

	given()
		.contentType("application/json")
		.body(updateBody)
		.when()
		.put("/api/v1/fee-categories/" + id)
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.name", is("Updated Category"))
		.body("data.description", is("After update"));
    }

    @Test
    void testDeleteFeeCategory() {
	Long id = createFeeCategoryAndFindId(1L, "Delete-Category", "Delete test");

	given()
		.when()
		.delete("/api/v1/fee-categories/" + id)
		.then()
		.statusCode(200)
		.body("success", is(true));
    }
}
