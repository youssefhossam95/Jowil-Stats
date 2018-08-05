package Jowil;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Optional;


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
    HBox reportsConfigHBox;

    @FXML
    VBox reportsVBox;

    @FXML
    VBox formatsVBox;

    @FXML
    Node trashIcon;




    HBox gradesLabelsHBox=new HBox();

    Label gradeName=new Label("Name");
    Label gradePercent=new Label("Score %");
    Label gradeRaw=new Label("Score");
    Label reportsLabel=new Label("Reports");
    Label formatsLabel=new Label("File Formats");





    private final static int DEFAULT_GRADE_CONFIGS_COUNT=3;
    int gradesConfigComboSelectedIndex;
    private  ArrayList<GradeHBox> gradesHBoxes;
    private  final static String standardLettersGradingFile="Standard Letters Scale.jgc",
            allLettersGradingFile="All Letters Scale.jgc",egyptianGradingFile1="Egyptian Scale 1.jgc"
            ,egyptianGradingFile2="Egyptian Scale 2.jgc",labelsColor="black";

    JSONObject prefsJsonObj;
    JSONObject gradeScalesJsonObj;

    ArrayList<ArrayList<GradeHBox>> configs=new ArrayList<>();


    ArrayList<CheckBox> reportsCheckBoxes=new ArrayList<>();
    ArrayList<CheckBox> formatsCheckBoxes=new ArrayList<>();


    ObservableList<String> comboItems=FXCollections.observableArrayList();





    @Override
    protected void initComponents() {


        initTrashIcon();
        initScrollPane();
        initGradesLabelsHBox();
        initGradesConfigCombo();
        initTitles();
        initGradesVBox();
        initDeleteConfigButton();
        initReportsDirChooser();
        initReportsConfigHBox();
        initReportsVBox();
        initFormatsVbox();

        
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
        gradesLabelsHBox.setPadding(new Insets(scrollPaneHeight*0.05,0,0,0));
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



        reportsConfigHBox.setLayoutX(reportsConfigTitle.getLayoutX());
        reportsConfigHBox.setLayoutY(scrollPane.getLayoutY());
        reportsConfigHBox.setPrefWidth(reportsDirHBox.getPrefWidth());
        reportsConfigHBox.setPrefHeight(scrollPane.getPrefHeight()*0.8);
        reportsConfigHBox.setSpacing(resXToPixels(0.04));

        reportsLabel.setFont(gradesLabelsFonts);
        reportsLabel.setPadding(new Insets(reportsConfigHBox.getPrefHeight()*0.05,0,reportsConfigHBox.getPrefHeight()*0.05,0));
        formatsLabel.setFont(gradesLabelsFonts);
        formatsLabel.setPadding(new Insets(reportsConfigHBox.getPrefHeight()*0.05,0,reportsConfigHBox.getPrefHeight()*0.05,0));
        reportsVBox.setSpacing(resYToPixels(0.02));
        reportsVBox.setPadding(new Insets(0,0,0,reportsConfigHBox.getPrefWidth()*0.02));
        formatsVBox.setSpacing(resYToPixels(0.02));


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
        updateGradesVBox();
    }



    public void deleteGrade(int callingIndex){

        if(gradesHBoxes.size()==1) //never delete last hbox
            return;

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
        gradesConfigCombo.setItems(comboItems);


        gradesConfigCombo.setVisibleRowCount(3);
        gradesConfigCombo.setOnShown(t->gradesConfigCombo.getSelectionModel().clearSelection());
        gradesConfigCombo.setOnHidden(t->{gradesConfigCombo.getSelectionModel().select(gradesConfigComboSelectedIndex); System.out.println("easy"+gradesConfigComboSelectedIndex);});
        gradesConfigCombo.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)-> {

            if((Integer)newValue==gradesConfigComboSelectedIndex || (Integer)newValue==-1 )
                return;

            gradesConfigComboSelectedIndex=(Integer)newValue;
            initGradesVBox();
            isContentEdited=false;

            if(gradesConfigComboSelectedIndex<DEFAULT_GRADE_CONFIGS_COUNT)
                trashIcon.setOpacity(0.3);
            else
                trashIcon.setOpacity(1);


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
        cloneGradesHBoxes(gradesConfigComboSelectedIndex);
        updateGradesVBox();

    }

    private void initGradesLabelsHBox(){


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


                boolean isJsonSuccess=loadPrefsJsonObj();
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle("Choose Reports Output Directory");
                String lastDir=(String)prefsJsonObj.get("reportsOutputDir");

                if(isJsonSuccess) {
                    lastDir = lastDir.isEmpty() ? System.getProperty("user.home") : lastDir;
                    dirChooser.setInitialDirectory(new File((lastDir)));
                }

                File newDir =dirChooser.showDialog(stage);

                if(isJsonSuccess) {
                    reportsDirTextField.setText(newDir.getPath());
                    reportsDirTextField.requestFocus();
                    reportsDirTextField.deselect();
                    if(isJsonSuccess && !newDir.getPath().equals(lastDir)){
                        prefsJsonObj.put("reportsOutputDir",newDir.getPath());
                        savePrefsJsonObj();

                    }
                }
            }
        });
    }


    private void initReportsConfigHBox(){
        reportsConfigHBox.setStyle("-fx-border-color: #A9A9A9;");
    }

    private void initReportsVBox(){

        reportsLabel.setStyle("-fx-text-fill:"+labelsColor+";-fx-font-weight: bold;");
        reportsVBox.getChildren().add(reportsLabel);

        //add checkboxes
        reportsCheckBoxes.add(new JFXCheckBox("Report 1: Grades Distribution Report"));
        reportsCheckBoxes.add(new JFXCheckBox("Report 2: Condensed Test Report"));
        reportsCheckBoxes.add(new JFXCheckBox("Report 3: Test Statistics Report"));
        reportsCheckBoxes.add(new JFXCheckBox("Report 4: Students Grades Report"));
        reportsCheckBoxes.add(new JFXCheckBox("Report 5: Questions Statistics Report"));

        //load json array
        boolean isJsonSuccess=loadPrefsJsonObj();

        JSONArray reportsChosen=(JSONArray)prefsJsonObj.get("reportsChosen");

        //initialize checkboxes
        for(int i=0;i<reportsCheckBoxes.size();i++) {
            Boolean value=true;
            if(isJsonSuccess)
                value=(Boolean) reportsChosen.get(i);
            reportsCheckBoxes.get(i).setSelected(value);
            reportsCheckBoxes.get(i).getStyleClass().add("smallCheckBox");
        }

        reportsVBox.getChildren().addAll(reportsCheckBoxes);
    }


    private void initFormatsVbox(){

        formatsLabel.setStyle("-fx-text-fill:"+labelsColor+";-fx-font-weight: bold;");
        formatsVBox.getChildren().add(formatsLabel);

        formatsCheckBoxes.add(new JFXCheckBox("PDF"));
        formatsCheckBoxes.add(new JFXCheckBox("HTML"));
        formatsCheckBoxes.add(new JFXCheckBox("TXT"));

        //load json array
        boolean isJsonSuccess=loadPrefsJsonObj();
        JSONArray formatsChosen=(JSONArray)prefsJsonObj.get("formatsChosen");

        //initialize checkboxes
        for(int i=0;i<formatsCheckBoxes.size();i++) {
            Boolean value=true;
            if(isJsonSuccess)
                value=(Boolean) formatsChosen.get(i);
            formatsCheckBoxes.get(i).setSelected(value);
            formatsCheckBoxes.get(i).getStyleClass().add("smallCheckBox");
        }

        formatsVBox.getChildren().addAll(formatsCheckBoxes);

    }

    private void initTrashIcon(){
        trashIcon.setOpacity(0.25);
        trashIcon.setOnMouseEntered(t->{
                if(gradesConfigComboSelectedIndex<DEFAULT_GRADE_CONFIGS_COUNT)
                    return;
                trashIcon.setStyle("-fx-fill:#87CEEB");
        });

        trashIcon.setOnMouseExited(t->trashIcon.setStyle("-fx-fill:#3184c9"));
    }

    private JSONObject loadJsonObj(String name){

        String file= "";
        JSONObject jsonObj=null;
        try {
            file = URLDecoder.decode(getClass().getResource("/"+name).getFile(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            jsonObj= (JSONObject)new JSONParser().parse(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonObj;
        
    }
    
    private void saveJsonObj(String name,JSONObject jsonObj){

        PrintWriter pw = null;
        String file= "";
        try {
            file = URLDecoder.decode(getClass().getResource("/"+name).getFile(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pw.write(jsonObj.toJSONString());
        pw.flush();
        pw.close();
    }

    
    private boolean loadPrefsJsonObj(){

        return (prefsJsonObj=loadJsonObj("UserPrefs.json"))!=null;
    }

    
    private void savePrefsJsonObj(){
        saveJsonObj("UserPrefs.json",prefsJsonObj);
    }

    private void deleteCurrentConfig(){

        if(gradeScalesJsonObj==null || gradesConfigComboSelectedIndex<DEFAULT_GRADE_CONFIGS_COUNT)
            return;

        if(showGradeScaleDeleteConfirmation()) {
            JSONArray scales = (JSONArray) gradeScalesJsonObj.get("scales");
            scales.remove(gradesConfigComboSelectedIndex);
            gradeScalesJsonObj.put("scales", scales);
        }
    }


    private void loadGradeConfigs(){


        if((gradeScalesJsonObj=loadJsonObj("GradeScales.json"))==null){
            showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Grade Configurations Error",
                    "Error in loading Grade Scale Configurations");
            return;
        }

        JSONArray scales=(JSONArray)gradeScalesJsonObj.get("scales");


        for(int i=0;i<scales.size();i++){
            ArrayList<GradeHBox> vBoxGrades=new ArrayList<>();

            JSONObject scale=(JSONObject)scales.get(i);
            comboItems.add((String)scale.keySet().iterator().next());
            JSONArray grades=(JSONArray)scale.values().iterator().next();

            for(int j=0;j<grades.size();j++){

                JSONObject grade=(JSONObject)grades.get(j);
                vBoxGrades.add(new GradeHBox(j,(String)grade.keySet().iterator().next(),(String)grade.values().iterator().next(),this));

            }

            configs.add(vBoxGrades);

        }


    }


    private void updateGradesVBox(){
        gradesVBox.getChildren().clear();
        gradesVBox.getChildren().add(gradesLabelsHBox);
        gradesVBox.getChildren().addAll(gradesHBoxes);
        updateSizes();
    }

    private void cloneGradesHBoxes(int index){

        gradesHBoxes=new ArrayList<>();
        ArrayList<GradeHBox> currentConfig=configs.get(index);

        for(GradeHBox hbox : currentConfig)
            gradesHBoxes.add(new GradeHBox(hbox));


    }


    private boolean showGradeScaleDeleteConfirmation() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete File");
        alert.setHeaderText("Are you sure want to delete this Grade Scale Configuration?");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/FXML/application.css").toExternalForm());
        Optional<ButtonType> option = alert.showAndWait();

        return option.get() == ButtonType.OK;
    }


}
