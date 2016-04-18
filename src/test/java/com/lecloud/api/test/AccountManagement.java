package com.lecloud.api.test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.sun.xml.internal.xsom.impl.Ref;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by hongyuechi on 4/5/16.
 */
public class AccountManagement {
    static String cpuToken = null;

    @BeforeClass
    public static void cpuLogin() throws JSONException,InterruptedException {
        String APIUrl = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String apiFormBody = "username=admin&password=admin&vin=09876543211234567";

        RequestSpecBuilder builder = new RequestSpecBuilder();

        builder.setBody(apiFormBody);
        builder.setContentType("application/x-www-form-urlencoded");

        RequestSpecification requestSpec = builder.build();
        Response response = given().authentication().preemptive().basic("", "").spec(requestSpec).when().post(APIUrl);
        JSONObject jsonResp = new JSONObject(response.body().asString());

        cpuToken =  jsonResp.getString("token");
    }
    @Test
    public void getAccount() throws JSONException {
        Response resp = given().header("x-auth-token", cpuToken).get("http://api.ffauto.us:8080/lecloud/api/account");
        JSONObject jsonResp = new JSONObject(resp.asString());
        String temp = jsonResp.getString("login");
//
//        Assert.assertEquals(temp, "admin");
        given().header("x-auth-token", cpuToken).when()
            .get("http://api.ffauto.us:8080/lecloud/api/account").then().statusCode(200)
            .assertThat().body("login", equalTo("admin")).body("firstName", equalTo("admin"));

        System.out.println("User logged in as " + temp);
    }

    @Test
    public void saveAccount() throws JSONException {
        String body = "{\"login\":\"admin\",\"password\":null,\"firstName\":\"admin\",\"lastName\":\"Administrator\",\"email\":\"admin@localhost\",  \"activated\": true,\"langKey\": \"cn\",\"vin\": null,\"carName\": null,\"carNickname\": null,\"carPassword\": null,\"model\": null,\"imei\": null,  \"imsi\": null,\"carNumber\": null,  \"authorities\": [\"ROLE_USER\",\"ROLE_ADMIN\" ] }";
        given().contentType(ContentType.JSON).header("x-auth-token", cpuToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/account").then().assertThat()
                .statusCode(200);
        System.out.println("Attribute lanKey changed to cn.");
    }
}
