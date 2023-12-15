package com.restA;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import io.restassured.specification.RequestSpecification;

public class GetresponseDemo {
	
	@Test
	public static void test1() throws IOException {
		getResponsemethod("/148703/latest",0, "UTI Nifty 200");
		getResponsemethod("/143903/latest",1,"ICICI Bharat");
		getResponsemethod("/120828/latest",2,"Quant Small Cap");
		getResponsemethod("/150679/latest",3,"SBI ");
		getResponsemethod("/120591/latest",4,"ICICI Small Cap");



	}
	public static void getResponsemethod(String url,int sheetindex,String mfNmae) throws IOException {
		RestAssured.baseURI = "https://api.mfapi.in/mf";
		System.out.println("Mutual fund name is -->"+mfNmae);
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.get(url);
		JsonPath  jsonPathEvaluator= response.jsonPath();
        String date = jsonPathEvaluator.getString("data[0].date");
        String nav = jsonPathEvaluator.getString("data[0].nav");

		System.out.println("fundhouse Body is: " + date);
		System.out.println("fundhouse Body is: " + nav);
		
		FileInputStream fis= new FileInputStream(System.getProperty("user.dir") +"\\ExcelData\\Portfolio.xlsx");
		
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet=workbook.getSheetAt(sheetindex);
		if(sheet.getPhysicalNumberOfRows()>=1) 
		{
        sheet.shiftRows(1, sheet.getPhysicalNumberOfRows(), 1);
        }
		 Row rowHeading = sheet.createRow(1);
	        rowHeading.createCell(0).setCellValue(date);
	        rowHeading.createCell(1).setCellValue(nav);
	        
	        FileOutputStream out = new FileOutputStream(System.getProperty("user.dir") +"\\ExcelData\\Portfolio.xlsx");
	        workbook.write(out);
	        out.flush();
	        out.close();

		        
		        	 
	}

}
