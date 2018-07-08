package Jowil;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Main extends Application {



    public final static Integer STUDENTID=0, STUDENTNAME=1, STUDENTFORM=2, IGNORE=3;

    /**
     *
     * @param filePath
     * @param identifiers student identifiers placed in the order of columns of the CSV file being loaded
     * @throws IOException
     */
    public static void loadCsv(String filePath, ArrayList<Integer> identifiers ,boolean isHeadersExist, boolean isCorrectAnswersExist) throws IOException {

        BufferedReader input = new BufferedReader(new FileReader(filePath));
        String line = null;
        ArrayList<ArrayList<String>> studentsAnswers=new ArrayList<ArrayList<String>>();
        ArrayList<String> studentNames=new ArrayList<String>();
        ArrayList<String> studentIDs=new ArrayList<String>();
        ArrayList<Integer> studentForms= new ArrayList<Integer>();

        if(isHeadersExist&&( line = input.readLine()) != null)//headers
            Statistics.setQuestionNames(cropArray(line.split(","),identifiers.size()));


        if(isCorrectAnswersExist &&( line = input.readLine()) != null ) { //correct answers
            ArrayList<ArrayList<String>> correctAnswers = new ArrayList<ArrayList<String>>();
            correctAnswers.add(cropArray(line.split(","), identifiers.size()));
            Statistics.setCorrectAnswers(correctAnswers);
        }
        while (( line = input.readLine()) != null) { //students answers
            String [] row=line.split(",");
            updateIdentifiers(studentIDs,studentNames,studentForms,row,identifiers);
            studentsAnswers.add(cropArray(line.split(","),identifiers.size()));
        }

        //initialize internal fields with parsed data
        Statistics.setStudentAnswers(studentsAnswers);
        Statistics.setIdentifierMode(Statistics.AUTOMODE);

        if(studentIDs.size()!=0){
            Statistics.setStudentIDs(studentIDs);
            Statistics.setIdentifierMode(Statistics.IDMODE);
        }

        if(studentNames.size()!=0){
            Statistics.setStudentNames(studentNames);
            if(studentIDs.size()==0)
                Statistics.setIdentifierMode(Statistics.NAMEMODE);
        }

        if(studentForms.size()==0)
            Statistics.setStudentForms(new ArrayList<Integer>(Statistics.getStudentIDs().size()));
        else
            Statistics.setStudentForms(studentForms);

    }

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/FXML/Sample.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("/FXML/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}



	//helper functions
    private static ArrayList<String> cropArray(String [] original,int skipCols){
        ArrayList<String> cropped=new ArrayList<String>();
        for(int i=skipCols;i<original.length;i++)
            cropped.add(original[i]);
        return cropped;
    }

    private static void updateIdentifiers(ArrayList<String> studentIDs, ArrayList<String> studentNames,ArrayList<Integer> studentForms, String [] row,ArrayList<Integer> identifiers){
        for(int i=0;i<identifiers.size();i++){
            if(identifiers.get(i)==STUDENTID)
                studentIDs.add(row[i]);
            else if(identifiers.get(i)==STUDENTNAME)
                studentNames.add(row[i]);
            else if(identifiers.get(i)==STUDENTFORM)
                studentForms.add(Integer.parseInt(row[i]));
        }
    }



}