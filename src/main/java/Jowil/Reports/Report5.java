package Jowil.Reports;

import Jowil.CSVHandler;
import Jowil.Group;
import Jowil.Reports.Utils.CsvUtils;
import Jowil.Reports.Utils.TxtUtils;
import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Report5 extends Report {

    private ArrayList<ArrayList<ArrayList<ArrayList<String>>>> formsStatsTables ;
    String imagesFolderFullPath ;

    public Report5(){
        workSpacePath = reportsPath + "report5\\" ;
        templatePath = workSpacePath + "report5Template.html";
        outputFileName = "Report5" ;
        pdfHtmlPath = workSpacePath + outputFileName + ".html";
        imagesFolderFullPath = "file://"+System.getProperty("user.dir") + workSpacePath  ;
    }

    private Document generatePdfHtml (ArrayList<ArrayList<ArrayList<ArrayList<String>>>> formsTables) throws IOException {
        File file = new File(templatePath);
        Document doc =  Jsoup.parse(file , "UTF-8") ;

        updateTemplateDate(doc); // updates the date of the footer to the current date

        String wrapperHtml = doc.select("div.wrapper").outerHtml() ;
        String pageBreakHtml = "<div class='page-break'> </div>" ;
        String templateHtml = doc.select("div#template").html() ;
        String groupNameHtml = doc.select("div.line").outerHtml() ;
//        doc.select("div.line").last().remove(); // remove the first line as it will be put by the function below

        final int  NUMBER_OF_ROWS_IN_PAGE = 41 ;
        final int  ROWS_OF_MAIN_HEADER = 12 ;
        final int  ROWS_OF_TABLE_HEADER = 6 ;
        final int  ROWS_OF_GROUP_NAME  = 6;

        ArrayList<Group> groups = CSVHandler.getDetectedGroups();
        for (int formIndex = 0; formIndex <Statistics.getNumberOfForms() ; formIndex++) {
            int groupIndex = 0 ;
            int nextGroupTableIndex = groups.get(groupIndex).getqCount() ;
//            ArrayList<ArrayList<ArrayList<String>>> tables = Statistics.report5stats(formIndex);
            ArrayList<ArrayList<ArrayList<String>>> tables = formsTables.get(formIndex);
            if(formIndex>0) {
                doc.select("div.wrapper").last().after(pageBreakHtml);
                doc.select("div.page-break").last().after(templateHtml) ;
                doc.select("div.divTitle").addClass("second-page-header") ;
                doc.select("div.divTitle").last().text("Form "+(formIndex+1) + " Condensed Test Report");
            }else
            if(Statistics.getNumberOfForms()>1)
                doc.select("div.divTitle").last().text("Form "+(formIndex+1) + " Condensed Test Report");

            int remainingRows = NUMBER_OF_ROWS_IN_PAGE - ROWS_OF_MAIN_HEADER;

            int htmlTableIndex = 0 ; //Index of table in html if no tables where deleted due to change in groups
            for (int tableIndex = 0; tableIndex < tables.size(); tableIndex++) {
                ArrayList<ArrayList<String>> table = tables.get(tableIndex);
                String spanClass = "second";
                if (htmlTableIndex % 2 == 0) {
                    spanClass = "first";
                    if (tableIndex != 0) {
                        remainingRows -= (table.size() + ROWS_OF_TABLE_HEADER);
                        doc.select("div.wrapper").last().after(wrapperHtml);
                        if (remainingRows < 0) {
                            doc.select("div.wrapper").last().before(pageBreakHtml);
                            remainingRows = NUMBER_OF_ROWS_IN_PAGE;
                        }

                    }else //for first table
                        doc.select("span.group-title").last().text(groups.get(groupIndex).getCleanedName());
                    if(tableIndex>nextGroupTableIndex) {
                        Elements rightTables =  doc.select("span.second") ;
                        int numberOfRightTables = rightTables.size() ;
                        rightTables.get(numberOfRightTables-2).remove();
                        table= tables.get(--tableIndex);
                    }
                    if(tableIndex==nextGroupTableIndex) {
                        doc.select("div.wrapper").last().before(groupNameHtml);
                        Element lastGroupNameElement = doc.select("div.line").last() ;
                        lastGroupNameElement.select("span.group-title").last().text(groups.get(++groupIndex).getCleanedName());
                        remainingRows -= ROWS_OF_GROUP_NAME;
                        if(remainingRows< 0 ){
                            lastGroupNameElement.before(pageBreakHtml);
                            remainingRows = NUMBER_OF_ROWS_IN_PAGE;
                        }
                        nextGroupTableIndex += groups.get(groupIndex).getqCount() ;
                    }
                }

                String questionName = Statistics.getQuestionNames().get(tableIndex);
                ArrayList<ArrayList<String>> modifiedTable = Utils.cloneTable(table);

                for (ArrayList<String> tableRow : modifiedTable) {
                    String barClass = tableRow.get(tableRow.size() - 1);
                    String barWidth  ;
                    if(tableRow.size()==4)
                         barWidth = tableRow.get(tableRow.size() - 2);
                    else
                        barWidth = tableRow.get(tableRow.size() - 3);
                    String divHtml = "<div class='emptyBar'> \n <div class='" + barClass + "' style='width:" + barWidth + "%'></div>\n</div>";
                    tableRow.set(tableRow.size() - 1, divHtml);
                }
                String tableRowsHtml = createRowsHtml(modifiedTable, ";gray-row", " ");
                Element nextTableToFillElement = doc.select("span." + spanClass).last();
                nextTableToFillElement.select("tr.header-row").after(tableRowsHtml);
                nextTableToFillElement.select("div.tableTitle").last().text(questionName);

                htmlTableIndex++ ;

            }
            // if tables ended with odd number remove the last table
            if(htmlTableIndex%2==1)
                doc.select("span.second").last().remove() ;
        }

        return doc ;
//        writeHtmlFile(pdfHtmlPath , doc);
    }

    public void changeLegendImgPath (Document doc , String imagesFolderFullPath ) {
        Elements legendImgs = doc.select("span.legend-img") ;
        for (Element img : legendImgs) {
            String x = img.attr("style");
            String[] parts = x.split("url");
            String imgOldUrl = parts[1].substring(2, parts[1].length() - 2);
//            System.out.println(x);
            System.out.println(imgOldUrl);
            String imgNewUrl = imagesFolderFullPath + imgOldUrl  ;
            img.attr("style" , "background-image: url('"+imgNewUrl+"')" ) ;
        }
    }

    @Override
    public void generateHtmlReport() throws IOException {
        Document doc = generatePdfHtml(formsStatsTables) ;
        doc.select("div#footer").remove() ;
        changeImgPath(doc, imagesFolderFullPath);
//        changeLegendImgPath(doc , imagesFolderFullPath);
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {

        Document doc  = generatePdfHtml(formsStatsTables);
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");

    }

    @Override
    public void generateTxtReport() {

        final int CHP = 2 ;

        ArrayList<String>headers= getHeaders() ;
        ArrayList<ArrayList<ArrayList<ArrayList<String>>>> printableFormsStatsTables = getPrintableTables(ReportsHandler.TXT) ;
        String outputTxt = "" ;

        int pageWidth = TxtUtils.calcTableWidth(printableFormsStatsTables.get(0).get(0) , CHP);

        for(int formIndex = 0  ;formIndex <Statistics.getNumberOfForms() ; formIndex++) {

            if(formIndex>0)
                outputTxt += Utils.generatePattern("#" , pageWidth)+TxtUtils.newLine;

            String reportTitle = "Condenced Test Report"  ;
            if(Statistics.getNumberOfForms()>1)
                reportTitle = "Form"+(formIndex+1) + " " + reportTitle ;

            String txtTitle = TxtUtils.generateTitleLine(reportTitle,
                    pageWidth,2) ;

            outputTxt+= txtTitle ;
            ArrayList<String> formTxtTables = new ArrayList<>() ;
            ArrayList<ArrayList<ArrayList<String>>> formTables = printableFormsStatsTables.get(formIndex);
            for(int tableIndex = 0; tableIndex < formTables.size() ; tableIndex++) {
                ArrayList<ArrayList<String>> table = cleanTable(formTables.get(tableIndex)) ;
                table.add(0,headers) ;
                String questionName = Statistics.getQuestionNames().get(tableIndex);
                formTxtTables.add( TxtUtils.generateTxtTableAlignCenter(table,questionName ,CHP , false));
            }
            outputTxt+= TxtUtils.stackTablesV(formTxtTables , 2) ;
        }

        TxtUtils.writeTxtToFile(outputTxt , outputFormatsFolderPaths[ReportsHandler.TXT]+outputFileName+".txt");
    }

    private void editRowForTxt (ArrayList<String> tableRow) {
        String rowClass = tableRow.get(tableRow.size()-1) ; // get last element
        int numberOfSolvers = Integer.valueOf(tableRow.get(1));
        String addedData;
        if(rowClass.equals("greenBar"))
            addedData = "<--" ;
        else if(numberOfSolvers == 0)
            addedData = "-";
        else if(rowClass.equals("distBar"))
            addedData = "!";
       else
           addedData="" ;
        tableRow.remove(tableRow.size()-1);
        tableRow.set(tableRow.size()-1 , addedData) ; // replace the bar cell with the info cell


    }

    private void editRowForPrintablePdf (ArrayList<String> tableRow) {
//        String rowClass = tableRow.get(tableRow.size()-1) ; // get last element
        int numberOfSolvers = Integer.valueOf(tableRow.get(1));
//        String addedImgName;
//        if(rowClass.equals("greenBar"))
//            addedImgName = "correct" ;
//        else if(numberOfSolvers == 0)
//            addedImgName = "nonDistractor";
//        else if(rowClass.equals("distBar"))
//            addedImgName = "distractor";
//        else {
//            addedImgName= null ;
//        }
//        String addedData = addedImgName!=null?
//                "<img src='"+addedImgName+".png' height='15px' class='type-img'> </img>":" " ;
//
////                                            tableRow.set(tableRow.size()-1 , "printable-bar");

        if(numberOfSolvers!=0)
            tableRow.set(tableRow.size()-1 , "printable-bar");
        else
            tableRow.set(tableRow.size()-1 , "");

//        tableRow.add(3 , addedData) ;

    }

    private  ArrayList<ArrayList<ArrayList<ArrayList<String>>>> getPrintableTables (int type){
        ArrayList<ArrayList<ArrayList<ArrayList<String>>>> printableFormsStatsTables = new ArrayList<>();
        for(ArrayList<ArrayList<ArrayList<String>>> tables: formsStatsTables) {
            ArrayList<ArrayList<ArrayList<String>>> clonedTables  = Utils.clone3D(tables) ;
            for ( ArrayList<ArrayList<String>> table :clonedTables ) {

                for(  ArrayList<String> tableRow :table){
                        if(type == ReportsHandler.PRINTABLE_PDF)
                            editRowForPrintablePdf(tableRow);
                        else
                            editRowForTxt(tableRow);
                }
            }
            printableFormsStatsTables.add(clonedTables);
        }
        return  printableFormsStatsTables ;
    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {

        ArrayList<ArrayList<ArrayList<ArrayList<String>>>> printableFormsStatsTables = getPrintableTables(ReportsHandler.PRINTABLE_PDF);
        Document doc = generatePdfHtml(printableFormsStatsTables) ;
//        doc.select("th.percent").after("<th>  </th>") ;
        //change border color of empty bar
        doc.select("div.emptyBar").attr("style" , "border-color: #999999") ;

        styleTitlePrintable(doc);

        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath , outputFormatsFolderPaths[ReportsHandler.PRINTABLE_PDF]+outputFileName+".pdf");
    }

    private ArrayList<String> getHeaders (){
        ArrayList<String> headers = new ArrayList<>( );
        headers.add("Response") ;headers.add("Count") ; headers.add("Percent"); headers.add("info") ;

        return headers ;
    }


    private String generateCharSeparatedValuesString(char separator) {
        ArrayList<String> headers = getHeaders() ;

        ArrayList<ArrayList<ArrayList<ArrayList<String>>>> printableFormsStatsTables = getPrintableTables(ReportsHandler.TXT) ;
        String outputCsv = "" ;

        int pageWidth = CsvUtils.calcTableWidth(printableFormsStatsTables.get(0).get(0));

        for(int formIndex = 0  ;formIndex <Statistics.getNumberOfForms() ; formIndex++) {

            String reportTitle = "Condenced Test Report"  ;
            if(Statistics.getNumberOfForms()>1)
                reportTitle = "Form"+(formIndex+1) + " " + reportTitle ;

            String txtTitle = CsvUtils.generateTitleLine(reportTitle,separator,
                    pageWidth,2) ;

            outputCsv+= txtTitle ;
            ArrayList<String> formTxtTables = new ArrayList<>() ;
            ArrayList<ArrayList<ArrayList<String>>> formTables = printableFormsStatsTables.get(formIndex);
            for(int tableIndex = 0; tableIndex < formTables.size() ; tableIndex++) {
                ArrayList<ArrayList<String>> table = cleanTable(formTables.get(tableIndex)) ;
                table.add(0,headers) ;
                String questionName = Statistics.getQuestionNames().get(tableIndex);
                formTxtTables.add( CsvUtils.generateTable(table,separator,questionName));
            }
            outputCsv+= CsvUtils.stackTablesV(formTxtTables , 2) ;
        }

        return outputCsv;
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
        formsStatsTables = new ArrayList<>();
        for (int formIndex = 0 ; formIndex < Statistics.getNumberOfForms() ; formIndex++) {
            formsStatsTables.add(Statistics.report5stats(formIndex)) ;
        }
    }
}
