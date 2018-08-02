package Jowil.Reports;

import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Report2 extends Report {


    public Report2(){
        workSpacePath = reportsPath + "report2\\" ;
        templatePath = workSpacePath + "report2Template.html";
        outputFileName = "Report2" ;
        pdfHtmlPath = workSpacePath+outputFileName+".html" ;
    }

    private void fillGeneralStatsReport2 (Document doc , Map<String , String> generalStatsMap) {
        doc.select("td.NumberOfStudents").last().text(generalStatsMap.get("Number Of Students")) ;
        doc.select("td.MaxPossibleScore").last().text(generalStatsMap.get("Maximum Possible Score")) ;

        //Basic Statistics
        doc.select("td.Mean").last().text(generalStatsMap.get("Mean")) ;
        doc.select("td.HighestScore").last().text(generalStatsMap.get("Highest Score")) ;
        doc.select("td.LowestScore").last().text(generalStatsMap.get("Lowest Score")) ;
        //Dispersion
        doc.select("td.StandardDeviation").last().text(generalStatsMap.get("Standard Deviation")) ;
        doc.select("td.ScoreRange").last().text(generalStatsMap.get("Range")) ;
        doc.select("td.Median").last().text(generalStatsMap.get("Median")) ;


        //Test Reliability
        doc.select("td.Kuder-RichardsonFormula20").last().text(generalStatsMap.get("Kuder-Richardson Formula 20")) ;

    }
    private void fillResponseFreqHeaders(Document doc , int questionIndex) {
        String questionChoicesHtml = "" ;
        ArrayList<String> questionChoices =  Statistics.getSpecificQuestionChoices(questionIndex) ;
        for(String qChoice: questionChoices )
            questionChoicesHtml+= "<th>" +qChoice+ "</th>\n";
        doc.select("th.total").last().before(questionChoicesHtml);
        doc.select("th.responseFreq").last().attr("colspan" , String.valueOf(questionChoices.size())) ;

    }


    @Override
    public void generateHtmlReport() throws IOException {

        File file = new File(templatePath);
        Document doc = Jsoup.parse(file, "UTF-8");
        doc.select("div#footer").remove() ;

        String tableHtml = doc.select("table.t2").last().outerHtml() ;
        String templateBodyHtml = doc.select("div#template").html() ;

        for (int formIndex = 0; formIndex < Statistics.getNumberOfForms() ; formIndex++) {
            if(formIndex>0) {
                doc.select("table").last().after(templateBodyHtml);
                doc.select("div.divTitle").addClass("second-page-header") ;
                doc.select("h2").last().text("Form "+(formIndex+1) + " Condensed Test Report");
            }
            fillGeneralStatsReport2(doc, Statistics.report2GeneralStats(formIndex));

            ArrayList<ArrayList<ArrayList<String>>> statsTables = Statistics.report2TableStats(formIndex);
            int questionIndex = 0;

            for (ArrayList<ArrayList<String>> table : statsTables) {
                if(questionIndex!=0)
                   doc.select("table").last().after(tableHtml);
                fillResponseFreqHeaders(doc, questionIndex);
                String rowsHtml = createRowsHtml(table, ";grayRow", "");
                doc.select("tr.bottom-header-row").last().after(rowsHtml);
                questionIndex += table.size();
            }
        }
        writeHtmlFile(outputHtmlFolderPath+outputFileName+".html" , doc);

    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {
        File file = new File(templatePath);
        Document doc = Jsoup.parse(file, "UTF-8");

        updateTemplateDate(doc); // updates the date of the footer to the current date

        final int ROWS_IN_BLANK_PAGE = 37 ;
        final int ROWS_IN_FIRST_PAGE = 18 ;
        final int NUMBER_OF_ROWS_FOR_TABLE_HEADER = 6 ;
        final int MINIMUM_REMAINING_ROWS = 7 +NUMBER_OF_ROWS_FOR_TABLE_HEADER;

        final String pageBreakHtml= "<div class='page-break'></div>\n" ;

        String tableHtml = doc.select("table.t2").last().outerHtml() ;
        String templateBodyHtml = doc.select("div#template").html() ;
        System.out.println(templateBodyHtml);

        for (int formIndex = 0; formIndex < Statistics.getNumberOfForms() ; formIndex++) {
            if(formIndex>0) {
                doc.select("table").last().after(pageBreakHtml);
                doc.select("div.page-break").last().after(templateBodyHtml) ;
                doc.select("div.divTitle").addClass("second-page-header") ;
                doc.select("h2").last().text("Form "+(formIndex+1) + " Condensed Test Report");
            }
            fillGeneralStatsReport2(doc, Statistics.report2GeneralStats(formIndex));
            ArrayList<ArrayList<ArrayList<String>>> statsTables = Statistics.report2TableStats(formIndex);
            int questionIndex = 0;
            int remainingRows = ROWS_IN_FIRST_PAGE;
            for (ArrayList<ArrayList<String>> table : statsTables) {
                //create new table unless its first time
                if (questionIndex != 0) {

                    //check if page break is needed
                    if (remainingRows < MINIMUM_REMAINING_ROWS) {
                        doc.select("table").last().after(pageBreakHtml);
                        remainingRows = ROWS_IN_BLANK_PAGE - 2;
                        doc.select("div.page-break").last().after(tableHtml);

                    } else {
                        remainingRows -= NUMBER_OF_ROWS_FOR_TABLE_HEADER;
                        doc.select("table").last().after(tableHtml);
                    }
                }
                fillResponseFreqHeaders(doc, questionIndex);

                //start and end indeces for questions to be shown in the page
                int startIndex = 0;
                int endIndex = (int) Utils.getNumberWithinLimits(table.size(), 0, remainingRows);
                do {
                    System.out.println("in the while looop");
                    //create html table
                    ArrayList<ArrayList<String>> pageTable = new ArrayList<ArrayList<String>>(table.subList(startIndex, endIndex));
                    String rowsHtml = createRowsHtml(pageTable, ";grayRow", "");
                    doc.select("tr.bottom-header-row").last().after(rowsHtml);

                    //update remaining rows counter
                    int numberOfInsertedRows = endIndex - startIndex;
                    remainingRows -= numberOfInsertedRows;
                    if (remainingRows < MINIMUM_REMAINING_ROWS && endIndex != table.size()) {
                        doc.select("table").last().after(pageBreakHtml);
                        //insert a new table in the new page
                        doc.select("div.page-break").last().after(tableHtml);
                        fillResponseFreqHeaders(doc, questionIndex);
                        remainingRows = ROWS_IN_BLANK_PAGE;
                    }
                    startIndex = endIndex;
                    endIndex = (int) Utils.getNumberWithinLimits(table.size(), 0, endIndex + remainingRows);
                } while (startIndex != endIndex);
                questionIndex += table.size();
            }
        }
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath , outputPdfFolderPath+outputFileName+".pdf");

    }

    @Override
    public void generateTxtReport() {

    }
}
