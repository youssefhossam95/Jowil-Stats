package Jowil;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static org.apache.commons.math3.stat.StatUtils.* ;


public class Statistics {



    ////////////////////fields
    public static final int IDMODE=0,NAMEMODE=1,AUTOMODE=2;
    private static int identifierMode=AUTOMODE;
    private static ArrayList<String> studentNames;
    private static ArrayList<String> studentIDs;
    private static ArrayList<Double> studentScores;
    private static ArrayList<Integer> studentForms;
    private static ArrayList<ArrayList<Double>> questionWeights; //form vs question weights
    private static ArrayList<String> questionNames;
    private static ArrayList<ArrayList<String>> correctAnswers; //form vs correct answers
    private static ArrayList<ArrayList<String>> studentAnswers;
    private static ArrayList<ArrayList<ArrayList<String>>>sortedStudentAnswers; //for each form :student vs (answers+score)
    private static ArrayList<ArrayList<ArrayList<Double>>>answersStats; //For each form :Questions vs each possible choice percentage ( every row can have different number of possible choices)
    private static ArrayList<ArrayList<String>> questionsChoices; //list of all possible choices in order for every question. (every row can have different number of choices)
    private static ArrayList<Double> subScores; //subjective score


    ////////////////////setters
    public static void setIdentifierMode(int identifierMode) {
        Statistics.identifierMode = identifierMode;
    }
    public static void setStudentNames(ArrayList<String> studentNames) {
        Statistics.studentNames = studentNames;
    }

    public static void setStudentIDs(ArrayList<String> studentIDs) {
        Statistics.studentIDs = studentIDs;
    }

    public static void setStudentForms(ArrayList<Integer> studentForms) {
        Statistics.studentForms = studentForms;
    }

    public static void setQuestionWeights(ArrayList<ArrayList<Double>> questionWeights) {
        Statistics.questionWeights = questionWeights;
    }

    public static void setQuestionNames(ArrayList<String> questionNames) {
        Statistics.questionNames = questionNames;
    }

    public static void setCorrectAnswers(ArrayList<ArrayList<String>> correctAnswers) {
        Statistics.correctAnswers = correctAnswers;
    }

    public static void setStudentAnswers(ArrayList<ArrayList<String>> studentAnswers) {
        Statistics.studentAnswers = studentAnswers;
    }

    public static void setQuestionsChoices(ArrayList<ArrayList<String>> questionsChoices) {
        Statistics.questionsChoices = questionsChoices;
    }
    public static void setSubScores(ArrayList<Double> subScores) {
        Statistics.subScores = subScores;
    }


    //getters
    public static int getIdentifierMode() {
        return identifierMode;
    }
    public static ArrayList<String> getStudentNames() {
        return studentNames;
    }

    public static ArrayList<String> getStudentIDs() {
        return studentIDs;
    }
    public static ArrayList<String> getQuestionNames() {
        return questionNames;
    }

    public static ArrayList<ArrayList<String>> getCorrectAnswers() {
        return correctAnswers;
    }

    public static ArrayList<ArrayList<String>> getStudentAnswers() {
        return studentAnswers;
    }


    // print fuctions
    public static void printStudentScores() {
        System.out.print("Student Scores: ");
        System.out.println(studentScores);
    }
    public static void printSortedStudentAnswers(){
        System.out.print("Sorted Student Answers: ");
        System.out.println(sortedStudentAnswers);
    }

    public static void printAnswerStats(){
        System.out.print("Answer Stats: ");
        System.out.println(answersStats);
    }
    public static void printBasicInfo(){
        System.out.println("-----------------------------------------------------");
        System.out.println("Info Headers: "+CSVHandler.getDetectedInfoHeaders());
        System.out.println("Q names: " + Jowil.Statistics.getQuestionNames().toString());
        System.out.println("Student Ids: " + Jowil.Statistics.getStudentIDs().toString());
        System.out.println("Student names: " + Jowil.Statistics.getStudentNames().toString());
        System.out.println("ID mode " + Jowil.Statistics.getIdentifierMode());
        System.out.println("Correct ans: " + Jowil.Statistics.getCorrectAnswers().toString());
        System.out.println("Student ans: " + Jowil.Statistics.getStudentAnswers().toString());
        System.out.println("Questions choices: "+ Statistics.questionsChoices);
    }
    public static void printCalculations(){
        System.out.println("-----------------------------------------------------");
        printStudentScores();
        printSortedStudentAnswers();
        printAnswerStats();
    }


