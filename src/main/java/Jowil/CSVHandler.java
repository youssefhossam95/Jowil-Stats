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
    public final static Integer STUDENTID=0, STUDENTNAME=1, STUDENTFORM=2, STUDENTIDCONT=3,IGNORE=4;
    private static String filePath;
    private static ArrayList<String> detectedQHeaders=new ArrayList<String>();
    private static ArrayList<String> detectedInfoHeaders=new ArrayList<String>();
    private static ArrayList<Group> detectedGroups= new ArrayList<Group>();
    private static int subScoresCount=0;
    private static int scoresStartIndex; //index of column where score columns (subj and non subj) start. (index based on QHeaders only)
    private static int subjStartIndex=-1;
    private static int subjEndIndex=-1;

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
        ArrayList<ArrayList<Double>> subScores= new ArrayList<>();

        if (isHeadersExist)//ignore headers
            input.readLine();


        if (isCorrectAnswersExist && (line = input.readLine()) != null) { //parse correct answers
            ArrayList<ArrayList<String>> correctAnswers = new ArrayList<ArrayList<String>>();
            ArrayList<String> answers=cropArray(line.split(","), identifiers.size(),identifiers.size()+scoresStartIndex);
            if(!isAllCellsFilled(answers))
                throw new EmptyAnswerKeyException();
            correctAnswers.add(answers);
            Statistics.setCorrectAnswers(correctAnswers);
        }
        while ((line = input.readLine()) != null) { //parse students answers
            String[] row = line.split(",");
            updateIdentifiers(studentIDs, studentNames, studentForms, row, identifiers,formsCount);
            studentsAnswers.add(cropArray(row, identifiers.size(),identifiers.size()+scoresStartIndex));
            if(subjEndIndex!=-1)
                subScores.add(getSubjScoresFromRow(row,identifiers.size()+subjStartIndex,identifiers.size()+subjEndIndex));
        }

        //initialize Statistics internal fields with parsed data


        Statistics.setStudentAnswers(studentsAnswers);

        if(subjStartIndex!=-1)
            Statistics.setSubScores(subScores);


        if (studentIDs.size() != 0) {
            Statistics.setStudentIDs(studentIDs);
//            Statistics.setIdentifierMode(Statistics.IDMODE);
        }

        if (studentNames.size() != 0) {
            Statistics.setStudentNames(studentNames);
//            if (studentIDs.size() == 0)
//                Statistics.setIdentifierMode(Statistics.NAMEMODE);
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


    public static boolean processHeaders() throws IOException, EmptyCSVException {
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
    private static ArrayList<String> cropArray(String [] original,int skipCols,int end){
        ArrayList<String> cropped=new ArrayList<String>();
        for(int i=skipCols;i<end;i++)
            cropped.add(original[i]);
        return cropped;
    }


    private static ArrayList<Double> getSubjScoresFromRow(String [] row, int subjStart, int subjEnd){

        ArrayList<Double> studentSubScores= new ArrayList<Double>();

        for(int i=subjStart;i<subjEnd;i++)
            studentSubScores.add(Double.parseDouble(row[i]));

        return studentSubScores;
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
            else if(identifiers.get(i)== STUDENTIDCONT)
                studentIDs.set(studentIDs.size()-1,studentIDs.get(studentIDs.size()-1)+row[i]);

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
        Pattern groupsPattern = Pattern.compile(".*\\d+");
        ArrayList<String> qHeaders= new ArrayList<String>();
        ArrayList<Group> groups= new ArrayList<Group>();

        //info headers
        int i;
        for(i=0;i<headers.length;i++){
            if(!(headers[i].toLowerCase().trim().startsWith("id")) && isQHeader(headers[i],groupsPattern)) //reached groups start
                break;
            detectedInfoHeaders.add(headers[i]);
        }

        // question headers and Groups creations
        int expectedIndex=1,digitBegin=0;
        String currentGroup="";

        for(;i<headers.length;i++) {
            if((digitBegin=headers[i].indexOf(Integer.toString(expectedIndex)))==-1){ //expected not found -> probably end of group
                groups.add(new Group(currentGroup, expectedIndex-1));
                expectedIndex=1;
                if((digitBegin=headers[i].indexOf(Integer.toString(expectedIndex)))==-1){
                    if(headers[i].toLowerCase().trim().startsWith("subj")) //subj without number -> only one sub score
                        subScoresCount=1;
                    else //probably score added by mistake -> ignore
                    continue;
                }
            }
            currentGroup=headers[i].substring(0,digitBegin);
            expectedIndex++;
            qHeaders.add(headers[i]);
        }
        cleanGroupsAndHeaders(qHeaders,groups);

    }

    
    private static boolean isQHeader(String header, Pattern pattern){
        Matcher matcher = pattern.matcher(header);
        return matcher.matches();
    }


    private static void cleanGroupsAndHeaders(ArrayList<String> qHeaders, ArrayList<Group> groups){

        //remove score1 ,score2 ,subj score1, etc. from qHeaders
        scoresStartIndex=qHeaders.size();
        for(int i=0;i<qHeaders.size();i++){
            if(qHeaders.get(i).toLowerCase().contains("score") || qHeaders.get(i).toLowerCase().contains("subj")) {
                scoresStartIndex=i;
                break;
            }
            detectedQHeaders.add(qHeaders.get(i));
        }

        //detect subj start and end indices
        for(int i=0;i<qHeaders.size();i++){
            if(qHeaders.get(i).toLowerCase().contains("subj")){
                if(subjStartIndex==-1) { //subj first time
                    subjStartIndex = i;
                    subjEndIndex=i;
                }
                else
                    subjEndIndex=i;
            }
            subjEndIndex++; //to be exclusive
        }

        //remove redundant score groups

        for(Group group : groups){

            if(group.getName().toLowerCase().contains("score") || group.getName().toLowerCase().contains("subj"))
                continue;
            detectedGroups.add(group);
        }

    }






}

