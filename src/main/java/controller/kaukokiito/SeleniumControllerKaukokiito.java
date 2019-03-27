package controller.kaukokiito;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.DeliveryStatus;
import util.IOController;

public class SeleniumControllerKaukokiito{
	private static WebDriver driver;
	private static WebDriverWait wait;
	private static final String KAUKOKIITO_URL = "https://www.kaukokiito.fi/en/kaukoputki/track-and-trace/";
	private IOController io;
	
	private void enterCode(String code){
		WebElement element = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div/div/div[1]/div/div/div[1]/div[1]/div/div/input"));
		element.sendKeys(Keys.CONTROL,"a");
		element.sendKeys(code);
	}
	private void clickSearch(){
		WebElement element = driver.findElement(By.xpath("//*[@id=\"track-and-trace-basic-submit\"]/button"));
		wait.until(ExpectedConditions.elementToBeClickable(element));
		element.click();
	}
	
	private String getDeliveryStatus(){
		By loc = By.xpath("/html/body/div[2]/div[2]/div/div/div/div/div[5]/div/div[2]/div/div/div[1]/div/div/p");
		io.wait(1);
		String ret;
		try{
			ret = driver.findElement(loc).getText();
		} catch(NoSuchElementException e){
			ret = "Not found";
		}
		
		return ret;
	}
	
	private DeliveryStatus checkStatus(String status){
		System.out.println(status);
		switch(status){
			case "Goods are shipping":
			case "Order received":
				return DeliveryStatus.NOT_DELIVERED;
			case "Shipment delivered":
				return DeliveryStatus.DELIVERED;
			default:
				return DeliveryStatus.INVALID;
		}
	}
	
	public DeliveryStatus doCheck(String code){
		if(code.startsWith("_")){
			System.out.println("["+code+"] starts with underscore");
			code = code.substring(1);
		}
		enterCode(code);
		clickSearch();
		String status = getDeliveryStatus();
		return checkStatus(status);
	}
	public SeleniumControllerKaukokiito(IOController ioc){
		io = ioc;
		System.setProperty("webdriver.opera.driver" , io.getLaunchPath()+"operadriver.exe");
		driver = new OperaDriver();
		driver.manage().window().setPosition(new Point(-1920, 0));
		driver.manage().window().maximize();
		wait  = new WebDriverWait(driver , 10);
		driver.navigate().to(KAUKOKIITO_URL);
	}
}