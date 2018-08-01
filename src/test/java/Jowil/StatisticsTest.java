package Jowil;

import Jowil.Reports.Report1;
import Jowil.Reports.Report2;
import Jowil.Reports.Report5;
import com.lowagie.text.DocumentException;
import jdk.nashorn.internal.ir.annotations.Ignore;
import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticsTest extends TestCase {

    public void setUp() throws Exception {
        System.out.println("hi");
        super.setUp();
    }



    public void testMulti1() throws IOException, CSVHandler.EmptyAnswerKeyException, CSVHandler.InvalidFormNumberException, CSVHandler.EmptyCSVException, DocumentException, CSVHandler.IllFormedCSVException {
            ////////////////////// test using jo csv ///////////////////////////////////////
        String inputFilesFolderPath = ".\\src\\test\\ReportTestCSVs\\" ;

        CSVHandler.setFilePath(inputFilesFolderPath+"StudentAnswers.csv");
        CSVHandler.loadAnswerKeys(inputFilesFolderPath+"AnswerKeys.csv");
        CSVHandler.setFormColIndex(3);
        CSVHandler.setIdentifierColStartIndex(0);
        CSVHandler.setIdentifierColEndIndex(1);
        boolean isHeaders=CSVHandler.processHeaders(false);
        Jowil.CSVHandler.loadCsv(isHeaders);

        TestUtils.setQuestionChoicesFromFile(inputFilesFolderPath+"QuestionChoices.csv");
        TestUtils.setQuestionsWeights(Statistics.getQuestionNames().size() , Statistics.getNumberOfForms());

        TestUtils.fillGradeRanges() ;
        Statistics.setStudentIdentifier(Statistics.getStudentIDs()) ;
//        Statistics.printBasicInfo();
        Statistics.initFormsScores();
        Jowil.Statistics.init();
        Jowil.Statistics.printBasicInfo();
        Jowil.Statistics.printCalculations();

        Report5 report5 = new Report5() ;
        report5.generatePdfReport();

    }

    public void tearDown() throws Exception {

        System.out.println("fuck you ");
    }


}