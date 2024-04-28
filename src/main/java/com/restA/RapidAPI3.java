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
import com.jayway.jsonpath.JsonPath;

public class RapidAPI3 {
	private static Sheets sheetsService;
	static String sheetName;
static Object lastValue;
static String lastValueString;
static String date;

	@Test
	public static void mainTest() throws IOException, InterruptedException {

		String spreadsheetId = "1KPDyKcKZXmvbIWE2NiWA0mI-tCAYWVMQc2P5RDzZzEw";
		//String sheetName = "Sheet1"; // Update with your sheet name

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json"))
				.createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

		// Initialize the Sheets service
		sheetsService = new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
				new HttpCredentialsAdapter(credentials)).setApplicationName("Portfolio").build();

//		getResponsemethod("148703", "UTI Nifty 200", spreadsheetId, "UTI");
//		getResponsemethod("143903", "ICICI Bharat", spreadsheetId, "ICICI");
//		getResponsemethod("120828", "Quant Small Cap", spreadsheetId, "QUANT");
//		getResponsemethod("150678", "SBI ", spreadsheetId, "SBI");
//		getResponsemethod("120591", "ICICI Small Cap", spreadsheetId, "ICICISMALL");
//		getResponsemethod("125497", "SBI Small ", spreadsheetId, "SBISMALL");
//		getResponsemethod("130503", "HDFC ", spreadsheetId, "HDFC");
//		getResponsemethod("120847", "QuantTax ", spreadsheetId, "QuantTax");
//		getResponsemethod("151739", "UTI500 ", spreadsheetId, "UTI500");		
//	getResponsemethod("149288", "HDFCNext50 ", spreadsheetId, "HDFCNext50");
		getResponsemethod("149894", "Axis ", spreadsheetId, "Axis");
		

		
	}

	public static void getResponsemethod(String url, String mfNmae, String spreadsheetId, String sheetName)
			throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://latest-mutual-fund-nav.p.rapidapi.com/fetchLatestNAV?SchemeCode="+url+""))
				.header("X-RapidAPI-Key", "696115166amsh062c2425e35d1afp1b68aajsnd513f6334923")
				.header("X-RapidAPI-Host", "latest-mutual-fund-nav.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		String bodyy=response.body();
		System.out.println(response.body());
		
		String netAssetValue = JsonPath.read(bodyy, "$[0]['Net Asset Value']");
		date = JsonPath.read(bodyy, "$[0]['Date']");
		String datestring=date.replaceAll("\\s", "");
		int datelength=datestring.length();
		System.out.println("fundhouse Name-->: " + mfNmae);
		System.out.println("fundhouse Body is: " + netAssetValue);
		System.out.println("fundhouse date is: " + datestring);
		System.out.println("length-->"+datelength);
		getLastDataRow(spreadsheetId, sheetName);
		
             if (!lastValueString.equals(datestring)) {	
            	 
            	 System.out.println("enter to if condition");
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
             else {
	
	System.out.println("date already Exist--->"+date);
	
}
	
	}

	private static int getLastDataRow(String spreadsheetId, String sheetName) throws IOException {
		String range = sheetName + "!A:A";
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		System.out.println("Response--->"+response);

		List<List<Object>> values = response.getValues();
		int lastIndex = values.size() - 1; // Index of the last element in the outer list
		List<Object> lastInnerList = values.get(lastIndex);
		int lastValueIndex = lastInnerList.size() - 1; // Index of the last element in the inner list
		lastValue = lastInnerList.get(lastValueIndex);
		lastValueString = lastValue.toString();
		int length1=lastValueString.length();
         System.out.println("lastValueString-->"+lastValueString);
         System.out.println("Valuelength-->"+length1);
		return (values != null) ? values.size() : 0;
	}

}
