package controller.celesta;

import controller.PdfController;
import model.Waybill;
import org.apache.pdfbox.pdmodel.PDDocument;
import util.DeliveryStatus;
import util.IOController;

import java.io.File;
import java.util.ArrayList;

public class AppRunnerCelesta{
	private static IOController io;
	private static ArrayList<Waybill> waybills;
	private static SeleniumControllerCelesta sCon;
	private static PdfController pCon;
	
	private void mergeCropAndSaveInvalid(){
		PDDocument invalidBills = new PDDocument();
		for(Waybill b: waybills){
			if(b.getStatus() == DeliveryStatus.INVALID){
				System.out.println("invalid!! "+b);
				invalidBills.addPage(b.getPdf().getPage(0));
			}
		}
		pCon.cropPDF(invalidBills);
		pCon.savePDF(invalidBills, "invalid");
	}
	
	private void enterInvalidCodes(){
		System.out.println("Enter barcodes for invalid Waybills");
		for(Waybill w: waybills){
			if(w.getStatus()==DeliveryStatus.INVALID){
				System.out.print(w.getFileName()+" >");
				w.setBarcode(io.readString());
			}
		}
	}
	
	private void saveNotDeliveredPDF(){
		PDDocument notDelivered = new PDDocument();
		for(Waybill w: waybills){
			if(w.getStatus()!= DeliveryStatus.DELIVERED){
				notDelivered.addPage(w.getPdf().getPage(0));
			}
		}
		pCon.uncropPDF(notDelivered);
		pCon.savePDF(notDelivered, "Not_Delivered");
	}
	
	private void getCSVData(){
		ArrayList<String> csvRows = io.loadCSV();
		for(String row: csvRows){
			String[] splitRow = row.split(",");
			String barcode = splitRow[16].substring(1,splitRow[16].length()-1);
			String name = splitRow[9].substring(1,splitRow[9].length()-1);
			waybills.add(new Waybill(barcode, name));
		}
	}
	
	private void loadPDFs(){
		ArrayList<String> waybillNames = new ArrayList<>();
		for(Waybill b : waybills){
			//b.setPdf(pConK.loadWaybill(b.getFileName());
			File[] bills = io.searchDirectory("pdf", "waybills");
			for(File f: bills){
				if(f.getName().equalsIgnoreCase(b.getFileName()))
					b.setPdf(pCon.loadPDF(f));
			}
			//System.out.println(b);
		}
	}
	
	public AppRunnerCelesta(String  path){
		io = new IOController(path);
		pCon = new PdfController(io);
		sCon = new SeleniumControllerCelesta();
		waybills=new ArrayList<>();
		
		getCSVData();
		for(Waybill b : waybills){
			if(b.getBarcode().isEmpty())
				b.setDeliveryStatus(DeliveryStatus.INVALID);
		}
		loadPDFs();
		mergeCropAndSaveInvalid();
		enterInvalidCodes();
		io.wait(5);
		for(Waybill b: waybills){
			b.setDeliveryStatus(sCon.doCheck(b.getBarcode()));
			System.out.println(b);
		}
		
	}
}
