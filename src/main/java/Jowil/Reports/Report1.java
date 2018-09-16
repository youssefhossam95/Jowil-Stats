package Jowil.Reports;

import Jowil.Reports.Utils.CsvUtils;
import Jowil.Reports.Utils.TxtUtils;
import Jowil.Reports.Utils.WordUtils;
import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import javafx.application.Platform;
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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;


import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;


import static org.apache.commons.math3.stat.StatUtils.sum;

public class Report1 extends Report{

    String report1ImgFullPath  ;
    ArrayList<ArrayList<String>> statsTable ;

    volatile boolean chartsReady = false ;

    public Report1(){
        reportTitle = "Grades Distribution Report" ;
        workSpacePath = reportsPath + "report1\\" ;
        templatePath = workSpacePath + "report1Template.html";
        outputFileName = "Report1" ;
        pdfHtmlPath = workSpacePath+outputFileName+".html" ;
        report1ImgFullPath = System.getProperty("user.dir") + workSpacePath.replace("." , "") + "GradesDistributionHistogram.png" ;
        while (!chartsReady);
    }


    /**
     * this function is called before the constructor
     * get the statistical calculations for statistics calss
     * generate the bar chart of the report
     */
    @Override
    public void init(){
        statsTable = Statistics.report1Stats() ;
        ArrayList<ArrayList<String>> statsTableTrans = Utils.transposeStringList(statsTable);

        double[] freq = statsTableTrans.get(3).stream().mapToDouble(d -> Double.valueOf(d)).toArray() ;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    generateReport1Chart(statsTableTrans.get(0) , freq );
                    chartsReady = true ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * generate a bar chart for reprot 1 that represent the grade distribution, and stores it in image.
     * @param grades ArrayList that contians all the grades of studest i.e. A+ , B ...
     * @param numberOfStudents the number of students that got the corresponding grade
     * @throws IOException if it couldn't store the img in the spicified path
     */
    public BarChart<String, Number> generateReport1Chart( ArrayList<String> grades , double[] numberOfStudents) throws IOException {
        Stage stage = new Stage() ;
        stage.setTitle("Student Grades");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Grades Distribution");
        xAxis.setLabel("Grade");
        yAxis.setLabel("Number of Students");

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
            Node n = bc.lookup(".data"+gradeIndex+".chart-bar");
            n.getStyleClass().add(addedClass);
        }


        bc.setAnimated(false);
        Scene scene  = new Scene(bc,1000,700);


        scene.getStylesheets().add("reports/report1/style.css");
        bc.applyCss();
        bc.layout();
        stage.setScene(scene);

        WritableImage snapShot = bc.snapshot(new SnapshotParameters() , null);
        ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", new File(workSpacePath+"GradesDistributionHistogram.png"));
        return bc ;
    }

    /**
     * generate the html document for the report. This html is then manipulated in different ways to be used in many formats
     * @return Document of the generated html
     * @throws IOException if it couldn't find the html template that it start with
     */

    private Document generatePdfHtml() throws IOException {
        final int MAX_NUMBER_OF_1PAGE_ROWS = 7;
        File file = new File(templatePath);
        Document doc = Jsoup.parse(file, "UTF-8");

        updateTemplateDate(doc); // updates the date of the footer to the current date

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
        doc.select("img").attr("src" ,"file://"+ report1ImgFullPath);
        doc.select("img").attr("width" , "60%") ;
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {

        Document doc = generatePdfHtml() ;
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath,outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");

    }
    private ArrayList<ArrayList<String>> getTableWithHeaders (){
        ArrayList<String>tableHeaders = new ArrayList<>();
        tableHeaders.add("Grade") ; tableHeaders.add("Percent Score") ;
        tableHeaders.add("Raw Score") ; tableHeaders.add("Frequency") ; tableHeaders.add("Percentage");

        ArrayList<ArrayList<String>>tableWithHeaders = Utils.cloneTable(statsTable) ;
        tableWithHeaders.add(0 , tableHeaders) ;
        return tableWithHeaders ;
    }
    @Override
    public void generateTxtReport() {

        int cellHorizontalPadding = 3 ;
        ArrayList<ArrayList<String>> tableWithHeaders = getTableWithHeaders() ;
        String txtTitle = TxtUtils.generateTitleLine(reportTitle,
                TxtUtils.calcTableWidth(tableWithHeaders,cellHorizontalPadding),2) ;
        String txtTable = TxtUtils.generateTxtTableAlignCenter(tableWithHeaders , "" , cellHorizontalPadding , false) ;

        String outputTxt =TxtUtils.newLine+txtTitle + txtTable ;
        System.out.println(outputTxt);

        TxtUtils.writeTxtToFile(outputTxt , outputFormatsFolderPaths[ReportsHandler.TXT]+outputFileName+".txt");
    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml() ;
        styleTitlePrintable(doc);
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath,outputFormatsFolderPaths[ReportsHandler.PRINTABLE_PDF]+outputFileName+".pdf");
    }


    private String generateCharSeparatedValuesString(char separator){
        String outputCsv = "";
        int pageWidth = CsvUtils.calcTableWidth(statsTable);
        String titleCsv = CsvUtils.generateTitleLine(reportTitle,separator ,pageWidth , 2) ;
        String tableCsv = CsvUtils.generateTable(getTableWithHeaders() , separator) ;

        outputCsv = titleCsv + tableCsv ;

        return outputCsv ;
    }
    @Override
    public void generateCsvReport() throws IOException {

        String outputCsv = generateCharSeparatedValuesString(',') ;
        CsvUtils.writeCsvToFile(outputCsv , outputFormatsFolderPaths[ReportsHandler.CSV]+outputFileName+".csv");
    }

    @Override
    public void generateTsvReprot() {

        String outputCsv = generateCharSeparatedValuesString('\t') ;
        System.out.println(outputCsv);
        CsvUtils.writeCsvToFile(outputCsv , outputFormatsFolderPaths[ReportsHandler.TSV]+outputFileName+".tsv");
    }


    @Override
    public void generateWordReport() throws IOException, InvalidFormatException {
        XWPFDocument document = new XWPFDocument();
        WordUtils.addTitle(document , reportTitle );


        WordUtils.createWordFooter(document);
        WordUtils.addTable(document, getTableWithHeaders());

        WordUtils.addImage(document , report1ImgFullPath);

        WordUtils.writeWordDocument(document , outputFormatsFolderPaths[ReportsHandler.WORD]+outputFileName+".docx");

    }

}
