package Jowil;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Percentile ;




import org.apache.commons.math3.distribution.TDistribution ;


import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static org.apache.commons.math3.stat.StatUtils.* ;


public class Statistics {



    ////////////////////fields
//    public static final int IDMODE=0,NAMEMODE=1,AUTOMODE=2;
//    private static int identifierMode=AUTOMODE;
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
    private static ArrayList<String> grades ; // list of university grades i.e. A , B , C
    private static ArrayList<Double> gradesLowerRange; // list of the lower range of the grades assuming that the upper range is the lower range of the next grade
    private static ArrayList<String> studentIdentifier;
    private static String idnetifierName ;

    ////////////////////setters
//    public static void setIdentifierMode(int identifierMode) {
//        Statistics.identifierMode = identifierMode;
//    }
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

    public static void setGradesLowerRange(ArrayList<Double> gradesLowerRange) {
        Statistics.gradesLowerRange = gradesLowerRange;
    }

    public static void setGrades(ArrayList<String> grades) {
        Statistics.grades = grades;
    }
    public static void setSubScores(ArrayList<Double> subScores) {
        Statistics.subScores = subScores;
    }


    public static void setStudentIdentifier(ArrayList<String> studentIdentifier) {
        Statistics.studentIdentifier = studentIdentifier;
    }

    public static void setIdnetifierName(String idnetifierName) {
        Statistics.idnetifierName = idnetifierName;
    }

