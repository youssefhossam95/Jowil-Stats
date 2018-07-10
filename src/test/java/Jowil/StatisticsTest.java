package Jowil;

import jdk.nashorn.internal.ir.annotations.Ignore;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticsTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
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
        for(int i=0;i<8;i++)
            out.add(1.0);
        return out;

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


    public void testMulti1() throws IOException, CSVHandler.EmptyAnswerKeyException, CSVHandler.InvalidFormNumberException, CSVHandler.EmptyCSVException {
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
        Jowil.Statistics.init();
        Jowil.Statistics.printBasicInfo();
        Jowil.Statistics.printCalculations();
    }




    public void tearDown() throws Exception {
    }


}