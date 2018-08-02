package Jowil;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.*;
import java.util.ArrayList;


public class GradeBoundariesController extends Controller{


    GradeBoundariesController(Controller back) {
        super("gradeBoundaries.fxml","Grading Scale and Report Generation",1.25,1.25,true,back);
    }

    @FXML
    ScrollPane scrollPane;

    @FXML
    VBox gradesVBox;

    @FXML
    JFXComboBox gradesConfigCombo;

    @FXML
    HBox comboHBox;

    @FXML
    StackPane deleteConfigButton;

    @FXML
    Label reportsConfigTitle;

    @FXML
    Label gradeBoundariesTitle;

    @FXML
    Separator midSeparator;




    int gradesConfigComboSelectedIndex;
    private static ArrayList<Node> gradesHBoxes;
    private  final String standardLettersGradingFile="Standard Letters Scale.jgc",
            allLettersGradingFile="All Letters Scale.jgc",egyptianGradingFile1="Egyptian Scale 1.jgc"
            ,egyptianGradingFile2="Egyptian Scale 2.jgc",gradesConfigDirPath=getClass().getResource("/GradeConfigs").toExternalForm();





    @Override
    protected void initComponents() {
        
        initScrollPane();
        initGradesConfigCombo();
        initTitles();

        
    }
    
    @Override
    protected void updateSizes(){

        super.updateSizes();
        //left half
        comboHBox.setLayoutX(rootWidthToPixels(0.05));
        comboHBox.setLayoutY(rootHeightToPixels(0.15));
        comboHBox.setSpacing(resXToPixels(0.005));
        gradesConfigCombo.setPrefWidth(rootWidthToPixels(0.25));
        scrollPane.setLayoutY(rootHeightToPixels(0.25));
        scrollPane.setLayoutX(rootWidthToPixels(comboHBox.getLayoutX()));
        gradeBoundariesTitle.setLayoutX(comboHBox.getLayoutX());
        gradeBoundariesTitle.setLayoutY(rootHeightToPixels(0.05));

        //right half

        midSeparator.setLayoutX(rootWidthToPixels(0.5));
        midSeparator.setLayoutY(rootHeightToPixels(0.03));
        midSeparator.setPrefHeight(rootHeightToPixels(0.8));
        reportsConfigTitle.setLayoutX(midSeparator.getLayoutX()+rootWidthToPixels(0.03));
        reportsConfigTitle.setLayoutY(gradeBoundariesTitle.getLayoutY());





    }


    @Override
    protected void saveChanges() {

    }

    @Override
    protected Controller getNextController() {
        return null;
    }
    
    //////helper methods
    private void initScrollPane(){
        scrollPane.setContent(gradesVBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        
    }
    
    private void initGradesConfigCombo(){
        
        gradesConfigCombo.setVisibleRowCount(3);
        gradesConfigCombo.setOnShown(t->gradesConfigCombo.getSelectionModel().clearSelection());
        gradesConfigCombo.setOnHidden(t->{gradesConfigCombo.getSelectionModel().select(gradesConfigComboSelectedIndex); System.out.println("easy"+gradesConfigComboSelectedIndex);});
        gradesConfigCombo.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)-> {
            if((Integer)newValue!=-1)
                gradesConfigComboSelectedIndex=(Integer)newValue;
        });

        gradesConfigCombo.setCellFactory(t->{
                        final ListCell<String> cell = new ListCell<String>() {
                            {
                                //super.setPrefHeight(gradesConfigCombo.getPrefHeight());
                            }
                            @Override public void updateItem(String item,
                                                             boolean empty) {
                                super.updateItem(item, empty);
                                setText(item);
                            }
                        };
                        return cell;
                    });


    }

    private void initDeleteConfigButton(){
        deleteConfigButton.setOnMouseClicked(t->deleteCurrentConfig());

    }

    private void initTitles(){
        gradeBoundariesTitle.setFont(new Font("Arial", headersFontSize));
        reportsConfigTitle.setFont(new Font("Arial", headersFontSize));
    }


    private static void initGradesVBox(){
        gradesHBoxes=new ArrayList<Node>();

    }


    private void deleteCurrentConfig(){


    }

    public static void addNextGrade(int callingIndex){
        int newIndex=callingIndex+1;
        gradesHBoxes.add(newIndex,new gradeHBox(newIndex,"Grade "+Integer.toString(newIndex+1),"50.0"));
    }

    public static void deleteGrade(int callingIndex){
        gradesHBoxes.remove(callingIndex);
    }

    private void loadGradeConfigs(){

        File configsDir = new File(gradesConfigDirPath);
        File[] directoryListing = configsDir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                String fileName=file.getName().substring(0,file.getName().indexOf(".jgc"));
                gradesConfigCombo.getItems().add(fileName);
                loadGradeConfigFile(file.getPath(),fileName);

            }
            return;
        } else {
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Configurations Error",
                    "Error in loading Grade Boundaries Configurations");
            return;
        }
    }


    private void loadGradeConfigFile(String filePath,String fileName){

        BufferedReader input;
        try {
             input= new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = input.readLine()) != null) {
                String[] row = line.split(",",-1);

            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Configuration loading Error",
                    "Error in loading \""+fileName+"\" grade configuration.");
            return;
        }

    }

}