    //getters
//    public static int getIdentifierMode() {
//        return identifierMode;
//    }
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
//        System.out.println("ID mode " + Jowil.Statistics.getIdentifierMode());
        System.out.println("Correct ans: " + Jowil.Statistics.getCorrectAnswers().toString());
        System.out.println("Student ans: " + Jowil.Statistics.getStudentAnswers().toString());
        System.out.println("Questions choices: "+ Statistics.questionsChoices);
        System.out.println("Grades: "+grades);
        System.out.println("Grades Lower Ranges: "+gradesLowerRange);
    }
    public static void printCalculations(){
        System.out.println("-----------------------------------------------------");
        printStudentScores();
        printSortedStudentAnswers();
        printAnswerStats();
    }


    private static void printMap (Map<String , String> map ) {
        System.out.println("Map Data");
        for(Map.Entry<String , String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    private static void printTable (ArrayList<ArrayList<String>> table) {
        System.out.println("Table Data: ");
        for(ArrayList<String> row : table) {
            System.out.println(row);
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
        initSortedStudentAnswers();
        initAnswersStats();
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




    private static double calcCI(int sampleSize, double level , double StandardDiviation ) {
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(sampleSize - 1 );
            // Calculate critical value
            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2);
            // Calculate confidence interval
            return critVal * StandardDiviation / Math.sqrt(sampleSize);
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }

    private static  double getNumberWithinLimits(double number , double lowerLimit , double upperLimit) {
        if(number < lowerLimit)
            return lowerLimit;
        else  if (number > upperLimit)
            return upperLimit;
        else
            return number ;
    }

    private static double calcSEM ( double scoreStd ) {
        return 0  ;
    }

    private static double calcKr20 (double var){
        double pqsum = 0 ;
        for ( int formIndex = 0 ; formIndex < answersStats.size() ; formIndex ++ ) {
            ArrayList<ArrayList<Double>> formStats = answersStats.get(formIndex);
            for(int qIndex = 0 ; qIndex < formStats.size() ; qIndex++) {
                ArrayList<Double> questionStats = formStats.get(qIndex);
                String correctAnswer = correctAnswers.get(formIndex).get(qIndex);
                //get index of the correct answer in the question choices List
                int correctAnswerIndex = questionsChoices.get(qIndex).indexOf(correctAnswer);
                // p is the proportion of correct responses
                double p = questionStats.get(correctAnswerIndex);
                // q is the proportion of incorrect responses
                double q = (1 - p);

                pqsum += p * q ;


            }

            int k  = studentScores.size();
            return k/(k-1) * (1-pqsum/var) ;
        }
        return 0 ;
    }


    public static double calcKr21 (double mean , double var , int n) {

//         [n/(n-1) * [1-(M*(n-M)/(n*Var))]
        return  n/(n-1) * ( 1 - (mean * (n - mean) / (n * var))) ;
    }

    public static  Map<String ,String> report3Stats() {

        DecimalFormat format = new DecimalFormat("0.#");

        Map<String , String>  statsMap = new HashMap<String, String>() ;


//        ArrayList<Double> sortedStudentScores = new ArrayList<Double>(studentScores);
//        Collections.sort(sortedStudentScores);
//        double[] sortedScores = sortedStudentScores.stream().mapToDouble(d -> d).toArray();


        double[] scores = studentScores.stream().mapToDouble(d -> d).toArray();
        double[] weights = questionWeights.get(0).stream().mapToDouble(d -> d).toArray() ; // weights of the first Form
        double benchMark = 75.0 ;
        double mean = mean(scores) ;
        double variance = variance(scores);
        double std = Math.sqrt(variance) ;
        double maxScore = sum(weights) ;
        double HightestScore = max(scores);
        double LowestScore = min(scores);
        int numberOfStudents = studentScores.size();

        // 3 5 7 8 12 14 14 15 18 21
//        double[] wello = {3, 7, 8, 5, 12, 14, 21, 15, 18, 14} ;
        DescriptiveStatistics ds = new DescriptiveStatistics(scores);
        double median = ds.getPercentile(50);
//        Percentile percentile = new Percentile() ;
//        double firstQ = percentile.evaluate(wello , 25 ) ;
        double firstQ = ds.getPercentile(25) ;
        double thirtQ = ds.getPercentile(75) ;

//        System.out.println("hi :"+ (calcCI(numberOfStudents , .98 , std))) ;


        double CI90Lower =getNumberWithinLimits( mean - calcCI(numberOfStudents,0.9 , std)  , 0 , maxScore) ;
        double CI90Higher = getNumberWithinLimits(mean + calcCI(numberOfStudents,0.9 , std)  , 0 , maxScore) ;

        double CI95Lower = getNumberWithinLimits(mean - calcCI(numberOfStudents,0.95 , std) , 0 , maxScore)  ;
        double CI95Higher = getNumberWithinLimits(mean + calcCI(numberOfStudents,0.95 , std)  , 0 , maxScore) ;

        double CI99Lower = getNumberWithinLimits(mean - calcCI(numberOfStudents,0.99 , std) , 0 , maxScore)  ;
        double CI99Higher = getNumberWithinLimits(mean + calcCI(numberOfStudents,0.99 , std)  , 0 , maxScore) ;

        double kr20  = calcKr20(variance);
        double kr21 = calcKr21(mean , variance , numberOfStudents) ;


        statsMap.put("Mean" , format.format(mean));
        statsMap.put("Number Of Graded Questions" , format.format(questionsChoices.size()) );
        statsMap.put("Maximum Possible Score" , format.format(maxScore)) ; // assuming all Forms should have the same weight sum
        statsMap.put("Benchmark" ,  format.format(benchMark) ) ;
        statsMap.put("Mean Percent Score" , format.format(mean/maxScore) ) ;
        statsMap.put("Highest Score" ,format.format(HightestScore) ) ;
        statsMap.put("Lowest Score" , format.format(LowestScore)) ;
        statsMap.put("Standard Deviation" , format.format(std) ) ;
        statsMap.put("Variance" , format.format(variance)) ;
        statsMap.put("Range" , format.format(HightestScore - LowestScore))  ;
        statsMap.put("Median" , format.format(median));
        statsMap.put("25th Percentile" , format.format(firstQ)) ;
        statsMap.put("75th Percentile" , format.format(thirtQ)) ;
        statsMap.put("Interquartile Range" , format.format(thirtQ-firstQ)) ;

        statsMap.put("90" , format.format(CI90Lower) + " - " + format.format(CI90Higher)) ;
        statsMap.put("95" , format.format(CI95Lower) + " - " + format.format(CI95Higher)) ;
        statsMap.put("99" , format.format(CI99Lower) + " - " + format.format(CI99Higher)) ;

        statsMap.put("Kuder-Richardson Formula 20" ,  format.format(kr20))  ;
        statsMap.put("Kuder-Richardson Formula 21" ,  format.format(kr21)) ;


        printMap(statsMap);
        return statsMap ;
    }

//    private static ArrayList<String> getIdentifierArray() {
//        ArrayList<String> autoIdentifierArray = new ArrayList<String>() ;
//        for(int i = 0 ; i < studentScores.size() ; i++) {
//            autoIdentifierArray.add(String.valueOf(i) );
//        }
//        if(identifierMode == IDMODE)
//            return studentIDs ;
//        else if (identifierMode == NAMEMODE)
//            return studentNames ;
//        else
//            return autoIdentifierArray ;
//    }


    private static String getGrade(double scorePrecentage) {
        for (int gradeIndex = 0 ; gradeIndex < grades.size() ; gradeIndex ++) {
            if(gradesLowerRange.get(gradeIndex)> scorePrecentage)
                return grades.get(gradeIndex-1) ;
        }
        return grades.get(grades.size()-1);
    }
    public static ArrayList<ArrayList<String>> report4Stats( ){
        DecimalFormat format = new DecimalFormat("0.#");

//        ArrayList<String> identifierArray = getIdentifierArray() ;
        ArrayList<ArrayList<String>> statsTable = new ArrayList<ArrayList<String>>();

        double[] weights = questionWeights.get(0).stream().mapToDouble(d -> d).toArray() ;
        double maxScore = sum(weights);


        for(int studentIndex  = 0 ; studentIndex < studentScores.size() ; studentIndex++ ) {
            ArrayList<String> tableRow = new ArrayList<String>() ;
            double scorePrecentage = studentScores.get(studentIndex)/ maxScore ;
            tableRow.add(studentIdentifier.get(studentIndex)) ;
            tableRow.add(getGrade(scorePrecentage)) ;
            tableRow.add(format.format(studentScores.get(studentIndex))+"/"+ format.format(maxScore)) ;
            tableRow.add(format.format(scorePrecentage*100)+"%") ;

            statsTable.add(tableRow);
        }

        printTable(statsTable);
        return statsTable ;
    }

}
class SortByScore implements Comparator<ArrayList<String>>
{
    public int compare(ArrayList<String> a, ArrayList<String> b)
    {
        return (int)Math.round(Double.parseDouble(b.get(b.size()-1))-Double.parseDouble(a.get(a.size()-1)));
    }
}