package Jowil;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;


public class StartController extends Controller{


    @FXML
    ImageView upperImageView;

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

    JSONObject projectsJson;



    StartController() {
        super("Start.fxml", "Jowil Stats", 1,1 , true, null);
    }

    @Override
    protected void initComponents() {
        Font jowilLabelFont=new Font("System Bold",resX*0.04);
        jowilLabel.setFont(jowilLabelFont);
        Font buttonsFont=new Font("System Bold",resX*0.011);
        openLabel.setFont(buttonsFont);
        newLabel.setFont(buttonsFont);
        lowerAncPane.setStyle("-fx-background-color:transparent;-fx-border-width:1 0 0 0;-fx-border-color:#626365"); //anchor pane not white

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


        upperImageView.setFitWidth(rootWidth);
        upperImageView.setFitHeight(upperHeight);

        lowerAncPane.setPrefWidth(rootWidth);
        //lowerAncPane.setPrefHeight(lowerHeight);

        logoImageView.setFitWidth(0.08*resX);
        logoImageView.setFitHeight(0.11*resY);
        logoImageView.setLayoutX(0.47*rootWidth);
        logoImageView.setLayoutY(0.12*rootHeight);



        jowilLabel.setLayoutX(0.4*rootWidth);
        jowilLabel.setLayoutY(0.03*rootHeight);


        openStack.setLayoutX(0.24*rootWidth);
        openStack.setLayoutY(0.24*rootHeight);

        openRect.setWidth(0.15*resX); // kant 0.173
        openRect.setHeight(0.34*resY); //kant 0.4
        openRect.setArcWidth(resX*0.01);
        openRect.setArcHeight(resY*0.02);

        openImageView.setFitWidth(0.055*resX);
        openImageView.setFitHeight(0.11*resY);

        StackPane.setMargin(openLabel,buttonLabelsMargin);
        StackPane.setMargin(openImageView,buttonImagesMargin);


        newStack.setLayoutX(0.575*rootWidth);
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
            if(result.get().trim().isEmpty()){
                showAlertAndWait(Alert.AlertType.ERROR,stage.getOwner(),"Project Name Error","Project name cannot be empty.");
                showNewProjectNameDialog(result.get());
            }
            else if(isProjectExists(result.get())) {
                showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Project Name Error", "" +
                        "A project with the name \"" + result.get() + "\" already exists.");
                showNewProjectNameDialog(result.get());
            }

            else if(isProjectNameInvalid(result.get())){
                showAlertAndWait(Alert.AlertType.ERROR,stage.getOwner(),"Project Name Error","Invalid project name. Project Name cannot" +
                        " contain any of the following characters: "+"< > : \" / \\ | ? *");
                showNewProjectNameDialog(result.get());
            }
            else
                new FileConfigController().startWindow();

        }

    }

    private boolean isProjectExists(String s) {
        return projectsNames.contains(s);
    }

    private boolean isProjectNameInvalid(String name){
        Pattern pattern = Pattern.compile("[<>:\"/\\\\|?*]");
        return pattern.matcher(name).find();
    }

    private void loadProjectsJson(){

        projectsJson=loadJsonObj("projects.json");
        if(projectsJson==null){
            if(showJsonError())
                new StartController().startWindow();

            stage.close();
            return;
        }
        JSONArray projectsArr=(JSONArray)projectsJson.get("projects");
        projectsNames=new ArrayList<>();

        for(int i=0;i<projectsArr.size();i++){
            JSONObject project=(JSONObject)projectsArr.get(i);
            projectsNames.add((String)project.get("name"));
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
        ObservableList<HBox> listItems=FXCollections.observableArrayList();

        for(String name:projectsNames){
            HBox hbox=new HBox(5);
            ImageView imageView=new ImageView();
            imageView.setImage(new Image("Images/Folder_96px.png"));
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);
            Label label =new Label(name);
            label.setFont(new Font(15));
            label.setAlignment(Pos.CENTER);
            hbox.getChildren().addAll(imageView,label);
            listItems.add(hbox);
            System.out.println(name);
        }


        Label emptyLabel=new Label("No Projects Exist");
        emptyLabel.setFont(new Font("System Bold",15));
        projectsList.setPlaceholder(emptyLabel);



        if(listItems.isEmpty())
            projectsList.setPrefHeight(0); //prevents enlargement of listView

        projectsList.setPrefWidth(dialog.getWidth());


        dialog.getDialogPane().setPadding(new Insets(20,20,0,20));
        projectsList.setItems(listItems);


        dialog.getDialogPane().setContent(projectsList);
        dialog.showAndWait();

    }




}
