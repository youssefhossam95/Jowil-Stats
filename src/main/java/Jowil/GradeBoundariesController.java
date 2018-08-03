package Jowil;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


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

    HBox gradesLabelsHBox=new HBox();

    Label gradeName=new Label("Name");
    Label gradePercent=new Label("Score %");
    Label gradeRaw=new Label("Score");



    int gradesConfigComboSelectedIndex;
    int gradesCreatedIndex=1;
    private  ArrayList<gradeHBox> gradesHBoxes;
    private  final String standardLettersGradingFile="Standard Letters Scale.jgc",
            allLettersGradingFile="All Letters Scale.jgc",egyptianGradingFile1="Egyptian Scale 1.jgc"
            ,egyptianGradingFile2="Egyptian Scale 2.jgc",gradesConfigDirPath=getClass().getResource("/GradeConfigs").getFile();

    ArrayList<ArrayList<gradeHBox>> configs=new ArrayList<>();




    @Override
    protected void initComponents() {
        
        initScrollPane();
        initGradesLabelsHBox();
        initGradesConfigCombo();
        initTitles();
        initGradesVBox();
        initDeleteConfigButton();



        
    }
    
    @Override
    protected void updateSizes(){

        super.updateSizes();

        double scrollPaneWidth=rootWidthToPixels(0.43);
        double scrollPaneHeight=rootHeightToPixels(0.56);
        Font gradesLabelsFonts=new Font("Arial",resX/100);
        //left half
        comboHBox.setLayoutX(rootWidthToPixels(0.05));
        comboHBox.setLayoutY(rootHeightToPixels(0.15));
        comboHBox.setSpacing(resXToPixels(0.005));
        gradesConfigCombo.setPrefWidth(rootWidthToPixels(0.25));
        scrollPane.setLayoutY(rootHeightToPixels(0.25));
        scrollPane.setLayoutX(comboHBox.getLayoutX());
        scrollPane.setPrefWidth(scrollPaneWidth);
        scrollPane.setPrefHeight(scrollPaneHeight);
        gradeBoundariesTitle.setLayoutX(comboHBox.getLayoutX());
        gradeBoundariesTitle.setLayoutY(rootHeightToPixels(0.05));
        gradesVBox.setSpacing(resYToPixels(0.025));
        gradesLabelsHBox.setSpacing(scrollPaneWidth*0.03);
        gradesLabelsHBox.setPadding(new Insets(scrollPaneWidth*0.05,0,0,0));
        gradesVBox.setPadding(new Insets(0,0,0,scrollPaneWidth*0.02));

        gradeName.setPrefWidth(scrollPaneWidth*0.15);
        gradeRaw.setPrefWidth(scrollPaneWidth*0.13);
        gradePercent.setPrefWidth(scrollPaneWidth*0.13);

        gradeName.setFont(gradesLabelsFonts);
        gradePercent.setFont(gradesLabelsFonts);
        gradeRaw.setFont(gradesLabelsFonts);


        //right half

        midSeparator.setLayoutX(rootWidthToPixels(0.5));
        midSeparator.setLayoutY(rootHeightToPixels(0.03));
        midSeparator.setPrefHeight(rootHeightToPixels(0.8));
        reportsConfigTitle.setLayoutX(midSeparator.getLayoutX()+rootWidthToPixels(0.03));
        reportsConfigTitle.setLayoutY(gradeBoundariesTitle.getLayoutY());


        for(gradeHBox hbox:gradesHBoxes)
            hbox.updateSizes(scrollPaneWidth,scrollPaneHeight);



    }


    @Override
    protected void saveChanges() {

    }

    @Override
    protected Controller getNextController() {
        return null;
    }


    public void addNextGrade(int callingIndex){
        int newIndex=callingIndex+1;

        for(int i=callingIndex+1;i<gradesHBoxes.size();i++)
            gradesHBoxes.get(i).incrementIndex();

        gradesHBoxes.add(newIndex,new gradeHBox(newIndex,"New Grade","50.0",this));
        gradesCreatedIndex++;
        updateGradesVBox();
    }



    public void deleteGrade(int callingIndex){

        for(int i=callingIndex+1;i<gradesHBoxes.size();i++)
            gradesHBoxes.get(i).decrementIndex();

        gradesHBoxes.remove(callingIndex);

        updateGradesVBox();
    }



    //////helper methods
    private void initScrollPane(){
        scrollPane.setContent(gradesVBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


    }
    
    private void initGradesConfigCombo(){

        loadGradeConfigs();

        gradesConfigCombo.setVisibleRowCount(3);
        gradesConfigCombo.setOnShown(t->gradesConfigCombo.getSelectionModel().clearSelection());
        gradesConfigCombo.setOnHidden(t->{gradesConfigCombo.getSelectionModel().select(gradesConfigComboSelectedIndex); System.out.println("easy"+gradesConfigComboSelectedIndex);});
        gradesConfigCombo.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)-> {
            if((Integer)newValue!=-1)
                gradesConfigComboSelectedIndex=(Integer)newValue;
            gradesHBoxes=configs.get(gradesConfigComboSelectedIndex);
            updateGradesVBox();

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



        gradesConfigCombo.getSelectionModel().select(0);
    }

    private void initDeleteConfigButton(){
        deleteConfigButton.setOnMouseClicked(t->deleteCurrentConfig());

    }



    private void initTitles(){
        gradeBoundariesTitle.setFont(new Font("Arial", headersFontSize));
        reportsConfigTitle.setFont(new Font("Arial", headersFontSize));
    }


    private  void initGradesVBox(){
        gradesHBoxes=configs.get(gradesConfigComboSelectedIndex);

    }

    private void initGradesLabelsHBox(){

        gradesLabelsHBox.getChildren().addAll(gradeName,gradePercent,gradeRaw);
        gradeName.setStyle("-fx-text-fill:#989898;-fx-font-weight: bold;");
        gradeName.setAlignment(Pos.CENTER);
        gradeRaw.setStyle("-fx-text-fill:#989898;-fx-font-weight: bold;");
        gradeRaw.setAlignment(Pos.CENTER);
        gradePercent.setStyle("-fx-text-fill:#989898;-fx-font-weight: bold;");
        gradePercent.setAlignment(Pos.CENTER);
    }


    private void deleteCurrentConfig(){

    }


    private void loadGradeConfigs(){

        File configsDir = new File("C:\\Users\\Youssef Hossam\\eclipse-workspace\\Jowil Stats\\target\\classes\\GradeConfigs");
        File[] directoryListing = configsDir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                String configName=file.getName().substring(0,file.getName().indexOf(".jgc"));
                loadGradeConfigFile(file.getPath(),configName);
            }
            return;
        } else {
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Configurations Error",
                    "Error in loading Grade Boundaries Configurations");
            return;
        }
    }


    private void loadGradeConfigFile(String filePath,String configName){

        BufferedReader input;
        try {
             input= new BufferedReader(new FileReader(filePath));

            String line;
            ArrayList<gradeHBox> fileGrades=new ArrayList<>();
            gradesConfigCombo.getItems().add(configName);
            configs.add(fileGrades);
            int i=0;
            while ((line = input.readLine()) != null) {
                String[] row = line.split(",",-1);
                fileGrades.add(new gradeHBox(i,row[0],row[1],this));
                i++;
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Configuration loading Error",
                    "Error in loading \""+configName+"\" grade configuration.");
        }

    }

    private void updateGradesVBox(){
        gradesVBox.getChildren().clear();
        gradesVBox.getChildren().add(gradesLabelsHBox);
        gradesVBox.getChildren().addAll(gradesHBoxes);
        updateSizes();
    }



}
