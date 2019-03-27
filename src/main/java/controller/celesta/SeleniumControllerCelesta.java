package controller.celesta;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.DeliveryStatus;

import java.awt.*;

public class SeleniumControllerCelesta{
	private static WebDriver driver;
	private static WebDriverWait wait;

	
	
	private  void contextClick(){
//		new Actions(driver).contextClick(element).build().perform();
//		By p = By.id("ctl00_ContentPlaceHolder1_ucActivitiesList_RadMenuActivity_detached");
//		wait.until(ExpectedConditions.attributeContains(p , "display" , "block"));
//		WebElement e = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_ucActivitiesList_RadMenuActivity_detached\"]/ul/li[1]/a"));
//		e.click();
	}
	
	private DeliveryStatus getOrderInfo(){
		System.out.println("get orderinfo");
		By xpath = By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_ucActivitiesList_radgvActivity_ctl00__0\"]/td[8]");
		System.out.println("wait until tablerow is present");
		wait.until(ExpectedConditions.presenceOfElementLocated(xpath));
		System.out.println("wait over...");
		WebElement element = driver.findElement(xpath);
		System.out.println(element.getText());
		switch(element.getText()){
			case "Loaded":
			case "New":  return DeliveryStatus.NOT_DELIVERED;
			case "Delivered": return DeliveryStatus.DELIVERED;
			default: return DeliveryStatus.INVALID;
		}
		
		
	}
	
	public DeliveryStatus doCheck(String code){
		if(code.startsWith("_")){
			System.out.println("["+code+"] starts with underscore");
			code = code.substring(1);
		}
		enterWaybillNumberCelesta(code);
		return getOrderInfo();
	}
	
	public void enterWaybillNumberCelesta(String number){
		By loc = By.id("ctl00_ContentPlaceHolder1_ucActivitiesList_radgvActivity_ctl00_ctl02_ctl02_ctl03");
		wait.until(ExpectedConditions.presenceOfElementLocated(loc));
		WebElement element = driver.findElement(loc);
		element.sendKeys(number);
		element.submit();
	}
	
	private void setNoFilterCelesta(){
		String dropdownId =  "ctl00_ContentPlaceHolder1_ucActivitiesList_filterToolbar_rtlbFilter_i15_cmbFilters_Input";
		String test = "ctl00_ContentPlaceHolder1_ucActivitiesList_filterToolbar_rtlbFilter_i15_cmbFilters_Input";
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(test)));
		WebElement element = driver.findElement(By.id(test));
		element.sendKeys(Keys.UP,Keys.UP,Keys.UP);
		element.submit();
	}
	
	private void loginCelesta(){
		driver.findElement(By.id("ContentPlaceHolderLogin_loginControl_UserName")).sendKeys(CELESTA_USERNAME);
		driver.findElement(By.id("ContentPlaceHolderLogin_loginControl_Password")).sendKeys(CELESTA_PASS);
		driver.findElement(By.id("ContentPlaceHolderLogin_loginControl_LoginButton")).click();
	}
	
	public SeleniumControllerCelesta(){
		System.setProperty("webdriver.opera.driver","p:/documents/pdfhandler/operadriver.exe");
		driver = new OperaDriver();
		wait  = new WebDriverWait(driver , 10);
		driver.navigate().to(CELESTA_URL);
		driver.manage().window().setPosition(new Point(-1920, 0));
		driver.manage().window().maximize();
		loginCelesta();
		setNoFilterCelesta();
	}
}