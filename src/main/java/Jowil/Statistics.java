package Jowil;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
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
    private static ArrayList<ArrayList<String>> studentAnswers; //student vs answers
    private static ArrayList<ArrayList<ArrayList<String>>>sortedStudentAnswers; //for each form :student vs (answers+score)
    private static ArrayList<ArrayList<ArrayList<Double>>>answersStats; //For each form :Questions vs each possible choice percentage ( every row can have different number of possible choices)
    private static ArrayList<ArrayList<String>> questionsChoices; //list of all possible choices in order for every question. (every row can have different number of choices)
    private static ArrayList<ArrayList<Double>> subScores; //subjective scores -> student  vs sub scores
    private static ArrayList<String> grades ; // list of university grades i.e. A , B , C
    private static ArrayList<Double> gradesLowerRange; // list of the lower range of the grades assuming that the upper range is the lower range of the next grade
    private static ArrayList<String> studentIdentifier;
    private static String identifierName ="ID";
    private static ArrayList<Double> subjMaxScores;
    private static ArrayList<ArrayList<Double>> formsScors ;
    private static ArrayList<ArrayList<ArrayList<Integer>>> pointBiserialTables;
    private static ArrayList<ArrayList<Integer>> pointBiserialTotalTable ;

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
    public static void setSubScores(ArrayList<ArrayList<Double>> subScores) {
        Statistics.subScores = subScores;
    }


    public static void setStudentIdentifier(ArrayList<String> studentIdentifier) {
        Statistics.studentIdentifier = studentIdentifier;
    }

    public static void setIdentifierName(String idnetifierName) {
        Statistics.identifierName = idnetifierName;
    }

    public static ArrayList<Double> getSubjMaxScores() {
        return subjMaxScores;
    }

    public static void setSubjMaxScores(ArrayList<Double> subjScores) {
        Statistics.subjMaxScores = subjScores;
    }


    public static void setFormsScors(ArrayList<ArrayList<Double>> formsScors) {
        Statistics.formsScors = formsScors;
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


    public static ArrayList<String> getSpecificQuestionChoices(int questionIndex){return questionsChoices.get(questionIndex);}

    public static int getNumberOfForms (){
        double[]forms = studentForms.stream().mapToDouble(d -> d).toArray();
        return (int)max(forms)+1 ;
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
//    private static void printMap (Map<String , Integer> map ) {
//        System.out.println("Map Data");
//        for(Map.Entry<String , Integer> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
//    }

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

    public static void initFormsScores(){

        double[]forms = studentForms.stream().mapToDouble(d -> d).toArray();
        int numberOfForms = (int)max(forms)+1 ;

        System.out.println(numberOfForms);
        formsScors = new ArrayList<ArrayList<Double>>() ;

        for (int formIndex =0 ; formIndex < numberOfForms ; formIndex++) {
            ArrayList<Double> formScores = new ArrayList<>() ;
            formsScors.add(formScores);
        }
        for(int i = 0 ; i <studentAnswers.size() ; i ++) {
            double studentScore = 0 ;
            ArrayList<String> studentAnswer = studentAnswers.get(i) ;
            for (int j = 0 ; j <  studentAnswer.size() ; j ++) {
                String questionAnswer = studentAnswer.get(j) ;
                if(questionAnswer.equals(correctAnswers.get(studentForms.get(i)).get(j))) {
                    studentScore+= questionWeights.get(studentForms.get(i)).get(j) ;
                }
            }
            formsScors.get(studentForms.get(i)).add(studentScore) ;
        }
//        printStudentScores();
        for (int formIndex =0 ; formIndex < numberOfForms ; formIndex++) {
            System.out.println("form "+(formIndex+1)+" answers: "+formsScors.get(formIndex)) ;
        }
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
        initPointBiserialTables();
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

    public static Map<String,String> calcGeneralStats (ArrayList<Double> studentScores , Double maxScore , int numberOfQuestions) {
        DecimalFormat format = new DecimalFormat("0.#");
        Map<String , String>  statsMap = new HashMap<String, String>() ;

        double[] scores = studentScores.stream().mapToDouble(d -> d).toArray();
        double benchMark = 75.0 ;
        double mean = mean(scores) ;
        double variance = variance(scores);
        double std = Math.sqrt(variance) ;
        double HightestScore = max(scores);
        double LowestScore = min(scores);
        int numberOfStudents = studentScores.size();

        DescriptiveStatistics ds = new DescriptiveStatistics(scores);
        double median = ds.getPercentile(50);
        double firstQ = ds.getPercentile(25) ;
        double thirtQ = ds.getPercentile(75) ;

        double CI90Lower =Utils.getNumberWithinLimits( mean - calcCI(numberOfStudents,0.9 , std)  , 0 , maxScore) ;
        double CI90Higher = Utils.getNumberWithinLimits(mean + calcCI(numberOfStudents,0.9 , std)  , 0 , maxScore) ;

        double CI95Lower = Utils.getNumberWithinLimits(mean - calcCI(numberOfStudents,0.95 , std) , 0 , maxScore)  ;
        double CI95Higher = Utils.getNumberWithinLimits(mean + calcCI(numberOfStudents,0.95 , std)  , 0 , maxScore) ;

        double CI99Lower = Utils.getNumberWithinLimits(mean - calcCI(numberOfStudents,0.99 , std) , 0 , maxScore)  ;
        double CI99Higher = Utils.getNumberWithinLimits(mean + calcCI(numberOfStudents,0.99 , std)  , 0 , maxScore) ;

        double kr20  = calcKr20(variance);
        double kr21 = calcKr21(mean , variance , numberOfStudents) ;


        statsMap.put("Number Of Students" , format.format(studentScores.size()));
        statsMap.put("Mean" , format.format(mean));
        statsMap.put("Number Of Graded Questions" , format.format(numberOfQuestions) );
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

    public static  Map<String ,String> report3Stats() {

        double[] wieghts = questionWeights.get(0).stream().mapToDouble(d -> d).toArray();
        return calcGeneralStats(studentScores , sum(wieghts), questionsChoices.size() )  ;

    }

    private static ArrayList<ArrayList<Integer>> transpose(ArrayList<ArrayList<Integer>> matrixIn) {
        ArrayList<ArrayList<Integer>> matrixOut = new ArrayList<ArrayList<Integer>>();
        if (!matrixIn.isEmpty()) {
            int noOfElementsInList = matrixIn.get(0).size();
            for (int i = 0; i < noOfElementsInList; i++) {
                ArrayList<Integer> col = new ArrayList<Integer>();
                for (List<Integer> row : matrixIn) {
                    col.add(row.get(i));
                }
                matrixOut.add(col);
            }
        }

        return matrixOut;
    }

    private static void  initPointBiserialTables ()  {

        double[]forms = studentForms.stream().mapToDouble(d -> d).toArray();
        int numberOfForms = (int)max(forms)+1 ;

        pointBiserialTables = new ArrayList<ArrayList<ArrayList<Integer>>>();
        pointBiserialTotalTable = new ArrayList<ArrayList<Integer>>();

        for(int i = 0 ; i <numberOfForms ; i ++) {
            ArrayList<ArrayList<Integer>> table = new ArrayList<ArrayList<Integer>>() ;
            pointBiserialTables.add(table);

            ArrayList<Integer> formTotal = new ArrayList<Integer>() ;
            pointBiserialTotalTable.add(formTotal);
        }

        for (int studentIndex = 0 ; studentIndex < studentScores.size() ; studentIndex++) {
           int studentForm =  studentForms.get(studentIndex) ;
           int studentTotal =0 ;
           ArrayList<Integer> isStudentAnswersCorrect = new ArrayList<>(); // array for each question = 1 if student answered correctly else = 0
           ArrayList<String> studentAnswer =  studentAnswers.get(studentIndex);
           for ( int questionIndex =0 ; questionIndex<studentAnswer.size() ; questionIndex++) {
               String correctAnswer = correctAnswers.get(studentForm).get(questionIndex) ;
               int isStudentAnswerCorrect  = studentAnswer.get(questionIndex).equals(correctAnswer)?1:0 ;
               studentTotal+= isStudentAnswerCorrect ;
               isStudentAnswersCorrect.add(isStudentAnswerCorrect);
           }

            pointBiserialTables.get(studentForm).add(isStudentAnswersCorrect) ;
            pointBiserialTotalTable.get(studentForm).add(studentTotal) ;
        }

        for(int i = 0 ; i < numberOfForms ; i++) {
            pointBiserialTables.set(i ,  transpose(pointBiserialTables.get(i)));
        }
        System.out.println(pointBiserialTables);
    }


    private static double calcPointBiserial(int formIndex , int questionIndex ) {
        double[] questionVector = pointBiserialTables.get(formIndex).get(questionIndex).stream().mapToDouble(d -> d).toArray();
        double[] totalVector = pointBiserialTotalTable.get(formIndex).stream().mapToDouble(d -> d).toArray();
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation() ;
        double pointBiserial =  pearsonsCorrelation.correlation(totalVector , questionVector) ;
        return Double.isNaN(pointBiserial)?0:pointBiserial;
    }

    

    private static double calcPrecentOfSolvers(double startPercent , double endPercent , int formIndex , int questionIndex ) {
        int totalNumberOfStudents = formsScors.get(formIndex).size();
        ArrayList<ArrayList<String> > formSortedStudentAnswers = sortedStudentAnswers.get(formIndex);
        String correctAnswer = correctAnswers.get(formIndex).get(questionIndex);
        double count = 0  ;
        int studentStartIndex = (int)(startPercent*totalNumberOfStudents) ;
        int studentEndIndex = (int) (endPercent * totalNumberOfStudents) ;

        for (int studentIndex =  studentStartIndex ; studentIndex<  studentEndIndex  ; studentIndex++) {
           if(formSortedStudentAnswers.get(studentIndex).get(questionIndex).equals(correctAnswer))
               count++;
        }
        int numberOfStudents  = studentEndIndex - studentStartIndex ;

        return (double)count/(double)numberOfStudents ;
    }

    public static Map<String , String> report2GeneralStats(int formIndex) {
        double[] wieghts = questionWeights.get(0).stream().mapToDouble(d -> d).toArray();
        return calcGeneralStats (formsScors.get(formIndex) , sum(wieghts),questionsChoices.size());
    }

    public static ArrayList<ArrayList<ArrayList<String>>> report2TableStats (int formIndex) {

        DecimalFormat format = new DecimalFormat("0.#");
        DecimalFormat format2 = new DecimalFormat("0.##") ;

        ArrayList<ArrayList<Double>> formStats = answersStats.get(formIndex) ;
        ArrayList<String> formCorrectAnswers= correctAnswers.get(formIndex);
        ArrayList<ArrayList<ArrayList<String>>> tables = new ArrayList<ArrayList<ArrayList<String>>>();
        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>() ;

        for(int questionIndex = 0 ; questionIndex < formCorrectAnswers.size() ; questionIndex++) {


            ArrayList<String> questionChoices = questionsChoices.get(questionIndex);
            ArrayList<String> previousQuestionChoices = questionIndex>0?questionsChoices.get(questionIndex-1):questionChoices;

            // check if the table format changed ... create new table
            if(!questionChoices.equals(previousQuestionChoices)) {
               tables.add(table);
               table = new ArrayList<ArrayList<String>>() ;
            }
            ArrayList<String>tableRow = new ArrayList<>() ;
            tableRow.add(String.valueOf(questionIndex+1)) ;
            tableRow.add(questionNames.get(questionIndex));
            ArrayList<Double> questionStats =  answersStats.get(formIndex).get(questionIndex) ;
            String correctAnswer = correctAnswers.get(formIndex).get(questionIndex) ;
            int correctAnswerIndex = questionsChoices.get(questionIndex).indexOf(correctAnswer);

            double correctAnswerPrecentage = questionStats.get(correctAnswerIndex);

            for(int answerIndex = 0 ; answerIndex < questionStats.size() ; answerIndex ++ ) {
                String addedClass = "" ;
                if(answerIndex== correctAnswerIndex)
                    addedClass=";green";
                else if(questionStats.get(answerIndex)> correctAnswerPrecentage)
                    addedClass=";red" ;
                else if(questionStats.get(answerIndex) ==0 )
                    addedClass=";gray";
                tableRow.add(format.format(questionStats.get(answerIndex) * 100)+addedClass) ;
            }

            tableRow.add(format.format(correctAnswerPrecentage)) ;
            tableRow.add(format.format(calcPrecentOfSolvers(.75 , 1.0,formIndex , questionIndex) *100) +"%");
            System.out.println(calcPrecentOfSolvers(0 , .25,formIndex , questionIndex));
            tableRow.add(format.format(calcPrecentOfSolvers(0 , .25,formIndex , questionIndex)*100) +"%");
            tableRow.add(format2.format(calcPointBiserial(formIndex, questionIndex))) ;
            table.add(tableRow);
//            tableRow.add()
        }
        tables.add(table) ;

        System.out.println("el table ya man "+ tables);
        return tables ;
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
        double[] scores = studentScores.stream().mapToDouble(d -> d).toArray();

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
        double meanScore = mean(scores) ;
        double meanScorePercentage = meanScore / maxScore ;
        ArrayList<String> meanRow = new ArrayList<String>() ;
        meanRow.add("mean;bold");
        meanRow.add(getGrade(meanScorePercentage)) ;
        meanRow.add(format.format(meanScore)+"/"+format.format(maxScore)) ;
        meanRow.add(format.format(meanScorePercentage*100) +"%") ;

        statsTable.add(meanRow);
//        printTable(statsTable);
        return statsTable ;
    }



    public static ArrayList<ArrayList<String>> report1Stats( ) {

        DecimalFormat format = new DecimalFormat("0.#");

        ArrayList<ArrayList<String>> statsTable = new ArrayList<ArrayList<String>>();

        double[] weights = questionWeights.get(0).stream().mapToDouble(d -> d).toArray() ;

        Double maxScore = sum(weights) ;
        int numberOfStudents = studentScores.size();

        Map<String, Integer> gradesCount = new HashMap<>();

        //initialize all counts to zero
        for(int i = grades.size()-1 ; i>=0 ; i-- ) {
            gradesCount.put(grades.get(i) , 0) ;
        }

        for(Double score: studentScores) {
           String grade =  getGrade(score/maxScore) ;
           gradesCount.put(grade , gradesCount.get(grade) +1) ;
        }

        for(int gradeIndex = grades.size()-1 ; gradeIndex>=0 ; gradeIndex-- ) {
            ArrayList<String>tableRow  = new ArrayList<>() ;
            int gradeCount = gradesCount.get(grades.get(gradeIndex));
            tableRow.add(grades.get(gradeIndex));
            tableRow.add(format.format(gradesLowerRange.get(gradeIndex)*100)+
                    " - " + format.format(gradesLowerRange.get(gradeIndex+1)*100) );
            tableRow.add(gradesLowerRange.get(gradeIndex)*maxScore+" - " + gradesLowerRange.get(gradeIndex+1)*maxScore) ;
            tableRow.add(String.valueOf(gradeCount)) ;
            tableRow.add(format.format( (double) gradeCount/(double)numberOfStudents *100) + "%");
            statsTable.add(tableRow);
        }
//
//        for(Map.Entry<String , Integer> entry : gradesCount.entrySet()) {
//            ArrayList<String>tableRow  = new ArrayList<>() ;
//            tableRow.add(entry.getKey());
//            tableRow.add()
////            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }



        printTable(statsTable);
        return statsTable ;
    }

}
class SortByScore implements Comparator<ArrayList<String>>
{
    public int compare(ArrayList<String> a, ArrayList<String> b)
    {
        return (int)Math.round(Double.parseDouble(a.get(a.size()-1))-Double.parseDouble(b.get(b.size()-1)));
    }
}