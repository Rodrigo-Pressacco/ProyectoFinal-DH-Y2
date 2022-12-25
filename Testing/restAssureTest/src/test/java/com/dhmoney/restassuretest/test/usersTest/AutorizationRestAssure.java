package com.dhmoney.restassuretest.test.usersTest;

import com.dhmoney.restassuretest.model.LoginDto;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;

public class AutorizationRestAssure {

    static LoginDto loginDto;
    static LoginDto loginDto2;
    private static String token;

    @BeforeAll
    static void beforeAll(){
        loginDto = new LoginDto("digitalbooking.g5@gmail.com", "Pas123");
        loginDto2 = new LoginDto("emailNoValidado@gmail.com", "Pas123");
    }

    @Test
    @Tag("regresion")
    @Order(1)
    @DisplayName("[POST] Hacer login correctamente")
    public void loginHappyPath(){
        JSONObject obj = new JSONObject();
        obj.put("email", loginDto.getEmail());
        obj.put("password", loginDto.getPassword());

        Response response = given()
                .header("Content-Type", "application/json")
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/login")
                .then()
                .statusCode(200)
                .log().all().extract().response();
        JsonPath jsnPath = response.jsonPath();
        token = jsnPath.get("accessToken");
    }

    @Test
    @Tag("regresion")
    @Order(2)
    @DisplayName("[POST] Hacer login Con mail invalidado")
    public void loginEmailNotValidated(){
        JSONObject obj = new JSONObject();
        obj.put("email", loginDto2.getEmail());
        obj.put("password", loginDto2.getPassword());

        given()
                .header("Content-Type", "application/json")
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/login")
                .then()
                .statusCode(400)
                .log().all();

    }

    @Test
    @Tag("regresion")
    @Order(3)
    @DisplayName("[POST] Logout correctamente")
    public void logoutHappyPath(){
        JSONObject obj = new JSONObject();
        System.out.println(token);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+token)
                .when()
                .post("http://localhost:8081/logout")
                .then()
                .statusCode(401)
                .log().all();

    }

    @AfterAll
    static void afterAll() {
    }
}
