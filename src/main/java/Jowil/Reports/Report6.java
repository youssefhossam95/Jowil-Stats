package Jowil.Reports;

import Jowil.Reports.Utils.CsvUtils;
import Jowil.Reports.Utils.TxtUtils;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Report6 extends Report {
    String report6ImgFullPath  ;
    ArrayList<ArrayList<ArrayList<String>>>  formsStatsTables ;


    public Report6(){
        workSpacePath = reportsPath + "report6\\" ;
        templatePath = workSpacePath + "report6Template.html";
        outputFileName = "Report6" ;
        pdfHtmlPath = workSpacePath+outputFileName+".html" ;
        report6ImgFullPath = "file://"+System.getProperty("user.dir") + workSpacePath + "DifficulityHistogram.png" ;
    }


    /**
     * this function is called before the constructor
     * get the statistical calculations for statistics calss
     * generate the bar chart of the report
     */
    @Override
    public void init(){
        formsStatsTables = Statistics.report6Stats() ;
        for(int formIndex = 0 ; formIndex < formsStatsTables.size() ; formIndex++) {
            ArrayList<ArrayList<String>> statsTable = formsStatsTables.get(formIndex);

            ArrayList<ArrayList<String>> statsTableTrans = Utils.transposeStringList(statsTable);

            double[] freq = statsTableTrans.get(6).stream().mapToDouble(d -> Double.valueOf(d)).toArray();

            final int formIndexForGraph = formIndex+1 ;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        generateReport6Chart(statsTableTrans.get(0), freq , formIndexForGraph);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * generate a bar chart for reprot 6 that represent the group hardness, and stores it in image.
     * @param groupNames ArrayList that contians all group Names i.e. MCQ , TF ...
     * @param hardness the hardness of each group
     * @throws IOException if it couldn't store the img in the spicified path
     */
    private void generateReport6Chart( ArrayList<String> groupNames , double[] hardness , int formIndex) throws IOException {
        Stage stage = new Stage() ;
        stage.setTitle("Student Grades");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Group Hardness");
        xAxis.setLabel("Group");
        yAxis.setLabel("Hardness");

        XYChart.Series series1 = new XYChart.Series();
        bc.setLegendVisible(false);
        double max = 0  ;
        int maxIndex = 0 ;
        for(int groupIndex = 0 ;  groupIndex <groupNames.size() ; groupIndex++) {

            double groupHardness  =  hardness[groupIndex];
            if(groupHardness>max) {
                max = groupHardness;
                maxIndex = groupIndex ;
            }
            series1.getData().add(new XYChart.Data(groupNames.get(groupIndex), groupHardness));

        }
        bc.getData().add(series1);

        for(int groupIndex = 0 ;  groupIndex<groupNames.size() ; groupIndex++) {
            String addedClass = "normal" ;
            if(groupIndex == maxIndex)
                addedClass = "hardest" ;
            Node n = bc.lookup(".data"+groupIndex+".chart-bar");
            n.getStyleClass().add(addedClass);
        }


        bc.setAnimated(false);
        Scene scene  = new Scene(bc,800,600);

        scene.getStylesheets().add("reports/report6/style.css");
        bc.applyCss();
        bc.layout();
        stage.setScene(scene);

        WritableImage snapShot = bc.snapshot(new SnapshotParameters() , null);
        ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png",
                new File(workSpacePath+"DifficulityHistogram"+formIndex+".png"));
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


        String templateBodyHtml = doc.select("div#template").html() ;
        final String pageBreakHtml= "<div class='page-break'></div>\n" ;


        for(int formIndex = 0 ; formIndex < formsStatsTables.size() ; formIndex++) {

            // append the template again for the new form
            if(formIndex>0) {
                doc.select("img").last().after(pageBreakHtml);
                doc.select("div.page-break").last().after(templateBodyHtml);
                doc.select("div.divTitle").addClass("second-page-header");
                doc.select("div.divTitle").last().text("Form " + (formIndex+1) + " Group Detils Report");
            } else if(Statistics.getNumberOfForms()>1)
                doc.select("div.divTitle").last().text("Form "+(formIndex+1) + " Group Detils Report");

            ArrayList<ArrayList<String>> statsTable = formsStatsTables.get(formIndex);
            String tableRowsHtml = createRowsHtml(statsTable, ";grayRow", "tg-l711");

            doc.select("tr.headerRow").last().after(tableRowsHtml);

            // check if img need to be put in a new page
            if (statsTable.size() > MAX_NUMBER_OF_1PAGE_ROWS)
                doc.select("img").addClass("new-page-img");

            doc.select("img").last().attr("src" , "DifficulityHistogram"+(formIndex+1)+".png");
        }
        return doc  ;
    }

    @Override
    public void generateHtmlReport() throws IOException {
        Document doc = generatePdfHtml() ;
        doc.select("div#footer").remove() ;
        doc.select("img").attr("src" , report6ImgFullPath);
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {

        Document doc = generatePdfHtml() ;
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath,outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");

    }
    @Override
    public void generateTxtReport() {

        int CHP = 3 ;
        ArrayList<String>tableHeaders = new ArrayList<>();
        tableHeaders.add("Section Name") ; tableHeaders.add("Hardest Question") ;
        tableHeaders.add("Easiest Question") ; tableHeaders.add("Average Correct ") ;
        tableHeaders.add("Percentage Of Questions"); tableHeaders.add("Average Point");
        tableHeaders.add("Difficulity");

        ArrayList<String> secondHeaders = new ArrayList<>();
        secondHeaders.add("");  secondHeaders.add("") ; secondHeaders.add("");
        secondHeaders.add("Percentage") ; secondHeaders.add("with Distractors");
        secondHeaders.add("Biserial") ; secondHeaders.add("(0-10)") ;

        String outputTxt = "" ;

        ArrayList<ArrayList<String>> temp = new ArrayList<>( );
        temp.add(tableHeaders) ;
        int pageWidth = TxtUtils.calcTableWidth(temp , CHP) ;
        for(int formIndex=  0 ; formIndex<formsStatsTables.size() ; formIndex++) {

            ArrayList<ArrayList<String>> tableWithHeaders = Utils.cloneTable(formsStatsTables.get(formIndex));
            tableWithHeaders.add(0, tableHeaders);
            tableWithHeaders.add(1, secondHeaders);



            String reportTitle = "Section Details Report"  ;
            if(Statistics.getNumberOfForms()>1)
                reportTitle = "Form "+(formIndex+1) + " " + reportTitle ;

            if(formIndex > 0 )
                outputTxt+= TxtUtils.newLine+Utils.generatePattern("*" , pageWidth)+TxtUtils.newLine;

            String txtTitle = TxtUtils.generateTitleLine(reportTitle,
                    TxtUtils.calcTableWidth(tableWithHeaders, CHP), 2);

            String txtTable = TxtUtils.generateTxtTableAlignCenter2(tableWithHeaders, "", CHP, false);

             outputTxt+= txtTitle + txtTable;
        }
        TxtUtils.writeTxtToFile(outputTxt , outputFormatsFolderPaths[ReportsHandler.TXT]+outputFileName+".txt");
    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml() ;

        styleTitlePrintable(doc);
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath,outputFormatsFolderPaths[ReportsHandler.PRINTABLE_PDF]+outputFileName+".pdf");
    }

    @Override
    public void generateCsvReport() throws IOException {

    }

}
