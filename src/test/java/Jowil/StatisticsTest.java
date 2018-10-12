package Jowil;

import Jowil.Reports.*;
import com.lowagie.text.DocumentException;
import jdk.nashorn.internal.ir.annotations.Ignore;
import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class StatisticsTest extends TestCase {

    public void setUp() throws Exception {
        System.out.println("hi");
        super.setUp();
    }



    public void testMulti1() throws IOException, CSVHandler.InConsistentAnswerKeyException, CSVHandler.InvalidFormNumberException, CSVHandler.EmptyCSVException, DocumentException, CSVHandler.IllFormedCSVException, CSVHandler.InvalidSubjColumnException {
            ////////////////////// test using jo csv ///////////////////////////////////////
        String inputFilesFolderPath = ".\\src\\test\\ReportTestCSVs\\" ;

        CSVHandler.setResponsesFilePath(inputFilesFolderPath+"StudentAnswers.csv");

        CSVHandler.setFormColIndex(3);
        CSVHandler.setIdentifierColStartIndex(0);
        CSVHandler.setIdentifierColEndIndex(1);
        CSVHandler.processHeaders(false);
        CSVHandler.loadAnswerKeys(inputFilesFolderPath+"AnswerKeys.csv",true);
        boolean isHeaders=CSVHandler.processHeaders(true);
        Jowil.CSVHandler.loadCsv(isHeaders);

        TestUtils.setQuestionChoicesFromFile(inputFilesFolderPath+"QuestionChoices.csv");
        TestUtils.setQuestionsWeights(Statistics.getQuestionsChoices().size() , Statistics.getNumberOfForms());

        TestUtils.fillGradeRanges() ;
//        Statistics.setStudentIdentifier(TestUtils.generateAutoIds(Statistics.getNumberOfStudents())) ;
//        Statistics.printBasicInfo();
        Statistics.initFormsScores();
        Jowil.Statistics.init();
        Jowil.Statistics.printBasicInfo();
        Jowil.Statistics.printCalculations();

        Report4 report4 = new Report4() ;
        report4.generateTextImgs();

//        Statistics.report5stats(0) ;
//        Statistics.questReportStats() ;
//
//        Report5 report8 = new Report5() ;
//        report8.generateHtmlReport(); ;
//        ArrayList<Report> reports = new ArrayList<>();
//
//        reports.add(new Report1()) ;
//        Report.initOutputFolderPaths("E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources");
//        ArrayList<Integer> formats = new ArrayList<>() ;
//        formats.add(ReportsHandler.HTML) ;
//        formats.add(ReportsHandler.PDF);
//
//        ReportsHandler reportsHandler = new ReportsHandler(true);
//        reportsHandler.generateReports(reports , formats);
    }

    public void tearDown() throws Exception {

        System.out.println("fuck you ");
    }


}