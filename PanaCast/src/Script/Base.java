package Script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import utils.ReportUtil;
import utils.TestConstants;
import utils.TestUtil;


public class Base {
 
	private static final String String = null;
	public static WebDriver driver;
	public String currentpath, path,path1;
	public FileInputStream fi;
	public Workbook testdatawb,controllerwb;
	public Sheet tdsheet,controlshet;
	public static Properties OR;
	public static String proceedOnFail;
	public static String testStatus;
	public FileOutputStream fo;
	public WritableWorkbook wwb;
	public WritableSheet ws;
	public String className;
	public String startTime;
	public static Logger log;
	public static Properties APPTEXT;
	File dir;
	public String MainWindowHandle;
	public static Process process;	 
	public static String keyword;
	public static String stepDescription;

	
	@BeforeClass
	public void initialize() throws Exception {
		//set logs
		 log = Logger.getLogger(Base.class.getName()); 
		 PropertyConfigurator.configure("log4j.properties"); 
		// set the system directory path
		currentpath = new java.io.File( "." ).getCanonicalPath();
		path=currentpath.replace("\\", "\\\\");
		
		// Reading test data file
		fi=new FileInputStream(path+TestConstants.TEST_DATA_DIR_PATH);
		testdatawb=Workbook.getWorkbook(fi);
		
		// Reading test steps file
		fi=new FileInputStream(path+TestConstants.CONTROLLER_DIR_PATH);
		controllerwb=Workbook.getWorkbook(fi);
		
		// Reading object properties
		OR=new Properties();
		fi=new FileInputStream(path+TestConstants.OBJECT_REPOSTRY_DIR_PATH);
		OR.load(fi);
		
	}
	
	/**
	 * this method set the class name
	 * @param className
	 */
	public String setTestClassName(String className){
		this.className=className;
		return className;
	}
	/**
	 * this method return class name
	 * @return
	 */
	public String getTestClassName(){
		return className;
	}
	
	/**
	 * this method creates a new Excel File
	 * @throws Exception
	 */
	public void generateExcel(int sno) throws Exception{
		Date currentdate=new Date();
		SimpleDateFormat ft=new SimpleDateFormat ("yyyy-MM-dd hh");
		String Date=ft.format(currentdate);
		
		/**
		 * creating a New Directory
		 */
	       dir=new File(path+TestConstants.TESTSUITE_RESULT_DIR_PATH+"\\"+Date+"TestSuite");
	       if(dir.exists()){
	           System.out.println("A folder with name 'new folder' is already exist in the path "+path);
	       }else{
	           dir.mkdir();
	       }
	     	fo=new FileOutputStream(dir.getPath()+"//"+getTestClassName()+"_TestResults.xls");
			wwb=Workbook.createWorkbook(fo);
			generateSheet(sno);
			
	}
	/**
	 * this method creates a new Excel Sheet
	 * 
	 */
	public void generateSheet(int sno) throws Exception{
		ws=wwb.createSheet("Sheet1", sno);
		Label testidlabel=new Label(0, 0, "Testcase ID");
		Label testdesclabel=new Label(1, 0, "Test Description");
		Label testresultlabel=new Label(2, 0, "Result");
		ws.addCell(testidlabel);
		ws.addCell(testdesclabel);
		ws.addCell(testresultlabel);
		
	}
	/**
	 * this method creates a new Cells in excel sheet 
	 * @param k
	 * @param testcasid
	 * @param testdesc
	 * @param result
	 */
	public void createLabel(int k, String testcasid, String testdesc, String result) throws Exception{
		Label testcaseid=new Label(0, k,testcasid);
		Label testcasedesc=new Label(1, k,testdesc);
		Label testresult=new Label(2, k, result);
		ws.addCell(testcaseid);
		ws.addCell(testcasedesc);
		ws.addCell(testresult);	
	}

	/**
	 * Copying test data
	 * @param colom
	 * @param row
	 * @param tdshetnum
	 * @return
	 */
	public Object testData(int colom,int row,String tdshetnum) {
		  String data=null;
		  tdsheet=testdatawb.getSheet(tdshetnum);
		  data=tdsheet.getCell(colom,row).getContents();
		  return data;
	  }
	
