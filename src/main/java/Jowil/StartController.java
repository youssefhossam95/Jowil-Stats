package Jowil;

import Jowil.Reports.Report;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pdfsam.ui.RingProgressIndicator;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;


public class StartController extends Controller{


    @FXML
    AnchorPane lowerAncPane;

    @FXML
    ImageView logoImageView;

    @FXML
    Label jowilLabel;

    @FXML
    StackPane openStack;

    @FXML
    Rectangle openRect;

    @FXML
    Label openLabel;

    @FXML
    ImageView openImageView;


    @FXML
    StackPane newStack;

    @FXML
    Rectangle newRect;

    @FXML
    Label newLabel;

    @FXML
    ImageView newImageView;

    final static double scalingFactor=1.03;

    ArrayList<String> projectsNames;


    @FXML
    StackPane closeButton;

    @FXML
    StackPane minusButton;

    @FXML
    ImageView backImageView;

    long lastClick;

    ObservableList<String> existingProjectsListItems;


    StartController() {
        super("Start.fxml", "Jowil Stats", 1.25,1.25 , true, null,false,true);
    }



    @Override
    protected void initComponents() {
        Font jowilLabelFont=new Font("System Bold",resX*0.059);
        jowilLabel.setFont(jowilLabelFont);
        Font buttonsFont=new Font("System Bold",resX*0.011);
        openLabel.setFont(buttonsFont);
        newLabel.setFont(buttonsFont);
        //lowerAncPane.setStyle("-fx-background-color:transparent;-fx-border-width:1 0 0 0;-fx-border-color:#626365"); //anchor pane not white

        newStack.setOnMouseEntered(event -> {

            newRect.setScaleX(scalingFactor);
            newRect.setScaleY(scalingFactor);
            newImageView.setScaleX(scalingFactor);
            newImageView.setScaleY(scalingFactor);
            Font scaledFont=new Font("System Bold",buttonsFont.getSize()*scalingFactor);
            newLabel.setFont(scaledFont);
        });
        newStack.setOnMouseExited(event -> {
            newRect.setScaleX(1);
            newRect.setScaleY(1);
            newImageView.setScaleX(1);
            newImageView.setScaleY(1);
            newLabel.setFont(buttonsFont);
        });

//        newStack.setOnMousePressed(event -> {
//            newRect.setScaleX(1);
//            newRect.setScaleY(1);
//            newImageView.setScaleX(1);
//            newImageView.setScaleY(1);
//            newLabel.setFont(buttonsFont);
//        });


        openStack.setOnMouseEntered(event -> {

            openRect.setScaleX(scalingFactor);
            openRect.setScaleY(scalingFactor);
            openImageView.setScaleX(scalingFactor);
            openImageView.setScaleY(scalingFactor);
            Font scaledFont=new Font("System Bold",buttonsFont.getSize()*scalingFactor);
            openLabel.setFont(scaledFont);
        });
        openStack.setOnMouseExited(event -> {
            openRect.setScaleX(1);
            openRect.setScaleY(1);
            openImageView.setScaleX(1);
            openImageView.setScaleY(1);
            openLabel.setFont(buttonsFont);
        });


        newStack.setOnMouseClicked(event -> showNewProjectNameDialog(""));
        openStack.setOnMouseClicked(event -> showExistingProjects());

        closeButton.setOnMouseClicked(event -> Platform.exit());
        minusButton.setOnMouseClicked(event -> stage.setIconified(true));

    }

    @Override
    public void startWindow(){
        super.startWindow();
        loadProjectsJson();
        stage.setOnCloseRequest(event -> {

        });
    }
    @Override
    protected void saveChanges() {

    }

    @Override
    protected Controller getNextController() { //not used here
        return null;
    }

    @Override
    protected void buildComponentsGraph(){

    }

