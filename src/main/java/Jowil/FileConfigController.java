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
    private JFXButton mainFileChooserButton;

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
    private JFXButton answersFileChooserButton;

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
    private int complexIDIndex=-1;
    private ArrayList<Integer> infoHeadersTypes=new ArrayList<>();
    private ObservableList<String> combosItems=FXCollections.observableArrayList();
    private int identifierComboSelectedIndex; //including none at index zero
    private int formComboSelectedIndex; //including none at index zero
    private boolean isCombosAllowed=false;











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
        //combosHBox.setSpacing(rootWidthToPixels(0.15));
        //formCombo.setLayoutX(rootWidthToPixels(0.7));
        mainFileTextField.setPrefWidth(rootWidthToPixels(0.5));
        mainFileTextField.setPrefHeight(resYToPixels(0.04));
        mainFileTextField.setPadding(Insets.EMPTY);



        answersFileTextField.setPrefWidth(rootWidthToPixels(0.5));
        answersFileTextField.setPrefHeight(resYToPixels(0.04));
        answersFileTextField.setPadding(Insets.EMPTY);


        //mainFileChooserButton.setPrefWidth(resYToPixels(0.04));
        //mainFileChooserButton.setPrefHeight(resYToPixels(0.04));
//        mainChooserButtonImage.setFitWidth(mainFileChooserButton.getPrefWidth());
//        mainChooserButtonImage.setFitHeight(mainFileChooserButton.getPrefHeight());

        answersFileChooserButton.setPrefWidth(resYToPixels(0.04));
        answersFileChooserButton.setPrefHeight(resYToPixels(0.04));
