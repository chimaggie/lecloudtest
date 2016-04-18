package com.lecloud.api.test.AccountResource;

import com.jayway.restassured.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by hongyuechi on 4/12/16.
 */
public class GetAccount {
    static String adminToken = null;
    static String userToken = null;

    @BeforeClass
    public static void cpuLogin() throws JSONException,InterruptedException {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String adminBody = "username=admin&password=admin&vin=09876543211234567";
        String userBody = "username=test&password=test1";

        Response adminResp = given().body(adminBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject adminObj = new JSONObject(adminResp.body().asString());
        adminToken = adminObj.getString("token");

        Response userResp = given().body(userBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject userObj = new JSONObject(userResp.body().asString());
        userToken = userObj.getString("token");
    }
    @Test
    //get account information with its login account
    public void getAccount() throws JSONException {
        given().header("x-auth-token", adminToken).when()
                .get("http://api.ffauto.us:8080/lecloud/api/account").then().statusCode(200)
                .assertThat().body("login", equalTo("admin")).body("firstName", equalTo("admin"));
    }

}
