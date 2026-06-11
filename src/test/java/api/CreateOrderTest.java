package api;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Создание заказа")
public class CreateOrderTest extends BaseTest {

    private User user;
    private String accessToken;
    private UserClient userClient;

    private List<String> validIngredients = Arrays.asList("61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72");

    @Before
    public void setUpData() {
        userClient = new UserClient();
        user = new User("orderuser_" + System.currentTimeMillis() + "@yandex.ru", "password123", "OrderUser");
        Response registerResponse = userClient.register(user);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией - проверка статуса и order")
    public void createOrderWithAuthShouldReturnSuccessTest() {
        Order order = new Order(validIngredients);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
        response.then()
                .statusCode(SC_OK)
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией - проверка поля success")
    public void createOrderWithAuthShouldReturnSuccessTrueTest() {
        Order order = new Order(validIngredients);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        Order order = new Order(validIngredients);
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/orders");
        response.then().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов - проверка статуса и success")
    public void createOrderWithoutIngredientsShouldReturnErrorTest() {
        Order order = new Order(null);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов - проверка сообщения")
    public void createOrderWithoutIngredientsShouldReturnErrorMessageTest() {
        Order order = new Order(null);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidHashTest() {
        List<String> invalidIngredients = Arrays.asList("invalid_hash_1", "invalid_hash_2");
        Order order = new Order(invalidIngredients);
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
        response.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}