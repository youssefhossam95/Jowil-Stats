package Jowil;

import Jowil.Reports.*;
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

public class ReportChartsTest extends Application {


    @Override public void start(Stage stage) throws IOException, CSVHandler.EmptyAnswerKeyException, CSVHandler.EmptyCSVException, CSVHandler.InvalidFormNumberException, DocumentException, CSVHandler.IllFormedCSVException {
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

        Statistics.initFormsScores();
        Jowil.Statistics.init();
        Jowil.Statistics.printBasicInfo();
        Jowil.Statistics.printCalculations();

        ArrayList<Report> reports = new ArrayList<>();
        reports.add(new Report1()) ;
        Report.initOutputFolderPaths("E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources");

//        reports.add(new Report2()) ;
//        reports.add(new Report3());
//        reports.add(new Report4()) ;
//        reports.add(new Report5()) ;
        ArrayList<Integer> formats = new ArrayList<>() ;
        formats.add(ReportsHandler.HTML) ;
        formats.add(ReportsHandler.PDF);

        ReportsHandler reportsHandler = new ReportsHandler();
        reportsHandler.generateReports(reports , formats);

    }

    public static void main(String[] args) {
        launch(args);
    }
}