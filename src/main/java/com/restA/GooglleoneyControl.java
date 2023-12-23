package com.restA;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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

public class GooglleoneyControl {
	private static Sheets sheetsService;
	static String sheetName;

	@Test
	public static void mainTest() throws IOException, InterruptedException {

		String spreadsheetId = "1PiUPE5XaN83h6W14jjTN0QpJzA2J3SN8OQXnrb5V11E";
		String sheetName = "Sheet1"; // Update with your sheet name

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json"))
				.createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

		// Initialize the Sheets service
		sheetsService = new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
				new HttpCredentialsAdapter(credentials)).setApplicationName("Portfolio").build();

		getResponsemethod("https://www.moneycontrol.com/mutual-funds/nav/uti-nifty200-momentum-30-index-fund-direct-plan-growth/MUT3614","UTI Nifty 200",spreadsheetId, "UTI");
		getResponsemethod("https://www.moneycontrol.com/mutual-funds/nav/icici-prudential-bharat-22-fof-direct-plan-growth/MPI3793","ICICI Bharat", spreadsheetId, "ICICI");
		getResponsemethod("https://www.moneycontrol.com/mutual-funds/nav/quant-small-cap-fund-direct-plan-growth/MES056","Quant Small Cap", spreadsheetId, "QUANT");
		getResponsemethod("https://www.moneycontrol.com/mutual-funds/nav/sbi-nifty-smallcap-250-index-fund-direct-plan-growth/MSB1871","SBI ", spreadsheetId, "SBI");
		getResponsemethod("https://www.moneycontrol.com/mutual-funds/nav/icici-prudential-smallcap-fund-direct-plan-growth/MPI1146","ICICI Small Cap", spreadsheetId, "ICICISMALL");
		getResponsemethod("https://www.moneycontrol.com/mutual-funds/nav/sbi-small-cap-fund-direct-plan-growth/MSA031","SBI Small Cap", spreadsheetId, "SBISMALL");
		getResponsemethod("https://www.moneycontrol.com/mutual-funds/nav/hdfc-small-cap-fund-direct-plan-growth/MMS025","Hdfc Small Cap", spreadsheetId, "HDFC");
		getResponsemethod("https://www.moneycontrol.com/mutual-funds/nav/quant-tax-plan-direct-plan-growth/MES037","QuantTAx Small Cap", spreadsheetId, "QuantTax");

	
	
	}

	public static void getResponsemethod(String url, String mfNmae, String spreadsheetId, String sheetName)
			throws IOException, InterruptedException {
		WebDriver driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		
		driver.get(url);
		Thread.sleep(3000);
		
		String nav = driver.findElement(By.xpath("//div[@class='leftblok']/span[@class='amt']")).getText()
				.replaceFirst("\\W", "").trim();
		
		//String change = driver.findElement(By.xpath("//div[@class='leftblok']/span[@class='amt']/following-sibling::span")).getText().trim();
				
		
		String date = driver.findElement(By.xpath("//div[@class='leftblok']/span[@class='amt']/following-sibling::div")).getText()
				.replaceFirst("\\W", "").trim();
		
		System.out.println("fundhouse name is: " + mfNmae);
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
		
		driver.quit();
	}

	private static int getLastDataRow(String spreadsheetId, String sheetName) throws IOException {
		String range = sheetName + "!A:A";
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();

		return (values != null) ? values.size() : 0;
	}

}
