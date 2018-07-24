package Jowil;

import com.lowagie.text.DocumentException;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;

public class fxChartawy extends Application {

    String  reportsPath="E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources\\reports\\";
    final static String austria = "A";
    final static String brazil = "B";
    final static String france = "C";
    final static String italy = "D";
    final static String usa = "F";


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
        grades.add("F") ;grades.add("D") ;grades.add("C") ; grades.add("B");grades.add("A");grades.add("W"); grades.add("h") ; grades.add("C+");
        Statistics.setGrades(grades);

        ArrayList<Double> gradeLowerRange  =new ArrayList<Double>();
        gradeLowerRange.add(0.0);
        for(int i=0;i<7;i++)
            gradeLowerRange.add(0.3 + 0.1 * i);
        gradeLowerRange.add(1.0);
        Statistics.setGradesLowerRange(gradeLowerRange);
    }

    @Override public void start(Stage stage) throws IOException, CSVHandler.EmptyAnswerKeyException, CSVHandler.EmptyCSVException, CSVHandler.InvalidFormNumberException, DocumentException {
        CSVHandler.setFilePath(".\\src\\test\\TestCSVs\\testMulti1.csv");
        CSVHandler.loadAnswerKeys(".\\src\\test\\TestCSVs\\answerKeys1.csv");
        ArrayList<Integer> studentData= new ArrayList<Integer>();
        studentData.add(CSVHandler.STUDENTID);
        studentData.add(CSVHandler.STUDENTNAME);
        studentData.add(CSVHandler.IGNORE);
        studentData.add(CSVHandler.STUDENTFORM);
        boolean isHeaders=CSVHandler.processHeaders();
        if(isHeaders)
            Main.updateQuestionHeaders(CSVHandler.getDetectedQHeaders());
        Jowil.CSVHandler.setFormsCount(2);
        Jowil.CSVHandler.loadCsv( isHeaders, false);
        Jowil.Statistics.setQuestionsChoices(generateTestAllQuestionsChoices());
        ArrayList<ArrayList<Double>> questionWeights=new ArrayList<ArrayList<Double>>();
        questionWeights.add(generateQuestionsWeights(8));
        questionWeights.add(generateQuestionsWeights(8));
        Jowil.Statistics.setQuestionWeights(questionWeights);
        fillGradeRanges() ;
        Statistics.setStudentIdentifier(Statistics.getStudentIDs()) ;
//        Statistics.printBasicInfo();
        Statistics.initFormsScores();
        Jowil.Statistics.init();
        Jowil.Statistics.printBasicInfo();
        Jowil.Statistics.printCalculations();
        ReportsHandler reportsHandler = new ReportsHandler() ;
        reportsHandler.generateReport1(stage);
         }

    public static void main(String[] args) {
        launch(args);
    }
}