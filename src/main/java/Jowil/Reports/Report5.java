package Jowil.Reports;

import Jowil.CSVHandler;
import Jowil.Group;
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

public class Report5 extends Report {

    public Report5(){
        workSpacePath = reportsPath + "report5\\" ;
        templatePath = workSpacePath + "report5Template.html";
        outputFileName = "Report5" ;
        pdfHtmlPath = workSpacePath + outputFileName + ".html";
    }

    private Document generatePdfHtml () throws IOException {
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
            ArrayList<ArrayList<ArrayList<String>>> tables = Statistics.report5stats(formIndex);

            if(formIndex>0) {
                doc.select("div.wrapper").last().after(pageBreakHtml);
                doc.select("div.page-break").last().after(templateHtml) ;
                doc.select("div.divTitle").addClass("second-page-header") ;
                doc.select("h2").last().text("Form "+(formIndex+1) + " Condensed Test Report");
            }else
            if(Statistics.getNumberOfForms()>1)
                doc.select("h2").last().text("Form "+(formIndex+1) + " Condensed Test Report");

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
                    String barWidth = tableRow.get(tableRow.size() - 2);
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

    @Override
    public void generateHtmlReport() throws IOException {
        Document doc = generatePdfHtml() ;
        doc.select("div#footer").remove() ;
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {

        Document doc  = generatePdfHtml();
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");

    }

    @Override
    public void generateTxtReport() {

    }

    @Override
    public void init() {

    }
}
