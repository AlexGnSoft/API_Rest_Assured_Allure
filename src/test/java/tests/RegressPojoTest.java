package tests;

import api.colors.ColorsDataResponse;
import api.time.UserTimeRequest;
import api.time.UserTimeResponse;
import api.users.UserDataResponse;
import api.registration.RegistrationRequest;
import api.registration.SuccessRegResponse;
import api.registration.UnSuccessfulRegResponse;
import constants.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import specifications.Specifications;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;
import static io.restassured.RestAssured.given;

public class RegressPojoTest {

    @Test
    @DisplayName("Avatar contains ID of users")
    public void testIsAvatarContainsUserID() {
        //Run Specification methods
        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpec200());

        //GET request
        List<UserDataResponse> users = given()
                .when()
                .get(Constants.getListOfUsersQuery())
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserDataResponse.class);  //put received data to UserData.class

        // Verify that avatar parameter contains user ID
        users.forEach(x -> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString()), "Avatar doesn't contain user ID"));

        //or by using stream map approach:
//        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
//        List<String> ids = users.stream().map(x-> x.getId().toString()).collect(Collectors.toList());
//        for (int i = 0; i < avatars.size(); i++) {
//            Assertions.assertTrue(avatars.get(i).contains(ids.get(i)));
//        }

    }

    @Test
    @DisplayName("Email domain is reqres.in")
    public void testEmail() {
        //Test data
        String emailDomain = "reqres.in";

        //Run Specification methods
        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpec200());

        //GET request
        List<UserDataResponse> users = given()
                .when()

                .get(Constants.getListOfUsersQuery())
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserDataResponse.class);  //saving received data to UserData.class

        // Verify that ALL users' email domains end with expected email domain
        Assertions.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith(emailDomain)));

        //or by using stream map approach:
        List<String> emails = users.stream().map(UserDataResponse::getEmail).collect(Collectors.toList());
        for (int i = 0; i < emails.size(); i++) {
            Assertions.assertTrue(emails.get(i).contains(emailDomain));
        }
    }

    @Test
    @DisplayName("Error 400 test")
    public void testError400() {
        //Run Specification methods
        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpecError400());
    }

    @Test
    @DisplayName("Successful registration")
    public void testIsRegistrationIsSuccessful() {
        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpec200());
        //Test data
        int idExpected = 4;
        String tokenExpected = "QpwL5tke4Pnpja7X4";
        String emailValid = "eve.holt@reqres.in";
        String passwordValid = "pistol";

        //POST request
        RegistrationRequest user = new RegistrationRequest(emailValid, passwordValid);
        SuccessRegResponse successRegResponse = given()
                .body(user)
                .when()
                .post(Constants.getSuccessfulRegQuery())
                .then().log().all()
                .extract().as(SuccessRegResponse.class);

        //Verify expected ID and Token

        Assertions.assertNotNull(successRegResponse.getId());
        Assertions.assertEquals(idExpected, successRegResponse.getId());

        Assertions.assertNotNull(successRegResponse.getToken());
        Assertions.assertEquals(tokenExpected, successRegResponse.getToken());
    }

    @Test
    @DisplayName("Unsuccessful registration")
    public void testIsRegistrationIsUnSuccessful() {
        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpecError400());
        //Test data
        String emailValid = "sydney@fife";
        String passwordInValid = "";
        String errorMessageExpected= "Missing password";

        //POST request
        RegistrationRequest user = new RegistrationRequest(emailValid, passwordInValid);
        UnSuccessfulRegResponse unSuccessfulRegResponse = given()
                .body(user)
                .when()
                .post(Constants.getUnsuccessfulRegQuery())
                .then().log().all()
                .extract().as(UnSuccessfulRegResponse.class);

        //Verify error Message and status code
        Assertions.assertEquals(errorMessageExpected, unSuccessfulRegResponse.getError());
    }

    @Test
    @DisplayName("Years are sorted in ascending order")
    public void testSortedYears() {
        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpec200());

        //GET request
        List<ColorsDataResponse> colors = given()
                .when()
                .get(Constants.getListResourcesQuery())
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsDataResponse.class);

        //Create lists of actual and expected data
        List<Integer> yearsActual = colors.stream().map(ColorsDataResponse::getYear).collect(Collectors.toList());
        List<Integer> yearsSortedExpected = yearsActual.stream().sorted().collect(Collectors.toList());

        //Compare lists
        Assertions.assertEquals(yearsSortedExpected, yearsActual);

        System.out.println(yearsActual);
        System.out.println(yearsSortedExpected);
    }

    @Test
    @DisplayName("Delete user")
    public void testDeleteUser() {
        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpecUnique(204));

        given()
                .when()
                .delete(Constants.getDeleteQuery())
                .then().log().all();
    }

    @Test
    @DisplayName("Compare Server and PC time")
    public void testTime() {
        //Test data
        String name = "morpheus";
        String job = "zion resident";

        Specifications.installSpecification(Specifications.requestSpec(Constants.getBaseUrl()), Specifications.responseSpec200());
        UserTimeRequest user = new UserTimeRequest(name,job);
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put(Constants.getUpdateQuery())
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        //так как время считается в плоть до миллисекунд, необходимо убрать последние 5 символов, чтобы время было одинаковое
        String regexToPCTime = "(.{11})$";
        String regexToServerTime = "(.{8})$";

        //Customize time by cutting last milliseconds
        String actualTimeOfPC = Clock.systemUTC().instant().toString().replaceAll(regexToPCTime,"");
        String actualTimeOfServerBeforeCut = response.getUpdatedAt();
        String actualTimeOfServerAfterCut = actualTimeOfServerBeforeCut.replaceAll(regexToServerTime, "");

        //Compare Server with PC time
        Assertions.assertEquals(actualTimeOfPC, actualTimeOfServerAfterCut);

//        System.out.println("PC time : "+ actualTimeOfPC);
//        System.out.println("Server time: " + actualTimeOfServerAfterCut);
    }
}
