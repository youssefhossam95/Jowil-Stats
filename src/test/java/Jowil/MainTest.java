//package Jowil;
//
//import junit.framework.TestCase;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class MainTest extends TestCase {
//
//    public void testLoadCsv() throws IOException {
//        try {
//            System.out.println("Working Directory = " +
//                    System.getProperty("user.dir"));
//
//            ArrayList<Integer> ids1 = new ArrayList<Integer>();
//            ids1.add(Main.STUDENTID);
//            ids1.add(Main.STUDENTNAME);
//            Jowil.Main.loadCsv(".\\src\\test\\TestCSVs\\test1.csv", ids1, true, true);
//            System.out.println("Test 1 output:");
//            System.out.println("Q names: " + Jowil.Statistics.getQuestionNames().toString());
//            System.out.println("Student Ids: " + Jowil.Statistics.getStudentIDs().toString());
//            System.out.println("Student names: " + Jowil.Statistics.getStudentNames().toString());
//            System.out.println("ID mode " + Jowil.Statistics.getIdentifierMode());
//            System.out.println("Correct ans: " + Jowil.Statistics.getCorrectAnswers().toString());
//            System.out.println("Student ans: " + Jowil.Statistics.getStudentAnswers().toString());
//            Jowil.Statistics.setQuestionNames(new ArrayList<String>());
//            Jowil.Statistics.setStudentIDs(new ArrayList<String>());
//            Jowil.Statistics.setStudentNames(new ArrayList<String>());
//            Jowil.Statistics.setCorrectAnswers(new ArrayList<String>());
//            Jowil.Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
//
//
//            ArrayList<Integer> ids2 = new ArrayList<Integer>();
//            ids2.add(Main.STUDENTID);
//
//            Jowil.Main.loadCsv(".\\src\\test\\TestCSVs\\test2.csv", ids2, true, false);
//            System.out.println("Test 2 output:");
//            System.out.println("Q names: " + Jowil.Statistics.getQuestionNames().toString());
//            System.out.println("Student Ids: " + Jowil.Statistics.getStudentIDs().toString());
//            System.out.println("Student names: " + Jowil.Statistics.getStudentNames().toString());
//            System.out.println("ID mode " + Jowil.Statistics.getIdentifierMode());
//            System.out.println("Correct ans: " + Jowil.Statistics.getCorrectAnswers().toString());
//            System.out.println("Student ans: " + Jowil.Statistics.getStudentAnswers().toString());
//            Jowil.Statistics.setQuestionNames(new ArrayList<String>());
//            Jowil.Statistics.setStudentIDs(new ArrayList<String>());
//            Jowil.Statistics.setStudentNames(new ArrayList<String>());
//            Jowil.Statistics.setCorrectAnswers(new ArrayList<String>());
//            Jowil.Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
//
//
//            ArrayList<Integer> ids3 = new ArrayList<Integer>();
//            ids3.add(Main.STUDENTNAME);
//            Jowil.Main.loadCsv(".\\src\\test\\TestCSVs\\test3.csv", ids3, false, true);
//            System.out.println("Test 3 output:");
//            System.out.println("Q names: " + Jowil.Statistics.getQuestionNames().toString());
//            System.out.println("Student Ids: " + Jowil.Statistics.getStudentIDs().toString());
//            System.out.println("Student names: " + Jowil.Statistics.getStudentNames().toString());
//            System.out.println("ID mode " + Jowil.Statistics.getIdentifierMode());
//            System.out.println("Correct ans: " + Jowil.Statistics.getCorrectAnswers().toString());
//            System.out.println("Student ans: " + Jowil.Statistics.getStudentAnswers().toString());
//            Jowil.Statistics.setQuestionNames(new ArrayList<String>());
//            Jowil.Statistics.setStudentIDs(new ArrayList<String>());
//            Jowil.Statistics.setStudentNames(new ArrayList<String>());
//            Jowil.Statistics.setCorrectAnswers(new ArrayList<String>());
//            Jowil.Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
//
//            ArrayList<Integer> ids4 = new ArrayList<Integer>();
//            Jowil.Main.loadCsv(".\\src\\test\\TestCSVs\\test4.csv", ids4, false, false);
//            System.out.println("Test 4 output:");
//            System.out.println("Q names: " + Jowil.Statistics.getQuestionNames().toString());
//            System.out.println("Student Ids: " + Jowil.Statistics.getStudentIDs().toString());
//            System.out.println("Student names: " + Jowil.Statistics.getStudentNames().toString());
//            System.out.println("ID mode " + Jowil.Statistics.getIdentifierMode());
//            System.out.println("Correct ans: " + Jowil.Statistics.getCorrectAnswers().toString());
//            System.out.println("Student ans: " + Jowil.Statistics.getStudentAnswers().toString());
//            Jowil.Statistics.setQuestionNames(new ArrayList<String>());
//            Jowil.Statistics.setStudentIDs(new ArrayList<String>());
//            Jowil.Statistics.setStudentNames(new ArrayList<String>());
//            Jowil.Statistics.setCorrectAnswers(new ArrayList<String>());
//            Jowil.Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
//
//            ArrayList<Integer> ids5 = new ArrayList<Integer>();
//            ids5.add(Main.STUDENTNAME);
//            ids5.add(Main.STUDENTID);
//            Jowil.Main.loadCsv(".\\src\\test\\TestCSVs\\test5.csv", ids5, false, true);
//            System.out.println("Test 5 output:");
//            System.out.println("Q names: " + Jowil.Statistics.getQuestionNames().toString());
//            System.out.println("Student Ids: " + Jowil.Statistics.getStudentIDs().toString());
//            System.out.println("Student names: " + Jowil.Statistics.getStudentNames().toString());
//            System.out.println("ID mode " + Jowil.Statistics.getIdentifierMode());
//            System.out.println("Correct ans: " + Jowil.Statistics.getCorrectAnswers().toString());
//            System.out.println("Student ans: " + Jowil.Statistics.getStudentAnswers().toString());
//            Jowil.Statistics.setQuestionNames(new ArrayList<String>());
//            Jowil.Statistics.setStudentIDs(new ArrayList<String>());
//            Jowil.Statistics.setStudentNames(new ArrayList<String>());
//            Jowil.Statistics.setCorrectAnswers(new ArrayList<String>());
//            Jowil.Statistics.setStudentAnswers(new ArrayList<ArrayList<String>>());
//        }
//        catch(NullPointerException e){
//
//        }
//    }
//}