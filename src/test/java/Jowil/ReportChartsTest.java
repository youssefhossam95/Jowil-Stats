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


    @Override public void start(Stage stage) throws IOException, CSVHandler.InConsistentAnswerKeyException, CSVHandler.EmptyCSVException, CSVHandler.InvalidFormNumberException, DocumentException, CSVHandler.IllFormedCSVException {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Treads");
                try {
                String inputFilesFolderPath = ".\\src\\test\\ReportTestCSVs\\" ;


                CSVHandler.setResponsesFilePath(inputFilesFolderPath+"StudentAnswers.csv");
                CSVHandler.setFormColIndex(3);
                CSVHandler.setIdentifierColStartIndex(0);
                CSVHandler.setIdentifierColEndIndex(1);
                boolean isHeaders=CSVHandler.processHeaders(false);
                CSVHandler.loadAnswerKeys(inputFilesFolderPath+"AnswerKeys.csv",true);
                CSVHandler.processHeaders(true);
                Jowil.CSVHandler.loadCsv(isHeaders);


                TestUtils.setQuestionChoicesFromFile(inputFilesFolderPath+"QuestionChoices.csv");
                TestUtils.setQuestionsWeights(Statistics.getQuestionsChoices().size() , Statistics.getNumberOfForms());
                TestUtils.fillGradeRanges() ;

                Statistics.initFormsScores();
                Jowil.Statistics.init();
                Jowil.Statistics.printBasicInfo();
                Jowil.Statistics.printCalculations();

                ArrayList<Report> reports = new ArrayList<>();
                Report.initOutputFolderPaths(".");
//
                reports.add(new Report1()) ;
//                reports.add(new Report2()) ;
//                reports.add(new Report3());
//                reports.add(new Report4()) ;
//                reports.add(new Report5()) ;
//                reports.add(new Report6()) ;
                reports.add(new Report7()) ;
//                reports.add(new Report8()) ;
                ArrayList<Integer> formats = new ArrayList<>() ;
                formats.add(ReportsHandler.HTML) ;
                formats.add(ReportsHandler.PDF);
                formats.add(ReportsHandler.PRINTABLE_PDF) ;
                formats.add(ReportsHandler.TXT) ;
                formats.add(ReportsHandler.CSV);
                formats.add(ReportsHandler.TSV) ;
                formats.add(ReportsHandler.WORD) ;

                ReportsHandler reportsHandler = new ReportsHandler(true);

                reportsHandler.generateReports(reports , formats);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        Thread thread =new Thread(runnable);

        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}