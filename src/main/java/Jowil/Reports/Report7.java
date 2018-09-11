package Jowil.Reports;

import Jowil.Reports.Utils.CsvUtils;
import Jowil.Reports.Utils.TxtUtils;
import Jowil.Reports.Utils.WordUtils;
import Jowil.Statistics;
import Jowil.Utils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.nodes.Document;
import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import sun.nio.cs.UTF_32LE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Report7 extends Report {

    ArrayList<ArrayList<ArrayList<ArrayList<String>>>> formsTableStats ;
    String [] tableTitles = {"Bad Questions" , "Hardest Questions" , "Easiest Questions"} ;
    String congratsMsg = "Congrats your Exam Doesn't have any Bag Questions";

    public Report7(){
        workSpacePath = reportsPath + "report7\\" ;
        templatePath = workSpacePath + "report7Template.html";
        outputFileName = "Report7" ;
        pdfHtmlPath = workSpacePath+outputFileName+".html" ;
    }

    private Document generatePdfHtml( ) throws IOException {
        File file = new File(templatePath);
        Document doc = Jsoup.parse(file, "UTF-8");

        updateTemplateDate(doc); // updates the date of the footer to the current date

        final String pageBreakHtml= "<div class='page-break'></div>\n" ;
        String templateBodyHtml = doc.select("div#template").html() ;
        for ( int formIndex = 0 ; formIndex < formsTableStats.size() ; formIndex++ ) {
            ArrayList<ArrayList<ArrayList<String>>> formTables = formsTableStats.get(formIndex);
            if(formIndex > 0 )
                doc.select("div.page-break").last().after(templateBodyHtml);
            if(formsTableStats.size()>1) // more than one Form
                doc.select("div.divTitle").last().text("Form "+(formIndex+1) +" Question Insights Report") ;

            for (int tableIndex = 0; tableIndex < formTables.size(); tableIndex++) {
                ArrayList<ArrayList<String>> table = formTables.get(tableIndex);
                String tableRowsHtml = createRowsHtml(table, ";grayRow", "");
                doc.select("tr.table-header" + (tableIndex + 1)).last().after(tableRowsHtml);
            }
            if(formIndex < formsTableStats.size()-1)
                doc.select("table").last().after(pageBreakHtml) ;
        }

        return doc ;
    }
    @Override
    public void generateHtmlReport() throws IOException {

        Document doc = generatePdfHtml();
        doc.select("div#footer").remove() ;
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);

    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {

        Document doc  = generatePdfHtml();
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");

    }

    private ArrayList<ArrayList<String>> getTableWithHeaders (ArrayList<ArrayList<String>> table , int tableIndex) {
        ArrayList<ArrayList<String>> tableWithHeaders = Utils.cloneTable(table) ;
        ArrayList<String> headers = new ArrayList<>();
        if(tableIndex ==0 ) {
            headers.add("Question Name"); headers.add("Difficulty (0-10)"); headers.add("Correct Response Percentage");
            headers.add("Distractors") ;

        }
        else if (tableIndex == 1) {
            headers.add("Question Name"); headers.add("Difficulty (0-10)"); headers.add("Correct Response Percentage");
            headers.add("Non Distractors") ;
        }
        else if (tableIndex == 2 ) {
            headers.add("Question Name");  headers.add("Point Biserial"); headers.add("Smart Distractors");
            headers.add("total"); headers.add("Upper 25%"); headers.add("Lower 25%");
        }
        tableWithHeaders.add(0,headers);
        return  tableWithHeaders ;
    }

    private String generateCharSeparatedValuesString(char separator) {
        int CHP = 2;
        String outputCsv = "";

        for (int formIndex = 0; formIndex < formsTableStats.size(); formIndex++) {
            String form = "";
            if (formsTableStats.size() > 1)
                form = "Form " + (formIndex + 1);


            ArrayList<ArrayList<ArrayList<String>>> statsTables = formsTableStats.get(formIndex);

            ArrayList<ArrayList<ArrayList<String>>> tempTables = new ArrayList<>(); // to calcPage With
            ArrayList<String> txtTables = new ArrayList<>();
            for (int tableIndex = 0; tableIndex < statsTables.size(); tableIndex++) {
                if(statsTables.get(tableIndex).size()==0)
                    outputCsv+=  congratsMsg + Utils.generatePattern(CsvUtils.NEW_LINE,2) ;
                ArrayList<ArrayList<String>> tableWithHeaders = getTableWithHeaders(statsTables.get(tableIndex), tableIndex);
                tempTables.add(tableWithHeaders);
                txtTables.add(CsvUtils.generateTable(tableWithHeaders,separator,tableTitles[tableIndex]));
            }

            ////////////////////////// Start filling the report ///////////////////////////

            int pageWidth = CsvUtils.calcPageWidth(tempTables);

            if (formIndex > 0)
                outputCsv += Utils.generatePattern("*", pageWidth) + CsvUtils.NEW_LINE;

            String txtTitle = CsvUtils.generateTitleLine(form + " Question Insights Reprot",separator,
                    pageWidth, 2);

            outputCsv += txtTitle;

            outputCsv += CsvUtils.stackTablesV(txtTables, 2);
        }

        return outputCsv ;
    }

    @Override
    public void generateTxtReport() {

        int CHP = 2 ;
        String outputTxt = ""  ;

        for (int formIndex= 0 ; formIndex < formsTableStats.size() ; formIndex++) {
            String form = "";
            if(formsTableStats.size()>1)
                form = "Form "+ (formIndex+1) ;


            ArrayList<ArrayList<ArrayList<String>>> statsTables = formsTableStats.get(formIndex);

            ArrayList<ArrayList<ArrayList<String>>> tempTables = new ArrayList<>(); // to calcPage With
            ArrayList<String> txtTables = new ArrayList<>();
            for (int tableIndex = 0; tableIndex < statsTables.size(); tableIndex++) {
                ArrayList<ArrayList<String>> tableWithHeaders = getTableWithHeaders(statsTables.get(tableIndex), tableIndex);
                tempTables.add(tableWithHeaders) ;
                txtTables.add(TxtUtils.generateTxtTableAlignCenter(tableWithHeaders, tableTitles[tableIndex], CHP, false));
            }

            ////////////////////////// Start filling the report ///////////////////////////

            int pageWidth  = TxtUtils.calcPageWidth(tempTables, CHP) ;

            if(formIndex>0)
                outputTxt += Utils.generatePattern("*" , pageWidth)+TxtUtils.newLine ;

            String txtTitle = TxtUtils.generateTitleLine(form+" Question Insights Reprot" ,
                    pageWidth, 2 ) ;

            outputTxt+= txtTitle ;

            outputTxt+= TxtUtils.stackTablesV(txtTables, 2);
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

        String outputCsv = generateCharSeparatedValuesString(',') ;
        CsvUtils.writeCsvToFile(outputCsv , outputFormatsFolderPaths[ReportsHandler.CSV]+outputFileName+".csv");

    }

    @Override
    public void generateTsvReprot() {
        String outputCsv = generateCharSeparatedValuesString('\t') ;
        CsvUtils.writeCsvToFile(outputCsv , outputFormatsFolderPaths[ReportsHandler.TSV]+outputFileName+".tsv");
    }

    @Override
    public void generateWordReport() throws IOException {

        XWPFDocument document = WordUtils.createDocument() ; // landscape size

        for(int formIndex = 0 ; formIndex < formsTableStats.size() ; formIndex++) {
            String title = " Question Insights Report";
            if( formsTableStats.size() >1) {
                title = "Form " + (formIndex+1) + title;
            }
            if(formIndex>0)
                WordUtils.addPageBreak(document);
            WordUtils.addTitle(document , title);


            ArrayList<ArrayList<ArrayList<String>>> statsTables = formsTableStats.get(formIndex);

            for(int tableIndex = 0 ; tableIndex<statsTables.size() ; tableIndex++) {
                ArrayList<ArrayList<String>> table = statsTables.get(tableIndex);
                ArrayList<ArrayList<String>> tableWithHeaders = getTableWithHeaders(table , tableIndex) ;
                WordUtils.addTable(document , tableWithHeaders , WordUtils.TABLE_ALIGN_CENTER , tableTitles[tableIndex] , 18 , true);
           }
        }
        WordUtils.writeWordDocument(document , outputFormatsFolderPaths[ReportsHandler.WORD]+outputFileName+".docx");

    }

    @Override
    public void init() {
        formsTableStats = Statistics.report7Stats() ;
    }
}
