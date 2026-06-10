package api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
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
import static org.hamcrest.Matchers.*;

@Feature("Создание заказа")
public class CreateOrderTest extends BaseTest {

    private User user;
    private String accessToken;
    private List<String> validIngredients = Arrays.asList("61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72");

    @Before
    @Step("Создание тестового пользователя и получение токена")
    public void setUpData() {
        user = new User("orderuser_" + System.currentTimeMillis() + "@yandex.ru", "password123", "OrderUser");
        Response registerResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @After
    @Step("Очистка тестовых данных: удаление пользователя")
    public void cleanUp() {
        if (accessToken != null) {
            String cleanToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
            given()
                    .header("Authorization", "Bearer " + cleanToken)
                    .when()
                    .delete("/api/auth/user");
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией - проверка статуса 200")
    @Description("Проверка статус кода при создании заказа с валидным токеном авторизации")
    public void createOrderWithAuthShouldReturn200Test() {
        Order order = new Order(validIngredients);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией - проверка поля success")
    @Description("Проверка что success=true при создании заказа с валидным токеном")
    public void createOrderWithAuthShouldReturnSuccessTrueTest() {
        Order order = new Order(validIngredients);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией - проверка наличия заказа в ответе")
    @Description("Проверка что поле order не null при создании заказа")
    public void createOrderWithAuthShouldReturnOrderNotNullTest() {
        Order order = new Order(validIngredients);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без токена авторизации")
    public void createOrderWithoutAuthTest() {
        Order order = new Order(validIngredients);
        given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/orders")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами - проверка статуса 200")
    public void createOrderWithIngredientsShouldReturn200Test() {
        Order order = new Order(validIngredients);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами - проверка поля success")
    public void createOrderWithIngredientsShouldReturnSuccessTrueTest() {
        Order order = new Order(validIngredients);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов - проверка статуса 400")
    public void createOrderWithoutIngredientsShouldReturn400Test() {
        Order order = new Order(null);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().statusCode(400);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов - проверка поля success")
    public void createOrderWithoutIngredientsShouldReturnSuccessFalseTest() {
        Order order = new Order(null);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов - проверка сообщения об ошибке")
    public void createOrderWithoutIngredientsShouldReturnErrorMessageTest() {
        Order order = new Order(null);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка ошибки при создании заказа с невалидными ингредиентами")
    public void createOrderWithInvalidHashTest() {
        List<String> invalidIngredients = Arrays.asList("invalid_hash_1", "invalid_hash_2");
        Order order = new Order(invalidIngredients);
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", authHeader)
                .body(order)
                .when()
                .post("/api/orders")
                .then().statusCode(500);
    }
}