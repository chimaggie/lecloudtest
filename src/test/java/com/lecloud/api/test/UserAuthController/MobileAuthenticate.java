package com.lecloud.api.test.UserAuthController;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.response.Response;
import static com.jayway.restassured.RestAssured.given;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class MobileAuthenticate {

    @Test
    // 1.3 admin login from mobile
    public void mobileAuthenticate() throws JSONException,InterruptedException {
        String url = "http://api.ffauto.us:8080/lecloud/api/mobile/authenticate";
        String body = "username=admin&password=admin";

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(body);
        builder.setContentType("application/x-www-form-urlencoded");

        RequestSpecification requestSpec = builder.build();
        Response response = given().authentication().preemptive().basic("", "").spec(requestSpec).when().post(url);
        Assert.assertEquals(200, response.getStatusCode());
        System.out.println("Mobile login in successfully.");
    }


    @Test
    // 1.7 cannot parse vin number to interface for pc and mobile login
    public void pcAuthenticate2() throws  JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String body = "username=admin&password=admin&vin=09876543211234567";
        given().body(body).contentType("application/x-www-form-urlencoded").when().post(url)
        .then().assertThat().statusCode(400);
    }

    @Test
    // 1.14 user login with self registered username and password to mobile
    public void userMobileAuthenticate() throws JSONException {
        given().body("username=faraday&password=faraday")
                .contentType("application/x-www-form-urlencoded")
                .when().post("http://api.ffauto.us:8080/lecloud/api/mobile/authenticate")
                .then().assertThat().statusCode(200);
    }
}
