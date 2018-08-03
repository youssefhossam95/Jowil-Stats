package Jowil;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URLDecoder;
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

    @FXML
    HBox reportsDirHBox;

    @FXML
    StackPane reportsDirChooser;

    @FXML
    JFXTextField reportsDirTextField;


    @FXML
    VBox reportsOuterVBox;

    @FXML
    Label reportsPaneLabel;


    @FXML
    Pane reportsPane;

    @FXML
    VBox reportsVBox;



    @FXML
    VBox formatsOuterVBox;

    @FXML
    Label formatsPaneLabel;

    @FXML
    Pane formatsPane;

    @FXML
    VBox formatsVBox;




    HBox gradesLabelsHBox=new HBox();

    Label gradeName=new Label("Name");
    Label gradePercent=new Label("Score %");
    Label gradeRaw=new Label("Score");





    int gradesConfigComboSelectedIndex;
    int gradesCreatedIndex=1;
    private  ArrayList<GradeHBox> gradesHBoxes;
    private  final String standardLettersGradingFile="Standard Letters Scale.jgc",
            allLettersGradingFile="All Letters Scale.jgc",egyptianGradingFile1="Egyptian Scale 1.jgc"
            ,egyptianGradingFile2="Egyptian Scale 2.jgc";


    ArrayList<ArrayList<GradeHBox>> configs=new ArrayList<>();




    @Override
    protected void initComponents() {
        
        initScrollPane();
        initGradesLabelsHBox();
        initGradesConfigCombo();
        initTitles();
        initGradesVBox();
        initDeleteConfigButton();
        initReportsDirChooser();
        initReportsPane();
        initFormatsPane();

        
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

        reportsDirHBox.setSpacing(resXToPixels(0.005));
        reportsDirHBox.setLayoutY(comboHBox.getLayoutY());
        reportsDirHBox.setLayoutX(reportsConfigTitle.getLayoutX());
        reportsDirHBox.setPrefWidth(rootWidthToPixels(0.95)-reportsDirHBox.getLayoutX());
        HBox.setHgrow(reportsDirTextField,Priority.ALWAYS);


        reportsPaneLabel.setPrefHeight(rootHeightToPixels(0.05));

        reportsOuterVBox.setLayoutX(reportsConfigTitle.getLayoutX());
        reportsOuterVBox.setLayoutY(scrollPane.getLayoutY()-reportsPaneLabel.getPrefHeight());
        reportsPane.setPrefWidth(reportsDirHBox.getPrefWidth()*0.4);
        reportsPane.setPrefHeight(scrollPane.getPrefHeight()*0.4);
//        reportsPane.setPrefWidth(reportsDirHBox.getPrefWidth()*0.5);
//        reportsPane.setPrefHeight(scrollPane.getPrefHeight()*0.7);



        formatsOuterVBox.setLayoutX(reportsConfigTitle.getLayoutX());
        formatsOuterVBox.setLayoutY(scrollPane.getLayoutY()+reportsPane.getPrefHeight()+rootHeightToPixels(0.05));
//        formatsOuterVBox.setLayoutX(reportsOuterVBox.getLayoutX()+reportsPane.getPrefWidth()+reportsDirHBox.getPrefWidth()*0.1);
//        formatsOuterVBox.setLayoutY(reportsOuterVBox.getLayoutY());
        formatsPane.setPrefWidth(reportsPane.getPrefWidth());
        formatsPane.setPrefHeight(reportsPane.getPrefHeight());



        for(GradeHBox hbox:gradesHBoxes)
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

        gradesHBoxes.add(newIndex,new GradeHBox(newIndex,"New Grade","50.0",this));
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
        scrollPane.focusedProperty().addListener((observable,oldValue,newValue)->{
            if(newValue)
                rootPane.requestFocus();
        });


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

        String labelsColor="black";
        gradesLabelsHBox.getChildren().addAll(gradeName,gradePercent,gradeRaw);
        gradeName.setStyle("-fx-text-fill:"+labelsColor+";-fx-font-weight: bold;");
        gradeName.setAlignment(Pos.CENTER);
        gradeRaw.setStyle("-fx-text-fill:"+labelsColor+";-fx-font-weight: bold;");
        gradeRaw.setAlignment(Pos.CENTER);
        gradePercent.setStyle("-fx-text-fill:"+labelsColor+";-fx-font-weight: bold;");
        gradePercent.setAlignment(Pos.CENTER);
    }

    private void initReportsDirChooser(){

        Tooltip tooltip = new Tooltip("Choose Output Directory");
        Tooltip.install(reportsDirChooser, tooltip);

        reportsDirChooser.setOnMouseClicked(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {

                JSONObject obj=new JSONObject();
                String userPrefsFile= "";
                try {
                    userPrefsFile = URLDecoder.decode(getClass().getResource("/UserPrefs.json").getFile(),"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    obj= (JSONObject)new JSONParser().parse(new FileReader(userPrefsFile));
                } catch (IOException e) {

                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle("Choose Reports Output Directory");
                String lastDir=(String)obj.get("reportsOutputDir");

                if(lastDir!=null) { //not coming from catch
                    lastDir = lastDir.isEmpty() ? System.getProperty("user.home") : lastDir;
                    dirChooser.setInitialDirectory(new File((lastDir)));
                }

                File newDir =dirChooser.showDialog(stage);

                if(newDir!=null) {
                    reportsDirTextField.setText(newDir.getPath());
                    reportsDirTextField.requestFocus();
                    reportsDirTextField.deselect();
                    if(lastDir!=null && !newDir.getPath().equals(lastDir)){
                        obj.put("reportsOutputDir",newDir.getPath());
                        PrintWriter pw = null;

                        try {
                            pw = new PrintWriter(userPrefsFile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        
                        pw.write(obj.toJSONString());
                        pw.flush();
                        pw.close();
                    }
                }
            }
        });
    }


    private void initReportsPane() {
        //reportsPane.getStyleClass().add("scroll-pane");
        reportsPane.setStyle("-fx-border-color:  #A9A9A9;");
    }

    private void initFormatsPane() {
        formatsPane.setStyle("-fx-border-color:  #A9A9A9;");
    }


    private void deleteCurrentConfig(){

    }


    private void loadGradeConfigs(){

        File configsDir = null;
        try {
            configsDir = new File(URLDecoder.decode(getClass().getResource("/GradeConfigs").getFile(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Configurations Error",
                    "Error in loading Grade Boundaries Configurations: Unsupported Path Encoding");
            return;
        }


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
            ArrayList<GradeHBox> fileGrades=new ArrayList<>();
            gradesConfigCombo.getItems().add(configName);
            configs.add(fileGrades);
            int i=0;
            while ((line = input.readLine()) != null) {
                String[] row = line.split(",",-1);
                fileGrades.add(new GradeHBox(i,row[0],row[1],this));
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
