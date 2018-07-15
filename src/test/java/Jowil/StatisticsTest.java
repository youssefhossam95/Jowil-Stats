package Jowil;

import com.lowagie.text.DocumentException;
import jdk.nashorn.internal.ir.annotations.Ignore;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticsTest extends TestCase {

    public void setUp() throws Exception {
        System.out.println("hi");
        super.setUp();
    }

    public void welloTest(){
        System.out.println("yaaay");
    }
    public static ArrayList<ArrayList<String>> generateTestAllQuestionsChoices(){
        ArrayList<ArrayList<String>> out=new ArrayList<ArrayList<String>>();
        for(int i=0;i<4;i++){
            out.add(new ArrayList<String>());
            char choice='a';
            for(int j=0;j<5;j++)
                out.get(i).add(Character.toString((char)(choice+j)));
        }

        for(int i=0;i<4;i++){
            out.add(new ArrayList<String>());
            for(int j=0;j<6;j++)
                out.get(out.size()-1).add(Integer.toString(j+1));
        }
        return out;
    }

    public static ArrayList<Double> generateQuestionsWeights(int n){
        ArrayList<Double> out =new ArrayList<Double>();
        for(int i=0;i<n;i++)
            out.add(1.0);
        return out;

    }
    //grade lower range contains the lower range of the corresponding grade and one extra element which is 1 (the lower range of what is after A+)
    private static void fillGradeRanges() {
        ArrayList<String> grades = new ArrayList<String>();
        grades.add("F") ;grades.add("D") ;grades.add("C") ; grades.add("B");grades.add("A");
        Statistics.setGrades(grades);

        ArrayList<Double> gradeLowerRange  =new ArrayList<Double>();
        gradeLowerRange.add(0.0);
        for(int i=0;i<4;i++)
            gradeLowerRange.add(0.6 + 0.1 * i);
        gradeLowerRange.add(1.0);
        Statistics.setGradesLowerRange(gradeLowerRange);
    }

//    public void testAll() throws IOException, CSVHandler.EmptyAnswerKeyException, CSVHandler.InvalidFormNumberException, CSVHandler.EmptyCSVException {
//        CSVHandler.setFilePath(".\\src\\test\\TestCSVs\\testAll2.csv");
//        ArrayList<Integer> studentData= new ArrayList<Integer>();
//        studentData.add(CSVHandler.STUDENTID);
//        studentData.add(CSVHandler.STUDENTNAME);
//        boolean isHeaders=CSVHandler.detectHeaders();
//        if(isHeaders)
//            Main.updateQuestionHeaders(CSVHandler.getDetectedQHeaders());
//        Jowil.CSVHandler.loadCsv(studentData, isHeaders, true,1);
//        Jowil.Statistics.setQuestionsChoices(generateTestAllQuestionsChoices());
//        ArrayList<ArrayList<Double>> questionWeights=new ArrayList<ArrayList<Double>>();
//        questionWeights.add(generateQuestionsWeights(8));
//        Jowil.Statistics.setQuestionWeights(questionWeights);
//        Jowil.Statistics.init();
//        Jowil.Statistics.printBasicInfo();
//        Jowil.Statistics.printCalculations();
//    }


    public void testMulti1() throws IOException, CSVHandler.EmptyAnswerKeyException, CSVHandler.InvalidFormNumberException, CSVHandler.EmptyCSVException, DocumentException {
        CSVHandler.setFilePath(".\\src\\test\\TestCSVs\\testMulti1.csv");
        CSVHandler.loadAnswerKeys(".\\src\\test\\TestCSVs\\answerKeys1.csv");
        ArrayList<Integer> studentData= new ArrayList<Integer>();
        studentData.add(CSVHandler.STUDENTID);
        studentData.add(CSVHandler.STUDENTNAME);
        studentData.add(CSVHandler.IGNORE);
        studentData.add(CSVHandler.STUDENTFORM);
        boolean isHeaders=CSVHandler.detectHeaders();
        if(isHeaders)
            Main.updateQuestionHeaders(CSVHandler.getDetectedQHeaders());
        Jowil.CSVHandler.loadCsv(studentData, isHeaders, false,2);
        Jowil.Statistics.setQuestionsChoices(generateTestAllQuestionsChoices());
        ArrayList<ArrayList<Double>> questionWeights=new ArrayList<ArrayList<Double>>();
        questionWeights.add(generateQuestionsWeights(8));
        questionWeights.add(generateQuestionsWeights(8));
        Jowil.Statistics.setQuestionWeights(questionWeights);
        fillGradeRanges() ;
        Statistics.setStudentIdentifier(Statistics.getStudentIDs()) ;
        Jowil.Statistics.init();
        Jowil.Statistics.printBasicInfo();
        Jowil.Statistics.printCalculations();
        Jowil.Statistics.report1Stats() ;
//        Jowil.Statistics.report4Stats() ;
//        ReportsHandler reportsHandler = new ReportsHandler() ;
//        reportsHandler.generateReport4();
    }




    public void tearDown() throws Exception {

        System.out.println("fuck you ");
    }


}