package com.dhmoney.restassuretest.test.transactionTest;

import com.dhmoney.restassuretest.model.CardDto;
import com.dhmoney.restassuretest.model.LoginDto;
import com.dhmoney.restassuretest.model.TransactionDTO;
import com.dhmoney.restassuretest.model.UserRegistrationDTO;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionTest {

    private static String idUser1 = "1";
    private static String idUser2 = "2";
    private static Integer idAccount1;
    private static Integer idAccount2;
    private static String idActivity1;
    private static String idActivity2;
    private static String cvuAccount1;
    private static String cvuAccount2;
    private static UserRegistrationDTO userRegDTO;
    private static UserRegistrationDTO userRegDTO2;
    private static TransactionDTO createDepositDTO;
    private static TransactionDTO createDTO2;
    static LoginDto loginDto;
    static LoginDto loginDto2;
    private static String token1;
    private static String token2;
    private static CardDto cardDto;
    private static CardDto cardDto2;
    private static String cardDto1Id;
    private static String cardDto2Id;

    @BeforeAll
    static void beforeAll(){


        loginDto = new LoginDto("digitalbooking.g5@gmail.com", "Pas123");
        loginDto2 = new LoginDto("digitalbooking.g6@gmail.com", "Pas123");

        userRegDTO = new UserRegistrationDTO("digitalbooking.g150@gmail.com", "test",
                "test", 354987624L, "45635142", "Pas123");
        userRegDTO2 = new UserRegistrationDTO("digitalbooking.g300@gmail.com", "test",
                "test", 354987624L, "45635142", "Pas123");
        createDepositDTO = new TransactionDTO(10000.0, null, "deposit",
                "Deposit", null, null, null);
        createDTO2 = new TransactionDTO(10.0,null, null,
                "Transfer",  null, null, null);
        cardDto = new CardDto(null, "11234567891210", "Digital Booking", "12/24", "123");
        cardDto2 = new CardDto(null, "11234567891211", "Digital Booking", "12/24", "123");


        JSONObject objLogin1 = new JSONObject();
        objLogin1.put("email", loginDto.getEmail());
        objLogin1.put("password", loginDto.getPassword());

        Response responseLogin1 = given()
                .header("Content-Type", "application/json")
                .body(objLogin1.toJSONString())
                .when()
                .post("http://localhost:8081/login")
                .then()
                .statusCode(200)
                .log().all().extract().response();
        JsonPath jsnPath1 = responseLogin1.jsonPath();
        token1 = jsnPath1.get("accessToken");

        JSONObject objLogin2 = new JSONObject();
        objLogin2.put("email", loginDto2.getEmail());
        objLogin2.put("password", loginDto2.getPassword());

        Response responseLogin2 = given()
                .header("Content-Type", "application/json")
                .body(objLogin2.toJSONString())
                .when()
                .post("http://localhost:8081/login")
                .then()
                .statusCode(200)
                .log().all().extract().response();
        JsonPath jsnPath2 = responseLogin2.jsonPath();
        token2 = jsnPath2.get("accessToken");


    }


    @Test
    @Tag("smoke")
    @Order(1)
    @DisplayName("[GET] Traer account por User ID")
    public void getAccountByUserID(){
        Response response1 = given()
                .header("Authorization", "Bearer "+token1)
                .accept(ContentType.JSON)
                .pathParam("id",idUser1)
                .when()
                .get("http://localhost:8081/accounts/{id}")
                .then()
                .statusCode(200).log().all()
                .extract().response();
        JsonPath jsnPath1 = response1.jsonPath();
        idAccount1 = (Integer) jsnPath1.get("id");
        cvuAccount1 = (String) jsnPath1.get("cvu");

        Response response2 = given()
                .header("Authorization", "Bearer "+token2)
                .accept(ContentType.JSON)
                .pathParam("id",idUser2)
                .when()
                .get("http://localhost:8081/accounts/{id}")
                .then()
                .statusCode(200).log().all()
                .extract().response();
        JsonPath jsnPath2 = response2.jsonPath();
        idAccount2 = (Integer) jsnPath2.get("id");
        cvuAccount2 = (String) jsnPath2.get("cvu");

        JSONObject objCard1 = new JSONObject();
        objCard1.put("number", cardDto.getNumber());
        objCard1.put("name", cardDto.getName());
        objCard1.put("expiration", cardDto.getExpiration());
        objCard1.put("cvc", cardDto.getCvc());

        cardDto1Id = given()
                .header("Authorization", "Bearer "+token1)
                .header("Content-Type", "application/json")
                .pathParam("id", idAccount1)
                .body(objCard1.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(201)
                .log().all()
                .extract().path("id").toString();

        JSONObject objCard2 = new JSONObject();
        objCard2.put("number", cardDto2.getNumber());
        objCard2.put("name", cardDto2.getName());
        objCard2.put("expiration", cardDto2.getExpiration());
        objCard2.put("cvc", cardDto2.getCvc());

        cardDto2Id = given()
                .header("Authorization", "Bearer "+token2)
                .header("Content-Type", "application/json")
                .pathParam("id", idAccount2)
                .body(objCard2.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/cards")
                .then()
                .statusCode(201)
                .log().all()
                .extract().path("id").toString();
    }

    @Test
    @Tag("smoke")
    @Order(2)
    @DisplayName("[POST] Create deposit 1")
    public void createDeposit1(){
        JSONObject obj = new JSONObject();
        obj.put("amount", createDepositDTO.getAmount());
        obj.put("dated", createDepositDTO.getDated());
        obj.put("description", "deposit");
        obj.put("type", "Deposit");
        obj.put("origin", cardDto1Id);

        idActivity1 = given()
                .header("Authorization", "Bearer "+token1)
                .header("Content-Type", "application/json")
                .pathParam("id", idAccount1)
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/transferences")
                .then()
                .statusCode(201)
                .log().all()
                .extract().path("id").toString();

    }

    @Test
    @Tag("smoke")
    @Order(3)
    @DisplayName("[POST] Create deposit 2")
    public void createDeposit2() {
        JSONObject obj2 = new JSONObject();
        obj2.put("amount", createDepositDTO.getAmount());
        obj2.put("dated", createDepositDTO.getDated());
        obj2.put("description", "deposit");
        obj2.put("type", "Deposit");
        obj2.put("origin", cardDto2Id);

        idActivity2 =  given()
                .header("Authorization", "Bearer "+token2)
                .header("Content-Type", "application/json")
                .pathParam("id", (idAccount2))
                .body(obj2.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/transferences")
                .then()
                .statusCode(201)
                .log().all()
                .extract().path("id").toString();

    }

    @Test
    @Tag("smoke")
    @Order(4)
    @DisplayName("[POST] Create transference")
    public void createTransference(){
        JSONObject obj = new JSONObject();
        obj.put("amount", createDTO2.getAmount());
        obj.put("dated", createDTO2.getDated());
        obj.put("description", createDTO2.getDescription());
        obj.put("type", createDTO2.getType());
        obj.put("account_id", idAccount1);
        obj.put("origin", cvuAccount1);
        obj.put("destination", cvuAccount2);


        given()
                .header("Authorization", "Bearer "+token1)
                .header("Content-Type", "application/json")
                .pathParam("id", (idAccount1))
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/transferences")
                .then()
                .statusCode(201)
                .log().all();

    }



    @Test
    @Tag("smoke")
    @Order(5)
    @DisplayName("[GET] transaction by account ID")
    public void getTransactionByAcountID(){
        given()
                .header("Authorization", "Bearer "+token1)
                .accept(ContentType.JSON)
                .pathParam("idAcount",idAccount1)
                .pathParam("idActivity",idActivity1)
                .when()
                .get("http://localhost:8081/accounts/{idAcount}/activities/{idActivity}")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("smoke")
    @Order(5)
    @DisplayName("[GET] Filter amount min")
    public void getTransactionByAcountIDUsingFilterAmount1(){
        given()
                .header("Authorization", "Bearer "+token1)
                .accept(ContentType.JSON)
                .pathParam("idAcount",idAccount1)
                .formParam("monto_min", 1)
                .when()
                .get("http://localhost:8081/accounts/{idAcount}/activities")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("smoke")
    @Order(6)
    @DisplayName("[GET] Filter amount max")
    public void getTransactionByAcountIDUsingFilterAmount2(){
        given()
                .header("Authorization", "Bearer "+token1)
                .accept(ContentType.JSON)
                .pathParam("idAcount",idAccount1)
                .formParam("monto_max", 200000)
                .when()
                .get("http://localhost:8081/accounts/{idAcount}/activities")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("smoke")
    @Order(7)
    @DisplayName("[GET] Filter amount both")
    public void getTransactionByAcountIDUsingFilterAmount3(){
        given()
                .header("Authorization", "Bearer "+token1)
                .accept(ContentType.JSON)
                .pathParam("idAcount",idAccount1)
                .formParam("monto_min", 1)
                .formParam("monto_max", 200000)
                .when()
                .get("http://localhost:8081/accounts/{idAcount}/activities")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("smoke")
    @Order(8)
    @DisplayName("[GET] Filter type")
    public void getTransactionByAcountIDUsingFilterType(){
        given()
                .header("Authorization", "Bearer "+token1)
                .accept(ContentType.JSON)
                .pathParam("idAcount",idAccount1)
                .formParam("type", "Deposit")
                .when()
                .get("http://localhost:8081/accounts/{idAcount}/activities")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("smoke")
    @Order(9)
    @DisplayName("[GET] Filter period min")
    public void getTransactionByAcountIDUsingFilterPeriod1(){
        given()
                .header("Authorization", "Bearer "+token1)
                .accept(ContentType.JSON)
                .pathParam("idAcount",idAccount1)
                .formParam("period_min", 0)
                .when()
                .get("http://localhost:8081/accounts/{idAcount}/activities")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("smoke")
    @Order(9)
    @DisplayName("[GET] Filter period max")
    public void getTransactionByAcountIDUsingFilterPeriod2(){
        given()
                .header("Authorization", "Bearer "+token1)
                .accept(ContentType.JSON)
                .pathParam("idAcount",idAccount1)
                .formParam("period_max", 20)
                .when()
                .get("http://localhost:8081/accounts/{idAcount}/activities")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("smoke")
    @Order(10)
    @DisplayName("[POST] Create deposit but account not exist")
    public void createDepositAccountNotExist(){
        JSONObject obj = new JSONObject();
        obj.put("amount", createDepositDTO.getAmount());
        obj.put("dated", createDepositDTO.getDated());
        obj.put("description", createDepositDTO.getDescription());
        obj.put("type", createDepositDTO.getType());
        obj.put("origin", idAccount1);

        given()
                .header("Authorization", "Bearer "+token1)
                .header("Content-Type", "application/json")
                .pathParam("id", 100)
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/deposits")
                .then()
                .statusCode(403)
                .log().all();

    }

    @Test
    @Tag("smoke")
    @Order(11)
    @DisplayName("[POST] Create transference but account not exist")
    public void createTransferenceAccountNotExist(){
        JSONObject obj = new JSONObject();
        obj.put("amount", createDTO2.getAmount());
        obj.put("dated", createDTO2.getDated());
        obj.put("description", createDTO2.getDescription());
        obj.put("type", createDTO2.getType());
        obj.put("account_id", idAccount1);
        obj.put("origin", cvuAccount1);
        obj.put("destination", cvuAccount2);


        given()
                .header("Authorization", "Bearer "+token1)
                .header("Content-Type", "application/json")
                .pathParam("id", 100)
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/accounts/{id}/transferences")
                .then()
                .statusCode(403)
                .log().all();

    }



    @AfterAll
    static void afterAll() {
    }


}
