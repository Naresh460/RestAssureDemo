package com.restA;

import java.io.FileInputStream;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import com.jayway.jsonpath.JsonPath;

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



public class RapidAPI2 {
	private static Sheets sheetsService;
	static String sheetName;

	@Test
	public static void mainTest() throws IOException, InterruptedException {

		String spreadsheetId = "1KPDyKcKZXmvbIWE2NiWA0mI-tCAYWVMQc2P5RDzZzEw";
		//String sheetName = "Sheet1"; // Update with your sheet name

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json"))
				.createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

		// Initialize the Sheets service
		sheetsService = new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
				new HttpCredentialsAdapter(credentials)).setApplicationName("Portfolio").build();

		getResponsemethod("148703", "UTI Nifty 200", spreadsheetId, "UTI");
		getResponsemethod("143903", "ICICI Bharat", spreadsheetId, "ICICI");
		getResponsemethod("120828", "Quant Small Cap", spreadsheetId, "QUANT");
		getResponsemethod("150678", "SBI ", spreadsheetId, "SBI");
		getResponsemethod("120591", "ICICI Small Cap", spreadsheetId, "ICICISMALL");
		getResponsemethod("125497", "SBI Small ", spreadsheetId, "SBISMALL");
		getResponsemethod("130503", "HDFC ", spreadsheetId, "HDFC");
		getResponsemethod("120847", "QuantTax ", spreadsheetId, "QuantTax");
		getResponsemethod("151739", "UTI500 ", spreadsheetId, "UTI500");

		
	}

	public static void getResponsemethod(String url, String mfNmae, String spreadsheetId, String sheetName)
			throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://latest-mutual-fund-nav.p.rapidapi.com/fetchLatestNAV?SchemeCode="+url+""))
				.header("X-RapidAPI-Key", "977753f462mshf2f6053ba578acfp103b1fjsn412332277c0c")
				.header("X-RapidAPI-Host", "latest-mutual-fund-nav.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		String bodyy=response.body();
		System.out.println(response.body());
		
		String netAssetValue = JsonPath.read(bodyy, "$[0]['Net Asset Value']");
		String date = JsonPath.read(bodyy, "$[0]['Date']");
		System.out.println("fundhouse Name-->: " + mfNmae);
		System.out.println("fundhouse Body is: " + netAssetValue);
		System.out.println("fundhouse Body is: " + date);

		int lastRow = getLastDataRow(spreadsheetId, sheetName);
		int newRow = lastRow + 1;
		String range = sheetName + "!A" + newRow + ":C" + newRow;

		ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList(date, netAssetValue)));

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
