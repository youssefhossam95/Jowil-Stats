package Jowil.Reports;

import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Report1 extends Report{

    String report1ImgFullPath  ;
    public Report1(){
        workSpacePath = reportsPath + "report1\\" ;
        templatePath = workSpacePath + "report1Template.html";
        outputFileName = "Report1" ;
        pdfHtmlPath = workSpacePath+outputFileName+".html" ;
        report1ImgFullPath = "file://"+System.getProperty("user.dir") + workSpacePath + "GradesDistributionHistogram.png" ;
    }


    private void generateReport1Chart(Stage stage , ArrayList<String> grades , double[] numberOfStudents) throws IOException {
        stage.setTitle("Student Grades");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Grades Distribution");
        xAxis.setLabel("Grade");
        yAxis.setLabel("Number Of students");

        XYChart.Series series1 = new XYChart.Series();
        bc.setLegendVisible(false);
        int maxIndex = 0 ;
        int max = 0  ;
        for(int gradeIndex = 0 ;  gradeIndex<grades.size() ; gradeIndex++) {

            int freq = (int) numberOfStudents[gradeIndex];
            if(freq>max) {
                max = freq;
                maxIndex = gradeIndex ;
            }
            series1.getData().add(new XYChart.Data(grades.get(gradeIndex), freq));

        }
        bc.getData().add(series1);

        for(int gradeIndex = 0 ;  gradeIndex<grades.size() ; gradeIndex++) {
            String addedClass = "normal" ;
//            if(gradeIndex == maxIndex)
//                addedClass = "largest" ;
            Node n = bc.lookup(".data"+gradeIndex+".chart-bar");
            n.getStyleClass().add(addedClass);
        }


        bc.setAnimated(false);
        Scene scene  = new Scene(bc,1000,700);


        scene.getStylesheets().add("reports/report1/style.css");
        bc.applyCss();
        bc.layout();
        stage.setScene(scene);
//        stage.show();

        WritableImage snapShot = bc.snapshot(new SnapshotParameters() , null);
        ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", new File(workSpacePath+"GradesDistributionHistogram.png"));
    }

    private Document generatePdfHtml() throws IOException {
        final int MAX_NUMBER_OF_1PAGE_ROWS = 7;
        File file = new File(templatePath);
        Document doc = Jsoup.parse(file, "UTF-8");

        updateTemplateDate(doc); // updates the date of the footer to the current date


        Stage stage = new Stage() ;

        ArrayList<ArrayList<String>> statsTable = Statistics.report1Stats() ;

        ArrayList<ArrayList<String>> statsTableTrans = Utils.transposeStringList(statsTable);

        double[] freq = statsTableTrans.get(3).stream().mapToDouble(d -> Double.valueOf(d)).toArray() ;

        generateReport1Chart(stage , statsTableTrans.get(0) , freq );

        String tableRowsHtml = createRowsHtml(statsTable , ";grayRow" , "tg-l711") ;

        doc.select("tr.headerRow").after(tableRowsHtml) ;

        // check if img need to be put in a new page
        if(statsTable.size()>MAX_NUMBER_OF_1PAGE_ROWS)
            doc.select("img").addClass("new-page-img");

        return doc  ;
    }

    @Override
    public void generateHtmlReport() throws IOException {
        Document doc = generatePdfHtml() ;
        doc.select("div#footer").remove() ;
//        String report1ImgPath = "file://"+workSpacePath+"GradesDistributionHistogram.png";
        doc.select("img").attr("src" , report1ImgFullPath);
        writeHtmlFile(outputHtmlFolderPath+outputFileName+".html" , doc);
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {

        Document doc = generatePdfHtml() ;
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath,outputPdfFolderPath+outputFileName+".pdf");

    }

    @Override
    public void generateTxtReport() {



    }
}
