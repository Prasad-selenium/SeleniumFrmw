package driverscript;
import jxl.Sheet;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import utils.ReportUtil;
import utils.TestUtil;

import Script.Base;

public class DriverScript extends Base {
	
	@BeforeSuite
	public static void startTesting() throws Exception{

		String currentpath = new java.io.File( "." ).getCanonicalPath();
		String path=currentpath.replace("\\", "\\\\");
		ReportUtil.startTesting(path+utils.TestConstants.TESTREPORT_RESULT_DIR_PATH, 
                TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"), 
                "Prod",
                "1.0");
	}
	@BeforeClass
	public void beforeClass(){
		setTestClassName(DriverScript.this.getClass().getName());	
	}
	@Test
	public void driverScript() throws Exception {
		//String startTime=null;
		ReportUtil.startSuite("Suite 1");
		// test data colom and test results starting row
		int colom=4,m=1;
		int sno=1;
		generateExcel(sno);
		tdsheet=testdatawb.getSheet("Suite");
		for (int i = 1; i < tdsheet.getRows(); i++) {
			String tsrunmode=tdsheet.getCell(2,i).getContents();
			if (tsrunmode.equalsIgnoreCase("Y")) {
				String tcaseid=tdsheet.getCell(0,i).getContents();
				Sheet tdsheet1=testdatawb.getSheet(tcaseid);
				//control sheet
				Sheet controlshet=controllerwb.getSheet(tcaseid);
				String fileName=null;
				for (int j = 1; j < tdsheet1.getRows(); j++) {
					String tcaserunmode=tdsheet1.getCell(3,j).getContents();
					if (tcaserunmode.equalsIgnoreCase("y")) {
						String testcaseid=tdsheet1.getCell(0,j).getContents();
						String testdesc=tdsheet1.getCell(1,j).getContents();
						fileName = "Suite1_TC"+(testcaseid)+"_TS"+tcaseid+"_"+keyword+j+".png";
						stepDescription=testdesc;
						keyword=testcaseid;
						String result=controlScript(j, colom, tcaseid,controlshet,testcaseid,stepDescription,keyword,fileName);
						report(result,fileName);
						createLabel(m, testcaseid, testdesc, "Pass");
						//closeBrowser();
					}  
					m++;
				}
			}
			tdsheet=testdatawb.getSheet("Suite");
		}
		wwb.write();
		wwb.close();
	} 
	
	@AfterSuite
	public static void EndTest(){
		ReportUtil.updateEndTime(TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"));
		 mail();
	}
	
	
	public void report(String result, String fileName){
		 startTime=TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa");
		  if(result.startsWith("Fail")){
				testStatus=result;
				//TestUtil.takeScreenShot(path+utils.TestConstants.TESTSUITE_RESULT_DIR_PATH+fileName);
				ReportUtil.addTestCase(keyword, 
						startTime, 
						TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"),
						testStatus );
		  }else if(result.startsWith("Pass")){
			  testStatus=result;
				//TestUtil.takeScreenShot(path+utils.TestConstants.TESTSUITE_RESULT_DIR_PATH+fileName);
				ReportUtil.addTestCase(keyword, 
						startTime, 
						TestUtil.now("dd.MMMMM.yyyy hh.mm.ss aaa"),
						testStatus );
		  }
	}
}