	//This is to open broswer
	public void openURL(String data71) throws Exception {
		tdsheet=testdatawb.getSheet("Browser");
		for (int i = 1; i < tdsheet.getRows(); i++)
		{
			String browser=tdsheet.getCell(2,i).getContents();
			String run=tdsheet.getCell(3,i).getContents();
			//String url=tdsheet.getCell(4,i).getContents();
			switch (browser) {
			case "GC":
				if(run.equalsIgnoreCase("Y")){
					File gc = new File( path+TestConstants.CHROME_BROWSER_DIR_PATH);
					System.setProperty("webdriver.chrome.driver", gc.getAbsolutePath());
					driver=new ChromeDriver();
					log.info("Browers is "+ browser); 
					driver.get(data71);
					log.info("Enter Url "+ data71); 
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					driver.manage().window().maximize();
				}
			break;
			case "MF":
				if(browser.equalsIgnoreCase("MF") && run.equalsIgnoreCase("Y")){
					driver=new FirefoxDriver();
					log.info("Browers is "+ browser); 
					driver.get(data71);
					log.info("Enter Url "+ data71);
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					driver.manage().window().maximize();
				}
			case "IE":
				if(browser.equalsIgnoreCase("IE") && run.equalsIgnoreCase("Y")){
					File ie = new File( path+TestConstants.IE_BROWSER_DIR_PATH);
					System.setProperty("webdriver.ie.driver", ie.getAbsolutePath());
					driver= new InternetExplorerDriver();
					log.info("Browers is "+ browser); 
					driver.get(data71);
					log.info("Enter Url "+ data71);
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					driver.manage().window().maximize();
						}
				
				break;
			default:
				break;
			}
		}
	}
	
	
	//this is to refresh
	public void Refresh(){
		driver.navigate().refresh();
		log.info("Page has Refreshed");

	}
	