    @Override
    protected void updateSizes(){
        Insets buttonLabelsMargin=new Insets(0.2*resY,0,0,0);
        Insets buttonImagesMargin=new Insets(0,0,0.04*resY,0);

        super.updateSizes();
        double upperHeight=(int)(rootHeight*0.245);
        double lowerHeight=1-upperHeight;


//        upperImageView.setFitWidth(rootWidth);
//        upperImageView.setFitHeight(upperHeight);

        lowerAncPane.setPrefWidth(rootWidth);
        backImageView.setFitWidth(rootWidth);
        backImageView.setFitHeight(rootHeight);
        //lowerAncPane.setPrefHeight(lowerHeight);

        jowilLabel.setLayoutX(0.353*rootWidth);
        jowilLabel.setLayoutY(0.12*rootHeight);


        double vSpacing=rootHeight*0.15;

        logoImageView.setFitWidth(0.08*resX);
        logoImageView.setFitHeight(0.11*resY);
        logoImageView.setLayoutX(0.474*rootWidth);
        //logoImageView.setLayoutY(jowilLabel.getLayoutY()+rootHeight*0.09);
        logoImageView.setLayoutY(jowilLabel.getLayoutY()+vSpacing);


        openStack.setLayoutX(0.25*rootWidth);
        openStack.setLayoutY(logoImageView.getLayoutY()+vSpacing);

        openRect.setWidth(0.15*resX); // kant 0.173
        openRect.setHeight(0.34*resY); //kant 0.4
        openRect.setArcWidth(resX*0.01);
        openRect.setArcHeight(resY*0.02);

        openImageView.setFitWidth(0.055*resX);
        openImageView.setFitHeight(0.11*resY);

        StackPane.setMargin(openLabel,buttonLabelsMargin);
        StackPane.setMargin(openImageView,buttonImagesMargin);


        newStack.setLayoutX(0.595*rootWidth);
        newStack.setLayoutY(openStack.getLayoutY());

        newRect.setWidth(openRect.getWidth());
        newRect.setHeight(openRect.getHeight());
        newRect.setArcWidth(openRect.getArcWidth());
        newRect.setArcHeight(openRect.getArcHeight());

        newImageView.setFitWidth(openImageView.getFitWidth());
        newImageView.setFitHeight(openImageView.getFitHeight());

        StackPane.setMargin(newLabel,buttonLabelsMargin);
        StackPane.setMargin(newImageView,buttonImagesMargin);


    }



    private void showNewProjectNameDialog(String initialText){


        TextInputDialog dialog = new TextInputDialog(initialText.isEmpty()?"New Project":initialText);
        dialog.setTitle("Set Project Name");
        dialog.setHeaderText(null);
        dialog.setContentText("Project Name:");
        ImageView pic=new ImageView();
        String file="";
        System.out.println(file);
        pic.setImage(new Image("Images/Add Folder_96px.png"));
        dialog.setGraphic(pic);
        pic.setFitWidth(30);
        pic.setFitHeight(30);


// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String projName=result.get();
            if(projName.trim().isEmpty()){
                showAlertAndWait(Alert.AlertType.ERROR,stage.getOwner(),"Project Name Error","Project name cannot be empty.");
                showNewProjectNameDialog(projName);
            }
            else if(isProjectExists(projName)) {
                showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Project Name Error", "" +
                        "A project with the name \"" + projName + "\" already exists.");
                showNewProjectNameDialog(projName);
            }

