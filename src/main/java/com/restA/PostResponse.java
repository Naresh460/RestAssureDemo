package com.restA;


import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class PostResponse {

	@Test @SuppressWarnings("unchecked")
	public void createBooking() {
		JSONObject booking = new JSONObject();
		JSONObject bookingdates = new JSONObject();
		
	
		booking.put("firstname", "Naresh");
		booking.put("lastname", "Reddy");
		booking.put("totalprice", 1000);
		booking.put("depositpaid",true );
		booking.put("additionalneeds","super bowls" );
		booking.put("bookingdates", bookingdates);
		
		bookingdates.put("checkin", "2023-01-01");
		bookingdates.put("checkout", "2023-01-01");
		
		RestAssured
				.given()
					.contentType(ContentType.JSON)
					.body(booking.toString())
					.baseUri("https://restful-booker.herokuapp.com/booking")
					
				.when()
					.post()
					
				.then()
					.assertThat()
					.statusCode(200)
					.body("booking.firstname", Matchers.equalTo("Naresh") );
	}
	
}