	public void Appium(String data9) throws Exception{
		String testdata = data9;
		String[] Appium = testdata.split(",");
		String  Apppackage=Appium[0];
		String  Appactivity=Appium[1];
		DesiredCapabilities capabilities = new DesiredCapabilities();
		   capabilities.setCapability(CapabilityType.BROWSER_NAME, TestConstants.BROWSER_NAME);
		   capabilities.setCapability(CapabilityType.VERSION, TestConstants.VERSION);
		   capabilities.setCapability(CapabilityType.PLATFORM, TestConstants.PLATFORM);
		   capabilities.setCapability("app-package", Apppackage); // This is package name of your app (you can get it from apk info app)
		   log.info("App package is " + Apppackage);
		   capabilities.setCapability("app-activity", Appactivity);
		   log.info("App activity is " + Appactivity);
		   driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);	  
		   log.info("App is Launched");
		   //Thread.sleep(3000);	
		   log.info("Waiting 2000 seconds");

	}
	/**
	 * Retrieving objects form control sheet
	 * @param i
	 * @param colom
	 * @param tdshetnum
	 * @param csheet
	 * @param fileName 
	 * @param keyword2 
	 * @param stepDescription 
	 * @throws Exception 
	 */
	 public String controlScript(int row, int colom, String tdshetnum, Sheet csheet, String testcaseid, String stepDescription, String keyword2, String fileName) throws Exception{
		 String result=null;
		 controlshet=csheet;
		 for (int k = 1; k < controlshet.getRows(); k++) {
			 String desc=controlshet.getCell(1, k).getContents();
			  String keyword=controlshet.getCell(2, k).getContents();
			  String keywordtype=controlshet.getCell(3,k).getContents();
			  String object=controlshet.getCell(4,k).getContents();
			  String TSID= controlshet.getCell(0,k).getContents();
			  try{
				  switch(keyword){
				  case "Click": case "click":
					  Click(keywordtype,object);
					  break;
				  case "input": case "Input":
					  Object testdata= testData(colom,row,tdshetnum);
					  String data=(String) testdata;
					  inputText(keywordtype, object, data);
					  colom++;
					  break;

				  case "select": case "Select":
					  Object testdata1= testData(colom,row,tdshetnum);
					  String data1=(String) testdata1;
					  selectValue(keywordtype, object, data1, TSID);
					  colom++;
					  break;
				  case "wait": case"Wait":
					  Object testdata3= testData(colom,row,tdshetnum);
					  String data3= (String) testdata3;
					  Waittime(keyword, data3);
					  colom++;
					  break;
				  
				  case "verify": case "Verify":
					  Object testdata4= testData(colom,row,tdshetnum);
					  String data4= (String) testdata4;
					  verifyText(object,data4);
					  colom++;
					  break;
				 
				  case "mail" : case "Mail":
					  mail();
					  break;
				  case "close": case "closeBrowser":
					  closeBrowser();
					  break;
				  case "OpenURL": case"openURL":
					  Object testdata71= testData(colom,row,tdshetnum);
					  String data71=(String) testdata71;
					  openURL(data71);
				  case"swipe": case"Swipe":
					  Object testdata7= testData(colom,row,tdshetnum);
					  String data7=(String) testdata7;
					  swipe(data7);
					  colom++;
					  break;
				  case"tap": case"Tap":
					  Object testdata5= testData(colom,row,tdshetnum);
					  String data5=(String) testdata5;
					  Tap(data5);
					  colom++;
					  break;
				  case"flick": case"Flick":
					  Object testdata6= testData(colom,row,tdshetnum);
					  String data6=(String) testdata6;
					  Flick(data6);
					  colom++;
					  break;
				  case"LaunchAppium":case"launchappium":
					  LaunchAppium(object);
				  case"Appium": case"appium":
					  Object testdata9= testData(colom,row,tdshetnum);
					  String data9=(String) testdata9;
					  Appium(data9);
					  colom++;
					  break;
				  case"createfile": case"Createfile":
					  createfile();
					  break;
				  case"button":case"Button":
					  Button(object);
					  break;
				  case"assertion":case"Assertion":
					  Object testdata11= testData(colom,row,tdshetnum);
					  String data11=(String) testdata11;
					  System.out.println("ibj--"+object);
					  System.out.println("dta"+data11);
					  Assertion();
					  colom++;
				  case"Refresh":case"refresh":
					  Refresh();
					  break;
				  }
				  result="Pass";
				  report(result,stepDescription,desc,fileName,object,testcaseid);
			  }catch(Exception e){
				 	 result="Fail";
					e.printStackTrace();
					report(result,stepDescription,desc,fileName,object,testcaseid);
					break;
			  }
		  }
		return result;
	 }
	 
	// this is for wait 
	 private void Waittime(String keyword, String data3) throws Exception {
		// TODO Auto-generated method stub
		 try{
		 Thread.sleep(Long.parseLong(data3));
		log.info("Waiting" + data3); 
		}catch(Exception e){
			
		}
		 
	}
	// this is for report
	public void report(String result,String stepDescription, String keyword, String fileName, String object, String testcaseid) throws Exception{
		startTime=TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa");
		switch (result) {
		case "Fail":
			testStatus=result;
		//	TestUtil.takeScreenShot(path+utils.TestConstants.TESTSUITE_RESULT_DIR_PATH+fileName);
			ReportUtil.addKeyword(stepDescription, keyword, result, fileName);
			break;
		case "Pass":
			testStatus=result;
			//TestUtil.takeScreenShot(path+utils.TestConstants.TESTSUITE_RESULT_DIR_PATH+fileName);
			ReportUtil.addKeyword(stepDescription, keyword, result, fileName);
			break;
		default:
			break;
		}
	}
	 /**
	  * Click on button
	  * @param keywordtype
	  * @param object
	  * @throws Exception
	  */
	public void Click(String keywordtype, String object) throws Exception{
		try{
		if (keywordtype.equalsIgnoreCase("id")) {
			Thread.sleep(900);
			driver.findElement(By.id(OR.getProperty(object))).click();
		}else if (keywordtype.equalsIgnoreCase("linktext")) {
			Thread.sleep(900);
			driver.findElement(By.linkText(OR.getProperty(object))).click();
		}else if (keywordtype.equalsIgnoreCase("xpath")) {
			Thread.sleep(900);
			driver.findElement(By.xpath(OR.getProperty(object))).click();
		}else if (keywordtype.equalsIgnoreCase("name")) {
			Thread.sleep(900);
			driver.findElement(By.name(OR.getProperty(object))).click();
		}
		log.info("Click on  "+ object+" "); 

		}catch(Exception e){
			
		}
	}
	/**
	 * Send data by using Send keys
	 * @param keywordtype
	 * @param object
	 * @param data
	 * @throws Exception
	 */
	public void inputText(String keywordtype, String object, String data) throws Exception{
		try{
		if (keywordtype.equalsIgnoreCase("id")) {
			Thread.sleep(900);
			driver.findElement(By.id(OR.getProperty(object))).sendKeys(data);
			System.out.println(data);
		}else if (keywordtype.equalsIgnoreCase("css")) {
			Thread.sleep(900);
			driver.findElement(By.cssSelector(OR.getProperty(object))).sendKeys(data);
		}else if (keywordtype.equalsIgnoreCase("linktext")) {
			Thread.sleep(900);
			driver.findElement(By.linkText(OR.getProperty(object))).sendKeys(data);
		}else if (keywordtype.equalsIgnoreCase("xpath")) {
			Thread.sleep(900);			
			driver.findElement(By.xpath(OR.getProperty(object))).sendKeys(data);
		}else if (keywordtype.equalsIgnoreCase("name")) {
			Thread.sleep(900);
			driver.findElement(By.name(OR.getProperty(object))).sendKeys(data);
		}
		log.info("Entering data in  "+ object+" ");
		}catch(Exception e){
			
		}
	}
	/**
	 * Select the values form the drop down
	 * @param keywordtype
	 * @param object
	 * @param data
	 * @throws Exception
	 */
	
	public void selectValue(String keywordtype, String object, String data, String TSID) throws Exception{
		try{
		if (keywordtype.equalsIgnoreCase("id")) {
			Thread.sleep(900);
			
			new Select(driver.findElement(By.id(OR.getProperty(object)))).selectByVisibleText(data);
		}else if (keywordtype.equalsIgnoreCase("xpath")) {
			Thread.sleep(900);
			System.out.println(data);
			new Select(driver.findElement(By.xpath(OR.getProperty(object)))).selectByVisibleText(data);
		}
		log.info("Select "+ object+" ");

		}catch(Exception e){
			
		}
	}
	
	public void  verifyText(String object, String edata){
		log.debug("Executing verifyText");
		String actual=driver.findElement(By.xpath(OR.getProperty(object))).getText();
		log.info(edata);
		log.info(actual);
		System.out.println("object----------------"+object);
		System.out.println("exp----------------"+edata);
		try{
			Assert.assertEquals(actual , edata);
		}catch(Throwable t){
			// error
			log.info("Error in text - "+object);
			log.info("Actual - "+actual);
			log.info("Expected -"+ edata);
			log.fatal("test verify");
			log.error("error");
		}
			
	}

	/**
	 * closing browser
	 * @throws Exception 
	 */
	public void closeBrowser() throws Exception {
		Thread.sleep(900);
		driver.close();
		log.info("Closing the Browser");

	}
	
