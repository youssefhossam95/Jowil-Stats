package Jowil.Reports;

import Jowil.CSVHandler;
import Jowil.Group;
import Jowil.Reports.Utils.CsvUtils;
import Jowil.Reports.Utils.TxtUtils;
import Jowil.Statistics;
import Jowil.Utils;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.jsoup.nodes.Document;
import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Report8 extends Report {

    ArrayList<ArrayList<ArrayList<Double>>> formsData ;
    volatile boolean chartsReady = false ;
    String imagesFullPath ;

    public Report8(){
        workSpacePath = reportsPath + "report8\\" ;
        templatePath = workSpacePath + "report8Template.html";
        outputFileName = "Report8" ;
        pdfHtmlPath = workSpacePath+outputFileName+".html" ;
        imagesFullPath = "file://"+System.getProperty("user.dir") + workSpacePath  ;
        System.out.println("in Constructor");
        while (!chartsReady) ;
    }


    public Document generatePdfHtml () throws IOException {
        File file = new File(templatePath);
        Document doc = Jsoup.parse(file, "UTF-8");
        String templateBodyHtml = doc.select("div#template").html() ;
        String wrapperHtml = doc.select("div.wrapper").outerHtml() ;
        final String pageBreakHtml= "<div class='page-break'></div>\n" ;
        final String spaceHtml = "<div class='give-me-space'></div>\n" ;

        ArrayList<Group> groups = CSVHandler.getDetectedGroups() ;

        for (int formIndex= 0 ; formIndex < formsData.size() ; formIndex++ ) {
            ArrayList<ArrayList<Double>> formGraphsData = formsData.get(formIndex);
            if(formIndex>0) {
                doc.select("div.wrapper").last().after(pageBreakHtml);
                doc.select("div.page-break").last().after(templateBodyHtml) ;
                doc.select("div.divTitle").last().text("Form " +(1+formIndex) + " Hardness Graduality Report") ;
            }
            else if( formsData.size()>1)
                doc.select("div.divTitle").last().text("Form " +(1+formIndex) + " Hardness Graduality Report") ;

            for ( int graphIndex = 0; graphIndex < formGraphsData.size() ; graphIndex++) {
               if(graphIndex > 0) {
                   if(graphIndex%2==0) {
                       doc.select("div.wrapper").last().after(pageBreakHtml);
                       doc.select("div.page-break").last().after(spaceHtml);
                       doc.select("div.give-me-space").last().after(wrapperHtml) ;
                   }
                   else {
                       doc.select("div.wrapper").last().after(wrapperHtml);
                   }
                   doc.select("span.group-title").last().text(groups.get(graphIndex-1).getCleanedName()) ;
               }
               ArrayList<Double> graphData = formGraphsData.get(graphIndex);
               graphData = new ArrayList<>(graphData.subList(graphData.size() - 3, graphData.size()));
               Elements tableCells = doc.select("table.t").last().select("td.right-td");
               for (int i = 0; i < tableCells.size(); i++) {
                   String cellText = Statistics.formatNumber(Statistics.format, graphData.get(i));
                   tableCells.get(i).text(cellText);
               }
               String imgName = "GradualityChart" + formIndex + graphIndex + ".png";
               doc.select("img").last().attr("src", imgName);
           }
        }
        return doc ;
    }

    @Override
    public void generateHtmlReport() throws IOException {
        Document doc = generatePdfHtml();
        doc.select("div#footer").remove() ;
        doc.select("div.give-me-space").remove() ;
        doc.select("div.line").attr("style" , "margin-bottom:60px");
        changeImgPath(doc, imagesFullPath);
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);

    }


    protected void changeImgPath (Document doc , String imagesFullPath) {
       Elements images =  doc.select("img") ;
        for (Element image: images) {
            String imgName = image.attr("src");
            image.attr("src" , imagesFullPath + imgName) ;
        }
    }
    private void generateReport8Chart () throws IOException {

        int maxIndex = 0 ;
        int max = 0  ;
        for(int formIndex = 0 ; formIndex < formsData.size() ; formIndex ++) {
            ArrayList<ArrayList<Double>> formGraphsData = formsData.get(formIndex);
            for (int graphIndex = 0; graphIndex < formGraphsData.size(); graphIndex++) {
                ArrayList<Double> graphData = formGraphsData.get(graphIndex);
                Stage stage = new Stage() ;

                stage.setTitle("Hardness Graduality");

                final NumberAxis xAxis = new NumberAxis();
                final NumberAxis yAxis = new NumberAxis();
                final LineChart<Number,Number> lc =
                        new LineChart<>(xAxis,yAxis);

//                lc.setTitle("Hardness Graduality");

                xAxis.setLabel("Question Number");
                yAxis.setLabel("Hardness");

                XYChart.Series series1 = new XYChart.Series();
                lc.setLegendVisible(false);
                lc.setCreateSymbols(false);

                for (int questionIndex = 0; questionIndex < graphData.size() - 3; questionIndex++) {
                    series1.getData().add(new XYChart.Data(questionIndex + 1, graphData.get(questionIndex)));
                }
                lc.getData().add(series1);

                lc.setPrefSize(800 , 500);
                lc.setAnimated(false);
                Scene scene = new Scene(lc);

                scene.getStylesheets().add("reports/report8/style.css");
                lc.applyCss();
                lc.layout();
                stage.setScene(scene);
                WritableImage snapShot = lc.snapshot(new SnapshotParameters(), null);
                String imgName = workSpacePath + "GradualityChart" + formIndex + graphIndex + ".png";
                ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", new File(imgName));
                System.out.println("hello");
            }
        }
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {
        Document doc  = generatePdfHtml();
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");
    }

    public ArrayList<ArrayList<String>> getTableWithHeaders (ArrayList<Double> data) {
        ArrayList<ArrayList<String>> table = new ArrayList<>( );
        ArrayList<String> tableRow = new ArrayList<>( );

        tableRow.add("slope") ; tableRow.add(Statistics.formatNumber(Statistics.format , data.get(0))) ;
        table.add(tableRow );
        tableRow = new ArrayList<>( );
        tableRow.add("error") ; tableRow.add(Statistics.formatNumber(Statistics.format , data.get(1))) ;
        table.add(tableRow );
        tableRow = new ArrayList<>( );
        tableRow.add("Jowil") ; tableRow.add(Statistics.formatNumber(Statistics.format , data.get(2))) ;
        table.add(tableRow) ;

        return table ;
    }

    private ArrayList<String> getTableTitles () {
        ArrayList<String> titles = new ArrayList<>( );
        ArrayList<Group> groups = CSVHandler.getDetectedGroups() ;
        titles.add("all Exam") ;
        for (Group g : groups)
            titles.add(g.getCleanedName() ) ;

        return titles ;
    }
    @Override
    public void generateTxtReport() {

        int CHP  = 3  ;
        ArrayList<String> tablesTitles = getTableTitles();
        String outputTxt=  "" ;
        ArrayList<Double> tempTable = formsData.get(0).get(0) ;
        int pageWidth = TxtUtils.calcTableWidth(getTableWithHeaders(
                new ArrayList<>(tempTable.subList(tempTable.size()-3 , tempTable.size()))),CHP) ;
        for ( int formIndex = 0 ; formIndex < formsData.size() ; formIndex++ ) {
            String form  = "" ;
            if(formsData.size()>1)
                form = "Form " + (formIndex+1) ;
            if(formIndex > 0)
                outputTxt+= Utils.generatePattern("*" , pageWidth) + TxtUtils.newLine ;
            String txtTitle = TxtUtils.generateTitleLine(form + " Hardness Graduality Reprot" , pageWidth , 3) ;
            outputTxt += txtTitle ;

            ArrayList<ArrayList<Double>> formGraphsData = formsData.get(formIndex);
            ArrayList<String> txtTables = new ArrayList<>( );
            for (int graphIndex = 0; graphIndex < formGraphsData.size() ; graphIndex ++ ) {
                ArrayList<Double> graphData = formGraphsData.get(graphIndex);
                ArrayList<ArrayList<String>> table = getTableWithHeaders(new ArrayList<>
                        (graphData.subList(graphData.size() - 3, graphData.size())));
                txtTables.add( TxtUtils.generateTxtTableAlignLR(table, tablesTitles.get(graphIndex), CHP));
            }
            outputTxt += TxtUtils.stackTablesV(txtTables , 2) ;
        }

        System.out.println(outputTxt);
        TxtUtils.writeTxtToFile(outputTxt , outputFormatsFolderPaths[ReportsHandler.TXT]+outputFileName+".txt");
    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml() ;

        styleTitlePrintable(doc);
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath , outputFormatsFolderPaths[ReportsHandler.PRINTABLE_PDF]+outputFileName+".pdf");
    }

    @Override
    public void generateCsvReport() throws IOException {

        char separator = ',' ;

        ArrayList<String> tablesTitles = getTableTitles();
        String outputCsv=  "" ;
        ArrayList<Double> tempTable = formsData.get(0).get(0) ;
        int pageWidth = CsvUtils.calcTableWidth(getTableWithHeaders(
                new ArrayList<>(tempTable.subList(tempTable.size()-3 , tempTable.size())))) ;
        for ( int formIndex = 0 ; formIndex < formsData.size() ; formIndex++ ) {
            String form  = "" ;
            if(formsData.size()>1)
                form = "Form " + (formIndex+1) ;
            String txtTitle = CsvUtils.generateTitleLine(form + " Hardness Graduality Reprot" , separator,  pageWidth , 3) ;
            outputCsv += txtTitle ;

            ArrayList<ArrayList<Double>> formGraphsData = formsData.get(formIndex);
            ArrayList<String> txtTables = new ArrayList<>( );
            for (int graphIndex = 0; graphIndex < formGraphsData.size() ; graphIndex ++ ) {
                ArrayList<Double> graphData = formGraphsData.get(graphIndex);
                ArrayList<ArrayList<String>> table = getTableWithHeaders(new ArrayList<>
                        (graphData.subList(graphData.size() - 3, graphData.size())));
                txtTables.add( CsvUtils.generateTable(table, ',' ,tablesTitles.get(graphIndex)));
            }
            outputCsv += CsvUtils.stackTablesV(txtTables , 2) ;
        }

        System.out.println(outputCsv);
        CsvUtils.writeCsvToFile(outputCsv , outputFormatsFolderPaths[ReportsHandler.CSV]+outputFileName+".csv");

    }

    @Override
    public void generateTsvReprot() {

    }

    @Override
    public void init() {

        System.out.println("in init");
        formsData = Statistics.report8Stats();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("fuck this shit am out");
                    generateReport8Chart();
                    chartsReady = true ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