//        answersChooserButtonImage.setFitWidth(mainFileChooserButton.getPrefWidth());
//        answersChooserButtonImage.setFitHeight(mainFileChooserButton.getPrefHeight());


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
        combosAnchor.setPadding(new Insets(0,answersFileChooserButton.getLayoutBounds().getWidth()+answersHBox.getSpacing(),0,0));

        subjVBox.setLayoutX(rootWidthToPixels(0.11));
        subjVBox.setLayoutY(rootHeightToPixels(0.5));

    }

    protected void initComponents(){
        initAnswersFileChooserButton();
        //initMainFileChooserButton();
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

            csvFile=new File(mainFileTextField.getText());
                if(!csvFile.exists()){
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV file Error",
                            "The file entered doesn't exist.");
                    return ;
                }
                if(!csvFile.getPath().endsWith(".csv")) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV file Error",
                            "Wrong file type: file must have a \".csv\" extension.");
                    return;
                }
                CSVHandler.setFilePath(csvFile.getPath());
                try {
                    if(CSVHandler.processHeaders()){
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

                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV file Error",
                            "Error reading file: "+e.getMessage()+".");
                } catch (CSVHandler.EmptyCSVException e) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV file Error",
                            "CSV file empty.");
                }

            });


    }



    private void initMainFileChooserButton(){

        mainFileChooserButton.setOnMouseClicked(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {
                mainFileChooserButton.setStyle("-fx-background-color:transparent;");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open CSV file");
                fileChooser.setInitialDirectory(new File((lastDir==null?System.getProperty("user.home"):lastDir)));
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
                csvFile =fileChooser.showOpenDialog(stage);
                if(csvFile!=null) {
                    lastDir = csvFile.getParent();
                    mainFileTextField.setText(csvFile.getPath());
                    mainFileTextField.requestFocus();
                    mainFileTextField.deselect();
                }
            }
        });
        mainFileChooserButton.setStyle("-fx-border-width:0;fx-background-color:transparent");


    }

    private void initAnswersFileChooserButton(){

        answersFileChooserButton.setOnMouseClicked(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {
                answersFileChooserButton.setStyle("-fx-background-color:transparent;");
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
                }
            }
        });
        answersFileChooserButton.setStyle("-fx-border-width:0;fx-background-color:transparent");


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
                CSVFileValidator validator= new CSVFileValidator(mainFileTextField,CSVFileValidator.MAINFILETEXTFIELD);
                mainFileTextField.getValidators().clear();
                mainFileTextField.getValidators().add(validator);

                mainFileTextField.validate();


                if(validator.getMessageType()==ValidatorBase.SUCCESS){
                    populateCombos();
                    manualModeToggle.setSelected(false);
                    isCombosAllowed=true;
                    formCombo.setDisable(false);
                    identifierCombo.setDisable(false);
                }
                else{
                    if(validator.getMessageType()==ValidatorBase.WARNING)
                        manualModeToggle.setSelected(true);
                    formCombo.setDisable(true);
                    identifierCombo.setDisable(true);
                    isCombosAllowed=false;

                }
            }

        });

    }


    private void initAnswersFileTextField(){

        answersFileTextField.textProperty().addListener((observable,oldValue,newValue)-> {
            isContentEdited=true;
        });

        answersFileTextField.focusedProperty().addListener((observable,oldValue,newValue)-> {

            if(!newValue){
                CSVFileValidator validator= new CSVFileValidator(answersFileTextField,CSVFileValidator.ANSWERSFILETEXTFIELD);
                answersFileTextField.getValidators().clear();
                answersFileTextField.getValidators().add(validator);
                answersFileTextField.validate();

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
        ArrayList<String> filteredInfoHeaders;
        Pattern digitsPattern = Pattern.compile("d+");
        List <Group> idGroups=new ArrayList<Group>();
        int countIDs=0;

        filteredInfoHeaders=processInfoHeaders(infoHeaders,idGroups);

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
    prepares the content of combo boxes while handling the complex IDs logic
     */
    private ArrayList<String> processInfoHeaders(ArrayList<String> infoHeaders,List<Group> idGroups) {

        ArrayList<String> filteredInfoHeaders = new ArrayList<>();
        ArrayList<Group> realIDGroups=new ArrayList<>();
        int expectedIndex = 1, digitBegin = 0;
        String currentGroup = "";


        //remove any header having an index and starting with keyword "id" from info headers and add it to IdGroups
        for (String header : infoHeaders) {
            if (!header.toLowerCase().trim().startsWith("id")) { //doesn't start with "id" -> normal info header
                filteredInfoHeaders.add(header);
                infoHeadersTypes.add(CSVHandler.IGNORE);
            }
            else {

                if ((digitBegin = header.lastIndexOf(Integer.toString(expectedIndex))) == -1) { //expected not found -> either end of group or non-indexed id header
                    if ((digitBegin = header.lastIndexOf("1")) == -1) {// column starting with id and contains no digits -> treat as normal info header
                        filteredInfoHeaders.add(header);
                        infoHeadersTypes.add(CSVHandler.IGNORE);
                        continue;
                    }

                    idGroups.add(new Group(currentGroup, expectedIndex - 1));
                    expectedIndex = 1;

                }
                currentGroup = header.substring(0, digitBegin);
                expectedIndex++;

            }
        }


        //extract realIdGroups from idGroups and add non-real idGroups(complexIDs) to filteredInfoHeaders
        for(Group group: idGroups){
            if(group.getqCount()>3) // real group
                realIDGroups.add(group);
            else{
                complexIDIndex=filteredInfoHeaders.size();
                String complexIDName=group.getName()+" ";
                for(int i=1;i<group.getqCount();i++) {
                    complexIDName += Integer.toString(i) + "-";
                    infoHeadersTypes.add(CSVHandler.STUDENTIDCONT);
                }
                complexIDName+=Integer.toString(group.getqCount());
                infoHeadersTypes.add(CSVHandler.STUDENTIDCONT);
                filteredInfoHeaders.add(complexIDName);
            }
        }


        //add realIDGroups to detected groups
        CSVHandler.addRealIDGroups(realIDGroups);
        return filteredInfoHeaders;

    }

    private void initManualModeToggle(){
        manualModeToggle.setStyle("-jfx-toggle-color: #3184c9");
        manualModeToggle.selectedProperty().addListener((observable,oldValue,newValue)->
        {
            if(newValue) {
                identifierCombo.setDisable(true);
                formCombo.setDisable(true);
            }
            else if(isCombosAllowed) {
                identifierCombo.setDisable(false);
                formCombo.setDisable(false);
            }

        });

    }



}
