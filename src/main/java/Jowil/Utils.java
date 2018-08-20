package Jowil;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static  double getNumberWithinLimits(double number , double lowerLimit , double upperLimit) {
        if(number < lowerLimit)
            return lowerLimit;
        else  if (number > upperLimit)
            return upperLimit;
        else
            return number ;
    }

    public static ArrayList<ArrayList<String>> transposeStringList(ArrayList<ArrayList<String>> matrixIn) {
        ArrayList<ArrayList<String>> matrixOut = new ArrayList<ArrayList<String>>();
        if (!matrixIn.isEmpty()) {
            int noOfElementsInList = matrixIn.get(0).size();
            for (int i = 0; i < noOfElementsInList; i++) {
                ArrayList<String> col = new ArrayList<String>();
                for (List<String> row : matrixIn) {
                    col.add(row.get(i));
                }
                matrixOut.add(col);
            }
        }

        return matrixOut;
    }


    public static ArrayList<ArrayList<Integer>> transpose(ArrayList<ArrayList<Integer>> matrixIn) {
        ArrayList<ArrayList<Integer>> matrixOut = new ArrayList<ArrayList<Integer>>();
        if (!matrixIn.isEmpty()) {
            int noOfElementsInList = matrixIn.get(0).size();
            for (int i = 0; i < noOfElementsInList; i++) {
                ArrayList<Integer> col = new ArrayList<Integer>();
                for (List<Integer> row : matrixIn) {
                    col.add(row.get(i));
                }
                matrixOut.add(col);
            }
        }

        return matrixOut;
    }

    public static ArrayList<ArrayList<String>> cloneTable (ArrayList<ArrayList<String>> table ) {
        ArrayList<ArrayList<String>> clonedTable = new ArrayList<>() ;
        for(ArrayList<String>tableRow:table) {
            ArrayList<String> clonedRow = (ArrayList<String>)tableRow.clone();
            clonedTable.add(clonedRow) ;
        }
        return clonedTable ;
    }

    public static ArrayList<ArrayList<ArrayList<String>>> clone3D ( ArrayList<ArrayList<ArrayList<String>>> input3D){
        ArrayList<ArrayList<ArrayList<String>>> cloned3D = new ArrayList<>() ;
        for(ArrayList<ArrayList<String>>table:input3D) {
            ArrayList<ArrayList<String>> clonedTable = cloneTable(table);
            cloned3D.add(clonedTable) ;
        }
        return cloned3D ;
    }


    public static String generatePattern(String block ,int lenght ){
        String output = "";
        for(int i = 0 ; i < lenght ; i++)
            output+=block ;
        return output ;
    }

}
