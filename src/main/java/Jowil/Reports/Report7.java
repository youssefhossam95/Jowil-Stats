package Jowil.Reports;

import Jowil.Reports.Utils.TxtUtils;
import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import sun.nio.cs.UTF_32LE;

import java.io.IOException;
import java.util.ArrayList;

public class Report7 extends Report {

    ArrayList<ArrayList<ArrayList<ArrayList<String>>>> formsTableStats ;
    String [] tableTitles = {"Bad Questions" , "Hardest Questions" , "Easyiest Questions"} ;
    @Override
    public void generateHtmlReport() throws IOException {

    }

    @Override
    public void generatePdfReport() throws IOException, DocumentException {

    }

    private ArrayList<ArrayList<String>> getTableWithHeaders (ArrayList<ArrayList<String>> table , int tableIndex) {
        ArrayList<ArrayList<String>> tableWithHeaders = Utils.cloneTable(table) ;
        ArrayList<String> headers = new ArrayList<>();
        if(tableIndex ==0 ) {
            headers.add("Question Name");  headers.add("Point Biserial"); headers.add("Smart Distractors");
            headers.add("total"); headers.add("Upper 25%"); headers.add("Lower 25%");
        }
        else if (tableIndex == 1) {
            headers.add("Question Name"); headers.add("Difficulity (0-10)"); headers.add("Correct Response Percentage");
            headers.add("Distractors") ;
        }
        else if (tableIndex == 2 ) {
            headers.add("Question Name"); headers.add("Difficulity (0-10)"); headers.add("Correct Response Percentage");
            headers.add("Non Distractors") ;
        }
        tableWithHeaders.add(0,headers);
        return  tableWithHeaders ;
    }
    @Override
    public void generateTxtReport() {

        int CHP = 2 ;
        int formIndex = 0 ;
        ArrayList<ArrayList<ArrayList<String>>> statsTables = formsTableStats.get(formIndex) ;
        ArrayList<String> txtTables = new ArrayList<>( );
        for(int tableIndex = 0 ; tableIndex < statsTables.size() ; tableIndex++) {
            ArrayList<ArrayList<String>> tableWithHeaders = getTableWithHeaders(statsTables.get(tableIndex) , tableIndex);
            txtTables.add(TxtUtils.generateTxtTableAlignCenter(tableWithHeaders,tableTitles[tableIndex], CHP , false) );
        }
        String outputTxt = TxtUtils.stackTablesV(txtTables ,2) ;
        System.out.println(outputTxt);
    }

    @Override
    public void generatePrintablePdfReport() throws IOException, DocumentException {

    }

    @Override
    public void generateCsvReport() throws IOException {

    }

    @Override
    public void generateTsvReprot() {

    }

    @Override
    public void init() {
        formsTableStats = Statistics.report7Stats() ;
    }
}
