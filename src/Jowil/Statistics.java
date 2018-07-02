package Jowil;

import java.util.ArrayList;
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
    private static ArrayList<String> studentNames;
    private static ArrayList<String> studentIDs;
    private static ArrayList<Double> studentScores;
    private static ArrayList<Double> questionWeights;
    private static ArrayList<String> questionNames;
    private static ArrayList<String> CorrectAnswers;
    private static ArrayList<ArrayList<String>> studentAnswers;
    private static ArrayList<ArrayList<String>>sortedStudentAnswers;
    private static ArrayList<ArrayList<Double>>answersStats; //Answers percentages vs Questions
    private static ArrayList<String> questionsMaxChoice;


    ////////////////////setters
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
        CorrectAnswers = correctAnswers;
    }

    public static void setStudentAnswers(ArrayList<ArrayList<String>> studentAnswers) {
        Statistics.studentAnswers = studentAnswers;
    }

    public static void setQuestionsMaxChoice(ArrayList<String> questionsMaxChoice) {
        Statistics.questionsMaxChoice = questionsMaxChoice;
    }


    
    ///////////////initializers

    private static void initScores(){

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

    public void simulateUI(){

    }















}