public static void mail(){
		final String username = "seleniumtest401@gmail.com";
		final String password = "Test123$";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("seleniumtest401@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("seleniumtest401@gmail.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n No spam to my email, please!");
 			Transport.send(message);
 			log.info("Mail Sent");
		}
		catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}



public void Button(String object) throws Exception{
	
	try{
		int Buttonnum=Integer.valueOf(OR.getProperty(object));
	HashMap<String, Integer> keycode = new HashMap<String, Integer>();
	keycode.put("keycode", Buttonnum);
	((JavascriptExecutor)driver).executeScript("mobile: keyevent", keycode);
	log.info("Click on button");
	}catch(Exception e){

	}
}
public void swipe(String data7){
		String testdata = data7;
		try{
			JavascriptExecutor js = (JavascriptExecutor) driver;
			HashMap<String, Double> swipeObject = new HashMap<String, Double>();
			String[] data = testdata.split(",");
			int a=Integer.valueOf(data[0]);
			int b=Integer.valueOf(data[1]);
			swipeObject.put("startX", (double) a);
			swipeObject.put("startY", (double) b);
			swipeObject.put("duration", 1.5);
			 js.executeScript("mobile: swipe", swipeObject);
			 log.info("Swipe on the screen");	
		}catch(Exception e){

			}
	}
public void createfile() throws Exception{
	Properties pro= new Properties();
	FileOutputStream fo= new FileOutputStream(path+TestConstants.text);
	log.info("Text file created successfully");
}
public void Flick(String data6 ){
	String testdata = data6;
	try{
			JavascriptExecutor js1 = (JavascriptExecutor) driver;
			HashMap<String, Double> flickObject = new HashMap<String, Double>();
			String[] data = testdata.split(",");
			int a=Integer.valueOf(data[0]);
			int b=Integer.valueOf(data[1]);
			flickObject.put("endX", (double) a);
			flickObject.put("endY", (double) b);
			flickObject.put("touchCount", (double) 2);
			js1.executeScript("mobile: flick", flickObject);
			log.info("Flick on the screen");
			}catch(Exception e){
			
		log.info("Flick on the screen");
	}	
}

public void Tap(String data5){
	
		String record = data5;
	try{
			JavascriptExecutor js = (JavascriptExecutor)driver;
			HashMap<String, Double> tapObject = new HashMap<String, Double>();
			String[] data = record.split(",");
			int x=Integer.valueOf(data[0]);
		 
			int y=Integer.valueOf(data[1]);
			
			tapObject.put("x", (double) x ); // in pixels from left
			tapObject.put("y", (double) y);
			tapObject.put("tapCount", (double) 2);
			tapObject.put ("touchCount", (double)1 );
			tapObject.put("duration", 1.0);
			js.executeScript("mobile: tap", tapObject);
			log.info("Tap on the screen");
		}catch(Exception e){
				
			}
	}	

public void LaunchAppium(String object) throws Exception{
	log.info("launch Appium");
	process = Runtime.getRuntime().exec(OR.getProperty(object)); 
}

public void Assertion(){
	//String exp=driver.findElement(By.name(OR.getProperty(object))).getText();

Assert.assertEquals("Welcome to the Pizza Palace.", driver.findElement(By.name("Welcome to the Pizza Palace.")).getText());
	//Assert.assertEquals(data11, exp);

	log.info("Assertion");
	
}
}