import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;


public class CreateOderTest {
    public User user;
    public UserClient userClient;
    public OderClient oderClient;
    public Oder oder;
    public Ingredient allIngredient;
    public IngredientClient ingredientClient;
    private String refreshToken;
    private String accessToken;
    public UserLogout userLogout;
    public List<String> ingredients;
    Response response;

    @Before
    public void setUp() {
        //Получение доступных ингредиентов и заполнение списка
        ingredientClient = new IngredientClient();
        allIngredient = ingredientClient.getIngredient();
        ingredients = new ArrayList<>();
        ingredients.add(allIngredient.data.get(0).get_id());
        ingredients.add(allIngredient.data.get(1).get_id());
        ingredients.add(allIngredient.data.get(2).get_id());
        //Создание и авторизация пользователя
        user = User.getRandom();
        userClient = new UserClient();
        userClient.create(user);
        Response responseForToken = userClient.login(user);
        responseForToken.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        refreshToken = responseForToken.then().extract().path("refreshToken");
        accessToken = responseForToken.then().extract().path("accessToken");
    }

    @After
    public void tearDown() {
        boolean refreshNotNullAndIsEmpty = (refreshToken != null) && (refreshToken.isEmpty());
        if (refreshNotNullAndIsEmpty) userClient.logout(userLogout);
    }

    @Test
    @DisplayName("Проверка создания заказа пользователем без авторизации")
    public void createOderWithoutAuthorizationTest() {
        oderClient = new OderClient();
        oder = new Oder(ingredients);
        response = oderClient.createOderWithoutAuthorization(oder);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    @Test
    @DisplayName("Проверка создания заказа авторизированным пользователем")
    public void createOderWithAuthorizationTest() {
        oder = new Oder(ingredients);
        oderClient = new OderClient();
        response = oderClient.createOderWithAuthorization(accessToken, oder);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    @Test
    @DisplayName("Проверка создания заказа с неверным хэшем ингредиента")
    public void createOderWithIncorrectIngredient() {

        ingredients.add("Test");
        oder = new Oder(ingredients);
        oderClient = new OderClient();
        response = oderClient.createOderWithAuthorization(accessToken, oder);
        response.then().assertThat().statusCode(500);
    }

    @Test
    @DisplayName("Проверка создания заказа без ингредиентов")
    public void createOderWithNullIngredient() {

        ingredients.clear();
        oder = new Oder(ingredients);
        oderClient = new OderClient();
        response = oderClient.createOderWithAuthorization(accessToken, oder);
        response.then().assertThat().statusCode(400);
    }
}
