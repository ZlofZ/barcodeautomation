package controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import util.IOController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class PdfController{
	private ArrayList<PDDocument> wayBills;
	private IOController io;
	
	//Getters
	public PDDocument getWaybill(int index){
		return wayBills.get(index);
	}
	public PDDocument getWaybill(String name){
		for(PDDocument bill: wayBills){
			if(bill.getDocumentInformation().getTitle().equalsIgnoreCase(name))
				return bill;
		}
		return null;
	}
	
	//Crops away the unneccesary area of every page
	private void cropPage(PDPage page){
		PDRectangle currentRect = page.getCropBox();
		PDRectangle rightwayup = new PDRectangle(currentRect.getWidth()/2, currentRect.getHeight()/2, currentRect.getWidth()/2, currentRect.getHeight()/2-150);
		PDRectangle upsidedown = new PDRectangle(0, 0, currentRect.getWidth()/2, currentRect.getHeight()/2-150);
		page.setCropBox(rightwayup);
		System.out.print("-");
	}
	
	//Resets the cropbox to contain the entire page.
	private void uncropPage(PDPage page){
		PDRectangle upsidedown = new PDRectangle(0, 0, page.getCropBox().getWidth()*2, (page.getCropBox().getHeight()+150)*2);
		page.setCropBox(upsidedown);
		System.out.print("-");
	}
	
	public void uncropPDF(PDDocument pdf){
		System.out.print("Uncropping PDF, Page [");
		for(int i = 0; i < pdf.getNumberOfPages(); i++){
			uncropPage(pdf.getPage(i));
		}
		System.out.println("]");
	}
	//Loops through the pdf pages and executes cropping on every one.
	public void cropPDF(PDDocument pdf){
		System.out.print("Cropping PDF, Page [");
		for(int i = 0; i < pdf.getNumberOfPages(); i++){
			cropPage(pdf.getPage(i));
		}
		System.out.println("]");
	}
	
	public PDDocument mergeWaybills(ArrayList<PDDocument> bills){
		PDDocument total = new PDDocument();
		for(PDDocument d : bills){
			total.addPage(d.getPage(0));
		}
		return total;
	}
	
	public void loadWaybills(){
		File [] files = io.searchDirectory("pdf", "waybills");
		wayBills = new ArrayList<>(	);
		for(File bill: files){
			wayBills.add(loadPDF(bill));
		}
	}
	
	//Loads the pdf from file.
	public PDDocument loadPDF(File f){
		PDDocument pddc = null;
		try {
			pddc = PDDocument.load(f);
			System.out.println("PDF Loaded..."+f.getName());
			return pddc;
		} catch (IOException e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	//Loads the pdf from path-string.
	private PDDocument loadPDF(String path){
		return loadPDF(new File(path));
	}
	
	//Saves the pdf to disk
	public void savePDF(PDDocument pdf){
		savePDF(pdf,"cropped");
	}
	public void savePDF(PDDocument pdf, String name){
		try{
			Date d = new Date();
			pdf.save(io.getLaunchPath()+"pdfout/"+d.getTime()+"-"+name+".pdf");
			System.out.println("PDF Saved...");
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	
	public PdfController(IOController ioc){
		io=ioc;
	}
}
