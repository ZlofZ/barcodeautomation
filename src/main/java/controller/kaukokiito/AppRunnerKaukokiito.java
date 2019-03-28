package controller.kaukokiito;

import controller.PdfController;
import model.Waybill;
import org.apache.pdfbox.pdmodel.PDDocument;
import util.DeliveryStatus;
import util.IOController;

import java.io.File;
import java.util.ArrayList;

public class AppRunnerKaukokiito{
	private static IOController io;
	private static SeleniumControllerKaukokiito sCon;
	private static PdfController pConK;
	
	private ArrayList<Waybill> waybills;
	
	private void mergeCropAndSaveInvalid(){
		PDDocument invalidBills = new PDDocument();
		for(Waybill b: waybills){
			int x = b.getFileName().compareTo(io.getLastHandled());
			System.out.println(b.getFileName() + " vs " + io.getLastHandled() + " = " + x);
			if(b.getStatus() == DeliveryStatus.INVALID && x>0){
				System.out.println("invalid!! "+b);
				invalidBills.addPage(b.getPdf().getPage(0));
			}
		}
		//pConK.cropPDF(invalidBills);
		pConK.savePDF(invalidBills, "invalid");
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
		pConK.uncropPDF(notDelivered);
		pConK.savePDF(notDelivered, "Not_Delivered");
	}
	
	private void getCSVData(){
		ArrayList<String> csvRows = io.loadCSV();
		String lastHandled = io.getLastHandled();
		for(String row: csvRows){
			String[] splitRow = row.split(",");
			String barcode = splitRow[16].substring(1,splitRow[16].length()-1);
			String name = splitRow[9].substring(1,splitRow[9].length()-1);
			if(lastHandled.compareTo(name)<0 && lastHandled.length() <= name.length()){
				waybills.add(new Waybill(barcode, name));
				System.out.println("adding "+name);
			}
		}
	}
	
	private void loadPDFs(){
		ArrayList<String> waybillNames = new ArrayList<>();
		File[] bills = io.searchDirectory("pdf", "waybills");
		for(Waybill b : waybills){
			//b.setPdf(pConK.loadWaybill(b.getFileName());
			for(File f: bills){
				if(f.getName().equalsIgnoreCase(b.getFileName()))
					b.setPdf(pConK.loadPDF(f));
			}
			//System.out.println(b);
		}
	}
	
	private void load(){
		getCSVData();
		for(Waybill b : waybills){
			if(b.getBarcode().isEmpty())
				b.setDeliveryStatus(DeliveryStatus.INVALID);
		}
		loadPDFs();
		mergeCropAndSaveInvalid();
		enterInvalidCodes();
		sCon = new SeleniumControllerKaukokiito(io);
		String lastBillHandled = io.getLastHandled();
		for(Waybill b: waybills){
			b.setDeliveryStatus(sCon.doCheck(b.getBarcode()));
			//System.out.println(b);
			int x = b.getFileName().compareTo(lastBillHandled);
			System.out.println(b+"\n"+b.getFileName()+" vs "+lastBillHandled+" = "+ x);
			if(x > 0 && b.getFileName().length()<=lastBillHandled.length()){
				lastBillHandled=b.getFileName();
			}
		}
		saveNotDeliveredPDF();
		io.saveLastHandled(lastBillHandled);
	}
	
	public AppRunnerKaukokiito(String path){
		io = new IOController(path);
		pConK = new PdfController(io);
		waybills = new ArrayList<>();
		load();
		sCon.stopDriver();
	}
}
