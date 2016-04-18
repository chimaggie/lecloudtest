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
public class CpuAuthenticate {
    @Test
    //UAT: 1.1 admin login from vehicle
    public void cpuAuthenticate() throws JSONException,InterruptedException {
        //Initializing Rest API's URL
        String APIUrl = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        //Initializing payload or API body
        String apiFormBody = "username=admin&password=admin&vin=09876543211234567";
        //Building request using requestSpecBuilder
        RequestSpecBuilder builder = new RequestSpecBuilder();

        //Setting APIs body
        builder.setBody(apiFormBody);
        //Setting content type as application/jason or application/xml
        builder.setContentType("application/x-www-form-urlencoded");

        RequestSpecification requestSpec = builder.build();
        //Making post request with authentication
        Response response = given().authentication().preemptive().basic("", "").spec(requestSpec).when().post(APIUrl);
//        JSONObject JSONResponseBody = new JSONObject(response.body().asString());

        Assert.assertEquals(200, response.getStatusCode());
        System.out.println("CPU login in successfully.");
    }

    @Test
    //1.2 verify cannot login with wrong vin number
    public void cpuAuthenticate2() throws JSONException,InterruptedException {
        String url = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String body = "username=admin&password=admin&vin=222222";
        given().contentType("application/x-www-form-urlencoded").body(body).when().post(url).then().assertThat().statusCode(401);
    }

    @Test
    //1.3 verify cannot login with wrong vin number(different vin length)
    public void cpuAuthenticate4() throws JSONException,InterruptedException {
        String url = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String body = "username=admin&password=admin&vin=22222222";
        given().contentType("application/x-www-form-urlencoded").body(body).when().post(url).then().assertThat().statusCode(401);
    }
    @Test
    //1.4 verify cannot login with wrong password
    public void cpuAuthenticate3() throws JSONException,InterruptedException {
        String url = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String body = "username=admin&password=wrong&vin=09876543211234567";
        given().contentType("application/x-www-form-urlencoded").body(body).when().post(url).then().assertThat().statusCode(401);
    }

    @Test
    // 1.5 admin login from vehicle without entering a vin number
    public void cpuAuthenticate5() throws  JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String body = "username=admin&password=admin";
        given().body(body).contentType("application/x-www-form-urlencoded").when().post(url)
                .then().assertThat().statusCode(400);
    }
    @Test
    // 1.6 admin login from vehicle with right password but another vin existing in db
    public void cpuAuthenticate6() throws  JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String body = "username=admin&password=admin&vin=654321";
        given().body(body).contentType("application/x-www-form-urlencoded").when().post(url)
                .then().assertThat().statusCode(401);
    }
    @Test
    // 1.7 user login from vehicle, "havevin" is created by admin with vin number
    public void userCpuAuthenticate() throws JSONException {
        given().body("username=havevin&password=havevin&vin=111111")
                .contentType("application/x-www-form-urlencoded")
                .when().post("http://api.ffauto.us:8080/lecloud/api/cpu/authenticate")
                .then().assertThat().statusCode(200);
    }
    @Test
    // 1.8 user login from vehicle, "test" is created by admin, and add the vin number by saveAccount
    public void userCpuAuthenticate2() throws JSONException {
        given().body("username=test&password=test1&vin=234567")
                .contentType("application/x-www-form-urlencoded")
                .when().post("http://api.ffauto.us:8080/lecloud/api/cpu/authenticate")
                .then().assertThat().statusCode(200);
    }
    @Test
    // 1.9 user login with self registered username and password and vin
    public void userCpuAuthenticate3() throws JSONException {
        given().body("username=faraday&password=faraday&vin=333333")
                .contentType("application/x-www-form-urlencoded")
                .when().post("http://api.ffauto.us:8080/lecloud/api/cpu/authenticate")
                .then().assertThat().statusCode(200);
    }

}
