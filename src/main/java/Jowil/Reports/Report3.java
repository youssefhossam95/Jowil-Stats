package Jowil.Reports;

import Jowil.Reports.Utils.CsvUtils;
import Jowil.Reports.Utils.TxtUtils;
import Jowil.Reports.Utils.WordUtils;
import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.ArabicLigaturizer;
import com.sun.xml.internal.ws.api.databinding.MappingInfo;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Map;

public class Report3 extends Report {


    ArrayList<Map<String , String>> report3Maps ;
    String[] tablesTitles = {"Test Insights", "Test Data", "Basic Statistics", "Dispersion", "Confidence Intervals", "Test Reliability"};

    public Report3(){
        reportTitle=  "Test Statistics Report" ;
        workSpacePath = reportsPath + "report3\\" ;
        templatePath = workSpacePath + "report3Template.html";
        pdfHtmlPath = workSpacePath + outputFileName + ".html";
    }

    private void fillHtmlWithMap (Document doc , Map<String , String> statsMap){

        doc.select("td.NumberOfObjectiveQuestions").last().text(statsMap.get("Number of Objective Questions")) ;
        doc.select("td.NumberOfSubjectiveQuestions").last().text(statsMap.get("Number of Subjective Questions")) ;
        doc.select("td.MaximumPossibleScore").last().text(statsMap.get("Maximum Possible Score")) ;
//        doc.select("td.Benchmark").last().text(statsMap.get("Benchmark")) ;

//        System.out.println("fuck you");
        //testInsights
        doc.select("td.EasiestQuestion").last().text(statsMap.get("Easiest Question")) ;
        doc.select("td.HardestQuestion").last().text(statsMap.get("Hardest Question")) ;
        doc.select("td.EasiestGroup").last().text(statsMap.get("Easiest Group")) ;
        doc.select("td.HardestGroup").last().text(statsMap.get("Hardest Group")) ;


        //Basic Statistics
        doc.select("td.Mean").last().text(statsMap.get("Mean Score")) ;
        doc.select("td.MeanPercentScore").last().text(statsMap.get("Mean Percent Score")) ;
        doc.select("td.HighestScore").last().text(statsMap.get("Highest Score")) ;
        doc.select("td.LowestScore").last().text(statsMap.get("Lowest Score")) ;
        //Dispersion
        doc.select("td.StandardDeviation").last().text(statsMap.get("Standard Deviation")) ;
        doc.select("td.Variance").last().text(statsMap.get("Variance")) ;
        doc.select("td.Range").last().text(statsMap.get("Range")) ;
        doc.select("td.Median").last().text(statsMap.get("Median")) ;
        doc.select("td.25thPercentile").last().text(statsMap.get("25th Percentile")) ;
        doc.select("td.75thPercentile").last().text(statsMap.get("75th Percentile")) ;
        doc.select("td.InterquartileRange").last().text(statsMap.get("Interquartile Range")) ;

        //Confidence Intervals
        doc.select("td.90").last().text(statsMap.get("90")) ;
        doc.select("td.95").last().text(statsMap.get("95")) ;
        doc.select("td.99").last().text(statsMap.get("99")) ;

        //Test Reliability
        doc.select("td.Kuder-RichardsonFormula20").last().text(statsMap.get("Kuder-Richardson Formula 20")) ;
        doc.select("td.Kuder-RichardsonFormula21").last().text(statsMap.get("Kuder-Richardson Formula 21")) ;
    }

