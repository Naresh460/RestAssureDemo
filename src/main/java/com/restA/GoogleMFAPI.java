package com.restA;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import io.restassured.specification.RequestSpecification;

public class GoogleMFAPI {
	private static Sheets sheetsService;
	static String sheetName;

	@Test
	public static void mainTest() throws IOException {

		String spreadsheetId = "1KPDyKcKZXmvbIWE2NiWA0mI-tCAYWVMQc2P5RDzZzEw";
		String sheetName = "Sheet1"; // Update with your sheet name

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json"))
				.createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

		// Initialize the Sheets service
		sheetsService = new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
				new HttpCredentialsAdapter(credentials)).setApplicationName("Portfolio").build();

		getResponsemethod("/148703/latest", "UTI Nifty 200", spreadsheetId, "UTI");
		getResponsemethod("/143903/latest", "ICICI Bharat", spreadsheetId, "ICICI");
		getResponsemethod("/120828/latest", "Quant Small Cap", spreadsheetId, "QUANT");
		getResponsemethod("/150678/latest", "SBI ", spreadsheetId, "SBI");
		getResponsemethod("/120591/latest", "ICICI Small Cap", spreadsheetId, "ICICISMALL");
		getResponsemethod("/125497/latest", "SBI Small ", spreadsheetId, "SBISMALL");
		getResponsemethod("/130503/latest", "HDFC ", spreadsheetId, "HDFC");
		getResponsemethod("/120847/latest", "QuantTax ", spreadsheetId, "QuantTax");

		
	}

	public static void getResponsemethod(String url, String mfNmae, String spreadsheetId, String sheetName)
			throws IOException {

		RestAssured.baseURI = "https://api.mfapi.in/mf";
		System.out.println("Mutual fund name is -->" + mfNmae);
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.get(url);
		JsonPath jsonPathEvaluator = response.jsonPath();
		String date = jsonPathEvaluator.getString("data[0].date");
		String nav = jsonPathEvaluator.getString("data[0].nav");

		System.out.println("fundhouse Body is: " + date);
		System.out.println("fundhouse Body is: " + nav);

		int lastRow = getLastDataRow(spreadsheetId, sheetName);
		int newRow = lastRow + 1;
		String range = sheetName + "!A" + newRow + ":C" + newRow;

		ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList(date, nav)));

		try {

			UpdateValuesResponse result = sheetsService.spreadsheets().values().update(spreadsheetId, range, body)
					.setValueInputOption("RAW").execute();

			System.out.println("Updated " + result.getUpdatedCells() + " cells");

		} catch (GoogleJsonResponseException e) {
			if (e.getStatusCode() == 400) {
				System.err.println("Error details: " + e.getDetails());
			} else {
				throw e;
			}
		}
	}

	private static int getLastDataRow(String spreadsheetId, String sheetName) throws IOException {
		String range = sheetName + "!A:A";
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();

		return (values != null) ? values.size() : 0;
	}

}
