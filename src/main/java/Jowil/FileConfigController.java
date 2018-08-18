package Jowil;
import com.jfoenix.controls.*;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;
import javafx.util.Callback;

import static Jowil.CSVHandler.NOT_AVAILABLE;


public class FileConfigController extends Controller{

 //components

    @FXML
    private JFXTextField mainFileTextField;
    @FXML
    private StackPane mainFileChooser;

    @FXML
    private ImageView mainChooserButtonImage;

    @FXML
    private JFXButton nextButton;

    @FXML
    private JFXTextField answersFileTextField;

    @FXML
    private JFXComboBox identifierCombo;

    @FXML
    private JFXComboBox formCombo;

    @FXML
    private JFXToggleButton manualModeToggle;


    @FXML
    private StackPane answersFileChooser;

    @FXML
    private ImageView answersChooserButtonImage;

    @FXML
    private HBox mainHBox;

    @FXML
    private HBox answersHBox;

    @FXML
    private HBox combosHBox;


    @FXML
    private AnchorPane combosAnchor;

    @FXML
    private VBox contentVbox;





    JFXToggleButton toggleButton = new JFXToggleButton();
    VBox subjVBox = new VBox();
    JFXSlider slider= new JFXSlider();
    final Popup popup = new Popup();



 //data fields
    private String lastDir;
    File csvFile;
    private static final String FX_LABEL_FLOAT_TRUE = "-fx-label-float:true;";
    private static final String EM1 = "1em";
    private static final String ERROR = "error";
    private ArrayList<String> filteredInfoHeaders;
    private ObservableList<String> combosItems=FXCollections.observableArrayList();
    private int identifierComboSelectedIndex; //including none at index zero
    private int formComboSelectedIndex; //including none at index zero
    private int mainTextFieldResult=CSVFileValidator.ERROR,answersTextFieldResult=CSVFileValidator.ERROR;
    private String mainTextFieldMessage="", answersTextFieldMessage="";
    private boolean isComplexIDAdded=false;
    private int complexIDSize=0;
    private int complexIdStartIndex;
    private final static int SKIPROW=0,CONTINUE=1,CANCEL=2,DECLARESUBJ=3;










    //getters and setters

   

    //Main methods
    FileConfigController(){
        super("FileConfig.fxml","File configuration",1.6,1.45,true,null);
    }
    
    
    protected void updateSizes(){
        super.updateSizes();

        contentVbox.setLayoutX(rootWidthToPixels(0.072));
        contentVbox.setLayoutY(rootHeightToPixels(0.13));
        contentVbox.setSpacing(rootHeightToPixels(0.1));
        contentVbox.setAlignment(Pos.BASELINE_LEFT);

        mainHBox.setSpacing(resXToPixels(0.005));
        answersHBox.setSpacing(resXToPixels(0.005));
        
        
        mainFileTextField.setPrefWidth(rootWidthToPixels(0.5));
        mainFileTextField.setPrefHeight(resYToPixels(0.04));
        mainFileTextField.setPadding(Insets.EMPTY);



        answersFileTextField.setPrefWidth(rootWidthToPixels(0.5));
        answersFileTextField.setPrefHeight(resYToPixels(0.04));
        answersFileTextField.setPadding(Insets.EMPTY);






        formCombo.setPadding(Insets.EMPTY);
        formCombo.setPrefWidth(rootWidthToPixels(0.227));
        identifierCombo.setPrefWidth(rootWidthToPixels(0.227));
        manualModeToggle.setPadding(new Insets(rootHeightToPixels(0.03),0,0,0));


        nextButton.setPrefWidth(resXToPixels(0.07));
        nextButton.setPrefHeight(resXToPixels(0.004));
        nextButton.setLayoutX(rootWidthToPixels(0.78));
        nextButton.setLayoutY(rootHeightToPixels(0.77));


        combosAnchor.setPrefWidth(rootWidthToPixels(0.4));
        AnchorPane.setLeftAnchor(identifierCombo,0.0);
        AnchorPane.setRightAnchor(formCombo,0.0);
        combosAnchor.setPadding(new Insets(0,answersFileChooser.getLayoutBounds().getWidth()+answersHBox.getSpacing(),0,0));

        subjVBox.setLayoutX(rootWidthToPixels(0.11));
        subjVBox.setLayoutY(rootHeightToPixels(0.5));

    }

