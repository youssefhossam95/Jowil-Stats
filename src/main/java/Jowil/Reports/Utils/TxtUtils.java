package Jowil.Reports.Utils;

import Jowil.Utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.apache.commons.math3.stat.StatUtils.sum;

public class TxtUtils {

    public static String newLine = "\r\n" ;

    public static ArrayList<Integer> calcCellsWidths ( ArrayList<ArrayList<String>>  table , int cellHorizontalPadding ) {
        ArrayList<Integer> cellsWidths = new ArrayList<>();
        for (int colIndex = 0; colIndex < table.get(1).size(); colIndex++) {
            int colMaxLength = 0;
            for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
                int tableCellLenght = table.get(rowIndex).get(colIndex).length();
                if (tableCellLenght > colMaxLength)
                    colMaxLength = tableCellLenght;
            }
            cellsWidths.add(colMaxLength + cellHorizontalPadding * 2);
        }
        return cellsWidths;
    }

    public static int calcTableWidth(ArrayList<ArrayList<String>> table , int cellHorizontalPadding){
      return (int) sum(calcCellsWidths(table , cellHorizontalPadding).stream().mapToDouble(d -> d).toArray());
    }

    public static String generateTitleLine (String title , int pageWidth , int paddingBelow) {
        String padding = Utils.generatePattern(newLine , paddingBelow) ;
        String spaceBeofore =   Utils.generatePattern( " " , (int)Math.floor((pageWidth-title.length())/2)) ;
        return spaceBeofore+ title + padding ;
    }

    public static String generateTxtTableAlignCenter (ArrayList<ArrayList<String>> table , String title , int cellHorizontalPadding ) {

        String txtTable = "" ;
        ArrayList<Integer> cellsWidths = calcCellsWidths(table , cellHorizontalPadding) ;
        int tableWidth = calcTableWidth( table ,  cellHorizontalPadding) ;
        if(title!="")
            txtTable+=title+newLine;
        txtTable+= Utils.generatePattern("-" , tableWidth)  +newLine  ;
        for ( int rowIndex = 0 ; rowIndex < table.size() ;rowIndex++ ) {
            for( int colIndex = 0 ; colIndex < table.get(1).size() ; colIndex++) {
                String cellString = table.get(rowIndex).get(colIndex) ;
                int numberOfSpacesNeeded = (int)Math.ceil((double)(cellsWidths.get(colIndex)- cellString.length())/2) ;
                if(colIndex!=0) {
                    String prevString =  table.get(rowIndex).get(colIndex-1) ;
                    numberOfSpacesNeeded += Math.floor((cellsWidths.get(colIndex - 1)  - prevString.length()) / 2) ;
                }
                txtTable += Utils.generatePattern(" ",numberOfSpacesNeeded) + cellString ;
            }
            // add line after the first Row
            if (rowIndex ==0)
                txtTable+=newLine + Utils.generatePattern("-" , tableWidth)    ;

            txtTable+=newLine ;

        }
        return txtTable ;
    }

    public static String generateTxtTableAlignLR (ArrayList<ArrayList<String>> table , String title , int cellHorizontalPadding ) {
        String txtTable = "" ;
        int paddingBetweenTables = 5 ;
        ArrayList<Integer> cellsWidths = calcCellsWidths(table , cellHorizontalPadding) ;
        int tableWidth = calcTableWidth( table ,  cellHorizontalPadding) ;
        if(title!="")
            txtTable+= title +newLine ;
        txtTable+= Utils.generatePattern("_" , (int)tableWidth)  +newLine  ;
        for ( int rowIndex = 0 ; rowIndex < table.size() ;rowIndex++ ) {
            for( int colIndex = 0 ; colIndex < table.get(1).size() ; colIndex++) {
                String cellString = table.get(rowIndex).get(colIndex) ;
                int numberOfSpacesNeeded = 0 ;
                if(colIndex%2==0) {
                    if(colIndex > 0)
                        numberOfSpacesNeeded+=paddingBetweenTables ;
//                    txtTable+= "    |";
                }
                else {
                    String prevString =  table.get(rowIndex).get(colIndex-1) ;
                    numberOfSpacesNeeded+= cellsWidths.get(colIndex) + cellsWidths.get(colIndex-1) - prevString.length() -cellString.length()-paddingBetweenTables ;
                }
                txtTable += Utils.generatePattern(" ",numberOfSpacesNeeded) + cellString ;
            }
            // add line after the first Row
//            if (rowIndex ==0)
                txtTable+=newLine + Utils.generatePattern("-" , tableWidth)    ;

            txtTable+=newLine ;

        }
        return txtTable ;
    }

    public static String stackTablesV (ArrayList<String> tables , int paddingBetweenTables ) {
        String outputTable = "";
        for(String table : tables) {
            outputTable += table ;
            outputTable += Utils.generatePattern(newLine , paddingBetweenTables) ;
        }

        return outputTable ;
    }

    public static void writeTxtToFile(String txt , String fileName )
    {
        try {
            PrintWriter out = new PrintWriter(fileName);
            out.println(txt);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}