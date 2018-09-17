package Jowil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
            super("Invalid Form number at row "+rowNumber+".");
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

    public static class InvalidSubjColumnException extends Exception{

        InvalidSubjColumnException(int rowNumber,String invalidScore){
            super("Invalid subjective question score \""+invalidScore+"\" at row "+rowNumber+".");
        }


    }



    //fields

    //NOTE: ALL END INDICES SUCH AS "identifierColEndIndex" ARE EXCLUSIVE

    private static String responsesFilePath;
    private static String answerKeyFilePath; //used in manual mode
    private static ArrayList<String> detectedQHeaders;
    private static ArrayList<String> detectedInfoHeaders;
    private static ArrayList<Group> detectedGroups;
    private static ArrayList<String[]> savedResponsesCSV; //both saved arrays contains headers if they exist
    private static ArrayList<String[]> savedAnswerKeyCSV;
    private static int scoresStartIndex; //index of column where score columns (subj and non subj) start
    private static int subjStartIndex;
    private static int subjEndIndex;
    private static int subjQuestionsCount;
    private static int formsCount;
    private static int identifierColStartIndex;
    private static int identifierColEndIndex;
    private static int formColIndex;
    private static int questionsColStartIndex;
    private static int questionsColEndIndex;
    private static boolean isAnswerKeyContainsBlanks;
    private static ArrayList<Boolean> isQuestionsIgnored;
    private static ArrayList<Group> realIDGroups;//groups in the beginning that start with "id".
    private static int responsesColsCount;
    private static int answersColsCount;
    private static boolean isResponsesContainsHeaders;
    private static boolean isAnswerKeyContainsHeaders;
    public final static int NOT_AVAILABLE=-1;







    //getters and setters
    public static void setResponsesFilePath(String responsesFilePath) {
        CSVHandler.responsesFilePath = responsesFilePath;
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

    public static boolean isIsAnswerKeyContainsBlanks() {
        return isAnswerKeyContainsBlanks;
    }


    public static boolean isIsResponsesContainsHeaders() {
        return isResponsesContainsHeaders;
    }

    public static void setIsResponsesContainsHeaders(boolean isResponsesContainsHeaders) {
        CSVHandler.isResponsesContainsHeaders = isResponsesContainsHeaders;
    }

    public static void setRealIDGroups(ArrayList<Group> realIDGroups) {
        CSVHandler.realIDGroups = realIDGroups;
    }

    public static int getResponsesColsCount() {
        return responsesColsCount;
    }

    public static int getAnswersColsCount() {
        return answersColsCount;
    }


    public static void setSubjStartIndex(int subjStartIndex) {
        CSVHandler.subjStartIndex = subjStartIndex;
    }

    public static void setSubjEndIndex(int subjEndIndex) {
        CSVHandler.subjEndIndex = subjEndIndex;
    }

    public static void setQuestionsColEndIndex(int questionsColEndIndex) {
        CSVHandler.questionsColEndIndex = questionsColEndIndex;
    }

    public static void setSubjQuestionsCount(int subjQuestionsCount) {
        CSVHandler.subjQuestionsCount = subjQuestionsCount;
    }


    public static void setDetectedQHeaders(ArrayList<String> detectedQHeaders) {
        CSVHandler.detectedQHeaders = detectedQHeaders;
    }


    public static int getIdentifierColStartIndex() {
        return identifierColStartIndex;
    }

    public static int getIdentifierColEndIndex() {
        return identifierColEndIndex;
    }

    public static int getFormColIndex() {
        return formColIndex;
    }


    public static int getSubjStartIndex() {
        return subjStartIndex;
    }

    public static int getSubjEndIndex() {
        return subjEndIndex;
    }

    public static int getQuestionsColStartIndex() {
        return questionsColStartIndex;
    }

    public static int getQuestionsColEndIndex() {
        return questionsColEndIndex;
    }


    public static String getResponsesFilePath() {
        return responsesFilePath;
    }

    public static String getAnswerKeyFilePath() {
        return answerKeyFilePath;
    }

    public static ArrayList<String[]> getSavedResponsesCSV() {
        return savedResponsesCSV;
    }

    public static ArrayList<Boolean> getIsQuestionsIgnored() {
        return isQuestionsIgnored;
    }

    public static void setIsQuestionsIgnored(ArrayList<Boolean> isQuestionsIgnored) {
        CSVHandler.isQuestionsIgnored = isQuestionsIgnored;
    }



    public static void setFormsCount(int formsCount) {
        CSVHandler.formsCount = formsCount;
    }

    public static void setAnswerKeyFilePath(String answerKeyFilePath) {
        CSVHandler.answerKeyFilePath = answerKeyFilePath;
    }


    public static void setSavedResponsesCSV(ArrayList<ArrayList<String>> saved) {
        savedResponsesCSV=new ArrayList<>();

        for(ArrayList<String> arrList: saved) {
            String[] arr = new String[arrList.size()];

            for (int i=0;i<arrList.size();i++)
                arr[i]=arrList.get(i);

            savedResponsesCSV.add(arr);
        }

    }

    public static ArrayList<String[]> getSavedAnswerKeyCSV() {
        return savedAnswerKeyCSV;
    }

    public static void setSavedAnswerKeyCSV(ArrayList<ArrayList<String>> saved) {
        savedAnswerKeyCSV=new ArrayList<>();

        for(ArrayList<String> arrList: saved) {
            String[] arr = new String[arrList.size()];

            for (int i=0;i<arrList.size();i++)
                arr[i]=arrList.get(i);

            savedAnswerKeyCSV.add(arr);
        }
    }

    public static boolean isIsAnswerKeyContainsHeaders() {
        return isAnswerKeyContainsHeaders;
    }

    public static void setIsAnswerKeyContainsHeaders(boolean isAnswerKeyContainsHeaders) {
        CSVHandler.isAnswerKeyContainsHeaders = isAnswerKeyContainsHeaders;
    }



    //public methods
    /**
     *
     * @throws IOException
     */
    public static  void loadCsv(boolean isHeadersExist ) throws IOException, InvalidFormNumberException, IllFormedCSVException, InvalidSubjColumnException {

        BufferedReader input = new BufferedReader(new FileReader(responsesFilePath));
        String line = null;
        int rowNumber=1;

        //initialization
        Statistics.setStudentIdentifier(new ArrayList<String>());
        Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
        Statistics.setStudentForms(new ArrayList<Integer>());
        Statistics.setSubjScores(new ArrayList<ArrayList<Double>>());
        Statistics.setSubjMaxScores(new ArrayList<Double>());

        savedResponsesCSV=new ArrayList<>();

        if (isHeadersExist) {
            savedResponsesCSV.add(removeDoubleQuotes(input.readLine().split(",",-1)));
            rowNumber=2;
        }




        //parse students data
        while ((line = input.readLine()) != null) {
            String[] row = removeDoubleQuotes(line.split(",",-1));
            if(row.length==0) //ignore empty lines
                continue;

            savedResponsesCSV.add(row);

            if(row.length!=responsesColsCount) //equals zero if no headers
                throw new IllFormedCSVException(rowNumber);


            Statistics.getStudentAnswers().add(extractStudentsAnswers(row, questionsColStartIndex,questionsColEndIndex));
            updateStudentIdentifier(row,isHeadersExist?rowNumber-1:rowNumber);
            updateStudentForms(row,rowNumber);
            updateSubjScores(row,rowNumber);
            rowNumber++;
        }


    }

    private static String[] removeDoubleQuotes(String[] arr) {
        for(int i=0;i<arr.length;i++)
            arr[i]=arr[i].replace("\"","");

        return arr;
    }


    public static void loadSavedCSV() throws InvalidFormNumberException, InvalidSubjColumnException {

        //initialization
        Statistics.setStudentIdentifier(new ArrayList<String>());
        Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
        Statistics.setStudentForms(new ArrayList<Integer>());
        Statistics.setSubjScores(new ArrayList<ArrayList<Double>>());
        Statistics.setSubjMaxScores(new ArrayList<Double>());

        boolean isHeadersExist=isResponsesContainsHeaders;
        int rowNumber=1;
        if(isHeadersExist)
            rowNumber=2;

        for(int i=(isHeadersExist?1:0);i<savedResponsesCSV.size();i++){
            String [] row=savedResponsesCSV.get(i);
            Statistics.getStudentAnswers().add(extractStudentsAnswers(row, questionsColStartIndex,questionsColEndIndex));
            updateStudentIdentifier(row,isHeadersExist?rowNumber-1:rowNumber);
            updateStudentForms(row,rowNumber);
            updateSubjScores(row,rowNumber);
            rowNumber++;
        }
    }




    //if loading mode off, it will be used only to check if csv is valid
    public static boolean loadAnswerKeys(String answersFilePath,boolean isLoadingMode) throws IOException, IllFormedCSVException, InConsistentAnswerKeyException {

        isAnswerKeyContainsBlanks=false;
        answerKeyFilePath=answersFilePath;
        BufferedReader input = new BufferedReader(new FileReader(answersFilePath));
        String line;
        ArrayList<ArrayList<String>> correctAnswers=new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> cleanedCorrectAnswers=new ArrayList<>(); //without ignored questions
        savedAnswerKeyCSV=new ArrayList<>();

        String firstLine=input.readLine(); //can't be null because empty csv check is already called in CSVFileValidator

        String[] firstRow;
        boolean isHeadersExist=isAllCellsLarge(firstRow=removeDoubleQuotes(firstLine.split(",",-1))) || isAnswerKeyContainsHeaders;

        savedAnswerKeyCSV.add(firstRow);




        int rowNumber=isHeadersExist?2:1;
        int colsCount=0;
        while ((line = (rowNumber==1?firstLine:input.readLine())) != null) { //read saved first line if no headers exist
            String [] answers=removeDoubleQuotes(line.split(",",-1));

            if(answers.length!=colsCount && colsCount!=0)
                throw new IllFormedCSVException(rowNumber);
            else
                colsCount=answers.length;

            savedAnswerKeyCSV.add(answers);

            if(isLoadingMode)
                updateCleanedCorrectAnswers(cleanedCorrectAnswers,answers,questionsColStartIndex,questionsColEndIndex);
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


        if(isLoadingMode)
            Statistics.setCorrectAnswers(cleanedCorrectAnswers);


        formsCount=correctAnswers.size();
        answersColsCount=colsCount;

        return isHeadersExist;
    }


    public static void loadSavedAnswerKey(){


        ArrayList<ArrayList<String>> cleanedCorrectAnswers=new ArrayList<>(); //without ignored questions

        boolean isHeadersExist=isAnswerKeyContainsHeaders;

        for(int i=(isHeadersExist?1:0);i<savedAnswerKeyCSV.size();i++)
            updateCleanedCorrectAnswers(cleanedCorrectAnswers,savedAnswerKeyCSV.get(i),questionsColStartIndex,questionsColEndIndex);


        Statistics.setCorrectAnswers(cleanedCorrectAnswers);
    }

    public static boolean processHeaders(boolean isIgnoreBlanks ) throws IOException, EmptyCSVException {

        if(realIDGroups==null)
            detectedGroups=new ArrayList<>();
        else
            detectedGroups=realIDGroups;

        detectedQHeaders=new ArrayList<>();
        detectedInfoHeaders=new ArrayList<>();
        subjStartIndex=NOT_AVAILABLE;
        subjEndIndex=NOT_AVAILABLE;


        BufferedReader input = new BufferedReader(new FileReader(responsesFilePath));
        String line;
        if((line = input.readLine()) != null){
            String headers[]=removeDoubleQuotes(line.split(",",-1));
            responsesColsCount=headers.length;
            if(!isAllCellsLarge(headers))
                return false;
            classifyHeaders(headers,isIgnoreBlanks);
        }
        else
            throw new EmptyCSVException();

        return true;
    }



    public static boolean isCSVFileEmpty(File file) throws IOException {
        return new BufferedReader(new FileReader(file)).readLine()==null;
    }



    public static void initQuestionsChoices(){

        Statistics.setQuestionsChoices(new ArrayList<ArrayList<String>>());

        int i=0;
        for(Group group : detectedGroups){

            int qCount=group.getqCount();
            String groupMax=getGroupMax(qCount,i);
            group.setCorrectAnswers(getGroupCorrectAnswers(qCount,i));
            i+=qCount;
            group.generatePossibleAnswers(groupMax);


            for(int j=0;j<qCount;j++)
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

    public static boolean isFilesHeadersMismatched(File first,File second) throws IOException {
        String[] firstHeaders=removeDoubleQuotes(new BufferedReader(new FileReader(first)).readLine().split(",",-1));
        String [] secondHeaders=removeDoubleQuotes(new BufferedReader(new FileReader(second)).readLine().split(",",-1));

        if(firstHeaders.length!=secondHeaders.length)
            return true;

        for(int i=0;i<firstHeaders.length;i++){
            if(!firstHeaders[i].equals(secondHeaders[i]))
                return true;
        }

        return false;
    }



    ////////////////////helper functions


    //assumes correct answers were loaded
    private static ArrayList<String> extractStudentsAnswers(String [] original,int skipCols,int end){
        ArrayList<String> cropped=new ArrayList<String>();
        int qIndex=0;


        for(int i=skipCols;i<end;i++) {
            if (!isQuestionsIgnored.get(qIndex))
                cropped.add(original[i]);
            else {
                isAnswerKeyContainsBlanks=true;
            }
            qIndex++;
        }

        return cropped;
    }


    private static void updateSubjScores(String [] row,int rowNumber) throws InvalidSubjColumnException {

        //no subj questions
        if(subjStartIndex==NOT_AVAILABLE)
            return;

        ArrayList<Double> studentSubScores= new ArrayList<Double>();

        for(int i=subjStartIndex;i<subjEndIndex;i++){
            try{
                studentSubScores.add(Double.parseDouble(row[i]));
            }catch(NumberFormatException e){
                throw new InvalidSubjColumnException(rowNumber,row[i]);
            }
        }

        Statistics.getSubjScores().add(studentSubScores);
    }


    private static void updateStudentForms( String [] row, int rowNumber) throws InvalidFormNumberException {

        if(formColIndex==NOT_AVAILABLE){
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

        if(identifierColStartIndex==NOT_AVAILABLE){ //autoID mode
            Statistics.getStudentIdentifier().add(Integer.toString(studentAutoNumber));
            return;
        }

        StringBuilder identifier=new StringBuilder();
        for(int i=identifierColStartIndex;i<identifierColEndIndex;i++)
            identifier.append(row[i]);

        Statistics.getStudentIdentifier().add(cleanID(identifier.toString()));
    }


    private static void updateCleanedCorrectAnswers(ArrayList<ArrayList<String>> cleanedCorrectAnswers,String[] formAnswers,int start,int end) {

        ArrayList<String> cleanedFormAnswers=new ArrayList<>();
        isQuestionsIgnored=new ArrayList<>();

        for(int i=start;i<end;i++){
            String answer =formAnswers[i];
            if(!answer.trim().isEmpty())
                cleanedFormAnswers.add(answer);
            else
                isAnswerKeyContainsBlanks=true;

            isQuestionsIgnored.add(answer.trim().isEmpty()); //blank answers must be consistent -> any form can be used

        }

        cleanedCorrectAnswers.add(cleanedFormAnswers);

    }


    private static HashSet<String> getGroupCorrectAnswers(int qCount,int groupStartCol){

        HashSet<String> groupCorrectAnswers=new HashSet<>();

        for(int i=0;i<Statistics.getCorrectAnswers().size();i++){
            for(int j=groupStartCol;j<groupStartCol+qCount;j++)
                groupCorrectAnswers.add(Statistics.getCorrectAnswers().get(i).get(j));
        }

        return groupCorrectAnswers;
    }
    private static String getGroupMax(int qCount,int groupStartCol) {

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


        currentMaxChoice=get2DArrayMax(Statistics.getCorrectAnswers(),isAnswersNumeric,groupStartCol,qCount,currentMaxChoice,isUpperCase);

        currentMaxChoice=get2DArrayMax(Statistics.getStudentAnswers(),isAnswersNumeric,groupStartCol,qCount,currentMaxChoice,isUpperCase);

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


    private static String cleanID(String s) {

        try{
            String cleanedStr;
            Integer.parseInt(cleanedStr=s.replace(" ",""));
            return cleanedStr;
        }
        catch(NumberFormatException e){
            return s;
        }
    }

    private static boolean isAllCellsLarge(String [] cells){
        for(String cell: cells){
            if(cell.length()<2)
                return false;
        }
        return true;
    }


    private static void classifyHeaders(String [] headers,boolean isIgnoreBlanks) {



        Pattern groupsPattern = Pattern.compile(".*\\d+");

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

        questionsColEndIndex=scoresStartIndex;



        // question headers and Groups creations
        int expectedIndex = 1, digitBegin = 0,currentGroupCount=0,qIndex=0;
        String currentGroup = "";

        for (; i < scoresStartIndex; i++) {

            if ((digitBegin = headers[i].lastIndexOf(Integer.toString(expectedIndex))) == -1) { //expected not found -> either end of group or weird column
                if ((digitBegin = headers[i].lastIndexOf("1")) == -1)//a weird column
                    break;
                detectedGroups.add(new Group(currentGroup, currentGroupCount));
                currentGroupCount=0;
                expectedIndex = 1;
            }
            currentGroup = headers[i].substring(0, digitBegin);
            expectedIndex++;

            if(isIgnoreBlanks && isQuestionsIgnored.get(qIndex))
                System.out.println("Ignored "+headers[i]);
            else {
                detectedQHeaders.add(headers[i]);
                currentGroupCount++;
            }
            qIndex++;
        }

        detectedGroups.add(new Group(currentGroup, currentGroupCount)); //add last group


        //find sub score start and end indices(if exist)
        for (i = scoresStartIndex; i < headers.length; i++) {

            if (headers[i].toLowerCase().startsWith("subj")) {
                if (subjStartIndex == NOT_AVAILABLE) { //subj first time
                    subjStartIndex = i;
                    subjEndIndex = i;
                } else
                    subjEndIndex = i;
            }
        }

        subjEndIndex++; //to be exclusive

        if (subjStartIndex == NOT_AVAILABLE)
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

    //true if csv file contains headers only
    public static boolean isFileContainsNoRows(File file) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(file));
        boolean isHeadersExist=isAllCellsLarge(removeDoubleQuotes(input.readLine().split(",",-1)));

        if(!isHeadersExist)
            return false;

        return input.readLine()==null;

    }

    public static ArrayList<ArrayList<String>> readPartialCSVFile(String filePath,int maxRowsCount) throws IOException {

        BufferedReader input = new BufferedReader(new FileReader(filePath));
        String line ;
        ArrayList<ArrayList<String>> csvRows = new ArrayList<>();
        int rowsCount=0;

        while( (line = input.readLine()) != null && rowsCount!=maxRowsCount) {
            String[] row = removeDoubleQuotes(line.split(",",-1));
            ArrayList<String> rowList = new ArrayList<String>() ;
            for(int i = 0 ; i < row.length ; i++)
                rowList.add(row[i]);
            csvRows.add(rowList);
            rowsCount++;
        }
        return csvRows ;
    }

    public static void generateObjectiveGroupsFromColSets(ArrayList<ColumnSet> objColSets) throws InConsistentAnswerKeyException, IOException, IllFormedCSVException {

        detectedGroups=new ArrayList<>();
        detectedQHeaders=new ArrayList<>();


        if(Controller.isOpenMode)
            loadSavedAnswerKey();

        else
            loadAnswerKeys(answerKeyFilePath,true);



        int qIndex=0;
        for(ColumnSet columnSet:objColSets){
            int nextQ=1,groupQCount=0;
            String groupName=columnSet.getName();

            for(int i=0;i<columnSet.getMySize();i++){
                if(!isQuestionsIgnored.get(qIndex)){
                    detectedQHeaders.add(groupName+nextQ);
                    groupQCount++;
                }
                nextQ++;
                qIndex++;
            }
            detectedGroups.add(new Group(groupName,groupQCount));
        }


    }


    public static ArrayList<ArrayList<String>> readCsvFile (String filePath) throws IOException {
        return readPartialCSVFile(filePath,-1);
    }

    public static ArrayList<ArrayList<String>> readResponsesFile(int maxRowsCount) throws IOException {
        return readPartialCSVFile(responsesFilePath,maxRowsCount);
    }


    public static String getObjColumnSetErrorMessage(ColumnSet columnSet) {

        if(isInconsistentAnswerTypes(columnSet))
            return "Invalid objective column set \""+columnSet.getName()+"\". Choices in an objective column set must be of the same type.";

        if(isContainsInvalidChoices(columnSet))
            return "Invalid choices at column set \""+columnSet.getName()+"\". A valid choice must be either a number in range (0-99) or an english letter.";

        return null;
    }


    private static boolean isInconsistentAnswerTypes(ColumnSet columnSet) {

        int start=columnSet.getStartIndex();
        int end=columnSet.getMySize()+start;

        String[] answers=savedAnswerKeyCSV.get(savedAnswerKeyCSV.size()-1); //to avoid checking on headers just consider the last form's answers.

        Boolean isNumeric=null;

        for(int i=start;i<end;i++){

            if(!answers[i].trim().isEmpty()) { //if empty answer ignore check

                boolean isCurrentNumeric = true;
                try {
                    Integer.parseInt(answers[i]);
                } catch (NumberFormatException e) {
                    isCurrentNumeric = false;
                }
                if(isNumeric==null) //first non empty answer
                    isNumeric=isCurrentNumeric;

                else if (isCurrentNumeric != isNumeric) //inconsistency
                    return true;
            }
        }

        return false;
    }

    private static boolean isContainsInvalidChoices(ColumnSet columnSet) {

        int start=columnSet.getStartIndex();
        int end=columnSet.getMySize()+start;

        for(int i=(isResponsesContainsHeaders?1:0);i<savedResponsesCSV.size();i++){

            for(int j=start;j<end;j++) {
                if (savedResponsesCSV.get(i)[j].length() > 2)
                    return true;
            }

        }

        return false;

    }



}

