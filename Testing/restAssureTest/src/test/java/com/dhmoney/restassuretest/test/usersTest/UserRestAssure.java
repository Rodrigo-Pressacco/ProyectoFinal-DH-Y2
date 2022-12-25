package com.dhmoney.restassuretest.test.usersTest;

import com.dhmoney.restassuretest.model.LoginDto;
import com.dhmoney.restassuretest.model.UserRegistrationDTO;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRestAssure {

    private static String idUser;
    private static UserRegistrationDTO userRegDTO;
    private static UserRegistrationDTO userRegDTOBadPassword;
    private static UserRegistrationDTO userRegDTOBadEmail;
    private static String token;
    static LoginDto loginDto;

    private static String idUser2="1";

    @BeforeAll
    static void beforeAll(){

        loginDto = new LoginDto("digitalbooking.g5@gmail.com", "Pas123");
        userRegDTO = new UserRegistrationDTO("digitalbooking.g101@gmail.com", "test",
                "test", 354987624L, "45635142", "Pas123");
        userRegDTOBadPassword = new UserRegistrationDTO("digitalbooking.g200@gmail.com", "test",
                "test", 354987624L, "45635142", "test");
        userRegDTOBadEmail = new UserRegistrationDTO("testgmail.com", "test",
                "test", 354987624L, "45635142", "Pas123");


    }

    @Test
    @Tag("regresion")
    @Order(1)
    @DisplayName("[POST] Crear usuario correctamente")
    public void createUserHappyPath(){
        JSONObject obj = new JSONObject();
        obj.put("email", userRegDTO.getEmail());
        obj.put("firstName", userRegDTO.getFirstName());
        obj.put("lastName", userRegDTO.getLastName());
        obj.put("dni", userRegDTO.getDni());
        obj.put("phone", userRegDTO.getPhone());
        obj.put("password", userRegDTO.getPassword());

        idUser = given()
                .header("Content-Type", "application/json")
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/users/register")
                .then()
                .statusCode(201)
                .log().all()
                .extract().path("id").toString();

        JSONObject objLogin = new JSONObject();
        objLogin.put("email", loginDto.getEmail());
        objLogin.put("password", loginDto.getPassword());

        Response response = given()
                .header("Content-Type", "application/json")
                .body(objLogin.toJSONString())
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
    @DisplayName("[GET] Traer todos los usuarios")
    public void getAllUsers(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .when()
                .get("http://localhost:8081/users")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(3)
    @DisplayName("[GET] Traer usuario por ID")
    public void getUsersByID(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id",idUser)
                .when()
                .get("http://localhost:8081/users/{id}")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(4)
    @DisplayName("[PATCH] Modificar usuario correctamente")
    public void updateUserHappyPath(){
        JSONObject obj = new JSONObject();
        obj.put("firstName", "test");
        obj.put("lastName", "test");
        obj.put("email", "digitalbooking.g100@gmail.com");
        obj.put("password", "Pas1234");

        given()
                .header("Content-Type", "application/json")
                .pathParam("id",idUser)
                .body(obj.toJSONString())
                .when()
                .patch("http://localhost:8081/users/{id}")
                .then()
                .statusCode(401)
                .log().all();

    }

    @Test
    @Tag("regresion")
    @Order(5)
    @DisplayName("[POST] Reset password")
    public void resetPassword(){
        JSONObject obj = new JSONObject();
        obj.put("email", userRegDTO.getEmail());

        given()
                .header("Content-Type", "application/json")
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/auth/reset-password")
                .then()
                .statusCode(404)
                .log().all();

    }

    @Test
    @Tag("regresion")
    @Order(6)
    @DisplayName("[GET] Traer account by user id")
    public void getAccountByID(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id",idUser2)
                .when()
                .get("http://localhost:8081/accounts/{id}")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(7)
    @DisplayName("[DELETE] Deletear usuario")
    public void deleteUser(){
        given()
                .accept(ContentType.JSON)
                .pathParam("id",idUser)
                .when()
                .delete("http://localhost:8081/users/{id}")
                .then()
                .statusCode(401).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(10)
    @DisplayName("[DELETE] Deletear usuario No Existente")
    public void deleteUserNotExistent(){
        given()
                .accept(ContentType.JSON)
                .pathParam("id","100")
                .when()
                .delete("http://localhost:8081/users/{id}")
                .then()
                .statusCode(404).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(11)
    @DisplayName("[GET] Traer usuario por ID que no existe")
    public void getUsersByIDButIDNotExist(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id","100")
                .when()
                .get("http://localhost:8081/users/{id}")
                .then()
                .statusCode(404).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(12)
    @DisplayName("[POST] Crear usuario con password erroneo")
    public void createUserWrongPassword(){
        JSONObject obj = new JSONObject();
        obj.put("email", userRegDTOBadPassword.getEmail());
        obj.put("firstName", userRegDTOBadPassword.getFirstName());
        obj.put("lastName", userRegDTOBadPassword.getLastName());
        obj.put("dni", userRegDTOBadPassword.getDni());
        obj.put("phone", userRegDTOBadPassword.getPhone());
        obj.put("password", userRegDTOBadPassword.getPassword());

        Response response1 = given()
                .header("Content-Type", "application/json")
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/users/register")
                .then()
                .statusCode(400)
                .log().all().extract().response();

    }

    @Test
    @Tag("regresion")
    @Order(13)
    @DisplayName("[POST] Crear usuario con mail erroneo")
    public void createUserWrongEmail(){
        JSONObject obj = new JSONObject();
        obj.put("email", userRegDTOBadEmail.getEmail());
        obj.put("firstName", userRegDTOBadEmail.getFirstName());
        obj.put("lastName", userRegDTOBadEmail.getLastName());
        obj.put("dni", userRegDTOBadEmail.getDni());
        obj.put("phone", userRegDTOBadEmail.getPhone());
        obj.put("password", userRegDTOBadEmail.getPassword());

        given()
                .header("Content-Type", "application/json")
                .body(obj.toJSONString())
                .when()
                .post("http://localhost:8081/users/register")
                .then()
                .statusCode(400)
                .log().all();

    }

    @Test
    @Tag("regresion")
    @Order(14)
    @DisplayName("[GET] Traer all account")
    public void getAllAccount(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .when()
                .get("http://localhost:8081/accounts")
                .then()
                .statusCode(200).log().all();
    }

    @Test
    @Tag("regresion")
    @Order(15)
    @DisplayName("[GET] Traer account by user id that not exist")
    public void getAccountByIDNotExist(){
        given()
                .header("Authorization", "Bearer "+token)
                .accept(ContentType.JSON)
                .pathParam("id",100)
                .when()
                .get("http://localhost:8081/accounts/{id}")
                .then()
                .statusCode(404).log().all();
    }

}
