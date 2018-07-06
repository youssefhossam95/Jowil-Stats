package Jowil;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Statistics {

    /*
    GDR: Grades Distribution Report
    CTR: Condensed Test Report
    TSR: Test Statistics Report
    SGR: Students Grades Report
    CIAR: Condensed Item Analysis Report
     */

    class SortByScore implements Comparator<ArrayList<String>>
    {
        public int compare(ArrayList<String> a, ArrayList<String> b)
        {
            return (int)Math.round(Double.parseDouble(b.get(b.size()-1))-Double.parseDouble(a.get(a.size()-1)));
        }
    }


    ////////////////////fields
    public static final int IDMODE=0,NAMEMODE=1,AUTOMODE=2;
    private static int identifierMode=AUTOMODE;
    private static ArrayList<String> studentNames;
    private static ArrayList<String> studentIDs;
    private static ArrayList<Double> studentScores;
    private static ArrayList<Double> questionWeights;
    private static ArrayList<String> questionNames;


    private static ArrayList<String> correctAnswers;
    private static ArrayList<ArrayList<String>> studentAnswers;
    private static ArrayList<ArrayList<String>>sortedStudentAnswers;
    private static ArrayList<ArrayList<Double>>answersStats; //Answers percentages vs Questions
    private static ArrayList<String> questionsMaxChoice;


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

    public static void setQuestionWeights(ArrayList<Double> questionWeights) {
        Statistics.questionWeights = questionWeights;
    }

    public static void setQuestionNames(ArrayList<String> questionNames) {
        Statistics.questionNames = questionNames;
    }

    public static void setCorrectAnswers(ArrayList<String> correctAnswers) {
        Statistics.correctAnswers = correctAnswers;
    }

    public static void setStudentAnswers(ArrayList<ArrayList<String>> studentAnswers) {
        Statistics.studentAnswers = studentAnswers;
    }

    public static void setQuestionsMaxChoice(ArrayList<String> questionsMaxChoice) {
        Statistics.questionsMaxChoice = questionsMaxChoice;
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

    public static ArrayList<String> getCorrectAnswers() {
        return correctAnswers;
    }

    public static ArrayList<ArrayList<String>> getStudentAnswers() {
        return studentAnswers;
    }


    // print fuctions
    public static void printStudentScores() {
        System.out.print("Student Scores: ");
        for(Double score : studentScores) {
            System.out.print(score + ", ");
        }
        System.out.println();
    }

    ///////////////initializers

    private static void initScores(){

        studentScores = new ArrayList<Double>() ;
        for(int i = 0 ; i <studentAnswers.size() ; i ++) {
            double studentScore = 0 ;
            ArrayList<String> studentAnswer = studentAnswers.get(i) ;
            for (int j = 0 ; j <  studentAnswer.size() ; j ++) {
                String questionAnswer = studentAnswer.get(j) ;
                if(questionAnswer.equals(correctAnswers.get(j))) {
                    studentScore+= questionWeights.get(j) ;
                }
            }
            studentScores.add(studentScore) ;
        }
        printStudentScores();
    }

    private static void initSortedStudentAnswers(){


    }

    private static void initAnswersStats(){

    }

    public static void init(){
        initScores();
        initSortedStudentAnswers();
        initAnswersStats();
    }

    public static void simulateUI(){

        //fill student names
        studentNames = new ArrayList<String>() ;
        studentNames.add("Walid");
        studentNames.add("youssef");

        //fill student ids
        studentIDs = new ArrayList<String>() ;
        studentIDs.add("1234") ;
        studentIDs.add("5678") ;

        //fill questionNames
        questionNames = new ArrayList<String>() ;
        questionNames.add("Question1") ;
        questionNames.add("Question2") ;
        questionNames.add("Question3") ;
        questionNames.add("Question4") ;
        questionNames.add("Question5") ;

        //fill question Weights
        questionWeights = new ArrayList<Double>() ;
        questionWeights.add(1.0);
        questionWeights.add(1.0);
        questionWeights.add(2.0);
        questionWeights.add(2.0);
        questionWeights.add(0.5);

        //fill question max choice
        questionsMaxChoice = new ArrayList<String>() ;
        questionsMaxChoice.add("D") ;
        questionsMaxChoice.add("D") ;
        questionsMaxChoice.add("D") ;
        questionsMaxChoice.add("D") ;
        questionsMaxChoice.add("D") ;

        //fill correctAnswers
        correctAnswers = new ArrayList<String>() ;
        correctAnswers.add("A") ;
        correctAnswers.add("B") ;
        correctAnswers.add("A") ;
        correctAnswers.add("C") ;
        correctAnswers.add("D") ;

        //fill student answers
        studentAnswers = new ArrayList<ArrayList<String>>() ;
        ArrayList<String>answer1 = new ArrayList<String>() ;
        answer1.add("A") ; answer1.add("B") ; answer1.add("C") ; answer1.add("C") ; answer1.add("A") ;

        ArrayList<String>answer2= new ArrayList<String>() ;
        answer2.add("A") ; answer2.add("B") ; answer2.add("C") ; answer2.add("C") ; answer2.add("D") ;

        studentAnswers.add(answer1)  ;
        studentAnswers.add(answer2) ;




    }















}
