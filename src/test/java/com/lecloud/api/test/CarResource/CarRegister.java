package com.lecloud.api.test.CarResource;

import com.jayway.restassured.http.ContentType;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by hongyuechi on 4/15/16.
 */
public class CarRegister {

    public void carRegister() {

        String body = "{\n" +
                "  \"diagnosticList\": 0,\n" +
                "  \"duns\": \"91223344\",\n" +
                "  \"ecuList\": 0,\n" +
                "  \"hwVer\": \"1.0\",\n" +
                "  \"ptclVer\": \"1.0\",\n" +
                "  \"swVer\": \"1.0\",\n" +
                "  \"model\": \"dk01\",\n" +
                "  \"moduleId\": \"91223344\",\n" +
                "  \"name\": \"dk01\",\n" +
                "  \"nickname\": \"dk01\",\n" +
                "  \"partNum\": \"91223344\",\n" +
                "  \"simInfo\": [\n" +
                "    {\n" +
                "      \"iccid\": \"4081234567\",\n" +
                "      \"id\": null,\n" +
                "      \"imei\": \"4081234567\",\n" +
                "      \"imsi\": \"4081234567\",\n" +
                "      \"name\": \"att\",\n" +
                "      \"num\": \"4081234567\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"traceId\": \"91223344\",\n" +
                "  \"vin\": \"99876543211234567\"\n" +
                "}";
        given().contentType(ContentType.JSON).body(body)
                .when().post("http://api.ffauto.us:8080/lecloud/api/cars/register").then().assertThat().statusCode(200);
    }
}