    private static void printMap (Map<String , Double> map ) {
        System.out.println("Map Data");
        for(Map.Entry<String , Double> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }



    ///////////////initializers

    private static void initScores(){

        studentScores = new ArrayList<Double>() ;
        for(int i = 0 ; i <studentAnswers.size() ; i ++) {
            double studentScore = 0 ;
            ArrayList<String> studentAnswer = studentAnswers.get(i) ;
            for (int j = 0 ; j <  studentAnswer.size() ; j ++) {
                String questionAnswer = studentAnswer.get(j) ;
                if(questionAnswer.equals(correctAnswers.get(studentForms.get(i)).get(j))) {
                    studentScore+= questionWeights.get(studentForms.get(i)).get(j) ;
                }
            }
            studentScores.add(studentScore) ;
        }
        printStudentScores();
    }

    private static void initSortedStudentAnswers(){

        for(int i=0;i<studentAnswers.size();i++)
            studentAnswers.get(i).add(studentScores.get(i).toString());

        sortedStudentAnswers=new ArrayList<ArrayList<ArrayList<String>>>();
        for(int i=0;i<correctAnswers.size();i++)
            sortedStudentAnswers.add(new ArrayList<ArrayList<String>>());

        for(int i=0;i<studentAnswers.size();i++)
            sortedStudentAnswers.get(studentForms.get(i)).add(studentAnswers.get(i));

        SortByScore sorter =new SortByScore();
        for(int i=0;i<sortedStudentAnswers.size();i++)
            Collections.sort(sortedStudentAnswers.get(i),sorter);
    }

    private static void initAnswersStats(){

        answersStats=new ArrayList<ArrayList<ArrayList<Double>>>(correctAnswers.size());
        for(int i=0;i<correctAnswers.size();i++){
            answersStats.add(new ArrayList<ArrayList<Double>>());
            calcformAnswerStats(answersStats.get(i),i);

        }

    }



    public static void init(){
        initScores();
//        initSortedStudentAnswers();
//        initAnswersStats();
    }



    public static void simulateUI(){

        //fill student names
        studentNames = new ArrayList<String>() ;
        studentNames.add("Walid");
        studentNames.add("youssef");
        studentNames.add("ahemd") ;

        //fill student ids
        studentIDs = new ArrayList<String>() ;
        studentIDs.add("1234") ;
        studentIDs.add("5678") ;
        studentIDs.add("9999") ;

        //fill questionNames
        questionNames = new ArrayList<String>() ;
        questionNames.add("Question1") ;
        questionNames.add("Question2") ;
        questionNames.add("Question3") ;
        questionNames.add("Question4") ;
        questionNames.add("Question5") ;

        //fill question Weights
        questionWeights = new ArrayList<ArrayList<Double>>() ;
        ArrayList<Double> form1Wieghts = new ArrayList<Double>() ;
        form1Wieghts.add(1.0) ; form1Wieghts.add(1.0) ;form1Wieghts.add(2.0) ;form1Wieghts.add(2.0) ;form1Wieghts.add(0.5) ;

        ArrayList<Double> form2Wieghts = new ArrayList<Double>() ;
        form2Wieghts.add(1.0) ; form2Wieghts.add(1.0) ;form2Wieghts.add(2.0) ;form2Wieghts.add(1.0) ;form2Wieghts.add(2.0) ;

        questionWeights.add(form1Wieghts);
        questionWeights.add(form2Wieghts) ;


        //fill correctAnswers
        correctAnswers = new ArrayList<ArrayList<String>>();
        ArrayList<String> form1Answers = new ArrayList<String>( );
        form1Answers.add("A");form1Answers.add("B");form1Answers.add("A");form1Answers.add("C");form1Answers.add("A");

        ArrayList<String> form2Answers = new ArrayList<String>( );
        form2Answers.add("A");form2Answers.add("B");form2Answers.add("A");form2Answers.add("C");form2Answers.add("D");

        correctAnswers.add(form1Answers);
        correctAnswers.add(form2Answers) ;

        //fill student forms
        studentForms = new ArrayList<Integer>() ;
        studentForms.add(0) ;
        studentForms.add(1) ;
        studentForms.add(0) ;

        //fill student answers
        studentAnswers = new ArrayList<ArrayList<String>>() ;
        ArrayList<String>answer1 = new ArrayList<String>() ;
        answer1.add("A") ; answer1.add("B") ; answer1.add("C") ; answer1.add("C") ; answer1.add("A") ;

        ArrayList<String>answer2= new ArrayList<String>() ;
        answer2.add("A") ; answer2.add("B") ; answer2.add("C") ; answer2.add("C") ; answer2.add("D") ;

        ArrayList<String>answer3= new ArrayList<String>() ;
        answer3.add("D") ; answer3.add("B") ; answer3.add("C") ; answer3.add("C") ; answer3.add("D") ;

        studentAnswers.add(answer1)  ;
        studentAnswers.add(answer2) ;
        studentAnswers.add(answer3) ;



    }


    //helper functions
    private static void calcformAnswerStats(ArrayList<ArrayList<Double>> formAnswerStats , int formIndex){

        double count;
        int studentsCount=getFormStudentsCount(formIndex);
        for(int i=0;i<questionsChoices.size();i++){
            formAnswerStats.add(new ArrayList<Double>());
            for(String choice: questionsChoices.get(i)){
                count=0;
                for(int j=0;j<studentAnswers.size();j++){
                    if(studentAnswers.get(j).get(i).equals(choice) && studentForms.get(j)==formIndex)
                        count++;
                }
                formAnswerStats.get(i).add(count/studentsCount);
            }
        }

    }
    private static int getFormStudentsCount(int formIndex){
        int count=0;
        for(int form: studentForms){
            if(form==formIndex)
                count++;
        }
        return count;
    }





    public static  Map<String , Double> report3Stats() {
        Map<String , Double>  statsMap = new HashMap<String, Double>() ;


//        ArrayList<Double> sortedStudentScores = new ArrayList<Double>(studentScores);
//        Collections.sort(sortedStudentScores);
//        double[] sortedScores = sortedStudentScores.stream().mapToDouble(d -> d).toArray();


//        System.out.println("yaaaaa man" + sortedScores.get(2));
        double[] scores = studentScores.stream().mapToDouble(d -> d).toArray();
        double[] weights = questionWeights.get(0).stream().mapToDouble(d -> d).toArray() ; // weights of the first Form
        double benchMark = 75.0 ;
        double mean = mean(scores) ;
        double variance = variance(scores);
        double std = Math.sqrt(variance) ;
        double maxScore = sum(weights) ;
        double HightestScore = max(scores);
        double LowestScore = min(scores);
//        int numberOfStudents = studentScores.size();

        // 1 3 5 6 7 8 9 10
//        double[] wello = {3, 7, 8, 5, 12, 14, 21, 15, 18, 14} ;
        DescriptiveStatistics ds = new DescriptiveStatistics(scores);
        double median = ds.getPercentile(50);
        double firstQ = ds.getPercentile(25) ;
        double thirtQ = ds.getPercentile(75) ;
//        if(numberOfStudents%2==0) {
//            double num1 = sortedScores[numberOfStudents/2 ] ;
//            double num2 = sortedScores[numberOfStudents/2-1] ;
//            median = (num1+num2)/2 ;
//        }
//        else
//            median = sortedScores[(int)Math.floor(numberOfStudents/2)] ;

        statsMap.put("Mean" , mean);
        statsMap.put("Number of Graded Questions" , (double)questionWeights.size() );
        statsMap.put("Maximum Possible Score" , maxScore) ; // assuming all Forms should have the same sum
        statsMap.put("Benchmark" , benchMark ) ;
        statsMap.put("Mean Percent Score" , mean/maxScore) ;
        statsMap.put("Highest Score" ,HightestScore ) ;
        statsMap.put("Lowest Score" , LowestScore) ;
        statsMap.put("Standard Deviation" , std ) ;
        statsMap.put("Variance" , variance) ;
        statsMap.put("Range" , HightestScore - LowestScore)  ;
        statsMap.put("Median" , median);
        statsMap.put("25th Percentile" , firstQ) ;
        statsMap.put("75th Percentile" , thirtQ) ;
        statsMap.put("Interquartile Range" , thirtQ-firstQ) ;
//        statsMap.put()

        printMap(statsMap);
        return statsMap ;
    }
}
class SortByScore implements Comparator<ArrayList<String>>
{
    public int compare(ArrayList<String> a, ArrayList<String> b)
    {
        return (int)Math.round(Double.parseDouble(b.get(b.size()-1))-Double.parseDouble(a.get(a.size()-1)));
    }
}