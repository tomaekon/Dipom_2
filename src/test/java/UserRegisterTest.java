import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserRegisterTest {
    public UserClient userClient;
    public User user;
    private String refreshToken;
    public UserLogout userLogout;


    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        try {
            if (refreshToken.isEmpty()) userClient.logout(userLogout);
        } catch (NullPointerException e) {
            System.out.println("Passed with Exception");
        }
    }

    //Проверка создания уникального пользователя
    @Test
    public void testUserIsCreated() {

        user = User.getRandom();
        Response response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        refreshToken = response.then().extract().path("refreshToken");
    }

    //Проверка создания пользователя, который уже зарегистрирован
    @Test
    public void testUserIsAlreadyRegister() {

        user = User.getRandom();
        userClient.create(user);
        Response response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(403);
        refreshToken = response.then().extract().path("refreshToken");

    }

    //Проверка создания пользователя без заполнения поля email
    @Test
    public void testUserIsCreatedWithoutEmail() {

        user = User.getRandom();
        user.setEmail(null);
        userClient.create(user);
        Response response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(403);
        refreshToken = response.then().extract().path("refreshToken");

    }

    //Проверка создания пользователя без заполнения поля password
    @Test
    public void testUserIsCreatedWithoutPassword() {

        user = User.getRandom();
        user.setPassword(null);
        userClient.create(user);
        Response response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(403);
        refreshToken = response.then().extract().path("refreshToken");

    }

    //Проверка создания пользователя без заполнения поля name
    @Test
    public void testUserIsCreatedWithoutName() {

        user = User.getRandom();
        user.setName(null);
        userClient.create(user);
        Response response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(403);
        refreshToken = response.then().extract().path("refreshToken");

    }
}
