package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.*;


public class IOController {
	private Scanner kb;
	private Robot robot;
	//private  final String LAUNCHPATH = "p:/documents/pdfhandler/";
	private String LAUNCHPATH;
	private String cPass, cUname;
	
	//Read ints read int from commandline
	public int readInt(){
		int i = kb.nextInt();
		kb.nextLine();
		return i;
	}
	//Read String from commandline
	public String readString(){
		return kb.nextLine();
	}
	
	public String getCelestaUsername(){
		return this.cUname;
	}
	public String getCelestaPassword(){
		return this.cPass;
	}
	public String getLaunchPath(){
		return this.LAUNCHPATH;
	}
	
	private void checkRequiredDirsPresent(){
		File[] dirs = findDirs("");
		String csvDir = "csv", waybillDir =  "waybills", outputDir = "pdfout";
		File a = new File(LAUNCHPATH+csvDir);
		System.out.println(a.getName());
		if(!a.exists()) a.mkdir();
		a = new File(LAUNCHPATH+waybillDir);
		System.out.println(a.getName());
		if(!a.exists()) a.mkdir();
		a = new File(LAUNCHPATH+outputDir);
		System.out.println(a.getName());
		if(!a.exists()) a.mkdir();
	}
	
	private void loadSecret(){
		ArrayList<String> lines = loadTxt(new File(LAUNCHPATH+"client.secret"));
		for(String s: lines){
			if(s.startsWith("username")){
				this.cUname = s.substring(9);
			}else if(s.startsWith("password")){
				this.cPass=s.substring(9);
			}else if(s.startsWith("current-directory")){
				this.LAUNCHPATH=s.substring(18);
			}
		}
	}
	
