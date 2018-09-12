package Jowil;

import Jowil.Reports.*;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.io.*;
import java.util.*;

import static Jowil.CSVHandler.NOT_AVAILABLE;
import static Jowil.CSVHandler.readPartialCSVFile;

/*Note: when storing in json files all integer variables are stored as strings to avoid a bug in jsonSimple library that
causes an inconsistent behaviour while dealing with int variables
*/

public class GradeBoundariesController extends Controller {


    GradeBoundariesController(Controller back) {
        super("gradeBoundaries.fxml", "Grading Scale and Report Generation", 1.25, 1.25, true, back);


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


    HBox gradesLabelsHBox = new HBox();

    Label gradeName = new Label("Name");
    Label gradePercent = new Label("Score %");
    Label gradeRaw = new Label("Score");
    Label reportsLabel = new Label("Reports");
    Label formatsLabel = new Label("File Formats");


    private final static int DEFAULT_GRADE_CONFIGS_COUNT = 4;
    private final static String REPORTS_PREFS_FILE_NAME = "ReportsPrefs.json", GRADE_SCALE_FILE_NAME = "GradeScales.json";
    int gradesConfigComboSelectedIndex;
    private ArrayList<GradeHBox> gradesHBoxes;
    private final static String labelsColor = "black";
    private int reportsCount;


    Font gradesLabelsFonts = new Font("Arial", resX / 100);




    private static Report[] reports;


    JSONObject prefsJsonObj;
    JSONObject gradeScalesJsonObj;

    ArrayList<ArrayList<GradeHBox>> configs = new ArrayList<>();


    ArrayList<CheckBox> reportsCheckBoxes = new ArrayList<>();
    ArrayList<CheckBox> formatsCheckBoxes = new ArrayList<>();


    ObservableList<String> comboItems = FXCollections.observableArrayList();


    @Override
    protected void initComponents() {


        initTrashIcon();
        initScrollPane();
        initGradesLabelsHBox();
        initGradesConfigCombo();
        initTitles();
        initGradesVBox();
        initDeleteConfigButton();
        initReportsDirTextField();;
        initReportsDirChooser();
        initReportsConfigHBox();
        initReportsVBox();
        initFormatsVbox();
        initFinishButton();

    }


    @Override
    protected void updateSizes() {

        super.updateSizes();

        double scrollPaneWidth = rootWidthToPixels(0.43);
        double scrollPaneHeight = rootHeightToPixels(0.56);

        //left half
        scrollPane.setLayoutY((int)(rootHeightToPixels(0.25)));
        scrollPane.setLayoutX((int)(rootWidthToPixels(0.05)));
        scrollPane.setPrefWidth((int)(scrollPaneWidth));
        scrollPane.setPrefHeight((int)(scrollPaneHeight));
        gradesConfigCombo.setPrefWidth(rootWidthToPixels(0.25));
        comboHBox.setLayoutX(scrollPane.getLayoutX());
        comboHBox.setLayoutY(rootHeightToPixels(0.15));
        //comboHBox.setSpacing(resXToPixels(0.005));
        comboHBox.setSpacing(scrollPane.getPrefWidth()-gradesConfigCombo.getPrefWidth()-deleteConfigButton.getWidth());

        gradeBoundariesTitle.setLayoutX(comboHBox.getLayoutX());
        gradeBoundariesTitle.setLayoutY(rootHeightToPixels(0.05));
        gradesVBox.setSpacing((int)(resYToPixels(0.025)));
        gradesLabelsHBox.setSpacing((int)(scrollPaneWidth * 0.03));
        gradesLabelsHBox.setPadding(new Insets((int)(scrollPaneHeight * 0.05), 0, 0, 0));
        gradesVBox.setPadding(new Insets(0, 0, 0, (int)(scrollPaneWidth * 0.02)));

        gradeName.setPrefWidth((int)(scrollPaneWidth * 0.15));
        gradeRaw.setPrefWidth((int)(scrollPaneWidth * 0.13));
        gradePercent.setPrefWidth((int)(scrollPaneWidth * 0.13));

        gradeName.setFont(gradesLabelsFonts);
        gradePercent.setFont(gradesLabelsFonts);
        gradeRaw.setFont(gradesLabelsFonts);


        //right half

        midSeparator.setLayoutX(rootWidthToPixels(0.5));
        midSeparator.setLayoutY(rootHeightToPixels(0.03));
        midSeparator.setPrefHeight(rootHeightToPixels(0.8));
        reportsConfigTitle.setLayoutX(midSeparator.getLayoutX() + rootWidthToPixels(0.03));
        reportsConfigTitle.setLayoutY(gradeBoundariesTitle.getLayoutY());

        reportsDirHBox.setSpacing(resXToPixels(0.005));
        reportsDirHBox.setLayoutY(comboHBox.getLayoutY());
        reportsDirHBox.setLayoutX(reportsConfigTitle.getLayoutX());
        reportsDirHBox.setPrefWidth(rootWidthToPixels(0.95) - reportsDirHBox.getLayoutX());
        HBox.setHgrow(reportsDirTextField, Priority.ALWAYS);


        reportsConfigHBox.setLayoutX(reportsConfigTitle.getLayoutX());
        reportsConfigHBox.setLayoutY(scrollPane.getLayoutY());
        reportsConfigHBox.setPrefWidth(reportsDirHBox.getPrefWidth());
        reportsConfigHBox.setPrefHeight(scrollPane.getPrefHeight() * 0.8);
        reportsConfigHBox.setSpacing(resXToPixels(0.04));

        reportsLabel.setFont(gradesLabelsFonts);
        reportsLabel.setPadding(new Insets(reportsConfigHBox.getPrefHeight() * 0.05, 0, reportsConfigHBox.getPrefHeight() * 0.05, 0));
        formatsLabel.setFont(gradesLabelsFonts);
        formatsLabel.setPadding(new Insets(reportsConfigHBox.getPrefHeight() * 0.05, 0, reportsConfigHBox.getPrefHeight() * 0.05, 0));
        reportsVBox.setSpacing(resYToPixels(0.02));
        reportsVBox.setPadding(new Insets(0, 0, 0, reportsConfigHBox.getPrefWidth() * 0.02));
        formatsVBox.setSpacing(resYToPixels(0.02));


        for (GradeHBox hbox : gradesHBoxes)
            hbox.updateSizes(scrollPaneWidth, scrollPaneHeight);


    }


    @Override
    protected void saveChanges() {
    }






    @Override
    protected void goToNextWindow() {



        if (reportsDirTextField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Output Directory Error",
                    "Reports Directory Path is required.");
            return;
        }


        File outDir = new File(reportsDirTextField.getText());


        if (!outDir.exists()) {
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Output Directory Error",
                    "Reports Directory \"" + reportsDirTextField.getText() + "\" doesn't exist.");
            return;
        }


