package Jowil.Reports.Utils;

import Jowil.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    public static final String NEW_LINE = "\r\n" ;

    public static String generateLine(ArrayList<String> values)  {
        return generateLine(values, DEFAULT_SEPARATOR, ' ');
    }

    public static String generateLine(ArrayList<String> values, char separators) {
        return generateLine(values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static String generateLine(ArrayList<String> values, char separators, char customQuote) {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append(NEW_LINE);

        return sb.toString();
    }



    public static  String generateTable(ArrayList<ArrayList<String>> table, char separator , String title)  {

        String tableCsv = "";

        if(!title.equals(""))
            tableCsv += title+NEW_LINE ;
        for (ArrayList<String> tableRow : table)
            tableCsv += generateLine(tableRow, separator,'"');
        return tableCsv;
    }
    public static  String generateTable(ArrayList<ArrayList<String>> table, char separator)  {
        return generateTable(table , separator , "");
    }
    public static String generateTitleLine (String title , char separator , int pageWidth ,int paddingBelow){
        int numberOfTitleWords = title.split(" ").length;

       return Utils.generatePattern(String.valueOf(separator), pageWidth/2-numberOfTitleWords/2)+title+Utils.generatePattern(NEW_LINE , paddingBelow);
    }

    public static int calcTableWidth (ArrayList<ArrayList<String>> table){
        return table.get(0).size() ;
    }

    public static int calcPageWidth(ArrayList<ArrayList<ArrayList<String>>> tables) {
        int maxWidth = 0 ;
        for (ArrayList<ArrayList<String>> table :tables){
            int tableWidth = calcTableWidth(table) ;
            if(tableWidth>maxWidth)
                maxWidth = tableWidth;
        }
        return maxWidth ;
    }

    public static String stackTablesV (ArrayList<String> tables , int paddingBetweenTables ) {
        String outputTable = "";
        for(String table : tables) {
            outputTable += table ;
            outputTable += Utils.generatePattern( NEW_LINE , paddingBetweenTables) ;
        }

        return outputTable ;
    }



    public static void writeCsvToFile(String csv, String fileName) {
        try {
            PrintWriter out = new PrintWriter(fileName);
            out.println(csv);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}