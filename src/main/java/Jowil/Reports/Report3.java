package Jowil.Reports;

import Jowil.Reports.Utils.TxtUtils;
import Jowil.Statistics;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.ArabicLigaturizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Report3 extends Report {


    public Report3(){
        workSpacePath = reportsPath + "report3\\" ;
        templatePath = workSpacePath + "report3Template.html";
        outputFileName = "Report3" ;
        pdfHtmlPath = workSpacePath + outputFileName + ".html";
    }

    public Document generatePdfHtml() throws IOException {
        File file = new File(templatePath);
        Document doc = Jsoup.parse(file, "UTF-8");

        updateTemplateDate(doc); // updates the date of the footer to the current date


        Map<String, String > report3Stats = Statistics.report3Stats() ;

        System.out.println("hello " + doc.select("td#90").first().text()) ;

        doc.select("td#NumberOfGradedQuestions").first().text(report3Stats.get("Number Of Graded Questions")) ;
        doc.select("td#MaximumPossibleScore").first().text(report3Stats.get("Maximum Possible Score")) ;
        doc.select("td#Benchmark").first().text(report3Stats.get("Benchmark")) ;

        //Basic Statistics
        doc.select("td#Mean").first().text(report3Stats.get("Mean")) ;
        doc.select("td#MeanPercentScore").first().text(report3Stats.get("Mean Percent Score")) ;
        doc.select("td#HighestScore").first().text(report3Stats.get("Highest Score")) ;
        doc.select("td#LowestScore").first().text(report3Stats.get("Lowest Score")) ;
        //Dispersion
        doc.select("td#StandardDeviation").first().text(report3Stats.get("Standard Deviation")) ;
        doc.select("td#Variance").first().text(report3Stats.get("Variance")) ;
        doc.select("td#Range").first().text(report3Stats.get("Range")) ;
        doc.select("td#Median").first().text(report3Stats.get("Median")) ;
        doc.select("td#25thPercentile").first().text(report3Stats.get("25th Percentile")) ;
        doc.select("td#75thPercentile").first().text(report3Stats.get("75th Percentile")) ;
        doc.select("td#InterquartileRange").first().text(report3Stats.get("Interquartile Range")) ;

        //Confidence Intervals
        doc.select("td#90").first().text(report3Stats.get("90")) ;
        doc.select("td#95").first().text(report3Stats.get("95")) ;
        doc.select("td#99").first().text(report3Stats.get("99")) ;

        //Test Reliability
        doc.select("td#Kuder-RichardsonFormula20").first().text(report3Stats.get("Kuder-Richardson Formula 20")) ;
        doc.select("td#Kuder-RichardsonFormula21").first().text(report3Stats.get("Kuder-Richardson Formula 21")) ;
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

        int [] tablesRowCount = {3 , 4 , 7 ,3 ,2} ;
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
        return tables ;
    }

    @Override
    public void generateTxtReport() {

        final int  PADDING_BETWEEN_TABLES = 2 ;
        final int CHP = 10 ;

        String outputTxt = "" ;

        String [] tablesTitles = {"Test Data" , "Basic Statistics" , "Dispersion" , "Confidence Intervals"} ;
        ArrayList<ArrayList<ArrayList<String>>> tables =  processMap(Statistics.report3Stats());
        ArrayList<Integer> CHPS = new ArrayList<>();
        for(ArrayList<ArrayList<String>> table:tables)
            CHPS.add(CHP);

        int pageWidth = TxtUtils.calcPageWidth(tables,CHPS);

        String txtTitle = TxtUtils.generateTitleLine("Test Statistics Report",
                pageWidth,2) ;

        outputTxt+= txtTitle ;
        ArrayList<String> txtTables = new ArrayList<>() ;
        for ( int tableIndex = 0 ; tableIndex < tables.size() ; tableIndex++){
            txtTables.add(TxtUtils.generateTxtTableAlignLR(tables.get(tableIndex) ,tablesTitles[tableIndex],CHP)) ;
        }
        outputTxt += TxtUtils.stackTablesV(txtTables,PADDING_BETWEEN_TABLES);

        TxtUtils.writeTxtToFile(outputTxt, outputFormatsFolderPaths[ReportsHandler.TXT]+outputFileName+".txt" );

    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {

        Document doc = generatePdfHtml();
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath , outputFormatsFolderPaths[ReportsHandler.PRINTABLE_PDF]+outputFileName+".pdf");

    }

    @Override
    public void init() {

    }
}
