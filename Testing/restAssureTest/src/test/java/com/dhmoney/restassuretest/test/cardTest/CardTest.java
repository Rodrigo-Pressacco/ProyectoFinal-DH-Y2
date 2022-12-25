package com.dhmoney.restassuretest.test.cardTest;

import com.dhmoney.restassuretest.model.CardDto;
import com.dhmoney.restassuretest.model.LoginDto;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CardTest {

    private static String idUser = "1";
    private static String idAccount2 = "0";
    private static String idCard;
    static LoginDto loginDto;
    static CardDto cardDto;
    private static String token;

    @BeforeAll
    static void beforeAll(){
        loginDto = new LoginDto("digitalbooking.g5@gmail.com", "Pas123");
        cardDto = new CardDto(null, "11234567891212", "Digital Booking", "12/24", "123");


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
        token = (String) jsnPath.get("accessToken");

    }

    @Test
    @Tag("regresion")
    @Order(1)
    @DisplayName("[GET] Traer account por User ID")
    public void getAccountByUserID(){
        idAccount2 = given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id",idUser)
                .when()
                .get("http://localhost:8081/accounts/{id}")
                .then()
                .statusCode(200).log().all()
                .extract().path("id").toString();
    }

    @Test
    @Tag("regresion")
    @Order(2)
    @DisplayName("[POST] Create Card")
    public void createCard(){
        JSONObject obj = new JSONObject();
        obj.put("number", cardDto.getNumber());
        obj.put("name", cardDto.getName());
        obj.put("expiration", cardDto.getExpiration());
        obj.put("cvc", cardDto.getCvc());

        idCard = given()
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .pathParam("id", Long.parseLong(idAccount2))
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(201)
                .log().all()
                .extract().path("id").toString();

    }

    @Test
    @Tag("regresion")
    @Order(3)
    @DisplayName("[GET] Traer card by account ID")
    public void getCardByAccountrID(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id",idAccount2)
                .when()
                .get("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(4)
    @DisplayName("[POST] validate creat card same number")
    public void createDepositWithSameNumber(){
        JSONObject obj = new JSONObject();
        obj.put("number", cardDto.getNumber());
        obj.put("name", cardDto.getName());
        obj.put("expiration", cardDto.getExpiration());
        obj.put("cvc", cardDto.getCvc());

        given()
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .pathParam("id", Long.parseLong(idAccount2))
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(409)
                .log().all();

    }



    @Test
    @Tag("regresion")
    @Order(5)
    @DisplayName("[GET] Traer card by accountId and Card ID")
    public void getCardByAccountIdAndCArdId(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id",idAccount2)
                .pathParam("idCard",idCard)
                .when()
                .get("http://localhost:8081/accounts/{id}/cards/{idCard}")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(6)
    @DisplayName("[DELETE] Delete Card")
    public void deleteCard(){

        given()
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .pathParam("id", idAccount2)
                .pathParam("idCard", idCard)
                .when()
                .delete("http://localhost:8081/accounts/{id}/cards/{idCard}")
                .then()
                .statusCode(403)
                .log().all();

    }

    @Test
    @Tag("regresion")
    @Order(7)
    @DisplayName("[POST] validate creat card same cvc")
    public void createDepositWithOutCvc(){
        JSONObject obj = new JSONObject();
        obj.put("number", "11234567891295");
        obj.put("name", cardDto.getName());
        obj.put("expiration", cardDto.getExpiration());

        given()
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .pathParam("id", Long.parseLong(idAccount2))
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(400)
                .log().all();

    }

    @Test
    @Tag("regresion")
    @Order(8)
    @DisplayName("[POST] validate creat card same Name")
    public void createDepositWithOutName(){
        JSONObject obj = new JSONObject();
        obj.put("number", "11234567891295");
        obj.put("expiration", cardDto.getExpiration());
        obj.put("cvc", cardDto.getCvc());

        given()
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .pathParam("id", Long.parseLong(idAccount2))
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(400)
                .log().all();

    }

    @Test
    @Tag("regresion")
    @Order(9)
    @DisplayName("[POST] validate creat card same ExpirationDate")
    public void createDepositWithOutExpirationDate(){
        JSONObject obj = new JSONObject();
        obj.put("number", "11234567891295");
        obj.put("name", cardDto.getName());
        obj.put("cvc", cardDto.getCvc());

        given()
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .pathParam("id", Long.parseLong(idAccount2))
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(400)
                .log().all();

    }

    @Test
    @Tag("regresion")
    @Order(3)
    @DisplayName("[GET] Traer card by account ID")
    public void getCardByAccountrIDNotExist(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id",100)
                .when()
                .get("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(403).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(3)
    @DisplayName("[GET] Traer card by account ID")
    public void getCardByAccountrIDAndCardNotExist(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id",idAccount2)
                .pathParam("idCard",100)
                .when()
                .get("http://localhost:8081/accounts/{id}/cards/{idCard}")
                .then()
                .statusCode(403).log().all();
    }


}
