package com.project.resident_fee_service.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class FeeTypeResourceTest {

	@Test
	void testGetAllFeeTypes() {
		given()
				.when()
				.get("/api/v1/fee-types")
				.then()
				.statusCode(200)
				.body("fee_types", is(not(empty())))
				.body("fee_types.id", hasItem(1))
				.body("fee_types.name", hasItem("OBLIGATORY"));
	}

	@Test
	void testGetFeeTypeById() {
		given()
				.when()
				.get("/api/v1/fee-types/1")
				.then()
				.statusCode(200)
				.body("id", is(1))
				.body("name", is("OBLIGATORY"));
	}
}
