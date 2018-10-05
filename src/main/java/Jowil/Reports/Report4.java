package Jowil.Reports;

import Jowil.Reports.Utils.CsvUtils;
import Jowil.Reports.Utils.TxtUtils;
import Jowil.Reports.Utils.WordUtils;
import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class Report4 extends Report{


    private ArrayList<ArrayList<String>> statsTable ;
    volatile boolean arabicTextReady = false ;

    public Report4(){
        reportTitle = "Students Grades Report" ;
        workSpacePath = reportsPath + "report4\\" ;
        templatePath = workSpacePath + "report4Template.html";
        pdfHtmlPath = workSpacePath + outputFileName + ".html";

    }


    protected void handleArabicPdf(ArrayList<ArrayList<String>> table ) throws IOException {
        for (String grade : Statistics.getGrades()) {
            if (!grade.matches("\\w+")) { // check if any grade is arabic
                generateTextImgs();
                while (arabicTextReady) ; // wait for the imgs to be created
                for (int i = 0; i < table.size(); i++) {  // replace each grade in the table with it's img
                    ArrayList<String> tableRow = table.get(i);
                    String tableGrade = tableRow.get(1).replace(" " , "%20");;
                    tableRow.set(1, "<img class='text-img'  src='" + tableGrade + ".png'> </img>");
                }
                break;
            }
        }
    }

    public void generateTextImgs () throws IOException {
        ArrayList<String> grades = Statistics.getGrades();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                Stage stage = new Stage() ;
                Pane pane = new Pane() ;
                pane.setStyle("-fx-background-color:white");
                Scene scene  = new Scene(pane, Color.WHITE);
                stage.setScene(scene);
                Label label = new Label("man");
                pane.getChildren().add(label);
                for (int i =0 ; i < grades.size(); i ++) {
                    Label label2 = new Label(grades.get(i));
                    label2.setStyle("-fx-font-weight: bold");
                    pane.getChildren().set(0, label2);

//        scene.getStylesheets().add("reports/report1/style.css");

                    WritableImage snapShot = label2.snapshot(new SnapshotParameters(), null);
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", new File(workSpacePath + grades.get(i)+".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                arabicTextReady = true ;
            }
        });


    }
    private Document generatePdfHtml(boolean pdf) throws IOException {

        final int NUMBER_OF_ROWS_BALNK_PAGE = 22 ;
        final int NUMBER_OF_ROWS_FIRST_PAGE = NUMBER_OF_ROWS_BALNK_PAGE - 4  ;

        Format format = new DecimalFormat("0.#");
        final String dataCellCommonClass = "tg-l711" ;

        File file = new File(templatePath);

        Document doc =  Jsoup.parse(file , "UTF-8") ;

        updateTemplateFooter(doc); // updates the date of the footer to the current date

        String headerHtml = doc.select("tr.headerRow").outerHtml();

        doc.select("th#identifier").last().text(Statistics.getIdentifierName()) ;

        ArrayList<ArrayList<String>> tempStatsTable = Utils.cloneTable(statsTable) ;

        // separate the maean row (last row)
        ArrayList<ArrayList<String>> meanRow =new ArrayList<ArrayList<String>> ( tempStatsTable.subList(tempStatsTable.size()-1 , tempStatsTable.size()));

        // adding colspan attribute to first element in each row
        for(ArrayList<String> tableRow:tempStatsTable) {
            tableRow.set(0,tableRow.get(0) + "#colspan='2'" ) ;
            String barWidth = tableRow.get(tableRow.size() - 1);
            if(Double.valueOf(barWidth.replace("%" ,"")) > 100)
                barWidth = "100%" ;
            String passingPercent = ((DecimalFormat) format).format(Statistics.getPassingPercent()*100) +"%";
            String divHtml = "<div class='emptyBar'> \n"+
                                "<div class='greenBar' style='width:" + barWidth + "'> </div>\n" +
                                "<div class='benchmark' style='width:" + passingPercent + "'> </div>"+
                             "</div>";
            tableRow.add(divHtml+";bar") ;
        }

        int startIndex = 0 ;
        int endIndex = (int)Utils.getNumberWithinLimits(tempStatsTable.size() , 0 , NUMBER_OF_ROWS_FIRST_PAGE) ;

        if(pdf) {
            handleArabicPdf(tempStatsTable);
        }
        do  {
            ArrayList<ArrayList<String>> pageTable ;
            if(endIndex == tempStatsTable.size()) {
                pageTable = new ArrayList<ArrayList<String>>(tempStatsTable.subList(startIndex, endIndex - 1));
                String rowsHtml = createRowsHtml(pageTable , "grayRow" ,dataCellCommonClass );
                doc.select("tr.headerRow").last().after(rowsHtml) ;
            }
            else {
                pageTable = new ArrayList<ArrayList<String>>(tempStatsTable.subList(startIndex, endIndex));
                String rowsHtml = createRowsHtml(pageTable , "" ,dataCellCommonClass );
                doc.select("tr.headerRow").last().after(rowsHtml + headerHtml);
            }
            startIndex = endIndex ;
            endIndex = (int)Utils.getNumberWithinLimits(  tempStatsTable.size() ,  0 , endIndex+NUMBER_OF_ROWS_BALNK_PAGE)  ;
        }while ((endIndex != startIndex));


        String rowsHtml = createRowsHtml(meanRow , "" ,"MeanRow" );
        doc.select("tr").last().after(rowsHtml) ;

        //remove the header class from the first header so as not to put a page break before it
        doc.select("tr.headerRow").first().removeClass("headerRow") ;


        return doc ;
    }

    public static void processPDF(String src, String dest) throws IOException, DocumentException
    {
        PdfReader reader = new PdfReader(src);
        PdfDictionary dict = reader.getPageN(1);
        PdfObject object = dict.getDirectObject(PdfName.CONTENTS);

        if (object instanceof PRStream)
        {
            PRStream stream = (PRStream)object;
            byte[] data = PdfReader.getStreamBytes(stream);
            String dd = new String(data);
            dd = dd.replace("F", "good");
//            dd = dd.replace("EEE:", "Our Ref:");
//            dd = dd.replace("WR", "IT TEST");
//            dd = dd.replace("2016", "2020");
            stream.setData(dd.getBytes());
        }

        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        stamper.close();
        reader.close();
    }

    @Override
    public void generateHtmlReport() throws IOException {
        Document doc = generatePdfHtml(false) ;
        doc.select("tr.headerRow").remove();
        doc.select("div#footer").remove();
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml(true) ;
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");
        processPDF(outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf" , reportsPath+"/report4/test.pdf");
    }

    private ArrayList<ArrayList<String>> getTableWithHeaders () {

        ArrayList<String>tableHeaders = new ArrayList<>();
        tableHeaders.add(Statistics.getIdentifierName()); tableHeaders.add("Grade") ;
        tableHeaders.add("Score") ; tableHeaders.add("Percentage");
        ArrayList<ArrayList<String>> tableWithHeaders = cleanTable(Utils.cloneTable(statsTable)) ;
        tableWithHeaders.add(0,tableHeaders) ;
        return tableWithHeaders ;
     }

    @Override
    public void generateTxtReport() {

        final int CHP = 2  ;
        ArrayList<ArrayList<String>> tableWithHeaders = getTableWithHeaders();

        String outputTxt = "" ;
        int pageWidth = TxtUtils.calcTableWidth(tableWithHeaders , CHP) ;

        String txtTitle = TxtUtils.generateTitleLine(reportTitle,
                pageWidth,2) ;

        outputTxt+= txtTitle ;

//        String tableTxt = TxtUtils.generateTxtTableAlignCenter((ArrayList)tableWithHeaders.subList(0 ,tableWithHeaders.size()-1 ) , "" , CHP ) ;

        String tableTxt = TxtUtils.generateTxtTableAlignCenter(tableWithHeaders , "" , CHP  , false) ;

        String [] tableLines = tableTxt.split(TxtUtils.newLine) ;
        String newTableTxt = "" ;
        for (int lineIndex = 0 ; lineIndex < tableLines.length ; lineIndex++) {
            if(lineIndex == tableLines.length-1){
                newTableTxt += Utils.generatePattern("*" , pageWidth)+TxtUtils.newLine ;
            }
            newTableTxt+= tableLines[lineIndex] + TxtUtils.newLine ;

        }
        outputTxt += newTableTxt ;

        TxtUtils.writeTxtToFile(outputTxt , outputFormatsFolderPaths[ReportsHandler.TXT]+outputFileName+".txt");
    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml(true) ;
        doc.select("th.bar-header").remove() ;
        doc.select("td.bar").remove() ;
        styleTitlePrintable(doc) ;
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, outputFormatsFolderPaths[ReportsHandler.PRINTABLE_PDF]+outputFileName+".pdf");
    }

    private String generateCharSeparatedValuesString(char separator) {
        ArrayList<ArrayList<String>> tableWithHeaders = getTableWithHeaders();

        String outputCsv = "";
        int pageWidth = CsvUtils.calcTableWidth(tableWithHeaders);

        String txtTitle = CsvUtils.generateTitleLine(reportTitle, separator,
                pageWidth, 2);

        outputCsv += txtTitle;

//        String tableTxt = CsvUtils.generateTxtTableAlignCenter((ArrayList)tableWithHeaders.subList(0 ,tableWithHeaders.size()-1 ) , "" , CHP ) ;

        String tableTxt = CsvUtils.generateTable(tableWithHeaders, separator);

        outputCsv += tableTxt;

        return outputCsv ;
    }

    @Override
    public void generateCsvReport() {

        String outputCsv = generateCharSeparatedValuesString(',') ;
        CsvUtils.writeCsvToFile(outputCsv , outputFormatsFolderPaths[ReportsHandler.CSV]+outputFileName+".csv");

    }

    @Override
    public void generateTsvReprot() {

        String outputCsv = generateCharSeparatedValuesString('\t') ;
        CsvUtils.writeCsvToFile(outputCsv , outputFormatsFolderPaths[ReportsHandler.TSV]+outputFileName+".tsv");

    }

    @Override
    public void generateWordReport() throws IOException, InvalidFormatException {

        XWPFDocument document = new XWPFDocument();

        WordUtils.createWordFooter(document); ;

        WordUtils.addTitle(document , reportTitle );


        ArrayList<ArrayList<String>> tableWithHeaders = getTableWithHeaders() ;
        tableWithHeaders.get(0).add("") ;
        for(int rowIndex = 1 ; rowIndex < tableWithHeaders.size() ; rowIndex++) {
            ArrayList<String> tableRow = tableWithHeaders.get(rowIndex) ;
            int rectWidth =(int) Math.round(Double.valueOf(tableRow.get(3).replace("%" , ""))) ;
            if(rectWidth>100)
                rectWidth = 100 ;
            tableRow.add("<<img,70,10>>"+resourcesPath+"RectImages\\Report4\\"+rectWidth+".png") ;
        }

        XWPFTable docTable = WordUtils.addTable(document,new ArrayList<>( tableWithHeaders.subList(0 , tableWithHeaders.size()-1)));
        XWPFTableRow row = docTable.createRow();
        ArrayList<String> lastRow = tableWithHeaders.get(tableWithHeaders.size()-1) ;
        for(int i = 0 ; i < lastRow.size() ; i++) {
            String cellData = lastRow.get(i)  ;
            XWPFTableCell cell = row.getCell(i) ;
            XWPFParagraph par = cell.getParagraphArray(0);
            par.setAlignment(ParagraphAlignment.CENTER);

            WordUtils.processCellData(cell , par, cellData)  ;
//            XWPFRun run = par.createRun();
//            run.setText(cellData);
//            run.setBold(true);
            WordUtils.addBorderToCell(cell);

        }

//        List <XWPFParagraph> paragraphs = document.getParagraphs();
//        document.removeBodyElement(document.getPosOfParagraph(paragraphs.get(paragraphs.size()-1))) ;
//
//        WordUtils.addTable(document, tableWithHeaders);


//        docTable.setCellMargins(50 ,200 , 50 , 200);
//        WordUtils.removeBorders(docTable);
//        docTable.addNewCol();
//        XWPFParagraph par = docTable.getRow(0).getCell(3).getParagraphArray(0);
//        WordUtils.addImage(par , resourcesPath +"RectImages\\Report4\\5.png",50 , 10) ;

        WordUtils.writeWordDocument(document , outputFormatsFolderPaths[ReportsHandler.WORD]+outputFileName+".docx");


    }

    @Override
    public void generateXlsReport() {

    }

    @Override
    public void init() {
        statsTable = Statistics.report4Stats() ;
    }
}
