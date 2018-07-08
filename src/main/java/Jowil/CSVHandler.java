package Jowil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVHandler {
    public static class EmptyAnswerKeyException extends Exception{

    }
    public static class InvalidFormNumberException extends Exception{

    }
    public static class EmptyCSVException extends Exception{

    }

    //fields
    public final static Integer STUDENTID=0, STUDENTNAME=1, STUDENTFORM=2, IGNORE=3;
    private static String filePath;
    private static ArrayList<String> detectedQHeaders=new ArrayList<String>();
    private static ArrayList<String> detectedInfoHeaders=new ArrayList<String>();


    //getters and setters
    public static void setFilePath(String filePath) {
        CSVHandler.filePath = filePath;
    }
    public static ArrayList<String> getDetectedQHeaders() {
        return detectedQHeaders;
    }

    public static ArrayList<String> getDetectedInfoHeaders() {
        return detectedInfoHeaders;
    }



    //public methods
    /**
     *
     * @param identifiers student identifiers placed in the order of columns of the CSV file being loaded
     * @throws IOException
     */
    public static  void loadCsv(ArrayList<Integer> identifiers , boolean isHeadersExist, boolean isCorrectAnswersExist , int formsCount) throws IOException, InvalidFormNumberException, EmptyAnswerKeyException {

        BufferedReader input = new BufferedReader(new FileReader(filePath));
        String line = null;
        ArrayList<ArrayList<String>> studentsAnswers = new ArrayList<ArrayList<String>>();
        ArrayList<String> studentNames = new ArrayList<String>();
        ArrayList<String> studentIDs = new ArrayList<String>();
        ArrayList<Integer> studentForms = new ArrayList<Integer>();

        if (isHeadersExist)//ignore headers
            input.readLine();


        if (isCorrectAnswersExist && (line = input.readLine()) != null) { //correct answers
            ArrayList<ArrayList<String>> correctAnswers = new ArrayList<ArrayList<String>>();
            ArrayList<String> answers=cropArray(line.split(","), identifiers.size());
            if(!isAllCellsFilled(answers))
                throw new EmptyAnswerKeyException();
            correctAnswers.add(answers);
            Statistics.setCorrectAnswers(correctAnswers);
        }
        while ((line = input.readLine()) != null) { //students answers
            String[] row = line.split(",");
            updateIdentifiers(studentIDs, studentNames, studentForms, row, identifiers,formsCount);
            studentsAnswers.add(cropArray(line.split(","), identifiers.size()));
        }

        //initialize internal fields with parsed data
        Statistics.setStudentAnswers(studentsAnswers);
        Statistics.setIdentifierMode(Statistics.AUTOMODE);

        if (studentIDs.size() != 0) {
            Statistics.setStudentIDs(studentIDs);
            Statistics.setIdentifierMode(Statistics.IDMODE);
        }

        if (studentNames.size() != 0) {
            Statistics.setStudentNames(studentNames);
            if (studentIDs.size() == 0)
                Statistics.setIdentifierMode(Statistics.NAMEMODE);
        }

        if (studentForms.size() == 0) {
            for (int i = 0; i < studentsAnswers.size(); i++)
                studentForms.add(0);
        }
        Statistics.setStudentForms(studentForms);

    }


    public static int loadAnswerKeys(String answersFilePath) throws IOException, EmptyAnswerKeyException {
        BufferedReader input = new BufferedReader(new FileReader(answersFilePath));
        String line;
        ArrayList<ArrayList<String>> correctAnswers=new ArrayList<ArrayList<String>>();
        while ((line = input.readLine()) != null) {
            String [] answers=line.split(",");
            if(!isAllCellsFilled(answers))
                throw new EmptyAnswerKeyException();
            ArrayList<String> answerKey= new ArrayList<String>(Arrays.asList(answers));
            correctAnswers.add(answerKey);
        }
        Statistics.setCorrectAnswers(correctAnswers);
        return correctAnswers.size();
    }


    public static boolean detectHeaders() throws IOException, EmptyCSVException {
        BufferedReader input = new BufferedReader(new FileReader(filePath));
        String line;
        if((line = input.readLine()) != null){
            String headers[]=line.split(",");
            if(!isAllCellsLarge(headers))
                return false;
            classifyHeaders(headers);
        }
        else
            throw new EmptyCSVException();

        return true;
    }


    //helper functions
    private static ArrayList<String> cropArray(String [] original,int skipCols){
        ArrayList<String> cropped=new ArrayList<String>();
        for(int i=skipCols;i<original.length;i++)
            cropped.add(original[i]);
        return cropped;
    }

    private static  void updateIdentifiers(ArrayList<String> studentIDs, ArrayList<String> studentNames,ArrayList<Integer> studentForms, String [] row,ArrayList<Integer> identifiers, int formsCount) throws InvalidFormNumberException {
        for(int i=0;i<identifiers.size();i++){
            if(identifiers.get(i)==STUDENTID)
                studentIDs.add(row[i]);
            else if(identifiers.get(i)==STUDENTNAME)
                studentNames.add(row[i]);
            else if(identifiers.get(i)==STUDENTFORM) {
                int f=Integer.parseInt(row[i])-1;
                if(f>=formsCount || f<0)
                    throw new InvalidFormNumberException();
                studentForms.add(f);
            }
        }
    }
    private static boolean isAllCellsFilled(String [] cells){
        for(String cell: cells){
            if(cell.length()==0)
                return false;
        }
        return true;
    }
    private static boolean isAllCellsFilled(ArrayList<String> cells){
        for(String cell: cells){
            if(cell.length()==0)
                return false;
        }
        return true;
    }

    private static boolean isAllCellsLarge(String [] cells){
        for(String cell: cells){
            if(cell.length()<2)
                return false;
        }
        return true;
    }
    private static void classifyHeaders(String [] headers){
        Pattern pattern = Pattern.compile("\\D+\\d+");
        int i;
        for(i=0;i<headers.length;i++){
            if(isQHeader(headers[i],pattern))
                break;
            detectedInfoHeaders.add(headers[i]);
        }

        for(;i<headers.length;i++)
            detectedQHeaders.add(headers[i]);

    }

    public static boolean isQHeader(String header, Pattern pattern){
        Matcher matcher = pattern.matcher(header);
        return matcher.matches();
    }


}