	//read Txtfile line by line
	private ArrayList<String> loadTxt(File f){
		ArrayList<String> bc = new ArrayList<>();
		try{
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null){
				bc.add(line);
				line  = br.readLine();
			}
		} catch (IOException e){
			System.out.println("Could not read text from file: " + f.getName());
			System.exit(-1);
		}
		return bc;
	}
	//Load CSV file
	public ArrayList<String> loadCSV(){
		System.out.println("load csv io");
		File csv = chooseFile(searchDirectory("csv"));
		return loadTxt(csv);
	}
	//Load images
	public ArrayList<BufferedImage> loadImages(){
		File dir = chooseDir(findDirs("images"));
		File[] files = searchDirectory("jpg", "images/"+dir.getName());
		ArrayList<BufferedImage> imgs = new ArrayList<>();
		if(files == null) {
			return null;
		}
		try{
			System.out.print("Loading");
			for(File f: files){
				imgs.add(ImageIO.read(f));
				System.out.print(".");
			}
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
		return imgs;
	}
	//Load Barcodes from txtfile
	public ArrayList<String> loadBarcodes(){
		File barcodes = chooseFile(searchDirectory("txt"));
		return loadTxt(barcodes);
	}
	
	//Choose file from Filearray
	public File chooseFile(File[] files){
		if(files.length == 1)return files[0];
		int answer = -1;
		while(answer < 1 || answer > files.length){
			System.out.print("Choose which file from "+1+"-"+files.length+">");
			answer = readInt();
		}
		System.out.println("File ["+files[answer-1].getName()+"] chosen.");
		return files[answer-1];
	}
	
	//Choose directory from an array
	public File chooseDir(File[] dirs){
		for(int i = 0; i<dirs.length; i++){
			System.out.println((i+1)+". "+dirs[i]);
		}
		return dirs[readInt()-1];
	}
	
	//Finds directories
	public File[] findDirs(String startDir){
		File dir = new File(LAUNCHPATH+startDir);
		return dir.listFiles();
	}
	
	//Searches a directory for files
	public File[] searchDirectory(String fileType){
		return searchDirectory(fileType, fileType);
	}
	public File[] searchDirectory(String fileType, String path){
		File dir = new File(LAUNCHPATH+path);
		System.out.println(dir.getAbsolutePath());
		File[] matchingFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(fileType);
			}
		});
		if(matchingFiles == null || matchingFiles.length == 0){
			System.out.println("No files found, Terminating");
			System.exit(1);
		} else if(matchingFiles.length > 1) {
			System.out.println("Files found: [");
			for (int i = 0; i < matchingFiles.length; i++) {
				System.out.println(i + 1 + ". " + matchingFiles[i].getName());
			}
			System.out.println("]");
		}else {
			System.out.println("One file found: ["+matchingFiles[0].getName()+"]");
		}
		return matchingFiles;
	}
	
	//Print barcodelist
	public void printBarcodes(ArrayList<String> barcodes){
		if(barcodes!=null)
			for(int i = 0; i < barcodes.size(); i++){
				System.out.println("Page "+(i+1)+": "+barcodes.get(i));
			}
		else System.out.println("Nothing in the Barcode list...");
	}
	
	//Saves barcodes to file
	public void saveBarCodes(ArrayList<String> barcodes){
		try{
			File output1 = new File(LAUNCHPATH+"txt/Barcodes.txt");
			File output2 = new File(LAUNCHPATH+"txt/bcnpage.txt");
			FileOutputStream fos1 = new FileOutputStream(output1);
			FileOutputStream fos2 = new FileOutputStream(output2);
			
			BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(fos1));
			BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(fos2));
			
			for(int i = 0; i < barcodes.size(); i++){
				bw1.write(barcodes.get(i));
				bw1.newLine();
				bw2.write("Page "+(i+1)+": "+barcodes.get(i));
				bw2.newLine();
			}
			bw1.close();
			bw2.close();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
		System.out.println("Barcodes saved.");
	}

	public BufferedImage captureScreen(Rectangle area){
		return robot.createScreenCapture(area);
	}
	
	public boolean checkIfDelivered(BufferedImage img) throws IndexOutOfBoundsException{
		System.out.print("Delivered: ");
		int sumRed = 0, sumGreen = 0, sumBlue = 0, whitePix = 0;
		int[] pixels = new int[img.getHeight()*img.getWidth()];
		PixelGrabber pg = new PixelGrabber(img,0,0,100,50,pixels,0, img.getWidth());
		try{
			pg.grabPixels();
		}catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
		for(int pixel : pixels){
			int  red = (pixel & 0x00ff0000) >> 16;
			int  green = (pixel & 0x0000ff00) >> 8;
			int  blue = pixel & 0x000000ff;
			if(red == 255 && green == 255 && blue == 255){whitePix++;}
			else{
				sumRed += red;
				sumGreen += green;
				sumBlue += blue;
			}
		}
		whitePix = pixels.length - whitePix;
		if(whitePix==0) whitePix = 1;
		sumRed /= whitePix;
		sumGreen /= whitePix;
		sumBlue /= whitePix;
		boolean  delivered = sumGreen/whitePix == 2;
		//System.out.println("Red ["+sumRed/whitePix+"], Green ["+sumGreen/whitePix+"], Blue ["+sumBlue/whitePix+"]\nDelivered: "+ (sumGreen/whitePix == 2));
		if(delivered) System.out.println("Yes.");
		else System.out.println("No.");
		return delivered;
	}
	public boolean checkBarcodeKK(String barcode){
		//type barcode into window
		typeStringKK(barcode);
		//wait .5 sec
		robot.delay(1500);
		//type enter into window
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		//wait .5 sec
		robot.delay(3000);
		//scan window for orange or green.
		BufferedImage img = captureScreen(new Rectangle(1920+380,405,30,20));
		return checkIfDelivered(img);
	}
	
	public void wait(int seconds){
		robot.delay(seconds*1000);
	}
	
	public ArrayList<String> checkBarcodes(ArrayList<String> barcodes){
		ArrayList<String> notDelivered = new ArrayList<>();
		robot.delay(2000);
		for(String code: barcodes){
			if(!checkBarcodeKK(code)){
				notDelivered.add(code);
			}
		}
		return notDelivered;
	}

	
	public void celestaClick(){
		robot.mouseMove(1920+370, 355);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public void typeStringKK(String s){
		System.out.print("writing: ");
		
		char[] chars = s.toCharArray();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		for(char c : chars){
			System.out.print(c);
			robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
			robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
			//robot.delay(50);
		}
		System.out.println();
	}
	
	public void pressEnter(){
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);	}
	
	public void typeStringCelesta(String s){
		System.out.print("writing: ");
		char[] chars = s.toCharArray();
		for(char c : chars){
			System.out.print(c);
			if(c != ' '){
				robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
				robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
				//robot.delay(50);
			}
		}
		System.out.println();
	}
	
	public IOController(String path){
		LAUNCHPATH = path+"/";
		loadSecret();
		kb = new Scanner(System.in);
		try{
			robot = new Robot();
		}catch(AWTException e){System.out.println(e.getMessage());}
		checkRequiredDirsPresent();
	}
}