    public Document generatePdfHtml() throws IOException {
        File file = new File(templatePath);
        Document doc = Jsoup.parse(file, "UTF-8");

        updateTemplateDate(doc); // updates the date of the footer to the current date

        String templateBodyHtml = doc.select("div#template").html() ;
        final String pageBreakHtml= "<div class='page-break'></div>\n" ;


//        ArrayList<Map<String, String >> report3Maps = Statistics.report3Stats() ;

        for(int mapIndex = 0 ; mapIndex < report3Maps.size() ; mapIndex ++) {
            if(mapIndex>0) {
                doc.select("table").last().after(pageBreakHtml);
                doc.select("div.page-break").last().after(templateBodyHtml) ;
                doc.select("div.divTitle").addClass("second-page-header") ;
                doc.select("div.divTitle").last().text(reportTitle +": Form "+ mapIndex );
            }

            Map<String , String > statsMap = report3Maps.get(mapIndex);
            fillHtmlWithMap(doc , statsMap);
        }

        return doc ;
    }
    @Override
    public void generateHtmlReport() throws IOException {
        Document doc = generatePdfHtml() ;
        doc .select("div#footer").remove();
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html", doc);

    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {

        Document doc = generatePdfHtml();
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath , outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");

    }

    public ArrayList<ArrayList<ArrayList<String>>> processMap (Map<String , String> map ){

        int [] tablesRowCount = {4 ,3 , 4 , 7 ,3 ,2} ;
        ArrayList<ArrayList<ArrayList<String>>> tables = new ArrayList<>() ;
        ArrayList<ArrayList<String>> table  = new ArrayList<>();
        ArrayList<String>tableRow = new ArrayList<>() ;

        int currentTableRows = 0 ;
        int tableIndex = 0 ;
        for(Map.Entry<String , String> entry:map.entrySet()) {
            if(currentTableRows == tablesRowCount[tableIndex]){
                tableIndex++ ;
                currentTableRows = 0 ;
                tables.add(table);
                table = new ArrayList<>() ;
            }
            tableRow.add(entry.getKey()) ; tableRow.add(entry.getValue());
            table.add(tableRow);
            tableRow = new ArrayList<>() ;
            currentTableRows++ ;

        }

        System.out.println(tables);
        tables.add(table) ;
        return tables ;
    }

    @Override
    public void generateTxtReport() {

        final int  PADDING_BETWEEN_TABLES = 2 ;
        final int CHP = 10 ;

        String outputTxt = "" ;

        String [] tablesTitles = {"Test Insights","Test Data" , "Basic Statistics" , "Dispersion" , "Confidence Intervals" , "Test Reliability"} ;

//        ArrayList<Map<String, String> > reprot3Maps = Statistics.report3Stats() ;
        for (int mapIndex=  0 ; mapIndex <report3Maps.size() ; mapIndex++ ) {

            ArrayList<ArrayList<ArrayList<String>>> tables = processMap(report3Maps.get(mapIndex));
            ArrayList<Integer> CHPS = new ArrayList<>();
            for (ArrayList<ArrayList<String>> table : tables)
                CHPS.add(CHP);

            int pageWidth = TxtUtils.calcPageWidth(tables, CHPS);

            String form = "" ;
            if(mapIndex>0) {
                form = ": Form " + mapIndex;
                outputTxt += Utils.generatePattern("#" , pageWidth)+TxtUtils.newLine;

            }
            String txtTitle = TxtUtils.generateTitleLine(reportTitle +form ,
                    pageWidth, 2);

            outputTxt += txtTitle;
            ArrayList<String> txtTables = new ArrayList<>();
            for (int tableIndex = 0; tableIndex < tables.size(); tableIndex++) {
                txtTables.add(TxtUtils.generateTxtTableAlignLR(tables.get(tableIndex), tablesTitles[tableIndex], CHP));
            }
            outputTxt += TxtUtils.stackTablesV(txtTables, PADDING_BETWEEN_TABLES);
        }
        TxtUtils.writeTxtToFile(outputTxt, outputFormatsFolderPaths[ReportsHandler.TXT]+outputFileName+".txt" );

    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {

        Document doc = generatePdfHtml();
        styleTitlePrintable(doc);
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath , outputFormatsFolderPaths[ReportsHandler.PRINTABLE_PDF]+outputFileName+".pdf");

    }

    private String generateCharSeparatedValuesString(char separator) {


        final int PADDING_BETWEEN_TABLES = 2;

        String outputCsv = "";


        for (int mapIndex = 0; mapIndex < report3Maps.size(); mapIndex++) {

            ArrayList<ArrayList<ArrayList<String>>> tables = processMap(report3Maps.get(mapIndex));

            int pageWidth = CsvUtils.calcPageWidth(tables);

            String form = "";
            if (mapIndex > 0) {
                form = ": Form " + mapIndex;
            }
            String txtTitle = CsvUtils.generateTitleLine(reportTitle + form , separator,
                    pageWidth, 2);

            outputCsv += txtTitle;
            ArrayList<String> txtTables = new ArrayList<>();
            for (int tableIndex = 0; tableIndex < tables.size(); tableIndex++) {
                txtTables.add(CsvUtils.generateTable(tables.get(tableIndex), separator, tablesTitles[tableIndex]));
            }
            outputCsv += CsvUtils.stackTablesV(txtTables, PADDING_BETWEEN_TABLES);
        }
        return  outputCsv ;
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

        XWPFDocument document = WordUtils.createDocument() ;

        for(int mapIndex = 0 ; mapIndex < report3Maps.size() ; mapIndex++) {
            String title = " Test Statistics Report";
            if(mapIndex>0) {
                WordUtils.addPageBreak(document);
                title =   title + ": Form " + mapIndex;
            }
            WordUtils.addTitle(document , title);
            ArrayList<ArrayList<ArrayList<String>>> tables = processMap(report3Maps.get(mapIndex)) ;
            for(int tableIndex = 0 ; tableIndex<tables.size() ; tableIndex++) {
                ArrayList<ArrayList<String>> table = tables.get(tableIndex);
                if(tableIndex==3)
                    WordUtils.addPageBreak(document);
                WordUtils.addTable(document , table , WordUtils.TABLE_ALIGN_LR , tablesTitles[tableIndex] , 14);
            }
        }
        WordUtils.writeWordDocument(document , outputFormatsFolderPaths[ReportsHandler.WORD]+outputFileName+".docx");



    }

    @Override
    public void init() {
        report3Maps = Statistics.report3Stats() ;
    }
}
