package Jowil.Reports;

import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Report4 extends Report{

    public Report4(){
        workSpacePath = reportsPath + "report5\\" ;
        templatePath = workSpacePath + "report5Template.html";
        outputFileName = "test" ;
        pdfHtmlPath = workSpacePath + outputFileName + ".html";
    }

    @Override
    public void generateHtmlReport() throws IOException {

    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {
        final String dataCellCommonClass = "tg-l711" ;

        File file = new File(templatePath);

        Document doc =  Jsoup.parse(file , "UTF-8") ;

        String headerHtml = doc.select("tr.headerRow").outerHtml();


        // get the table data from statistics class
        ArrayList<ArrayList<String>> statsTable = Statistics.report4Stats() ;

        // separate the maean row (last row)
        ArrayList<ArrayList<String>> meanRow =new ArrayList<ArrayList<String>> ( statsTable.subList(statsTable.size()-1 , statsTable.size()));

        // adding colspan attribute to first element in each row
        for(ArrayList<String> tableRow:statsTable) {
            tableRow.set(0,tableRow.get(0) + "#colspan='2'" ) ;
        }

        int startIndex = 0 ;
        int endIndex = (int)Utils.getNumberWithinLimits(statsTable.size() , 0 , 21) ;

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
            endIndex = (int)Utils.getNumberWithinLimits(endIndex+25 , 0 , statsTable.size())  ;
        }while ((endIndex != startIndex));


        String rowsHtml = createRowsHtml(meanRow , "" ,"MeanRow" );
        doc.select("tr").last().after(rowsHtml) ;

        //remove the header class from the first header so as not to put a page break before it
        doc.select("tr.headerRow").first().removeClass("headerRow") ;


        writeHtmlFile(pdfHtmlPath , doc);
        generatePDF(pdfHtmlPath, workSpacePath+outputFileName+".pdf");
    }

    @Override
    public void generateTxtReport() {

    }
}
