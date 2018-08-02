package Jowil.Reports;

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


        // get the table data from statistics class
        ArrayList<ArrayList<String>> statsTable = Statistics.report4Stats() ;

        // separate the maean row (last row)
        ArrayList<ArrayList<String>> meanRow =new ArrayList<ArrayList<String>> ( statsTable.subList(statsTable.size()-1 , statsTable.size()));

        // adding colspan attribute to first element in each row
        for(ArrayList<String> tableRow:statsTable) {
            tableRow.set(0,tableRow.get(0) + "#colspan='2'" ) ;
            String barWidth = tableRow.get(tableRow.size() - 1);
            String passingPercent = ((DecimalFormat) format).format(Statistics.getPassingPercent()*100) +"%";
            String divHtml = "<div class='emptyBar'> \n"+
                                "<div class='greenBar' style='width:" + barWidth + "'> </div>\n" +
                                "<div class='benchmark' style='width:" + passingPercent + "'> </div>"+
                             "</div>";
            tableRow.add(divHtml) ;
        }

        int startIndex = 0 ;
        int endIndex = (int)Utils.getNumberWithinLimits(statsTable.size() , 0 , NUMBER_OF_ROWS_FIRST_PAGE) ;

        do  {
            ArrayList<ArrayList<String>> pageTable ;
            if(endIndex == statsTable.size()) {
                pageTable = new ArrayList<ArrayList<String>>(statsTable.subList(startIndex, endIndex - 1));
                String rowsHtml = createRowsHtml(pageTable , "grayRow" ,dataCellCommonClass );
                doc.select("tr.headerRow").last().after(rowsHtml) ;
            }
            else {
                pageTable = new ArrayList<ArrayList<String>>(statsTable.subList(startIndex, endIndex));
                String rowsHtml = createRowsHtml(pageTable , "" ,dataCellCommonClass );
                doc.select("tr.headerRow").last().after(rowsHtml + headerHtml);
            }
            startIndex = endIndex ;
            endIndex = (int)Utils.getNumberWithinLimits(  statsTable.size() ,  0 , endIndex+NUMBER_OF_ROWS_BALNK_PAGE)  ;
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
        writeHtmlFile(outputHtmlFolderPath+outputFileName+".html" , doc);
    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {
        Document doc = generatePdfHtml() ;
        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, outputPdfFolderPath+outputFileName+".pdf");
    }

    @Override
    public void generateTxtReport() {

    }
}