        if (!outDir.isDirectory()) {
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Output Directory Error",
                    "Path \"" + reportsDirTextField.getText() + "\" is not a valid directory path.");
            return;
        }


        if(!isCheckBoxesValid()){
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CheckBoxes Selection Error",
                    "You must select at least one report and one output format.");
            return;
        }

        String projectDirName=getProjectDirName();
        String outPath=outDir.getAbsolutePath()+"\\";

        if(!createOutDirs(outPath,projectDirName)){
            showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Output Directory Error",
                    "Cannot write to the specified reports directory. Make sure that you are permitted" +
                            " to edit in this directory");
            return;

        }



        ArrayList<Pair<String, Double>> scale = new ArrayList<>();
        ArrayList<String> gradeNames = new ArrayList<>();
        ArrayList<Double> gradeMins = new ArrayList<>();

        int counter = 1;
        for (GradeHBox hbox : gradesHBoxes) {
            Pair<String, Double> grade = hbox.getGrade();
            if (grade.getKey().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Grade Scale Error",
                        "Error in Grade number " + counter + ": Grade name cannot be empty.");
                return;
            }
            scale.add(grade);
            counter++;
        }

        ArrayList<Pair<String,Double>> origScale=new ArrayList<>();
        for(Pair<String,Double> pair:scale)
            origScale.add(pair);

        Collections.sort(scale,new PairSorter());

        if(scale.get(0).getValue()!=0)
            scale.add(0,new Pair<String,Double>("F",0.0));


        for(Pair<String,Double> grade:scale){
            gradeNames.add(grade.getKey());
            gradeMins.add(grade.getValue() / 100);
        }


        gradeMins.add(1.0);

        Statistics.setGrades(gradeNames);
        Statistics.setGradesLowerRange(gradeMins);

        String selectedIndex;

        gradeScalesJsonObj.put("lastSelectedScaleIndex",selectedIndex=Integer.toString(gradesConfigComboSelectedIndex));
        //save new changes if user wants to

        if (isContentEdited) {
            String result = showSaveChangesDialog();
            if (result != null) {
                saveNewConfig(result,origScale);
                gradeScalesJsonObj.put("lastSelectedScaleIndex",selectedIndex=Integer.toString(comboItems.size()));
            }
        }


        saveJsonObj(GRADE_SCALE_FILE_NAME, gradeScalesJsonObj);

        
        //generate Reports

        ArrayList<Report> reportsOut = new ArrayList<>();
        ArrayList<Integer> formatsOut = new ArrayList<>();
        JSONArray reportsConfig = new JSONArray();
        JSONArray formatsConfig = new JSONArray();

        Statistics.init();

        reports = new Report[]{new Report1(), new Report2(), new Report3(), new Report4(), new Report5()};

        //parse checkboxes and save their configs
        int i = 0;
        for (CheckBox checkBox : reportsCheckBoxes) {
            boolean isSelected = checkBox.isSelected();
            if (isSelected) {
                reportsOut.add(reports[i]);
                reportsCount++;
            }
            reportsConfig.add(isSelected);
            i++;
        }


        i = 0;
        for (CheckBox checkBox : formatsCheckBoxes) {
            boolean isSelected = checkBox.isSelected();
            if (isSelected)
                formatsOut.add(i);
            formatsConfig.add(isSelected);
            i++;
        }

        
        prefsJsonObj.put(REPORTS_CHOSEN_JSON_KEY, reportsConfig);
        prefsJsonObj.put(FORMATS_CHOSEN_JSON_KEY, formatsConfig);
        savePrefsJsonObj();


        //save project to json

        JSONObject projObject=new JSONObject();
        if(isOpenMode){
            projObject=currentOpenedProjectJson;
        }
        else {
            ((JSONArray) savedProjectsJson.get("projects")).add(projObject);
            projObject.put(PROJECT_NAME_JSON_KEY, Controller.projectName);
        }




        projObject.put(Q_COL_START_INDEX_JSON_KEY,Integer.toString(CSVHandler.getQuestionsColStartIndex()));
        projObject.put(Q_COL_END_INDEX_JSON_KEY,Integer.toString(CSVHandler.getQuestionsColEndIndex()));

        projObject.put(SUBJ_COL_START_INDEX_JSON_KEY,Integer.toString(CSVHandler.getSubjStartIndex()));
        projObject.put(SUBJ_COL_END_INDEX_JSON_KEY,Integer.toString(CSVHandler.getSubjEndIndex()));
        projObject.put(SUBJ_Q_COUNT_JSON_KEY,Integer.toString(CSVHandler.getSubjQuestionsCount()));


        projObject.put(IS_MANUAL_MODE_JSON_KEY,ManualModeController.isIsManualModeUsedBefore());



        projObject.put(ID_COL_START_INDEX_JSON_KEY,Integer.toString(CSVHandler.getIdentifierColStartIndex()));
        projObject.put(ID_COL_END_INDEX_JSON_KEY,Integer.toString(CSVHandler.getIdentifierColEndIndex()));
        projObject.put(FORM_COL_INDEX_JSON_KEY,Integer.toString(CSVHandler.getFormColIndex()));
        projObject.put(FORMS_COUNT_JSON_KEY,Integer.toString(CSVHandler.getFormsCount()));
        projObject.put(IDENTIFIER_NAME_JSON_KEY,Controller.selectedIdentifierName);
        projObject.put(FORM_COL_NAME_JSON_KEY,Controller.selectedFormColName);



        projObject.put(IS_RESPONSES_CONTAINS_HEADERS_JSON_KEY,CSVHandler.isIsResponsesContainsHeaders());
        projObject.put(IS_ANSWER_KEY_CONTAINS_HEADERS_JSON_KEY,CSVHandler.isIsAnswerKeyContainsHeaders());
        projObject.put(RESPONSES_FILE_PATH_JSON_KEY,CSVHandler.getResponsesFilePath());
        projObject.put(ANSWERS_FILE_PATH_JSON_KEY,CSVHandler.getAnswerKeyFilePath());


        saveObjectiveGroups(projObject);
        saveQNames(projObject);
        saveQuestionsChoices(projObject);
        saveObjWeights(projObject);
        saveSubjWeights(projObject);
        saveSavedResponsesCSV(projObject);
        saveSavedAnswerKeyCSV(projObject);
        saveInfoHeaders(projObject);

        projObject.put(SELECTED_SCALE_JSON_KEY,selectedIndex);
        projObject.put(REPORTS_CHOSEN_JSON_KEY,reportsConfig);
        projObject.put(FORMATS_CHOSEN_JSON_KEY,formatsConfig);
        projObject.put(REPORTS_OUT_PATH_JSON_KEY, reportsDirTextField.getText());

        saveJsonObj(SAVED_PROJECTS_FILE_NAME,savedProjectsJson);

        Report.initOutputFolderPaths(outPath+projectDirName);
        showProgressDialog(reportsOut,formatsOut);


        stage.close();
    }



    @Override
    protected Controller getNextController() {
        return null;
    }

    @Override public void showWindow(){
        super.showWindow();

        for(GradeHBox hbox:gradesHBoxes) //
            hbox.refreshRawScore();
    }


    public void addNextGrade(int callingIndex) {
        int newIndex = callingIndex + 1;

        for (int i = callingIndex + 1; i < gradesHBoxes.size(); i++)
            gradesHBoxes.get(i).incrementIndex();

        gradesHBoxes.add(newIndex, new GradeHBox(newIndex, "New Grade", "50.0", this));
        updateGradesVBox();
    }


    public void deleteGrade(int callingIndex) {

        if (gradesHBoxes.size() == 1) //never delete last hbox
            return;

        for (int i = callingIndex + 1; i < gradesHBoxes.size(); i++)
            gradesHBoxes.get(i).decrementIndex();

        gradesHBoxes.remove(callingIndex);

        updateGradesVBox();
    }


    //////helper methods


    private void initScrollPane() {
        scrollPane.setContent(gradesVBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //scrollPane.setStyle("-fx-border-color: #A9A9A9;fx-border-width:1");
        scrollPane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                rootPane.requestFocus();
        });


    }

    private void initGradesConfigCombo() {

        loadGradeConfigs();
        gradesConfigCombo.setItems(comboItems);


        gradesConfigCombo.setVisibleRowCount(3);
        gradesConfigCombo.setOnShown(t -> gradesConfigCombo.getSelectionModel().clearSelection());
        gradesConfigCombo.setOnHidden(t -> {
            gradesConfigCombo.getSelectionModel().select(gradesConfigComboSelectedIndex);
            System.out.println("easy" + gradesConfigComboSelectedIndex);
        });
        gradesConfigCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {

            if ((Integer) newValue == gradesConfigComboSelectedIndex || (Integer) newValue == -1)
                return;

            gradesConfigComboSelectedIndex = (Integer) newValue;
            initGradesVBox();
            isContentEdited = false;

            if (gradesConfigComboSelectedIndex < DEFAULT_GRADE_CONFIGS_COUNT)
                trashIcon.setOpacity(0.3);
            else
                trashIcon.setOpacity(1);


        });

        gradesConfigCombo.setCellFactory(t -> {
            final ListCell<String> cell = new ListCell<String>() {
                {
                    this.setPrefHeight(rootHeight*0.05);
                }

                @Override
                public void updateItem(String item,
                                       boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            return cell;
        });

        //System.out.println(gradeScalesJsonObj.get("lastSelectedScaleIndex")+" ya joeeeee");
        int index=Integer.parseInt((String)gradeScalesJsonObj.get("lastSelectedScaleIndex"));
        gradesConfigCombo.getSelectionModel().select(index);

    }


    private void initDeleteConfigButton() {
        Tooltip tooltip = new Tooltip("Delete Grade Scale Configuration");
        Tooltip.install(deleteConfigButton, tooltip);
        deleteConfigButton.setOnMouseClicked(t -> deleteCurrentConfig());

    }


    private void initTitles() {
        gradeBoundariesTitle.setFont(new Font("Arial", headersFontSize));
        reportsConfigTitle.setFont(new Font("Arial", headersFontSize));
    }


    private void initGradesVBox() {
        cloneGradesHBoxes(gradesConfigComboSelectedIndex);
        updateGradesVBox();

    }

    private void initGradesLabelsHBox() {


        gradesLabelsHBox.getChildren().addAll(gradeName, gradePercent, gradeRaw);
        gradeName.setStyle("-fx-text-fill:" + labelsColor + ";-fx-font-weight: bold;");
        gradeName.setAlignment(Pos.CENTER);
        gradeRaw.setStyle("-fx-text-fill:" + labelsColor + ";-fx-font-weight: bold;");
        gradeRaw.setAlignment(Pos.CENTER);
        gradePercent.setStyle("-fx-text-fill:" + labelsColor + ";-fx-font-weight: bold;");
        gradePercent.setAlignment(Pos.CENTER);
    }


    private void initReportsDirChooser() {

        Tooltip tooltip = new Tooltip("Choose Output Directory");
        Tooltip.install(reportsDirChooser, tooltip);

        reportsDirChooser.setOnMouseClicked(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {

                boolean isJsonSuccess=true;

                if(prefsJsonObj==null)
                    isJsonSuccess= loadPrefsJsonObj();


                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle("Choose Reports Output Directory");
                String lastDir = (String) prefsJsonObj.get("reportsOutputDir");

                if (isJsonSuccess) {
                    lastDir = lastDir.isEmpty() ? System.getProperty("user.home") : lastDir;
                    dirChooser.setInitialDirectory(new File((lastDir)));
                }

                File newDir = dirChooser.showDialog(stage);

                if (isJsonSuccess) {
                    reportsDirTextField.setText(newDir.getAbsolutePath());
                    reportsDirTextField.requestFocus();
                    reportsDirTextField.deselect();
                    if (isJsonSuccess && !newDir.getAbsolutePath().equals(lastDir)) {
                        prefsJsonObj.put(REPORTS_OUT_PATH_JSON_KEY, newDir.getAbsolutePath());
                    }
                }
            }
        });
    }


    private void initReportsDirTextField(){
        boolean isJsonSuccess = loadPrefsJsonObj();
        String lastDir = (String) prefsJsonObj.get("reportsOutputDir");
        reportsDirTextField.setText(isJsonSuccess?lastDir:"");
    }

    private void initReportsConfigHBox() {
        reportsConfigHBox.setStyle("-fx-border-color: #A9A9A9;");
    }

    private void initReportsVBox() {

        reportsLabel.setStyle("-fx-text-fill:" + labelsColor + ";-fx-font-weight: bold;");
        reportsVBox.getChildren().add(reportsLabel);

        //add checkboxes
        reportsCheckBoxes.add(new JFXCheckBox("Report 1: Grades Distribution Report"));
        reportsCheckBoxes.add(new JFXCheckBox("Report 2: Condensed Test Report"));
        reportsCheckBoxes.add(new JFXCheckBox("Report 3: Test Statistics Report"));
        reportsCheckBoxes.add(new JFXCheckBox("Report 4: Students Grades Report"));
        reportsCheckBoxes.add(new JFXCheckBox("Report 5: Questions Statistics Report"));

        //load json array
        boolean isJsonSuccess = loadPrefsJsonObj();

        JSONArray reportsChosen = (JSONArray) prefsJsonObj.get("reportsChosen");

        //initialize checkboxes
        for (int i = 0; i < reportsCheckBoxes.size(); i++) {
            Boolean value = true;
            if (isJsonSuccess)
                value = (Boolean) reportsChosen.get(i);
            reportsCheckBoxes.get(i).setSelected(value);
            reportsCheckBoxes.get(i).getStyleClass().add("smallCheckBox");
        }

        reportsVBox.getChildren().addAll(reportsCheckBoxes);
    }


    private void initFormatsVbox() {

        formatsLabel.setStyle("-fx-text-fill:" + labelsColor + ";-fx-font-weight: bold;");
        formatsVBox.getChildren().add(formatsLabel);


        formatsCheckBoxes.add(new JFXCheckBox("PDF"));
        formatsCheckBoxes.add(new JFXCheckBox("HTML"));
        formatsCheckBoxes.add(new JFXCheckBox("TXT"));

        //load json array
        boolean isJsonSuccess = loadPrefsJsonObj();
        JSONArray formatsChosen = (JSONArray) prefsJsonObj.get("formatsChosen");

        //initialize checkboxes
        for (int i = 0; i < formatsCheckBoxes.size(); i++) {
            Boolean value = true;
            if (isJsonSuccess)
                value = (Boolean) formatsChosen.get(i);
            formatsCheckBoxes.get(i).setSelected(value);
            formatsCheckBoxes.get(i).getStyleClass().add("smallCheckBox");
        }

        formatsVBox.getChildren().addAll(formatsCheckBoxes);

    }

    private void initTrashIcon() {
        trashIcon.setOpacity(0.25);
        trashIcon.setOnMouseEntered(t -> {
            if (gradesConfigComboSelectedIndex < DEFAULT_GRADE_CONFIGS_COUNT)
                return;
            trashIcon.setStyle("-fx-fill:#87CEEB");
        });

        trashIcon.setOnMouseExited(t -> trashIcon.setStyle("-fx-fill:#3184c9"));
    }

    private void initFinishButton() {
        this.nextButton.setText("Finish");
    }

    private boolean loadPrefsJsonObj() {

        return (prefsJsonObj = loadJsonObj(REPORTS_PREFS_FILE_NAME)) != null;
    }


    private void savePrefsJsonObj() {
        saveJsonObj(REPORTS_PREFS_FILE_NAME, prefsJsonObj);
    }

    private void deleteCurrentConfig() {

        if (gradeScalesJsonObj == null || gradesConfigComboSelectedIndex < DEFAULT_GRADE_CONFIGS_COUNT)
            return;

        if (showGradeScaleDeleteConfirmation()) {
            JSONArray scales = (JSONArray) gradeScalesJsonObj.get("scales");
            scales.remove(gradesConfigComboSelectedIndex);
            gradeScalesJsonObj.put("scales", scales);
            comboItems.remove(gradesConfigComboSelectedIndex);
            gradeScalesJsonObj.put("lastSelectedScaleIndex",Integer.toString(gradesConfigComboSelectedIndex));
            saveJsonObj(GRADE_SCALE_FILE_NAME, gradeScalesJsonObj);


        }
    }

    private void saveNewConfig(String scaleName, ArrayList<Pair<String, Double>> scale) {

        if (gradeScalesJsonObj == null)
            return;

        JSONArray scales = (JSONArray) gradeScalesJsonObj.get("scales");
        JSONArray grades = new JSONArray();

        for (Pair<String, Double> grade : scale) {

            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(grade.getKey(), Double.toString(grade.getValue()));
            grades.add(map);
        }

        LinkedHashMap<String, JSONArray> newScale = new LinkedHashMap<String, JSONArray>();
        newScale.put(scaleName, grades);
        scales.add(newScale);

        gradeScalesJsonObj.put("scales", scales);


    }


    private void loadGradeConfigs() {


        if ((gradeScalesJsonObj = loadJsonObj(GRADE_SCALE_FILE_NAME)) == null) {
            showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Grade Configurations Error",
                    "Error in loading Grade Scale Configurations");
            return;
        }

        JSONArray scales = (JSONArray) gradeScalesJsonObj.get("scales");


        for (int i = 0; i < scales.size(); i++) {
            ArrayList<GradeHBox> vBoxGrades = new ArrayList<>();

            JSONObject scale = (JSONObject) scales.get(i);
            comboItems.add((String) scale.keySet().iterator().next());
            JSONArray grades = (JSONArray) scale.values().iterator().next();

            for (int j = 0; j < grades.size(); j++) {

                JSONObject grade = (JSONObject) grades.get(j);
                vBoxGrades.add(new GradeHBox(j, (String) grade.keySet().iterator().next(), (String) grade.values().iterator().next(), this));

            }

            configs.add(vBoxGrades);

        }


    }


    private void updateGradesVBox() {
        gradesVBox.getChildren().clear();
        gradesVBox.getChildren().add(gradesLabelsHBox);
        gradesVBox.getChildren().addAll(gradesHBoxes);
        updateSizes();
    }

    private void cloneGradesHBoxes(int index) {

        gradesHBoxes = new ArrayList<>();
        ArrayList<GradeHBox> currentConfig = configs.get(index);

        for (GradeHBox hbox : currentConfig)
            gradesHBoxes.add(new GradeHBox(hbox));


    }


    private boolean showGradeScaleDeleteConfirmation() {

        return showConfirmationDialog("Delete Grade Scale Configuration","Are you sure you want to delete \""+comboItems.get(gradesConfigComboSelectedIndex)+"\" grade scale configuration?",stage.getOwner());
    }

    private String showSaveChangesDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Save Changes");
        dialog.setHeaderText("Do you want to save your changes as a new grade scale configuration?");
        dialog.setGraphic(new Alert(Alert.AlertType.CONFIRMATION).getGraphic());
        ButtonType saveButton = new ButtonType("Save New Configuration", ButtonBar.ButtonData.OK_DONE);
        ButtonType continueButton = new ButtonType("Ignore Changes", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, continueButton);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/FXML/application.css").toExternalForm());


        TextField configNameTextField = new TextField();
        configNameTextField.setPromptText("Configuration Name");

        HBox box = new HBox();
        Label label = new Label("Configuration Name:");
        label.prefHeightProperty().bind(configNameTextField.heightProperty());
        label.setAlignment(Pos.CENTER);
        box.getChildren().addAll(label, configNameTextField);
        box.setSpacing(7);

        dialog.getDialogPane().setContent(box);

        Node saveButt = dialog.getDialogPane().lookupButton(saveButton);

        dialog.setResultConverter(dialogButton -> {

            if (dialogButton == saveButton)
                return configNameTextField.getText();

            return null;

        });


        saveButt.addEventFilter(ActionEvent.ACTION, event -> {


            if (configNameTextField.getText().trim().isEmpty()) {
                event.consume();
                showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Configuration Name Error",
                        "Configuration name cannot be empty");
            } else if (isScaleExists(configNameTextField.getText().trim())) {
                event.consume();
                showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Configuration Name Error",
                        "\"" + configNameTextField.getText().trim() + "\" already exists.");
            }

        });

        Optional<String> result = dialog.showAndWait();

        if (result == null || !result.isPresent())
            return null;

        return result.get().trim();
    }


    private boolean isScaleExists(String targetScale) {

        JSONArray scales = ((JSONArray) gradeScalesJsonObj.get("scales"));

        for (Object scale : scales) {
            if (((JSONObject) scale).keySet().iterator().next().equals(targetScale))
                return true;
        }

        return false;

    }


    private void showProgressDialog(ArrayList<Report> reportsOut, ArrayList<Integer> formatsOut) {
        new ReportProgressController(reportsCount,reportsOut,formatsOut).startWindow();
    }



    private String getProjectDirName(){
        //return new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(Calendar.getInstance().getTime());
        return Controller.projectName;
    }

    private boolean createOutDirs(String outPath,String projectName) {

//        //project dir
//        if(!makeDir(outPath+projectName))
//            return false;
//
//
//        //format dirs

        System.out.println(new File(outPath).exists());

        for(int i=0;i<formatsCheckBoxes.size();i++){

            if(formatsCheckBoxes.get(i).isSelected()) {
                boolean success = makeDir(outPath + projectName + "\\" + Report.formatDirNames[i]);
                if (!success)
                    return false;
            }

        }

        return true;
    }


    private boolean makeDir(String dirPath){

        File theDir = new File(dirPath);
        System.out.println("Dir "+ theDir.getAbsolutePath());

        if (!theDir.exists()) {

            try{
                return theDir.mkdirs();
            }
            catch(SecurityException se){
                return false;
            }

        }
        else
            return true;

    }

    private boolean isCheckBoxesValid(){

        boolean formatsValid=false;
        boolean reportsValid=false;

        for(CheckBox checkBox : formatsCheckBoxes){
            if(checkBox.isSelected())
                formatsValid=true;
        }

        for(CheckBox checkBox : reportsCheckBoxes){
            if(checkBox.isSelected())
                reportsValid=true;
        }

        return formatsValid && reportsValid;

    }

    private void saveObjectiveGroups(JSONObject projObject) {

        JSONArray jsonGroups=new JSONArray();
        for(Group group:CSVHandler.getDetectedGroups()){
            JSONObject groupObj=new JSONObject();
            groupObj.put("name",group.getName());
            groupObj.put("qCount",Integer.toString(group.getqCount()));
            jsonGroups.add(groupObj);
        }
        projObject.put(OBJ_GROUPS_JSON_KEY,jsonGroups);
    }

    private void saveQNames(JSONObject projObject) {
        JSONArray jsonQNames=new JSONArray();

        for(String qName :CSVHandler.getDetectedQHeaders())
            jsonQNames.add(qName);

        projObject.put(Q_NAMES_JSON_KEY,jsonQNames);
    }

    private void saveQuestionsChoices(JSONObject projObject) {
        JSONArray qChoicesOuterArr=new JSONArray();
        for(ArrayList<String> innerArr:Statistics.getQuestionsChoices()){
            JSONArray choices=new JSONArray();
            for(String choice : innerArr)
                choices.add(choice);
            qChoicesOuterArr.add(choices);
        }
        projObject.put(Q_CHOICES_JSON_KEY,qChoicesOuterArr);
    }

    private void saveObjWeights(JSONObject projObject) {
        JSONArray objWeightsOuterArr=new JSONArray();

        for(ArrayList<Double> formWeights:Statistics.getQuestionWeights()){
            JSONArray innerArr=new JSONArray();
            for(Double weight:formWeights)
                innerArr.add(weight);

            objWeightsOuterArr.add(innerArr);
        }
        projObject.put(OBJ_WEIGHTS_JSON_KEY,objWeightsOuterArr);
    }


    private void saveSubjWeights(JSONObject projObject) {
        JSONArray jsonSubjWeights=new JSONArray();

        for(Double weight:Statistics.getSubjMaxScores())
            jsonSubjWeights.add(weight);

        projObject.put(SUBJ_WEIGHTS_JSON_KEY,jsonSubjWeights);
    }

    private void saveSavedAnswerKeyCSV(JSONObject projObject) {

        JSONArray jsonOuterArr=new JSONArray();

        for(String [] row: CSVHandler.getSavedAnswerKeyCSV()){
            JSONArray jsonRow=new JSONArray();
            for(String cell : row)
                jsonRow.add(cell);
            jsonOuterArr.add(jsonRow);
        }
        projObject.put(SAVED_ANSWER_KEY_CSV_JSON_KEY,jsonOuterArr);
    }

    private void saveInfoHeaders(JSONObject projObject) {

        if(isOpenMode) //info headers are never affected in open mode
            return;

        JSONArray jsonInfoHeaders=new JSONArray();

        for(String header :CSVHandler.getDetectedInfoHeaders())
            jsonInfoHeaders.add(header);

        projObject.put(SAVED_INFO_HEADERS_JSON_KEY,jsonInfoHeaders);
    }

    private void saveSavedResponsesCSV(JSONObject projObject) {

        JSONArray jsonOuterArr=new JSONArray();

        for(String [] row: CSVHandler.getSavedResponsesCSV()){
            JSONArray jsonRow=new JSONArray();
            for(String cell : row)
                jsonRow.add(cell);
            jsonOuterArr.add(jsonRow);
        }
        projObject.put(SAVED_RESPONSES_CSV_JSON_KEY,jsonOuterArr);
    }


    class PairSorter implements Comparator<Pair<String, Double>> {

        @Override
        public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
            return (int) (double) (o1.getValue() - o2.getValue());
        }
    }
}