            else if(isProjectNameInvalid(projName)){
                showAlertAndWait(Alert.AlertType.ERROR,stage.getOwner(),"Project Name Error","Invalid project name. Project Name cannot" +
                        " contain any of the following characters: "+"< > : \" / \\ | ? *");
                showNewProjectNameDialog(projName);
            }
            else {
                Controller.projectName=projName;
                Controller.isOpenMode=false;
                Controller.currentOpenedProjectJson=null;
                new FileConfigController().startWindow();
            }

        }

    }

    private boolean isProjectExists(String s) {
        loadProjectNames();
        return projectsNames.contains(s);
    }

    private boolean isProjectNameInvalid(String name){
        Pattern pattern = Pattern.compile("[<>:\"/\\\\|?*]");
        return pattern.matcher(name).find();
    }

    private void loadProjectsJson(){

        savedProjectsJson=loadJsonObj(SAVED_PROJECTS_FILE_NAME);
        if(savedProjectsJson==null){
            if(showJsonError())
                new StartController().startWindow();

            stage.close();
            return;
        }
    }

    private boolean showJsonError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());

        alert.setTitle("Projects Loading Error");
        alert.getButtonTypes().add(ButtonType.CANCEL);
        alert.setHeaderText(null);
        alert.setContentText("Cannot load existing projects. Would you like to restart Jowil Stats?");
        Optional<ButtonType> result=alert.showAndWait();

        return result.isPresent() && result.get()==ButtonType.OK;
    }

    private void  showExistingProjects(){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Open Existing Project");
        dialog.setHeaderText("Projects");
        dialog.setGraphic(null);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/FXML/application.css").toExternalForm());
        dialog.getDialogPane().setMinWidth(resX*300/1280);

        dialog.getDialogPane().getStyleClass().add("projectsDialog");
        //dialog.getDialogPane().setStyle("-fx-background-color:transparent");
        dialog.setResizable(true);
        //dialog.getDialogPane().setMinSize(resX*0.5,resY*0.5);
        dialog.setWidth(resX*0.6);
        dialog.setHeight(resY*0.8);


        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);

        Button butt=(Button)dialog.getDialogPane().lookupButton(ButtonType.CLOSE); //butt is needed to be able to close dialog using x
        butt.setVisible(false);

        JFXListView projectsList=new JFXListView();



        existingProjectsListItems=FXCollections.observableArrayList();
        loadProjectNames();


        projectsList.setCellFactory(event->OpenProjectCell.createOpenProjectCell(dialog,this));



        for(String name:projectsNames){

            existingProjectsListItems.add(name);
            System.out.println(name);
        }


        Label emptyLabel=new Label("No Projects Exist");
        emptyLabel.setFont(new Font("System Bold",15));
        projectsList.setPlaceholder(emptyLabel);



        if(existingProjectsListItems.isEmpty())
            projectsList.setPrefHeight(0); //prevents enlargement of listView

        projectsList.setPrefWidth(dialog.getWidth());


        dialog.getDialogPane().setPadding(new Insets(20,20,0,20));
        projectsList.setItems(existingProjectsListItems);

        dialog.getDialogPane().setContent(projectsList);
        dialog.showAndWait();

    }


    /*
    used to refresh project names 3shn if it was changed anywhere in the app.
     */
    void loadProjectNames(){
        JSONArray projectsArr=(JSONArray)savedProjectsJson.get("projects");
        projectsNames=new ArrayList<>();

        for(int i=0;i<projectsArr.size();i++){
            JSONObject project=(JSONObject)projectsArr.get(i);
            projectsNames.add((String)project.get("name"));
        }
    }

    public void startOpenMode(String projName,Dialog dialog) {

        Controller.projectName = projName;
        Controller.isOpenMode=true;
        ManualModeController.isIgnoreSavedObjectiveWeights=false;

        int projIndex=projectsNames.indexOf(projName);
        JSONArray projects=(JSONArray)savedProjectsJson.get("projects");
        currentOpenedProjectJson= (JSONObject)projects.get(projIndex);

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("proj: " + Controller.projectName);
        dialog.close();
        new FileConfigController().startWindow();
    }


    public void deleteProject(String projName) {


        if(!showConfirmationDialog("Delete Project", "Are you sure you want to delete project \""+projName+"\" ?",stage.getOwner()))
            return;
        JSONArray projects=(JSONArray)savedProjectsJson.get("projects");
        for(int i=0;i<projects.size();i++){
            if(((JSONObject)projects.get(i)).get(PROJECT_NAME_JSON_KEY).equals(projName)){
                projects.remove(i);
                break;
            }
        }
        existingProjectsListItems.remove(projName);
        projectsNames.remove(projName);
        saveJsonObj(SAVED_PROJECTS_FILE_NAME,savedProjectsJson);



    }

    public void openProjectInExplorer(String projName) {

        int projIndex=projectsNames.indexOf(projName);
        JSONArray projects=(JSONArray)savedProjectsJson.get("projects");
        JSONObject proj=(JSONObject)projects.get(projIndex);

        String dirPath=(String)proj.get(Controller.REPORTS_OUT_PATH_JSON_KEY)+"\\"+projName;


        File file = new File (dirPath);
        Desktop desktop = Desktop.getDesktop();


        try {
            desktop.open(file);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }


    }
}
