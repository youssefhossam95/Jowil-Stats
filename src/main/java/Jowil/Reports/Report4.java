package Jowil.Reports;

import Jowil.Reports.Utils.CsvUtils;
import Jowil.Reports.Utils.TxtUtils;
import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;

public class Report4 extends Report{


    private ArrayList<ArrayList<String>> statsTable ;

    public Report4(){
        workSpacePath = reportsPath + "report4\\" ;
        templatePath = workSpacePath + "report4Template.html";
        outputFileName = "Report4" ;
        pdfHtmlPath = workSpacePath + outputFileName + ".html";
    }

    private Document generatePdfHtml() throws IOException {

        final int NUMBER_OF_ROWS_FIRST_PAGE = 20 ;
        final int NUMBER_OF_ROWS_BALNK_PAGE = 24 ;

        Format format = new DecimalFormat("0.#");
        final String dataCellCommonClass = "tg-l711" ;

        File file = new File(templatePath);

        Document doc =  Jsoup.parse(file , "UTF-8") ;

        updateTemplateDate(doc); // updates the date of the footer to the current date

        String headerHtml = doc.select("tr.headerRow").outerHtml();

        doc.select("th#identifier").last().text(Statistics.getIdentifierName()) ;
        ArrayList<ArrayList<String>> tempStatsTable = Utils.cloneTable(statsTable) ;

        // separate the maean row (last row)
        ArrayList<ArrayList<String>> meanRow =new ArrayList<ArrayList<String>> ( tempStatsTable.subList(tempStatsTable.size()-1 , tempStatsTable.size()));

        // adding colspan attribute to first element in each row
        for(ArrayList<String> tableRow:tempStatsTable) {
            tableRow.set(0,tableRow.get(0) + "#colspan='2'" ) ;
            String barWidth = tableRow.get(tableRow.size() - 1);
            String passingPercent = ((DecimalFormat) format).format(Statistics.getPassingPercent()*100) +"%";
            String divHtml = "<div class='emptyBar'> \n"+
                                "<div class='greenBar' style='width:" + barWidth + "'> </div>\n" +
                                "<div class='benchmark' style='width:" + passingPercent + "'> </div>"+
                             "</div>";
            tableRow.add(divHtml+";bar") ;
        }

        int startIndex = 0 ;
        int endIndex = (int)Utils.getNumberWithinLimits(tempStatsTable.size() , 0 , NUMBER_OF_ROWS_FIRST_PAGE) ;

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
    @Override
    public void generateHtmlReport() throws IOException {
        Document doc = generatePdfHtml() ;
        doc.select("tr.headerRow").remove();
        doc.select("div#footer").remove();
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml() ;
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");
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

        String txtTitle = TxtUtils.generateTitleLine("Studets Grades Report",
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
        Document doc = generatePdfHtml() ;
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

        String txtTitle = CsvUtils.generateTitleLine("Studets Grades Report", separator,
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
    public void init() {
        statsTable = Statistics.report4Stats() ;
    }
}
