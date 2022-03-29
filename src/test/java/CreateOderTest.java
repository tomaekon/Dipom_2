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
        try {
            if (refreshToken.isEmpty()) userClient.logout(userLogout);
        } catch (NullPointerException e) {
            System.out.println("Passed with Exception");
        }
    }

    //Проверка создания заказа пользователем без авторизации
    @Test
    public void createOderWithoutAuthorizationTest() {
        oderClient = new OderClient();
        oder = new Oder(ingredients);
        response = oderClient.createOderWithoutAuthorization(oder);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    //Проверка создания заказа авторизированным пользователем
    @Test
    public void createOderWithAuthorizationTest() {
        oder = new Oder(ingredients);
        oderClient = new OderClient();
        response = oderClient.createOderWithAuthorization(accessToken, oder);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    //Проверка создания заказа с неверным хэшем ингредиента
    @Test
    public void createOderWithIncorrectIngredient() {

        ingredients.add("Test");
        oder = new Oder(ingredients);
        oderClient = new OderClient();
        response = oderClient.createOderWithAuthorization(accessToken, oder);
        response.then().assertThat().statusCode(500);
    }

    //Проверка создания заказа без ингредиентов
    @Test
    public void createOderWithNullIngredient() {

        ingredients.clear();
        oder = new Oder(ingredients);
        oderClient = new OderClient();
        response = oderClient.createOderWithAuthorization(accessToken, oder);
        response.then().assertThat().statusCode(400);
    }
}
