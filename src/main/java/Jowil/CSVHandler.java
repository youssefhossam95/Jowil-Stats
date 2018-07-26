package Jowil;

import org.omg.CORBA.DynAnyPackage.Invalid;
import sun.swing.plaf.synth.DefaultSynthStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVHandler {
    public static class EmptyAnswerKeyException extends Exception{

    }
    public static class InvalidFormNumberException extends Exception{


        InvalidFormNumberException(int rowNumber){
            super("Student in row "+rowNumber+ " has an invalid form number.");
        }
    }
    public static class EmptyCSVException extends Exception{

    }


    //fields
    public final static Integer STUDENTID=0, STUDENTNAME=1, STUDENTFORM=2, STUDENTIDCONT=3,IGNORE=4;
    private static String filePath;
    private static ArrayList<String> detectedQHeaders=new ArrayList<String>();
    private static ArrayList<String> detectedInfoHeaders=new ArrayList<String>();
    private static ArrayList<Group> detectedGroups= new ArrayList<Group>();
    private static ArrayList<Integer> infoHeadersTypes=new ArrayList<>();
    private static int scoresStartIndex; //index of column where score columns (subj and non subj) start
    private static int subjStartIndex=-1;
    private static int subjEndIndex=-1;
    private static int subjQuestionsCount=0;
    private static int formsCount=2;
    private static int identifierColStartIndex;
    private static int identifierColEndIndex;
    private static int formColIndex;
    private static boolean isAutoIDMode=false;
    private static int questionsColStartIndex;


    //getters and setters
    public static void setFilePath(String filePath) {
        CSVHandler.filePath = filePath;
    }
    public static ArrayList<String> getDetectedQHeaders() {
        return detectedQHeaders;
    }
    public static void setDetectedQHeaders(ArrayList<String> detectedQHeaders) {
        CSVHandler.detectedQHeaders = detectedQHeaders;
    }


    public static ArrayList<String> getDetectedInfoHeaders() {
        return detectedInfoHeaders;
    }

    public static ArrayList<Group> getDetectedGroups() {
        return detectedGroups;
    }

    public static void setDetectedGroups(ArrayList<Group> detectedGroups) {
        CSVHandler.detectedGroups = detectedGroups;
    }
    public static int getSubjQuestionsCount() {
        return subjQuestionsCount;
    }

    public static void setSubjQuestionsCount(int subjQuestionsCount) {
        CSVHandler.subjQuestionsCount = subjQuestionsCount;
    }
    public static int getFormsCount() {
        return formsCount;
    }

    public static void setFormsCount(int formsCount) {
        CSVHandler.formsCount = formsCount;
    }

    public static void setInfoHeadersTypes(ArrayList<Integer> infoHeadersTypes) {
        CSVHandler.infoHeadersTypes = infoHeadersTypes;
    }

    public static void setAutoIDMode(boolean autoIDMode) {
        isAutoIDMode = autoIDMode;
    }
    public static void setIdentifierColStartIndex(int identifierColStartIndex) {
        CSVHandler.identifierColStartIndex = identifierColStartIndex;
    }

    public static void setIdentifierColEndIndex(int identifierColEndIndex) {
        CSVHandler.identifierColEndIndex = identifierColEndIndex;
    }



    public static void setFormColIndex(int formColIndex) {
        CSVHandler.formColIndex = formColIndex;
    }

    public static void setQuestionsColStartIndex(int questionsColStartIndex) {
        CSVHandler.questionsColStartIndex = questionsColStartIndex;
    }

    //public methods
    /**
     *
     * @throws IOException
     */
    public static  void loadCsv(boolean isHeadersExist ) throws IOException, InvalidFormNumberException, EmptyAnswerKeyException {

        BufferedReader input = new BufferedReader(new FileReader(filePath));
        String line = null;


        //initialization
        Statistics.setStudentIdentifier(new ArrayList<String>());
        Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
        Statistics.setStudentForms(new ArrayList<Integer>());
        Statistics.setSubjScores(new ArrayList<ArrayList<Double>>());

        if (isHeadersExist)//ignore headers
            input.readLine();


        int rowNumber=1;

        //parse students data
        while ((line = input.readLine()) != null) {
            String[] row = line.split(",");
            Statistics.getStudentAnswers().add(cropArray(row, questionsColStartIndex,scoresStartIndex));
            updateStudentIdentifier(row);
            updateStudentForms(row,rowNumber);
            updateSubjScores(row);
            rowNumber++;
        }
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

        formsCount=correctAnswers.size();

        return formsCount;
    }


    public static boolean processHeaders() throws IOException, EmptyCSVException {
        detectedGroups=new ArrayList<>();
        detectedQHeaders=new ArrayList<>();
        detectedInfoHeaders=new ArrayList<>();
        subjStartIndex=-1;
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

        Statistics.setQuestionNames(detectedQHeaders);
        return true;
    }



    public static boolean isCSVFileEmpty(File file) throws IOException {
        return new BufferedReader(new FileReader(file)).readLine()==null;
    }

    public static void addRealIDGroups(ArrayList<Group> realIDGroups){
        realIDGroups.addAll(detectedGroups); //append existing groups to realIDGroups
        updateGroupsAndQHeaders(realIDGroups);
    }


    public static void updateGroupsAndQHeaders(ArrayList<Group> newGroups){

        detectedGroups=newGroups;
        detectedQHeaders=new ArrayList<>();
        for (Group group : detectedGroups) {
            for (int i = 0; i < group.getqCount(); i++)
                detectedQHeaders.add(group.getName() + (i + 1));
        }
        Statistics.setQuestionNames(detectedQHeaders);
    }

    public static int getLinesCount(String filePath) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filePath));
        int count=0;
        while(input.readLine() != null)
            count++;
        return count;
    }


    ////////////////////helper functions
    private static ArrayList<String> cropArray(String [] original,int skipCols,int end){
        ArrayList<String> cropped=new ArrayList<String>();
        for(int i=skipCols;i<end;i++)
            cropped.add(original[i]);
        return cropped;
    }


    private static void updateSubjScores(String [] row){

        //no subj questions
        if(subjStartIndex==-1)
            return;

        ArrayList<Double> studentSubScores= new ArrayList<Double>();

        for(int i=subjStartIndex;i<subjEndIndex;i++)
            studentSubScores.add(Double.parseDouble(row[i]));

        Statistics.getSubjScores().add(studentSubScores);
    }


    private static void updateStudentForms( String [] row, int rowNumber) throws InvalidFormNumberException {

        if(formColIndex<0){
            Statistics.getStudentForms().add(0);
            return;
        }

        int f;
        try {
             f = Integer.parseInt(row[formColIndex].trim()) - 1;
        }catch (NumberFormatException e){
            throw new InvalidFormNumberException(rowNumber);
        }

        if(f>=formsCount || f<0)
            throw new InvalidFormNumberException(rowNumber);

        Statistics.getStudentForms().add(f);

    }

    private static void updateStudentIdentifier(String [] row){

        StringBuilder identifier=new StringBuilder();
        for(int i=identifierColStartIndex;i<identifierColEndIndex;i++)
            identifier.append(row[i]);

        Statistics.getStudentIdentifier().add(identifier.toString());
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
        Pattern groupsPattern = Pattern.compile(".*\\d+");

        //info headers
        int i;
        for(i=0;i<headers.length;i++){
            if(!(headers[i].toLowerCase().trim().startsWith("id")) && isQHeader(headers[i],groupsPattern)) //reached groups start
                break;
            detectedInfoHeaders.add(headers[i]);
        }

        questionsColStartIndex=detectedInfoHeaders.size();
        //search for scores section start (if exists)
        scoresStartIndex=headers.length-1;
        while(scoresStartIndex>=0 && (headers[scoresStartIndex].toLowerCase().trim().startsWith("subj") || headers[scoresStartIndex].toLowerCase().contains("score")))
            scoresStartIndex--;

        scoresStartIndex++;

        // question headers and Groups creations
        int expectedIndex=1,digitBegin=0;
        String currentGroup="";
        for(;i<scoresStartIndex;i++) {

            if((digitBegin=headers[i].lastIndexOf(Integer.toString(expectedIndex)))==-1){ //expected not found -> either end of group or weird column
                if((digitBegin=headers[i].lastIndexOf("1"))==-1)//a weird column
                    break;
                detectedGroups.add(new Group(currentGroup, expectedIndex-1));
                expectedIndex=1;
            }
            currentGroup=headers[i].substring(0,digitBegin);
            expectedIndex++;
            detectedQHeaders.add(headers[i]);
        }
        detectedGroups.add(new Group(currentGroup,expectedIndex-1)); //add last group

        //find sub score start and end indices(if exist)
        for(i=scoresStartIndex;i<headers.length;i++){

            if(headers[i].toLowerCase().startsWith("subj")){
                if(subjStartIndex==-1) { //subj first time
                    subjStartIndex = i;
                    subjEndIndex=i;
                }
                else
                    subjEndIndex=i;
            }
        }

        subjEndIndex++; //to be exclusive

        if(subjStartIndex==-1)
            subjQuestionsCount=0;
        else
            subjQuestionsCount=subjEndIndex-subjStartIndex;

    }


    private static boolean isQHeader(String header, Pattern pattern){
        Matcher matcher = pattern.matcher(header);
        return matcher.matches();
    }











}

