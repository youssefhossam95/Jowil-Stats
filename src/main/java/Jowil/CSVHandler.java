package Jowil;

import org.omg.CORBA.DynAnyPackage.Invalid;
import sun.swing.plaf.synth.DefaultSynthStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVHandler {


    public static class InConsistentAnswerKeyException extends Exception{

        private int rowNumber;

        InConsistentAnswerKeyException(int rowNumber){
            this.rowNumber=rowNumber;
        }

        public int getRowNumber() {
            return rowNumber;
        }

    }
    public static class InvalidFormNumberException extends Exception{


        InvalidFormNumberException(int rowNumber){
            super("Student in row "+rowNumber+ " has an invalid form number.");
        }
    }
    public static class EmptyCSVException extends Exception{

    }

    public static class IllFormedCSVException extends Exception{


        private int rowNumber;
        public int getRowNumber() {
            return rowNumber;
        }

        IllFormedCSVException(int rowNumber){
            this.rowNumber=rowNumber;
        }


    }


    public static class AnswersCaseInsensitive extends Exception{


        AnswersCaseInsensitive(String group){
            super("Group "+group+" contains answers with different case sensitivity.");
        }
    }


    //fields

    public final static Integer NORMAL_MODE=0,DECLARE_SUBJ_MODE=1,IGNORE_BLANKS_MODE=2;
    private static String filePath;
    private static ArrayList<String> detectedQHeaders=new ArrayList<String>();
    private static ArrayList<String> detectedInfoHeaders=new ArrayList<String>();
    private static ArrayList<Group> detectedGroups= new ArrayList<Group>();
    private static int scoresStartIndex; //index of column where score columns (subj and non subj) start
    private static int subjStartIndex;
    private static int subjEndIndex;
    private static int subjQuestionsCount;
    private static int formsCount;
    private static int identifierColStartIndex;
    private static int identifierColEndIndex;
    private static int formColIndex;
    private static int questionsColStartIndex;
    private static int questionsLimit;
    private static int CSVHeaderColsCount;
    private static boolean isAnswerKeyContainsBlanks;
    private static ArrayList<Boolean> isQuestionsIgnored;
    private static ArrayList<Group> realIDGroups;//groups in the beginning that start with "id".

    private static boolean isSkipRowInManual;


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

    public static ArrayList<Group> getDetectedGroups() {
        return detectedGroups;
    }

    public static void setDetectedGroups(ArrayList<Group> detectedGroups) {
        CSVHandler.detectedGroups = detectedGroups;
    }
    public static int getSubjQuestionsCount() {
        return subjQuestionsCount;
    }


    public static int getFormsCount() {
        return formsCount;
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

    public static void setQuestionsLimit(int questionsLimit) {
        CSVHandler.questionsLimit = questionsLimit;
    }

    public static boolean isIsAnswerKeyContainsBlanks() {
        return isAnswerKeyContainsBlanks;
    }

    public static boolean isIsSkipRowInManual() {
        return isSkipRowInManual;
    }

    public static void setIsSkipRowInManual(boolean isSkipRowInManual) {
        CSVHandler.isSkipRowInManual = isSkipRowInManual;
    }

    public static void setRealIDGroups(ArrayList<Group> realIDGroups) {
        realIDGroups = realIDGroups;
    }

    //public methods
    /**
     *
     * @throws IOException
     */
    public static  void loadCsv(boolean isHeadersExist ) throws IOException, InvalidFormNumberException, IllFormedCSVException {

        BufferedReader input = new BufferedReader(new FileReader(filePath));
        String line = null;
        int rowNumber=1;

        //initialization
        Statistics.setStudentIdentifier(new ArrayList<String>());
        Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
        Statistics.setStudentForms(new ArrayList<Integer>());
        Statistics.setSubjScores(new ArrayList<ArrayList<Double>>());
        Statistics.setSubjMaxScores(new ArrayList<Double>());


        if (isHeadersExist) {//ignore headers
             input.readLine();
             rowNumber=2;
        }


        int colsCount=CSVHeaderColsCount;

        //parse students data
        while ((line = input.readLine()) != null) {
            String[] row = line.split(",",-1);

            if(row.length==0) //ignore empty lines
                continue;

            if(row.length!=colsCount && colsCount!=0)
                throw new IllFormedCSVException(rowNumber);
            else
                colsCount=row.length;

            Statistics.getStudentAnswers().add(extractStudentsAnswers(row, questionsColStartIndex,scoresStartIndex));
            updateStudentIdentifier(row,rowNumber);
            updateStudentForms(row,rowNumber);
            updateSubjScores(row);
            rowNumber++;
        }



    }




    public static int loadAnswerKeys(String answersFilePath) throws IOException, IllFormedCSVException, InConsistentAnswerKeyException {

        isAnswerKeyContainsBlanks=false;
        BufferedReader input = new BufferedReader(new FileReader(answersFilePath));
        String line;
        ArrayList<ArrayList<String>> correctAnswers=new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> cleanedCorrectAnswers=new ArrayList<>(); //without ignored questions

        int rowNumber=1;
        int colsCount=0;
        while ((line = input.readLine()) != null) {
            String [] answers=line.split(",",-1);

            if(answers.length!=colsCount && colsCount!=0)
                throw new IllFormedCSVException(rowNumber);
            else
                colsCount=answers.length;

            updateCleanedCorrectAnswers(cleanedCorrectAnswers,answers);
            ArrayList<String> answerKey= new ArrayList<String>(Arrays.asList(answers));
            correctAnswers.add(answerKey);
            rowNumber++;
        }


        //check for inconsistent blanks
        if(isAnswerKeyContainsBlanks) {

            ArrayList<String> form1Answers = correctAnswers.get(0);

            for (int i = 0; i < form1Answers.size(); i++) { //for every question

                boolean isQuestionBlank = form1Answers.get(i).isEmpty();
                for (int j = 0; j < correctAnswers.size(); j++) { //for every form

                    if (correctAnswers.get(j).get(i).isEmpty() != isQuestionBlank)
                        throw new InConsistentAnswerKeyException(j + 1);

                }
            }

        }

        Statistics.setCorrectAnswers(cleanedCorrectAnswers);
        formsCount=correctAnswers.size();
        return formsCount;
    }


    public static boolean processHeaders(int mode) throws IOException, EmptyCSVException {

        if(realIDGroups==null)
            detectedGroups=new ArrayList<>();
        else
            detectedGroups=realIDGroups;

        detectedQHeaders=new ArrayList<>();
        detectedInfoHeaders=new ArrayList<>();
        subjStartIndex=-1;

        BufferedReader input = new BufferedReader(new FileReader(filePath));
        String line;
        if((line = input.readLine()) != null){
            String headers[]=line.split(",",-1);
            CSVHeaderColsCount=headers.length;
            if(!isAllCellsLarge(headers))
                return false;
            classifyHeaders(headers,mode);
        }
        else
            throw new EmptyCSVException();

        Statistics.setQuestionNames(detectedQHeaders);
        return true;
    }



    public static boolean isCSVFileEmpty(File file) throws IOException {
        return new BufferedReader(new FileReader(file)).readLine()==null;
    }



    public static void initQuestionsChoices(){

        Statistics.setQuestionsChoices(new ArrayList<ArrayList<String>>());

        int i=0;
        for(Group group : detectedGroups){

            String groupMax=getGroupMax(group,i);
            i+=group.getqCount();
            group.generatePossibleAnswers(groupMax);

            for(int j=0;j<group.getqCount();j++)
                Statistics.getQuestionsChoices().add(group.getPossibleAnswers());

        }
    }


    public static void updateQuestionsChoices(){

        Statistics.setQuestionsChoices(new ArrayList<ArrayList<String>>());

        for(Group group : detectedGroups){
            for(int i=0;i<group.getqCount();i++)
                Statistics.getQuestionsChoices().add(group.getPossibleAnswers());
        }
    }



    ////////////////////helper functions


    //assumes correct answers are set
    private static ArrayList<String> extractStudentsAnswers(String [] original,int skipCols,int end){
        ArrayList<String> cropped=new ArrayList<String>();
        int qIndex=0;

        for(int i=skipCols;i<end;i++) {
            if (!isQuestionsIgnored.get(qIndex))
                cropped.add(original[i]);
            else {
                System.out.println(detectedQHeaders.get(qIndex) + " ignored ya joe");
                isAnswerKeyContainsBlanks=true;
            }
        }

        return cropped;
    }


    private static void updateSubjScores(String [] row){

        //no subj questions
        if(subjStartIndex==-1)
            return;

        ArrayList<Double> studentSubScores= new ArrayList<Double>();

        for(int i=subjStartIndex;i<subjEndIndex;i++){
            try{
                studentSubScores.add(Double.parseDouble(row[i]));
            }catch(NumberFormatException e){
                studentSubScores.add(0.0);
            }
        }

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

    private static void updateStudentIdentifier(String [] row,int studentAutoNumber){

        if(identifierColStartIndex==-1){ //autoID mode
            Statistics.getStudentIdentifier().add(Integer.toString(studentAutoNumber));
            return;
        }

        StringBuilder identifier=new StringBuilder();
        for(int i=identifierColStartIndex;i<identifierColEndIndex;i++)
            identifier.append(row[i]);

        Statistics.getStudentIdentifier().add(identifier.toString());
    }

    private static void updateCleanedCorrectAnswers(ArrayList<ArrayList<String>> cleanedCorrectAnswers,String[] formAnswers) {

        ArrayList<String> cleanedFormAnswers=new ArrayList<>();
        isQuestionsIgnored=new ArrayList<>();

        for(String answer:formAnswers){
            if(!answer.isEmpty())
                cleanedFormAnswers.add(answer);
            else
                isAnswerKeyContainsBlanks=true;

            isQuestionsIgnored.add(answer.isEmpty()); //blank answers must be consistent -> any form can be used

        }

        cleanedCorrectAnswers.add(cleanedFormAnswers);

    }

    private static String getGroupMax(Group group,int groupStartCol) {

        boolean isAnswersNumeric=false;
        boolean isUpperCase=false;

        String firstAnswer=Statistics.getCorrectAnswers().get(0).get(groupStartCol).trim();

        try{
            Integer.parseInt(firstAnswer);
            isAnswersNumeric=true;
        }catch(NumberFormatException e){
            isUpperCase=isUpperCase(firstAnswer);
        }

        String currentMaxChoice=firstAnswer;


        currentMaxChoice=get2DArrayMax(Statistics.getCorrectAnswers(),isAnswersNumeric,groupStartCol,group.getqCount(),currentMaxChoice,isUpperCase);

        currentMaxChoice=get2DArrayMax(Statistics.getStudentAnswers(),isAnswersNumeric,groupStartCol,group.getqCount(),currentMaxChoice,isUpperCase);

        return currentMaxChoice;



    }

    private static String get2DArrayMax(ArrayList<ArrayList<String>> array,boolean isAnswersNumeric,int groupStartCol,int groupQCount,String currentMaxChoice,boolean isUpperCase) {



        int maxInt=0,currentInt;
        String maxString="",currentString;

        if(isAnswersNumeric)
            maxInt=Integer.parseInt(currentMaxChoice);
        else
            maxString=currentMaxChoice;




        for(int j=groupStartCol;j<groupStartCol+groupQCount;j++) //for all questions in group
        {
            for(int i=0;i<array.size();i++) { //for each form in answer key
                currentString=array.get(i).get(j).trim();
                try{
                    if(isAnswersNumeric) {
                        currentInt = Integer.parseInt(currentString);
                        maxInt = Math.max(currentInt, maxInt);
                    }
                    else
                        maxString=currentString.compareTo(maxString)>0?currentString:maxString;

                }catch(NumberFormatException e){ //ignore non-integers when isAnswerNumeric=true

                }
            }
        }

        if(isAnswersNumeric)
            maxString=Integer.toString(maxInt);

        return maxString;
    }



    private static boolean isAllCellsLarge(String [] cells){
        for(String cell: cells){
            if(cell.length()<2)
                return false;
        }
        return true;
    }


    private static void classifyHeaders(String [] headers,int mode) {

        boolean isIgnoreBlanks=(mode==DECLARE_SUBJ_MODE|| mode==IGNORE_BLANKS_MODE);

        Pattern groupsPattern = Pattern.compile(".*\\d+");
        subjStartIndex=-1;
        subjEndIndex=-1;
        //info headers
        int i;
        for (i = 0; i < headers.length; i++) {
            if (!(headers[i].toLowerCase().trim().startsWith("id")) && isQHeader(headers[i], groupsPattern)) //reached groups start
                break;
            detectedInfoHeaders.add(headers[i]);
        }

        questionsColStartIndex = detectedInfoHeaders.size();



        scoresStartIndex = headers.length - 1;

        //search for scores section start (if exists)
        while (scoresStartIndex >= 0 && (headers[scoresStartIndex].toLowerCase().trim().startsWith("subj") || headers[scoresStartIndex].toLowerCase().contains("score")))
            scoresStartIndex--;

        scoresStartIndex++;

        if (mode==DECLARE_SUBJ_MODE)
            scoresStartIndex = questionsLimit + questionsColStartIndex;

        // question headers and Groups creations
        int expectedIndex = 1, digitBegin = 0,currentGroupCount=0,qIndex=0;
        String currentGroup = "";

        for (; i < scoresStartIndex; i++) {

            if ((digitBegin = headers[i].lastIndexOf(Integer.toString(expectedIndex))) == -1) { //expected not found -> either end of group or weird column
                if ((digitBegin = headers[i].lastIndexOf("1")) == -1)//a weird column
                    break;
                detectedGroups.add(new Group(currentGroup, currentGroupCount));
                expectedIndex = 1;
            }
            currentGroup = headers[i].substring(0, digitBegin);
            expectedIndex++;
            qIndex++;

            if(isQuestionsIgnored.get(qIndex) && isIgnoreBlanks)
                System.out.println("Ignored "+headers[i]);
            else {
                detectedQHeaders.add(headers[i]);
                currentGroupCount++;
            }

        }

        detectedGroups.add(new Group(currentGroup, currentGroupCount)); //add last group

        if (mode==DECLARE_SUBJ_MODE) {
            subjStartIndex=scoresStartIndex;
            subjEndIndex=headers.length;
            subjQuestionsCount=subjEndIndex-subjStartIndex;
            return;
        }

        //find sub score start and end indices(if exist)
        for (i = scoresStartIndex; i < headers.length; i++) {

            if (headers[i].toLowerCase().startsWith("subj")) {
                if (subjStartIndex == -1) { //subj first time
                    subjStartIndex = i;
                    subjEndIndex = i;
                } else
                    subjEndIndex = i;
            }
        }

        subjEndIndex++; //to be exclusive

        if (subjStartIndex == -1)
            subjQuestionsCount = 0;
        else
            subjQuestionsCount = subjEndIndex - subjStartIndex;

    }


    private static boolean isQHeader(String header, Pattern pattern){
        Matcher matcher = pattern.matcher(header);
        return matcher.matches();
    }

    private static boolean isUpperCase(String s){

        return s.toUpperCase().equals(s);
    }





    public static ArrayList<ArrayList<String>> readCsvFile (String filePath) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filePath));
        String line ;
        ArrayList<ArrayList<String>> csvRows = new ArrayList<>();

        while( (line = input.readLine()) != null ) {
            String[] row = line.split(",") ;
            ArrayList<String> rowList = new ArrayList<String>() ;
            for(int i = 0 ; i < row.length ; i++)
                rowList.add(row[i]);
            csvRows.add(rowList);
        }
        return csvRows ;
    }









}

