package com.lecloud.api.test.AccountResource;

import com.jayway.restassured.response.Response;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Created by hongyuechi on 4/13/16.
 */
public class SaveAccount {
    static String adminToken = null;
    static String userToken = null;

    @BeforeClass
    public static void userLogin() throws JSONException, InterruptedException {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String adminBody = "username=admin&password=admin";
        String userBody = "username=test&password=test1";

        Response adminResp = given().body(adminBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject adminObj = new JSONObject(adminResp.body().asString());
        adminToken = adminObj.getString("token");

        Response userResp = given().body(userBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject userObj = new JSONObject(userResp.body().asString());
        userToken = userObj.getString("token");
    }
    @AfterClass
    public static void setOrigin() throws JSONException {
        String adminBody = "{\n" +
                "\"login\": \"admin\",\n" +
                "\"password\": \"admin\",\n" +
                "\"email\": \"admin@localhost\",\n" +
                "\"firstName\": \"admin\",\n" +
                "\"langKey\": \"cn\",\n" +
                "\"activated\": true\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token",adminToken)
                .body(adminBody).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(200);

        String userBody = "{\n" +
                "\"login\": \"test\",\n" +
                "\"password\": \"test1\",\n" +
                "\"email\": \"test@localhost\",\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token",userToken)
                .body(userBody).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(200);
    }
    @Test
    // admin cannot change password by saveAccount API, this function is in ChangePassword
    public void adminSaveAccount() throws JSONException {
        String body = "{\n" +
                "\"login\": \"admin\",\n" +
                "\"password\": \"change\"\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token",adminToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }
    @Test
    // check admin change email not duplicate
    public void adminSaveAccount2() throws JSONException {
        String body = "{\n" +
                "\"login\": \"admin\",\n" +
                "\"email\": \"user@localhost\"\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token",adminToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }
    @Test
    // admin cannot change his login name
    public void adminSaveAccount3() throws JSONException {
        String body = "{\n" +
                "\"login\": \"adminchange\"" +
                "}";
        given().contentType("application/JSON").header("x-auth-token",adminToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }

    // TODO: check admin change his vin number is not duplicate
    public void adminSaveAccount4() throws JSONException {

    }
    @Test
    // admin cannot save user's account
    public void adminSaveAccount5() throws JSONException {
        String body = "{\n" +
                "\"login\": \"test\",\n" +
                "\"email\": \"change@localhost\"\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token", adminToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }

    @Test
    // admin can save information
    public void adminSaveAccount6() throws JSONException {
        String body = "{\n" +
                "\"login\": \"admin\",\n" +
                "\"langKey\": \"en\"\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token",adminToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(200);
        given().header("x-auth-token", adminToken)
                .when().get("http://api.ffauto.us:8080/lecloud/api/users/admin")
                .then().assertThat().body("langKey", equalTo("en"));
    }

    @Test
    // user can change his own info and save successfully
    public void userSaveAccount() {
        String body = "{\n" +
                "\"login\": \"test\",\n" +
                "\"email\":\"test@localhost\",\n" +
                "\"langKey\": \"en\"\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token",userToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(200);
        given().header("x-auth-token", adminToken)
                .when().get("http://api.ffauto.us:8080/lecloud/api/users/test")
                .then().assertThat().body("langKey", equalTo("en"));
    }

    @Test
    // user cannot save admin's account
    public void userSaveAccount2() {
        String body = "{\n" +
                "\"login\": \"admin\",\n" +
                "\"email\":\"hack@localhost\",\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token", userToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }
    @Test
    // user cannot change role, save role can not be successful
    public void userSaveAccount3() {
        String body = "{\n" +
                "\"login\": \"test\",\n" +
                "\"email\":\"test@localhost\",\n" +
                "\"authorities\": [ \n " +
                "\"ROLE_USER\",\n"  +"\"ROLE_ADMIN\"\n],\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token", userToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }
    @Test
    // user can not change password in this API , should do in ChangePassword API
    public void userSaveAccount4() {
        String body = "{\n" +
                "\"login\": \"test\",\n" +
                "\"email\":\"test@localhost\",\n" +
                "\"password\": \"test2\" \n " +
                "}";
        given().contentType("application/JSON").header("x-auth-token", userToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }

    //TODO: user edit car related info and verify record updated in db

    @Test
    // user change email address, check email should not duplicate
    public void userSaveAccount5() {
        String body = "{\n" +
                "\"login\": \"test\",\n" +
                "\"email\":\"system@localhost\"\n" +
                "}";
        given().contentType("application/JSON").header("x-auth-token", userToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }
    @Test
    // user cannot change user login name
    public void userSaveAccount6() {
        String body = "{\n" +
                "\"login\": \"testtest\",\n" +
                "\"email\":\"test@localhost\"" +
                "}";
        given().contentType("application/JSON").header("x-auth-token", userToken)
                .body(body).when().post("http://api.ffauto.us:8080/lecloud/api/account")
                .then().assertThat().statusCode(400);
    }

}
