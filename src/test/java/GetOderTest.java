import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class GetOderTest {
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

    @Before
    public void setUp() {
        ingredientClient = new IngredientClient();
        allIngredient = ingredientClient.getIngredient();
        ingredients = new ArrayList<>();
        ingredients.add(allIngredient.data.get(0).get_id());
        ingredients.add(allIngredient.data.get(1).get_id());
        ingredients.add(allIngredient.data.get(2).get_id());

        user = User.getRandom();
        userClient = new UserClient();
        userClient.create(user);
        Response responseForToken = userClient.login(user);
        refreshToken = responseForToken.then().extract().path("refreshToken");
        accessToken = responseForToken.then().extract().path("accessToken");
        oder = new Oder(ingredients);
        oderClient = new OderClient();
        oderClient.createOderWithAuthorization(accessToken, oder);
    }

    @After
    public void tearDown() {
        try {
            if (refreshToken.isEmpty()) userClient.logout(userLogout);
        } catch (NullPointerException e) {
            System.out.println("Passed with Exception");
        }
    }

    //Проверка получение заказа для неаввторизированного пользователя
    @Test
    public void getOderForUserWithoutAuthorizationTest() {

        Response response = oderClient.getOderUserWithoutAuthorization();
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(401);
    }

    //Проверка получение заказа для аввторизированного пользователя
    @Test
    public void getOderForUserWithAuthorizationTest() {
        Response response = oderClient.getOderUserWithAuthorization(accessToken);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }
}
