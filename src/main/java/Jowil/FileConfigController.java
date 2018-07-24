package Jowil;
import com.jfoenix.controls.*;
import com.jfoenix.skins.JFXTextFieldSkin;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.util.Callback;

import static com.jfoenix.validation.base.ValidatorBase.PSEUDO_CLASS_ERROR;


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
    private final static int SKIPROW=0,CONTINUEANYWAY=1,CANCEL=2;










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

    }

    @Override
    protected Controller getNextController() {
        return null;
    }

    @Override
    protected void saveChanges(){

        //remove the None effect
        identifierComboSelectedIndex--;
        formComboSelectedIndex--;

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
    private void initNextButton(){

//        nextButton.setOnMouseEntered(new EventHandler<MouseEvent>
//                () {
//            public void handle(MouseEvent t) {
//                nextButton.setStyle("-fx-background-color:#878a8a;");
//            }
//        });
//
//        nextButton.setOnMouseExited(new EventHandler<MouseEvent>
//                () {
//
//
//            public void handle(MouseEvent t) {
//                nextButton.setStyle("-fx-background-color:transparent;");
//            }
//        });

        nextButton.setOnMouseClicked(t->{
            //nextButton.setStyle("-fx-background-color:transparent;");
            rootPane.requestFocus();



            if(mainFileTextField.getText().length()==0){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Students Responses File Error", "No students responses file provided.");
                return;
            }

            if(answersFileTextField.getText().length()==0){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Answer key File Error", "No answer key file provided.");
                return;
            }

            if(mainTextFieldResult==CSVFileValidator.ERROR){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Students Responses File Error", "Error in students responses file: "+mainTextFieldMessage);
                return;
            }

            if(answersTextFieldResult==CSVFileValidator.ERROR){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Answer key File Error", "Error in answer key file: "+answersTextFieldMessage);
                return;
            }

            int formsCount=0;

            try {
                formsCount=CSVHandler.loadAnswerKeys(answersFileTextField.getText());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV File Error",
                        "Error reading answers file: "+e.getMessage()+".");
                return;
            } catch (CSVHandler.EmptyAnswerKeyException e) {
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Answers File Error",
                        "Answer key file has empty cells.");
                return;
            }

            if(formsCount>1 && formComboSelectedIndex==0 &&mainTextFieldResult!=CSVFileValidator.WARNING){
                showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Answers File Error",
                        formsCount+" answer keys detected. Form column cannot have a \"None\" value. Select a valid form column to continue.");
                return;
            }

            boolean isHeadersExist=true;

            if(mainTextFieldResult==CSVFileValidator.WARNING){
                isHeadersExist=false;
                int selectedAction=showHeadersWarningDialog();
                if(selectedAction==CANCEL)
                    return;
                if(selectedAction==SKIPROW)
                    isHeadersExist=true;
            }

            CSVHandler.setFormsCount(formsCount);
            saveChanges();


















            if(true){
                GroupsController controller;
                if(next==null || isContentEdited) {
                    next = controller = new GroupsController(this);
                    controller.startWindow();
                }
                else {
                    controller = (GroupsController) next;
                    controller.showWindow();
                }
                isContentEdited=false;
            }
            else{
                HeadersCreateController controller=new HeadersCreateController(this);
                controller.startWindow();
            }
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
        if(idGroups.size()!=0)
            CSVHandler.addRealIDGroups(idGroups);

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
        manualModeToggle.setStyle("-jfx-toggle-color: #3184c9");
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

        if(identifierComboSelectedIndex==-1) { //none chosen
            CSVHandler.setAutoIDMode(true);
            return;
        }

        Statistics.setIdentifierName(filteredInfoHeaders.get(identifierComboSelectedIndex));

        int idStartIndex=getUnFilteredIndex(identifierComboSelectedIndex);
        CSVHandler.setIdentifierColStartIndex(idStartIndex);


        int idEndIndex;

        if(identifierComboSelectedIndex==complexIdStartIndex && isComplexIDAdded)
            idEndIndex=complexIDSize+idStartIndex;
        else
            idEndIndex=idStartIndex+1;

        CSVHandler.setIdentifierColEndIndex(idEndIndex);

    }

    private void saveFormColumn(){

        CSVHandler.setFormColIndex(getUnFilteredIndex(formComboSelectedIndex));
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
        alert.setTitle("No Headers Detected");
        alert.setHeaderText("Students Responses file doesn't contain headers.");
        alert.setContentText("Choose the \"Skip First Row\" option if the students responses file contains headers, otherwise click \"Continue Anyway\".");
        ButtonType skipRowButton = new ButtonType("Skip First Row");
        ButtonType continueButton = new ButtonType("Continue Anyway");
        ButtonType cancelButton= new ButtonType("Cancel");
        alert.getButtonTypes().setAll(skipRowButton,continueButton,cancelButton);
        Optional<ButtonType> result = alert.showAndWait();

        int selected;
        if(result.get()==skipRowButton)
            selected=SKIPROW;
        else if(result.get()==continueButton)
            selected=CONTINUEANYWAY;
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

}