    protected void initComponents(){
        initAnswersFileChooser();
        initMainFileChooser();
        initNextButton();
        initMainFileTextField();
        initAnswersFileTextField();
        initIdentifierCombo();
        initFormCombo();
        initManualModeToggle();
        mainFileTextField.setText(".\\src\\test\\AppTestCSVs\\TestGOnly.csv");
        answersFileTextField.setText(".\\src\\test\\AppTestCSVs\\alexAnswerKeysGOnly.csv");

    }

    @Override
    protected Controller getNextController() {
        return null;
    }

    @Override
    protected void saveChanges(){

        saveIdentifierColumn();
        saveFormColumn();


    }

    @Override
    protected void buildComponentsGraph(){

//        subjVBox.getChildren().add(toggleButton);
//        subjVBox.getChildren().add(slider);
//        rootPane.getChildren().add(subjVBox);

        Label label=new Label();
        label.setText("This is an error message");
        label.getStyleClass().add("chat-bubble");
        popup.getContent().add(label);

    }

    //helper methods
    protected void initNextButton(){

//
        nextButton.setOnMouseClicked(t->{


            boolean isManualMode=manualModeToggle.isSelected();
            rootPane.requestFocus();


            validateMainTextField();
            validateAnswersTextField();

            //check for errors
            if(mainFileTextField.getText().length()==0){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Students Responses File Error", "No students responses file provided.");
                return;
            }

            if(answersFileTextField.getText().length()==0){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Answer Key File Error", "No answer key file provided.");
                return;
            }

            if(mainTextFieldResult==CSVFileValidator.ERROR){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Students Responses File Error", "Error in students responses file: "+mainTextFieldMessage);
                return;
            }

            if(answersTextFieldResult==CSVFileValidator.ERROR){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Answer Key File Error", "Error in answer key file: "+answersTextFieldMessage);
                return;
            }

            int formsCount=CSVHandler.getFormsCount();

            if(formsCount>1 && formComboSelectedIndex==0 &&mainTextFieldResult!=CSVFileValidator.WARNING){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Answer Key File Error",
                        formsCount+" answer keys detected. Form column cannot have a \"None\" value. Select a valid form column to continue.");
                return;
            }

            int responsesColCount=CSVHandler.getResponsesColsCount();
            int answersColCount=CSVHandler.getAnswersColsCount();

            if(responsesColCount!=answersColCount){ //check if columns count doesn't match
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Columns Count Mismatch", "Student responses file contains " +
                        responsesColCount + " columns, while the answer key file contains " + answersColCount + " columns.");
                return;
            }


            if(isManualMode) {

                //if no headers in responses
                if(mainTextFieldResult==CSVFileValidator.WARNING){

                    int selectedAction=showHeadersWarningDialog();
                    if(selectedAction==CANCEL)
                        return;
                    CSVHandler.setIsSkipRowInManual(selectedAction==SKIPROW);
                }
                else //manual mode set by the user
                    CSVHandler.setIsSkipRowInManual(true);

                openManualMode();
                return;
            }


            //auto mode->contains headers->ignore headers if switched later to manual mode
            CSVHandler.setIsSkipRowInManual(true);


            if(answersTextFieldResult==CSVFileValidator.SUCCESS){ //both files contain headers -> check for headers mismatch
                boolean isMismatched;
                try {
                    isMismatched=CSVHandler.isFilesHeadersMismatched(new File(mainFileTextField.getText()),new File(answersFileTextField.getText()));
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Responses File Error",
                            "Error in reloading files.");
                    return;
                }
                if(isMismatched){
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Headers Mismatch Error","Headers of " +
                            "answers and student responses files are not identical.");
                    return;
                }
            }


            try {
                CSVHandler.loadAnswerKeys(answersFileTextField.getText(),true);
            } catch (IOException e) {

            } catch (CSVHandler.IllFormedCSVException e) {
                e.printStackTrace(); //msh hat7sl hena la2en lw feh aslun kan hasal f el call el f CSVFileValidator
            } catch (CSVHandler.InConsistentAnswerKeyException e) {
                e.printStackTrace(); //nafs el kalam zy el fo2eha
            }

            if(CSVHandler.isIsAnswerKeyContainsBlanks()){ //questions counts matching in auto mode but correct answers contain blanks
                try {

                    CSVHandler.processHeaders(true); //clean the blanks
                } catch(Exception e){
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Responses File Error",
                            "Error in reloading responses file.");
                    return;
                }
            }

            saveChanges();


            try {
                CSVHandler.loadCsv(true);
            } catch (CSVHandler.IllFormedCSVException e) {
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Students Responses File Error",
                        "Error in students responses file at row "+e.getRowNumber()+". File must contain the same number of columns in all rows.");
                return;
            }catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Students Responses File Error",
                        "Error in reading students responses file.");
                return;
            } catch (CSVHandler.InvalidFormNumberException e) {
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Students Responses File Error",
                        "Error in students responses file: "+e.getMessage());
                return;
            }



            new GroupsController(this).startWindow();
            stage.close();

            });




    }



    private void initMainFileChooser(){


        Tooltip tooltip = new Tooltip("Open CSV file");
        Tooltip.install(mainFileChooser, tooltip);

        mainFileChooser.setOnMouseClicked(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {
                mainFileChooser.setStyle("-fx-background-color:transparent;");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open CSV file");
                fileChooser.setInitialDirectory(new File((lastDir==null?System.getProperty("user.home"):lastDir)));
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
                csvFile =fileChooser.showOpenDialog(stage);
                if(csvFile!=null) {
                    lastDir = csvFile.getParent();
                    mainFileTextField.setText(csvFile.getPath());
                    validateMainTextField();
                    mainFileTextField.requestFocus();
                    mainFileTextField.deselect();

                }
            }
        });



    }

    private void initAnswersFileChooser(){

        Tooltip tooltip = new Tooltip("Open CSV file");
        Tooltip.install(answersFileChooser, tooltip);



        answersFileChooser.setOnMouseClicked(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {
                //answersFileChooser.setStyle("-fx-background-color:transparent;");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open CSV file");
                fileChooser.setInitialDirectory(new File((lastDir==null?System.getProperty("user.home"):lastDir)));
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
                csvFile =fileChooser.showOpenDialog(stage);
                if(csvFile!=null) {
                    lastDir = csvFile.getParent();
                    answersFileTextField.setText(csvFile.getPath());
                    answersFileTextField.requestFocus();
                    answersFileTextField.deselect();
                    validateAnswersTextField();
                }


            }
        });




    }

    private void initToggleButton(){

        toggleButton.setText("Subjective Questions");
        toggleButton.setStyle("-fx-font-weight: bold;-jfx-toggle-color: #00BFFF");
        slider.setMax(20);
        slider.setMin(0);
    }

    private void initMainFileTextField(){


        mainFileTextField.textProperty().addListener((observable,oldValue,newValue)-> {
            isContentEdited=true;
        });

        mainFileTextField.focusedProperty().addListener((observable,oldValue,newValue)-> {

            if(!newValue){
                validateMainTextField();


            }

        });

    }


    private void initAnswersFileTextField(){

        answersFileTextField.textProperty().addListener((observable,oldValue,newValue)-> {
            isContentEdited=true;
        });

        answersFileTextField.focusedProperty().addListener((observable,oldValue,newValue)-> {

            if(!newValue){
                validateAnswersTextField();
            }

        });
    }

    private void initIdentifierCombo(){
        identifierCombo.setDisable(true);
        identifierCombo.setItems(combosItems);
        identifierCombo.setVisibleRowCount(3);
        identifierCombo.setOnShown(t->identifierCombo.getSelectionModel().clearSelection());
        identifierCombo.setOnHidden(t->{identifierCombo.getSelectionModel().select(identifierComboSelectedIndex); System.out.println("easy"+identifierComboSelectedIndex);});
        identifierCombo.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)-> {

            if((Integer)newValue!=-1)
                identifierComboSelectedIndex=(Integer)newValue;

        });
        
        identifierCombo.setCellFactory(
                new Callback<ListView<String>, ListCell<String>>() {
                    @Override public ListCell<String> call(ListView<String> param) {
                        final ListCell<String> cell = new ListCell<String>() {
                            {
                                this.setPrefHeight(rootHeight*0.06);
                                //this.setFont(new Font("Arial",resY/30));
                            }
                            @Override public void updateItem(String item,
                                                             boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item);
                                }
                                else {
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }

                });
    }


    private void initFormCombo(){
        formCombo.setDisable(true);
        formCombo.setItems(combosItems);
        formCombo.setVisibleRowCount(3);
        formCombo.setOnShown(t->formCombo.getSelectionModel().clearSelection());
        formCombo.setOnHidden(t->{formCombo.getSelectionModel().select(formComboSelectedIndex); System.out.println("easy"+formComboSelectedIndex);});
        formCombo.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)-> {

            if((Integer)newValue!=-1)
                formComboSelectedIndex=(Integer)newValue;

        });


        formCombo.setCellFactory(
                new Callback<ListView<String>, ListCell<String>>() {
                    @Override public ListCell<String> call(ListView<String> param) {
                        final ListCell<String> cell = new ListCell<String>() {
                            {
                                //super.setPrefHeight(identifierCombo.getPrefHeight());
                            }
                            @Override public void updateItem(String item,
                                                             boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item);
                                }
                                else {
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }

                });
    }

    private void populateCombos(){
        ArrayList<String> infoHeaders=CSVHandler.getDetectedInfoHeaders();
        Pattern digitsPattern = Pattern.compile("d+");
        int countIDs=0;

        processInfoHeaders(infoHeaders);

        combosItems.clear();
        combosItems.add("None");
        identifierCombo.getSelectionModel().selectFirst();
        formCombo.getSelectionModel().selectFirst();

        for(String header: filteredInfoHeaders){
            combosItems.add(header);
            if(header.toLowerCase().trim().contains("id"))
                identifierCombo.getSelectionModel().select(combosItems.size()-1);
            else if(header.toLowerCase().trim().contains("form") || header.toLowerCase().trim().contains("model"))
                formCombo.getSelectionModel().select(combosItems.size()-1);
        }


    }


    /*
    prepares the content of combo boxes while handling the complexIDs logic
     */
    private void processInfoHeaders(ArrayList<String> infoHeaders) {

        filteredInfoHeaders = new ArrayList<>();
        int expectedIndex = 1, digitBegin = 0, lastNumberedHeader=-2;
        String currentGroup = "";
        Pattern groupsPattern = Pattern.compile(".*\\d+");
        ArrayList <Group> idGroups=new ArrayList<Group>();
        isComplexIDAdded=false;



        //remove any header having an index and starting with keyword "id" from info headers and add it to IdGroups
        for (int i=0;i<infoHeaders.size();i++) {
            if (!groupsPattern.matcher(infoHeaders.get(i)).matches()) { //no groups pattern -> normal header
                if(lastNumberedHeader==i-1){
                    int groupSize=expectedIndex-1;
                    processGroup(groupSize,currentGroup,idGroups,i);
                    expectedIndex = 1;
                    currentGroup="";
                }
                filteredInfoHeaders.add(infoHeaders.get(i));

            }
            else { //groups pattern
                lastNumberedHeader=i;
                if ((digitBegin = infoHeaders.get(i).lastIndexOf(Integer.toString(expectedIndex))) == -1) { //expected not found or end of array-> end of group

                    int groupSize=expectedIndex-1;
                    processGroup(groupSize,currentGroup,idGroups,i);
                    expectedIndex = 2;
                    currentGroup="";

                }
                else { //still inside same group
                    currentGroup = infoHeaders.get(i).substring(0, digitBegin);
                    expectedIndex++;
                }
            }
        }

        if(currentGroup.length()!=0) //process last group if exists
            processGroup(expectedIndex-1,currentGroup,idGroups,lastNumberedHeader+1);


        //add idGroups to detected groups

        if(idGroups.isEmpty())
            CSVHandler.setRealIDGroups(null);
        else
            CSVHandler.setRealIDGroups(idGroups);

    }


    private void processGroup(int groupSize,String currentGroup,ArrayList<Group> idGroups, int idEnd){
        if(groupSize>3 || isComplexIDAdded) { //if a complexID occurred before or consider this a questions group
            if(idGroups.size()==0) //start of questions groups
                CSVHandler.setQuestionsColStartIndex(idEnd-groupSize);
            idGroups.add(new Group(currentGroup, groupSize));
        }
        else{
            isComplexIDAdded=true;
            complexIdStartIndex=idEnd-groupSize;
            complexIDSize=groupSize;
            filteredInfoHeaders.add(constructComplexIDString(currentGroup,groupSize));
        }
    }

    private void initManualModeToggle(){
        manualModeToggle.setStyle("-jfx-untoggle-color:#3184c9;-jfx-toggle-color:#3184c9");
        manualModeToggle.selectedProperty().addListener((observable,oldValue,newValue)->
        {
            if(newValue) {
                identifierCombo.setDisable(true);
                formCombo.setDisable(true);
            }
            else{
                if(mainTextFieldResult==CSVFileValidator.SUCCESS) {
                identifierCombo.setDisable(false);
                formCombo.setDisable(false);
                }
                else if(mainTextFieldResult==CSVFileValidator.WARNING){
                    manualModeToggle.setSelected(true);
                }
            }

        });

    }

    private void saveIdentifierColumn(){

        int identifierSelectedIndex=identifierComboSelectedIndex-1; //remove None effect
        String idenfierName=identifierSelectedIndex==-1?"ID":filteredInfoHeaders.get(identifierSelectedIndex);
        Statistics.setIdentifierName(idenfierName);

        if(identifierSelectedIndex==-1) { //none chosen
            CSVHandler.setIdentifierColStartIndex(NOT_AVAILABLE);
            CSVHandler.setIdentifierColEndIndex(NOT_AVAILABLE);
            return;
        }



        int idStartIndex=getUnFilteredIndex(identifierSelectedIndex);
        CSVHandler.setIdentifierColStartIndex(idStartIndex);


        int idEndIndex;

        if(identifierSelectedIndex==complexIdStartIndex && isComplexIDAdded)
            idEndIndex=complexIDSize+idStartIndex;
        else
            idEndIndex=idStartIndex+1;

        CSVHandler.setIdentifierColEndIndex(idEndIndex);

    }

    private void saveFormColumn(){

        int formSelectedIndex=CSVHandler.getFormsCount()==1?NOT_AVAILABLE:formComboSelectedIndex-1; //remove None effect
        CSVHandler.setFormColIndex(getUnFilteredIndex(formSelectedIndex));
    }


    private String constructComplexIDString(String groupName, int groupSize){
        String complexIDString=groupName+" ";
        for(int i=1;i<groupSize;i++) {
            complexIDString += Integer.toString(i) + "-";
        }
        complexIDString+=Integer.toString(groupSize);
        
        return complexIDString;
        
    }

    private int getUnFilteredIndex(int filteredIndex){

        if(!isComplexIDAdded || filteredIndex<=complexIdStartIndex)  //still one to one mapping between filtered and unfiltered info headers
            return filteredIndex;

        return filteredIndex+(complexIDSize-1);


    }

    private int showHeadersWarningDialog(){

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/FXML/application.css").toExternalForm());
        alert.setTitle("No Headers Detected");
        alert.setHeaderText("Students Responses file doesn't contain headers");
        alert.setContentText("Choose the \"Skip First Row\" option if the students responses file contains headers, otherwise click \"Continue Anyway\". Both of these options will direct you to the manual mode.");
        //alert.setOnCloseRequest(t->alert.hide());
        ButtonType skipRowButton = new ButtonType("Skip First Row");
        ButtonType continueButton = new ButtonType("Continue Anyway");
        ButtonType close =ButtonType.CLOSE;


        alert.getButtonTypes().setAll(skipRowButton,continueButton,close);
        Optional<ButtonType> result = alert.showAndWait();

        int selected;
        if(result.get()==skipRowButton)
            selected=SKIPROW;
        else if(result.get()==continueButton)
            selected=CONTINUE;
        else
            selected=CANCEL;

        return selected;


    }




    private void validateMainTextField(){

        CSVFileValidator validator= new CSVFileValidator(mainFileTextField,CSVFileValidator.MAINFILETEXTFIELD);
        mainFileTextField.getValidators().clear();
        mainFileTextField.getValidators().add(validator);

        mainFileTextField.validate();

        mainTextFieldResult=validator.getMessageType();
        mainTextFieldMessage=validator.getMessage();

        if(validator.getMessageType()==ValidatorBase.SUCCESS){
            populateCombos();
            manualModeToggle.setSelected(false);
            formCombo.setDisable(false);
            identifierCombo.setDisable(false);
        }
        else{
            if(validator.getMessageType()==ValidatorBase.WARNING)
                manualModeToggle.setSelected(true);
            formCombo.setDisable(true);
            identifierCombo.setDisable(true);
        }
    }

    private void validateAnswersTextField(){
        CSVFileValidator validator= new CSVFileValidator(answersFileTextField,CSVFileValidator.ANSWERSFILETEXTFIELD);
        answersFileTextField.getValidators().clear();
        answersFileTextField.getValidators().add(validator);
        answersFileTextField.validate();
        answersTextFieldResult=validator.getMessageType();
        answersTextFieldMessage=validator.getMessage();
    }

    private void openManualMode(){
        new ManualModeController(this).startWindow();
        CSVHandler.setRealIDGroups(null);//populate combos wasn't called -> reintialize realIDGroups
        stage.close();

    }



}
