package model;

import org.apache.pdfbox.pdmodel.PDDocument;
import util.DeliveryStatus;

public class Waybill{
	private PDDocument pdf = null;
	private String barcode = null;
	private String fileName = null;
	private DeliveryStatus status = DeliveryStatus.UNSET;
	
	public Waybill(){}
	public Waybill(String barcode, String fileName){
		this.barcode = barcode;
		this.fileName = fileName;
	}
	public Waybill(PDDocument pdf, String barcode, String fileName){
		this.pdf = pdf;
		this.barcode = barcode;
		this.fileName = fileName;
	}
	
	public void setPdf(PDDocument pdf){
		this.pdf = pdf;
	}
	public void setBarcode(String barcode){
		this.barcode = barcode;
	}
	public void setFileName(String fileName){
		this.fileName= fileName;
	}
	public void setDeliveryStatus(DeliveryStatus status){
		this.status = status;
	}
	
	public PDDocument getPdf(){
		return pdf;
	}
	public String getBarcode(){
		return barcode;
	}
	public String getFileName(){
		return fileName;
	}
	public DeliveryStatus getStatus(){
		return status;
	}
	
	@Override
	public String toString(){
		String name = fileName;
		String code = barcode;
		if(fileName.isEmpty()) name = "No Name";
		if(barcode.isEmpty()) code = "No Code";
		return "[" + code + ", " +  name + ", "+ status +", PDF present:" + (pdf!=null) + "]";
	}
}
