package Jowil;

import com.sun.jndi.toolkit.ctx.StringHeadTail;

import java.io.IOException;
import java.util.ArrayList;

public class TestUtils {

    public static ArrayList<ArrayList<String>> generateTestAllQuestionsChoices(){
        ArrayList<ArrayList<String>> out=new ArrayList<ArrayList<String>>();
        for(int i=0;i<3;i++){
            out.add(new ArrayList<String>());
            char choice='a';
            for(int j=0;j<5;j++)
                out.get(i).add(Character.toString((char)(choice+j)));
        }

        for(int i=0;i<3;i++){
            out.add(new ArrayList<String>());
            for(int j=0;j<9;j++)
                out.get(out.size()-1).add(Integer.toString(j+1));
        }
        return out;
    }

    public static void setQuestionChoicesFromFile(String filePath) throws IOException {
         Statistics.setQuestionsChoices(CSVHandler.readCsvFile(filePath) );
    }

    public static void setQuestionsWeights(int numberOfQuestions , int numberOfForms){
        ArrayList<ArrayList<Double>> formsWeights = new ArrayList<>() ;
        ArrayList<Double> formWeights = new ArrayList<Double>();
        for (int i = 0; i < numberOfQuestions; i++)
            formWeights.add(1.0);

        for(int formIndex = 0 ; formIndex < numberOfForms ; formIndex++) {
            formsWeights.add(formWeights);
        }

        Statistics.setQuestionWeights(formsWeights);
    }
    //grade lower range contains the lower range of the corresponding grade and one extra element which is 1 (the lower range of what is after A+)
    public static void fillGradeRanges() {
        ArrayList<String> grades = new ArrayList<String>();
        grades.add("F") ;grades.add("D") ;grades.add("C") ; grades.add("B");grades.add("A");
        Statistics.setGrades(grades);

        ArrayList<Double> gradeLowerRange  =new ArrayList<Double>();
        gradeLowerRange.add(0.0);
        for(int i=0;i<4;i++)
            gradeLowerRange.add(0.6 + 0.1 * i);
        gradeLowerRange.add(1.0);
        Statistics.setGradesLowerRange(gradeLowerRange);
    }

    public static ArrayList<String> generateAutoIds (int n) {
        ArrayList<String> out = new ArrayList<>();
        for(int i = 0 ; i < n ; i ++ )
            out.add(String.valueOf(i+1));

        return out;
    }
}
