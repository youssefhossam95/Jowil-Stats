package Jowil;

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
    public void testAll() throws IOException {
        ArrayList<Integer> studentData= new ArrayList<Integer>();
        studentData.add(Main.STUDENTID);
        studentData.add(Main.STUDENTNAME);
        Jowil.Main.loadCsv(".\\src\\test\\TestCSVs\\testAll2.csv",studentData, true, true);
        Jowil.Statistics.setQuestionsChoices(generateTestAllQuestionsChoices());
        Jowil.Statistics.setQuestionWeights(generateQuestionsWeights(8));
        Jowil.Statistics.init();
        Jowil.Statistics.printBasicInfo();
        Jowil.Statistics.printCalculations();
    }
    public void tearDown() throws Exception {
    }
}