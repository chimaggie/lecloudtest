package com.lecloud.api.test.UserResource;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by hongyuechi on 4/8/16.
 */
public class CreateUser {
    static String adminToken = null;

    @BeforeClass
    public static void userLogin() {

        // get cpuToken
        String APIUrl = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String apiFormBody = "username=admin&password=admin&vin=09876543211234567";
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(apiFormBody);
        builder.setContentType("application/x-www-form-urlencoded");
        RequestSpecification requestSpec = builder.build();
        Response response = given().authentication().preemptive().basic("", "").spec(requestSpec).when().post(APIUrl);
        JSONObject jsonResp = new JSONObject(response.body().asString());
        adminToken =  jsonResp.getString("token");

    }
    @Before
    public void deleteUser() {
        System.out.println(adminToken);
        given().header("x-auth-token", adminToken).delete("http://api.ffauto.us:8080/lecloud/api/users/le");
        given().header("x-auth-token", adminToken).delete("http://api.ffauto.us:8080/lecloud/api/users/adminadmin");
    }


    @Test
    //2.1 admin role create user with all attributes, both required and optional.
    public void createUser() throws JSONException {
        String body = "{\n" +
                "  \"carName\": \"le\",\n" +
                "  \"carNickname\": \"le\",\n" +
                "  \"carNumber\": \"111\",\n" +
                "  \"carPassword\": \"111111\",\n" +
                "  \"email\": \"le@gmail.com\",\n" +
                "  \"firstName\": \"le\",\n" +
//                "  \"id\": \"user-5\",\n" +
                "  \"imei\": \"123456789\",\n" +
                "  \"imsi\": \"123456789\",\n" +
                "  \"langKey\": \"cn\",\n" +
                "  \"lastName\": \"le\",\n" +
                "  \"login\": \"le\",\n" +
                "  \"model\": null,\n" +
                "  \"password\": \"le\",\n" +
                "  \"vin\": \"234567\"\n" +
                "}";
        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat()
                .statusCode(201);
    }

    @Test
    //2.2 admin create a user with only required attributes
    public void createUser2() throws JSONException {
        String body = "{" +
                "  \"email\": \"le@gmail.com\"," +
                "  \"login\": \"le\"," +
                "  \"password\": \"le\"" +
                "}";
        System.out.println(body);
        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat()
                .statusCode(201);
    }
    @Test
    //2.5 create a user without input email, should not be created without email
    public void createUser3() throws JSONException {
        String body = "{\n" +
                "  \"carName\": \"le\",\n" +
                "  \"carNickname\": \"le\",\n" +
                "  \"carNumber\": \"111\",\n" +
                "  \"carPassword\": \"111111\",\n" +
//                "  \"email\": \"le@gmail.com\",\n" +
                "  \"firstName\": \"le\",\n" +
                "  \"imei\": \"123456789\",\n" +
                "  \"imsi\": \"123456789\",\n" +
                "  \"langKey\": \"cn\",\n" +
//                "  \"lastModifiedBy\": \"le\",\n" +
//                "  \"lastModifiedDate\": \"2016-04-07T21:33:09.358Z\",\n" +
                "  \"lastName\": \"le\",\n" +
                "  \"login\": \"le\",\n" +
                "  \"model\": null,\n" +
                "  \"password\": \"le\",\n" +
                "  \"vin\": \"234567\"\n" +
                "}";
        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat()
                .statusCode(400);
    }

    @Test
    //2.3 cannot create a user with same user information/ record
    public void createUser4() throws JSONException {
        String body = "{\n" +
                "  \"carName\": \"le\",\n" +
                "  \"carNickname\": \"le\",\n" +
                "  \"carNumber\": \"111\",\n" +
                "  \"carPassword\": \"111111\",\n" +
                "  \"email\": \"le@gmail.com\",\n" +
                "  \"firstName\": \"le\",\n" +
                "  \"id\": \"user-5\",\n" +
                "  \"imei\": \"123456789\",\n" +
                "  \"imsi\": \"123456789\",\n" +
                "  \"langKey\": \"cn\",\n" +
                "  \"lastName\": \"le\",\n" +
                "  \"login\": \"le\",\n" +
                "  \"model\": null,\n" +
                "  \"password\": \"le\",\n" +
                "  \"vin\": \"234567\"\n" +
                "}";
        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .post("http://api.ffauto.us:8080/lecloud/api/users");

        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat()
                .statusCode(400);
    }
    @Test
    // 2.4 cannot create user with existing email address
    public void createUser5() throws JSONException {
        String body = "{\n" +
                "  \"email\": \"admin@localhost\",\n" +
                "  \"login\": \"le\",\n" +
                "  \"password\": \"le\"\n" +
                "}";
        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat()
                .statusCode(400);
    }
    @Test
    // 2.6 cannot create user with empty login name
    public void createUser6() throws JSONException {
        String body = "{\n" +
                "  \"email\": \"le@gmail.com\",\n" +
//                "  \"login\": \"le\",\n" +
                "  \"password\": \"le\"\n" +
                "}";
        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat()
                .statusCode(400);
    }
    @Test
    // 2.7 cannot create user without entering a password
    public void createUser7() throws JSONException {
        String body = "{\n" +
                "  \"email\": \"le@gmail.com\",\n" +
                "  \"login\": \"le\"\n" +
                "}";
        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat()
                .statusCode(400);
    }
    @Test
    //2.8 admin create a admin role user
    public void createUser8() throws JSONException {
        String body = "{\n" +
                "  \"email\": \"adminadmin@gmail.com\",\n" +
                "  \"login\": \"adminadmin\",\n" +
                "  \"password\": \"adminadmin\",\n" +
                "    \"authorities\": [\n" +
                "      \"ROLE_USER\",\n" +
                "      \"ROLE_ADMIN\"\n" +
                "    ]\n" +
                "}";

        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat().statusCode(201);

        Response resp = given().given().header("x-auth-token", adminToken).when().get("http://api.ffauto.us:8080/lecloud/api/users/adminadmin");
        JSONObject obj = new JSONObject(resp.body().asString());
        JSONArray authorities = (JSONArray) obj.get("authorities");
        assertEquals("ROLE_USER", authorities.get(0));
        assertEquals("ROLE_ADMIN", authorities.get(1));
    }

    @Test
    //2.9 admin create a user without specify role, the default role should not be null
    public void createUser9() throws JSONException {
        String body = "{\n" +
                "  \"email\": \"adminadmin@gmail.com\",\n" +
                "  \"login\": \"adminadmin\",\n" +
                "  \"password\": \"adminadmin\"\n" +
                "}";

        given().contentType(ContentType.JSON).header("x-auth-token", adminToken).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/users").then().assertThat().statusCode(201);

        Response resp = given().given().header("x-auth-token", adminToken).when()
                            .get("http://api.ffauto.us:8080/lecloud/api/users/adminadmin");
        JSONObject obj = new JSONObject(resp.body().asString());
        JSONArray authorities = (JSONArray) obj.get("authorities");

        assertTrue(authorities.length() != 0);
    }
}
