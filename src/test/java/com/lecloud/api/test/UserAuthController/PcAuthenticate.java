package com.lecloud.api.test.UserAuthController;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by hongyuechi on 4/13/16.
 */
public class PcAuthenticate {

    @Test
    // 1.2 admin login from pc
    public void pcAuthenticate() throws JSONException,InterruptedException {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String body = "username=admin&password=admin";

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(body);
        builder.setContentType("application/x-www-form-urlencoded");

        RequestSpecification requestSpec = builder.build();
        Response response = given().authentication().preemptive().basic("", "").spec(requestSpec).when().post(url);
        Assert.assertEquals(200, response.getStatusCode());
        System.out.println("PC login in successfully.");
    }

    @Test
    // 1.13 user login with self registered username and password to pc
    public void userPcAuthenticate() throws JSONException {
        given().body("username=faraday&password=faraday")
                .contentType("application/x-www-form-urlencoded")
                .when().post("http://api.ffauto.us:8080/lecloud/api/pc/authenticate")
                .then().assertThat().statusCode(200);
    }
}
