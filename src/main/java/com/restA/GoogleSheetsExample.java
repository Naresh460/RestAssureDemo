package com.restA;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;


public class GoogleSheetsExample {

	private static Sheets sheetsService;
	static String spreadsheetId;
	

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		// Specify the spreadsheet ID and range
		spreadsheetId = "1PiUPE5XaN83h6W14jjTN0QpJzA2J3SN8OQXnrb5V11E";
		String sheetName = "Sheet1"; // Update with your sheet name


		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json"))
				.createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

		// Initialize the Sheets service
		sheetsService = new Sheets.Builder(new NetHttpTransport(),
				GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
				.setApplicationName("Portfolio").build();




		// Write data to the spreadsheet
		writeToSheet(spreadsheetId,sheetName);
	}

	private static void writeToSheet(String spreadsheetId,String sheetName) throws IOException {
		
		
        int lastRow = getLastDataRow(spreadsheetId, sheetName);
        int newRow = lastRow + 1;
        String range = sheetName + "!A" + newRow + ":C" + newRow;


		ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList("John Doe",22,"Naresh")));
		
		try {
			
		UpdateValuesResponse result = sheetsService.spreadsheets().values().update(spreadsheetId, range, body)
				.setValueInputOption("RAW").execute();
		
		System.out.println("Updated " + result.getUpdatedCells() + " cells");

		}
		catch (GoogleJsonResponseException e) {
		    if (e.getStatusCode() == 400) {
		        System.err.println("Error details: " + e.getDetails());
		    } else {
		        throw e;
		    }}
	}
	
	private static int getLastDataRow(String spreadsheetId, String sheetName) throws IOException {
        String range = sheetName + "!A:A";
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();

        return (values != null) ? values.size() : 0;
    }
}
