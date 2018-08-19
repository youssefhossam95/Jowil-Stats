package Jowil.Reports;

import Jowil.Reports.Utils.TxtUtils;
import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Report2 extends Report {

    private ArrayList<Map<String , String>> formsStatsMaps ;
    private ArrayList<ArrayList<ArrayList<ArrayList<String>>>> formsStatsTables ;
    ArrayList<ArrayList<ArrayList<ArrayList<String>>>> formsStatsPrintableTables ;

    public Report2(){
        workSpacePath = reportsPath + "report2\\" ;
        templatePath = workSpacePath + "report2Template.html";
        outputFileName = "Report2" ;
        pdfHtmlPath = workSpacePath+outputFileName+".html" ;
    }

        private Document generatePdfHtml(boolean isPrintable) throws IOException {
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
                fillGeneralStatsReport2(doc , formsStatsMaps.get(formIndex));
                ArrayList<ArrayList<ArrayList<String>>> statsTables ;
                if(isPrintable)
                    statsTables = formsStatsPrintableTables.get(formIndex);
                else
                    statsTables = formsStatsTables.get(formIndex);


//            fillGeneralStatsReport2(doc, Statistics.report2GeneralStats(formIndex));
//            ArrayList<ArrayList<ArrayList<String>>> statsTables = Statistics.report2TableStats(formIndex);
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
            return doc ;
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
            fillGeneralStatsReport2(doc , formsStatsMaps.get(formIndex));
            ArrayList<ArrayList<ArrayList<String>>> statsTables = formsStatsTables.get(formIndex);

//            fillGeneralStatsReport2(doc, Statistics.report2GeneralStats(formIndex));
//
//            ArrayList<ArrayList<ArrayList<String>>> statsTables = Statistics.report2TableStats(formIndex);
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
        writeHtmlFile(outputFormatsFolderPaths[ReportsHandler.HTML]+outputFileName+".html" , doc);

    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml(false) ;
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath , outputFormatsFolderPaths[ReportsHandler.PDF]+outputFileName+".pdf");

    }

    private ArrayList<ArrayList<String>> preprocessMap (Map<String , String> statsMap){
        ArrayList<ArrayList<String>> mapAsTable  = new ArrayList<>();
        ArrayList<String> tableRow = new ArrayList<>() ;

        tableRow.add("Number Of Students") ; tableRow.add(statsMap.get("Number Of Students")) ;
        tableRow.add("Mean") ; tableRow.add(statsMap.get("Mean")) ;
        tableRow.add("Lowest Score") ; tableRow.add(statsMap.get("Lowest Score")) ;

        mapAsTable.add(tableRow) ;
        tableRow = new ArrayList<>() ;

        tableRow.add("Maximum Possible Score") ; tableRow.add(statsMap.get("Maximum Possible Score")) ;
        tableRow.add("Median") ; tableRow.add(statsMap.get("Median"));
        tableRow.add("Highest Score") ; tableRow.add(statsMap.get("Highest Score")) ;

        mapAsTable.add(tableRow) ;
        tableRow = new ArrayList<>() ;

        tableRow.add("Score Range") ; tableRow.add(statsMap.get("Range")) ;
        tableRow.add("Standard Deviation") ; tableRow.add(statsMap.get("Standard Deviation"));
        tableRow.add("KR20") ; tableRow.add(statsMap.get("Kuder-Richardson Formula 20")) ;

        mapAsTable.add(tableRow) ;

        return mapAsTable ;
    }

    private ArrayList<String> getHeaders ( int questionIndex ) {

        ArrayList<String>tableHeaders = new ArrayList<>();
        tableHeaders.add("No.") ; tableHeaders.add("Question") ; tableHeaders.add("Correct Answer") ;

        ArrayList<String> questionChoices =  Statistics.getSpecificQuestionChoices(questionIndex) ;
        for(String qChoice: questionChoices )
            tableHeaders.add(qChoice) ;
        tableHeaders.add("Non Distractors") ;
        tableHeaders.add("Point Biserial") ; tableHeaders.add("total") ; tableHeaders.add("lower 27%");
        tableHeaders.add("upper 27%") ;

        return tableHeaders ;
    }

    private int calcTxtPageWidth(int chpCenterTable  ,int chpLRTable )  {
        ArrayList<Integer> tabelsChp  = new ArrayList<>( );
        ArrayList<ArrayList<ArrayList<String>>> firstFormTables = new ArrayList<>();

        int questionIndex = 0 ;
        for(ArrayList<ArrayList<String>> table:formsStatsPrintableTables.get(0)) {
            ArrayList<ArrayList<String>> tableWithHeaders = Utils.cloneTable(table);
            tableWithHeaders.add(0, getHeaders(questionIndex));
            tableWithHeaders = cleanTable(tableWithHeaders) ;
            firstFormTables.add(tableWithHeaders) ;
            tabelsChp.add(chpCenterTable) ;
        }
        firstFormTables.add( preprocessMap( formsStatsMaps.get(0)) )  ;
        tabelsChp.add(chpLRTable) ;
        return  TxtUtils.calcPageWidth(firstFormTables , tabelsChp) ;
    }
    @Override
    public void generateTxtReport() {
        final int CHP_CENTER_TABLE = 3 ; // cell horizontal padding for center aligned tables
        final  int CHP_LR_TABLE = 5 ; // cell horizontal padding for Tables aligned Left Right
        final int PADDING_BETWEEN_TABLES = 2 ;

        String outputTxt="" ;


        int pageWidth = calcTxtPageWidth(CHP_CENTER_TABLE , CHP_LR_TABLE);

        for(int formIndex = 0 ; formIndex < Statistics.getNumberOfForms() ; formIndex++) {
            Map<String, String> statsMap = formsStatsMaps.get(formIndex);
            ArrayList<ArrayList<ArrayList<String>>> formsStatsTables = formsStatsPrintableTables.get(formIndex);



            ArrayList<ArrayList<String>> mapAsTable = preprocessMap(statsMap);
            String generalStatsTxt = TxtUtils.generateTxtTableAlignLR(mapAsTable, "", CHP_LR_TABLE);

            String reportTitle = "Condenced Test Report"  ;
            if(Statistics.getNumberOfForms()>1)
                reportTitle = "Form"+(formIndex+1) + " " + reportTitle ;

            String txtTitle = TxtUtils.generateTitleLine(reportTitle,
                    pageWidth,2) ;

            if(formIndex>0)
                outputTxt += Utils.generatePattern("#" , pageWidth)+TxtUtils.newLine;

            outputTxt+= txtTitle ;

            outputTxt+= generalStatsTxt  ;

            String legend = "* : Distractor"+TxtUtils.newLine ;

            outputTxt+= Utils.generatePattern(TxtUtils.newLine , 2 ) + legend ;

            ArrayList<String> txtTables = new ArrayList<>();


            int questionIndex = 0;

            for(int tableIndex = 0 ; tableIndex< formsStatsTables.size() ; tableIndex++ ) {
                ArrayList<ArrayList<String>> statsTable = cleanTable(formsStatsTables.get(tableIndex));
                ArrayList<ArrayList<String>> tableWithHeaders = Utils.cloneTable(statsTable);
                tableWithHeaders.add(0, getHeaders(questionIndex));
                txtTables.add(TxtUtils.generateTxtTableAlignCenter(tableWithHeaders, "", CHP_CENTER_TABLE , false));
                questionIndex += statsTable.size() ;
             }

             outputTxt += TxtUtils.stackTablesV(txtTables, PADDING_BETWEEN_TABLES) ;
            }
//        String outputTxt = TxtUtils.stackTablesV(tables, PADDING_BETWEEN_TABLES) ;
        System.out.println(outputTxt);

        TxtUtils.writeTxtToFile(outputTxt , outputFormatsFolderPaths[ReportsHandler.TXT]+outputFileName+".txt");

    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml(true) ;
        updateTemplateDate(doc); // updates the date of the footer to the current date

        String printableLegendHtml = "<div class=\"wrapper\">\n" +
                "            <span><strong> * : </strong></span>\n" +
                "            <span class=\" second\"> Distractor</span>\n" +
                "        </div>\n" +
                "       \n" +
                "        <div class=\"wrapper\" style=\"margin-bottom: 30px\">\n" +
                "            <span>\n" +
                "                <strong>\n" +
                "                    <u>under Line</u>:\n" +
                "                </strong>\n" +
                "            </span>\n" +
                "            <span class=\" second\"> Correct Answer</span>\n" +
                "        </div>\n" ;
        String correctAnswerHtml = "<th rowspan='2' >Correct <br> Answer</th>" ;
        String nonDistractorHtml = "<th rowspan='2' >Non <br> Distractors</th>" ;

        doc.select("div.legend").html(printableLegendHtml) ;
        doc.select("th.question").after(correctAnswerHtml) ;
        doc.select("th.point-biserial").before(nonDistractorHtml) ;

        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath , outputFormatsFolderPaths[ReportsHandler.PRINTABLE_PDF]+outputFileName+".pdf");
    }

    @Override
    public void init()  {
        formsStatsMaps = new ArrayList<>() ;
        formsStatsTables = new ArrayList<>();
        formsStatsPrintableTables = new ArrayList<>();
        for (int formIndex = 0 ; formIndex < Statistics.getNumberOfForms() ; formIndex++) {
            formsStatsMaps.add(Statistics.report2GeneralStats(formIndex)) ;
            formsStatsTables.add(Statistics.report2TableStats(formIndex)) ;
            formsStatsPrintableTables.add(Statistics.report2PrintableStats(formsStatsTables.get(formIndex) , formIndex)) ;
        }
    }
}
