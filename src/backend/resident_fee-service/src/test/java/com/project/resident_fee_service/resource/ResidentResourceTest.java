package com.project.resident_fee_service.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ResidentResourceTest {

    /**
     * Helper: Create apartment first, because resident requires an existing apartment_id.
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
     * Helper: Create resident and read its id via list API (POST does not return id).
     */
    private Long createResidentAndFindId(Long apartmentId, String fullName, String phone, String email) {
	String body = String.format(
		"{\"full_name\": \"%s\", \"email\": \"%s\", \"phone_number\": \"%s\", \"apartment_id\": %d}",
		fullName, email, phone, apartmentId
	);

	given()
		.contentType("application/json")
		.body(body)
		.post("/api/v1/residents")
		.then()
		.statusCode(201);

	Response response = given()
		.queryParam("full_name", fullName)
		.get("/api/v1/residents")
		.then()
		.statusCode(200)
		.extract().response();

	return response.jsonPath().getLong("data.residents.find { it.full_name == '" + fullName + "' }.resident_id");
    }

    @Test
    void testCreateResident() {
	Long apartmentId = createApartment("R-CREATE", "101-C");
	given()
		.contentType("application/json")
		.body(String.format(
			"{\"full_name\": \"Resident Create\", \"email\": \"create@example.com\", \"phone_number\": \"0900000000\", \"apartment_id\": %d}",
			apartmentId
		))
		.when()
		.post("/api/v1/residents")
		.then()
		.statusCode(201)
		.body("success", is(true))
		.body("code", is(201));
    }

    @Test
    void testGetAllResidents() {
	Long apartmentId = createApartment("R-LIST", "202-L");
	createResidentAndFindId(apartmentId, "List Resident", "0911111111", "list@example.com");

	given()
		.queryParam("page", 1)
		.queryParam("limit", 10)
		.when()
		.get("/api/v1/residents")
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.residents", is(not(empty())))
		.body("data.total_items", greaterThanOrEqualTo(1))
		.body("data.page", is(1));
    }

    @Test
    void testGetResidentById() {
	Long apartmentId = createApartment("R-GET", "303-G");
	Long residentId = createResidentAndFindId(apartmentId, "Get Resident", "0922222222", "get@example.com");

	given()
		.when()
		.get("/api/v1/residents/" + residentId)
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.resident_id", is(residentId.intValue()));
    }

    @Test
    void testUpdateResident() {
	Long apartmentId = createApartment("R-UPD", "404-U");
	Long residentId = createResidentAndFindId(apartmentId, "Update Resident", "0933333333", "update@example.com");

	String updateBody = String.format(
		"{\"full_name\": \"Updated Name\", \"email\": \"updated@example.com\", \"phone_number\": \"0999999999\"}"
	);

	given()
		.contentType("application/json")
		.body(updateBody)
		.when()
		.put("/api/v1/residents/" + residentId)
		.then()
		.statusCode(200)
		.body("success", is(true))
		.body("data.full_name", is("Updated Name"))
		.body("data.email", is("updated@example.com"))
		.body("data.phone_number", is("0999999999"));
    }

    @Test
    void testDeleteResident() {
	Long apartmentId = createApartment("R-DEL", "505-D");
	Long residentId = createResidentAndFindId(apartmentId, "Delete Resident", "0944444444", "delete@example.com");

	given()
		.when()
		.delete("/api/v1/residents/" + residentId)
		.then()
		.statusCode(200)
		.body("success", is(true));
    }
}
