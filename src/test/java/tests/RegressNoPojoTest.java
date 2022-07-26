package tests;

import constants.Constants;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import specifications.Specifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class RegressNoPojoTest {

    @Test
    @DisplayName("Avatar contains ID of users")
    void testIsAvatarContainsUserIDNoPojo() {
        //Test data
        String emailDomain = "reqres.in";

        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpec200());

        //Get request
        Response response = given()
                .when()
                .get(Constants.getListOfUsersQuery())
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();

        //Put response to json and get emails, avatars ids
        JsonPath jsonPath = response.jsonPath();   //put response to json
        List<String> emails = jsonPath.get("data.email");
        List<String> avatars = jsonPath.get("data.avatar");
        List<Integer> ids = jsonPath.get("data.id");

        // Verify that avatar parameter contains user ID
        for (int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(ids.get(i).toString()));
        }
    }

    @Test
    @DisplayName("Email domain is reqres.in")
    void testEmailNoPojo() {
        //Test data
        String emailDomain = "reqres.in";

        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpec200());

        //Get request
        Response response = given()
                .when()
                .get(Constants.getListOfUsersQuery())
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.email", notNullValue())
                .extract().response();

        //Put response to json and get emails
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");

        // Verify that ALL users' email domains end with expected email domain
        Assertions.assertTrue(emails.stream().allMatch(x->x.endsWith(emailDomain)));
    }

    @Test
    @DisplayName("Successful registration")
    public void testIsRegistrationIsSuccessfulNoPojo(){
        //Test data
        int idExpected = 4;
        String tokenExpected = "QpwL5tke4Pnpja7X4";
        String emailValid = "eve.holt@reqres.in";
        String passwordValid = "pistol";

        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpec200());

        //Put email and password to HashMap
        Map<String, String> user = new HashMap<>();
        user.put("email", emailValid);
        user.put("password", passwordValid);

        //POST request
        Response response = given()
                .body(user)
                .when()
                .post(Constants.getSuccessfulRegQuery())
                .then().log().all()
                .body("id", equalTo(idExpected))
                .body("token", equalTo(tokenExpected))
                .extract().response();

        //Put data to json and get values
        JsonPath jsonPath = response.jsonPath();
        int idActual = jsonPath.get("id");
        String tokenActual = jsonPath.get("token");

        //Compare data
        Assertions.assertEquals(idExpected, idActual);
        Assertions.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    @DisplayName("Unsuccessful registration")
    public void testIsRegistrationIsUnSuccessfulNoPojo(){
        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpecError400());

        //Test data
        String emailValid = "sydney@fife";
        String passwordInValid = "";
        String errorMessageExpected= "Missing password";

        //Put email to HashMap
        Map<String, String> user = new HashMap<>();
        user.put("email", emailValid);

        //POST request
        Response response = given()
                .body(user)
                .when()
                .post(Constants.getUnsuccessfulRegQuery())
                .then().log().all()
//                .body("error", equalTo(errorMessageExpected))
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        String errorMessageActual = jsonPath.get("error");

        //Verify error Message
        Assertions.assertEquals(errorMessageExpected, errorMessageActual);
    }
}
