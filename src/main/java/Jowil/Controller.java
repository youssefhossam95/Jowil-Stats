package Jowil;

import com.jfoenix.controls.JFXButton;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class Controller {

    ///components

    @FXML
    protected Pane rootPane;
    protected Scene scene;
    protected Stage stage;
    protected Controller back;
    protected Controller next;
    protected JFXButton backButton= new JFXButton("Back");
    protected JFXButton nextButton=new JFXButton("Next");


    //data fields
    static public double resX;
    static public double resY;
    protected double rootWidth;
    protected double rootHeight;
    protected static final Logger logger = Logger.getLogger( FileConfigController.class.getName());
    final String myFXML;
    final String myTitle;
    final double XSCALE,YSCALE;
    final boolean isResizable;
    double navWidth;
    double navHeight;
    boolean isHeightCalling;


    protected boolean isContentEdited=false;
    protected HBox buttonsHbox= new HBox();
    protected final double headersFontSize=resX/64;
    private boolean isMaximizedChanged=false;
    boolean isWait=false;
    static boolean isOpenMode;
    boolean isBeginMaximised;
    boolean isStepWindow;
    static String projectName;
    static String SAVED_PROJECTS_FILE_NAME="Projects.json";
    static JSONObject savedProjectsJson;
    static JSONObject currentOpenedProjectJson;
    static String selectedIdentifierName;
    static String selectedFormColName;
    static final String MANUAL_MODE_INDICATOR=" (Manual Mode)";
    static final String Q_NAMES_JSON_KEY="qNames",OBJ_GROUPS_JSON_KEY="objGroups",Q_CHOICES_JSON_KEY="questionsChoices",
            OBJ_WEIGHTS_JSON_KEY="objWeights",SUBJ_WEIGHTS_JSON_KEY="subjWeights",SELECTED_SCALE_JSON_KEY="SelectedScaleIndex",
            REPORTS_CHOSEN_JSON_KEY="reportsChosen",FORMATS_CHOSEN_JSON_KEY="formatsChosen",REPORTS_OUT_PATH_JSON_KEY="reportsOutputDir",
            Q_COL_START_INDEX_JSON_KEY="qColStartIndex",Q_COL_END_INDEX_JSON_KEY="qColEndIndex",SUBJ_COL_START_INDEX_JSON_KEY="subjColStartIndex",
            SUBJ_COL_END_INDEX_JSON_KEY="subjColEndIndex", SUBJ_Q_COUNT_JSON_KEY="subjQuestionsCount",FORM_COL_INDEX_JSON_KEY="formColIndex",
            ID_COL_START_INDEX_JSON_KEY="idStartIndex",ID_COL_END_INDEX_JSON_KEY="idEndIndex",IS_MANUAL_MODE_JSON_KEY="isManualModeUsed",
            IS_RESPONSES_CONTAINS_HEADERS_JSON_KEY="isResponsesContainsHeaders",IS_ANSWER_KEY_CONTAINS_HEADERS_JSON_KEY="isAnswerKeyContainsHeaders",
            RESPONSES_FILE_PATH_JSON_KEY="responsesFilePath", ANSWERS_FILE_PATH_JSON_KEY="answersFilePath",IDENTIFIER_NAME_JSON_KEY="identifierName"
            ,FORM_COL_NAME_JSON_KEY="formColName",SAVED_RESPONSES_CSV_JSON_KEY="savedResponsesCSV",SAVED_ANSWER_KEY_CSV_JSON_KEY="savedAnswerKeyCSV",
            SAVED_INFO_HEADERS_JSON_KEY="infoHeaders";



    //Main methods

    Controller(String fxmlName, String myTitle, double XSCALE , double YSCALE, boolean isResizable,Controller back){
        this(fxmlName,myTitle,XSCALE,YSCALE,isResizable,back,true,false);
        //progressImage=new ImageView(new Image("Images/"+progressImageName));
    }



    Controller(String fxmlName, String myTitle, double XSCALE , double YSCALE, boolean isResizable,Controller back, boolean isStepWindow,boolean isMaximised){

        this.myFXML="/FXML/"+fxmlName;
        this.myTitle=myTitle;
        this.XSCALE=XSCALE;
        this.YSCALE=YSCALE;
        this.isResizable = isResizable;
        if(back==null)
            backButton.setVisible(false);
        this.back=back;
        this.isBeginMaximised=isMaximised;
        this.isStepWindow=isStepWindow;

    }


    protected abstract void initComponents();
    protected abstract void saveChanges();

    public void initialize() {

        rootPane.prefHeightProperty().bind(stage.heightProperty());
        rootPane.prefWidthProperty().bind(stage.widthProperty());
        initBackButton();
        initNextButton();
        initButtonsHBox();
        initComponents();
        updateSizes();
        buildComponentsGraph();
        stabalizeTables();





    }

    public void showWindow(){
        stage.show();
        stage.setMaximized(false);
        rootPane.requestFocus();
        this.updateSizes();
    }

    public void startWindow(){
        try {

            // Hide this current window (if this is what you want)

            FXMLLoader loader = new FXMLLoader(getClass().getResource(myFXML));
            stage = new Stage();
            loader.setController(this);
            Pane root = loader.load();

            if(XSCALE==1 && YSCALE==1) {
                stage.setMaximized(true);
            }
            scene = new Scene(root, resX / XSCALE, resY / YSCALE);


            stage.setTitle(myTitle);
            scene.getStylesheets().add(getClass().getResource("/FXML/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(isResizable);

            stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old, Boolean neww) {
                    isMaximizedChanged=true;


                }
            });


            stage.widthProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {

                    isHeightCalling=false;
                    updateSizes();

                }
            });



            stage.heightProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {

                    isHeightCalling=true;
                    updateSizes();
                }
            });


            rootPane.requestFocus();
            rootPane.setOnMouseClicked(t->rootPane.requestFocus());

            if(this instanceof StartController)
                stage.initStyle(StageStyle.UNDECORATED);
            else
                stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(event -> {
                if(!showConfirmationDialog("Cancel Project","Are you sure you want to cancel this project?",stage.getOwner()))
                    event.consume();
            });
            stage.show();
            stage.setMaximized(isBeginMaximised);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void updateSizes(){

        Insets navPadding=new Insets(0,0,0,0);
        navWidth=resX/15;
        navHeight=resX/47.5;




        if(!isHeightCalling) {
            if (scene != null && !isMaximizedChanged) {
                rootWidth = scene.getWidth();
            } else { //if maximised property was changed use scene width will not be properly set
                rootWidth = rootPane.getPrefWidth();
                System.out.println("maxim");
                isMaximizedChanged = false;
            }
        }

        rootHeight=rootPane.getPrefHeight();


        backButton.setPrefWidth(navWidth);
        backButton.setPrefHeight(navHeight);
        backButton.setPadding(navPadding);

        nextButton.setPrefWidth(navWidth);
        nextButton.setPrefHeight(navHeight);
        nextButton.setPadding(navPadding);
        buttonsHbox.setLayoutX(rootWidth*0.05);

        buttonsHbox.setPrefHeight(resY*680/35);

        if(rootHeight<=resY/1.25)
            buttonsHbox.setLayoutY(rootHeight/1.14);
        else
            buttonsHbox.setLayoutY(rootHeight-resY*(1-1/1.14));

        buttonsHbox.setSpacing(resX/100);
        buttonsHbox.setPrefWidth(rootWidth*0.9);
        buttonsHbox.setPadding(new Insets(resY/100, 0, 0, 0));


        buttonsHbox.setAlignment(Pos.TOP_RIGHT);
        System.out.println(rootWidth);
        System.out.println(rootHeight);

    }




    //helper methods
    protected static void showAlert(Alert.AlertType alertType, javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        //alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    protected static void showAlertAndWait(Alert.AlertType alertType, javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        //alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());

        alert.getButtonTypes().setAll(ButtonType.CLOSE);
        Button closeButt=(Button)alert.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButt.setText("OK");
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }


    /**
     *
     * @param s
     * @return null if the value is less than zero or is not Parsable to Double , otherwise returns a new string in a non-integer representation
     */
    public static String tryDouble(String s){

        s=s.trim();
        try
        {
            double x=Double.parseDouble(s);
            if(x>=0)
                return Double.toString(x);
            else
                return null;
        }
        catch(NumberFormatException e)
        {
            return null;
        }

    }

    private void initBackButton(){
        //backButton.setStyle("-fx-border-width:1;-fx-border-color:#949797");
        rootPane.getChildren().add(backButton);
        backButton.setOnMouseClicked(t->{
            rootPane.requestFocus();
            back.showWindow();
            stage.close();
        });

//        backButton.setOnMouseEntered(t->backButton.setStyle("-fx-background-color:#878a8a;"));
//        backButton.setOnMouseExited(t->backButton.setStyle("-fx-background-color:transparent"));
    }

    private void initButtonsHBox(){
        buttonsHbox.setStyle("-fx-border-width: 1 0 0 0;-fx-border-color:#A9A9A9");
        //buttonsHbox.setStyle("-fx-border-width: 1 0 0 0;-fx-border-color:#3184c9");
    }


    protected void initNextButton(){
        //nextButton.setStyle("-fx-border-width:1;-fx-border-color:#949797");

//        nextButton.setOnMouseEntered(t->nextButton.setStyle("-fx-background-color:#878a8a;"));
//        nextButton.setOnMouseExited(t->nextButton.setStyle("-fx-background-color:transparent;"));
        nextButton.setOnMouseClicked(t->{
            rootPane.requestFocus();
            goToNextWindow();});

    }

    protected abstract Controller getNextController();

    protected void goToNextWindow(){
        saveChanges();

        if(next==null || isContentEdited) { //if first time or edit manually has been pressed
            next = getNextController();
            next.startWindow();
        }
        else
            next.showWindow();

        isContentEdited=false;
        stage.close();
    }


    protected void buildComponentsGraph(){

        buttonsHbox.getChildren().add(backButton);
        buttonsHbox.getChildren().add(nextButton);
        rootPane.getChildren().add(buttonsHbox);
    }

    protected double resXToPixels(double relativeVal){

        return relativeVal*resX;
    }

    protected double resYToPixels(double relativeVal){

        return relativeVal*resY;
    }

    protected double rootWidthToPixels(double relativeVal){

        return relativeVal*rootWidth;
    }

    protected double rootHeightToPixels(double relativeVal){

        return relativeVal*rootHeight;
    }



    protected void stabalizeTables(){

    }

    protected static void disableTableDrag(TableView table){
        try {
            table.setOnMouseEntered(t -> {
                TableHeaderRow header = (TableHeaderRow) table.lookup("TableHeaderRow");
                header.setMouseTransparent(true);
            });
        }catch(Exception e){

        }
    }

    public void setContentEdited(boolean contentEdited) {
        isContentEdited = contentEdited;
    }

    public static boolean showConfirmationDialog(String title,String content,Window owner) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Button okButt=(Button)alert.getDialogPane().lookupButton(ButtonType.OK);
        okButt.setText("Yes");

        Button cancelButt=(Button)alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButt.setText("No");

        alert.initOwner(owner);
        //alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());
        Optional<ButtonType> option = alert.showAndWait();

        return option.get() == ButtonType.OK;
    }


    protected JSONObject loadJsonObj(String path) {

        String file = "";
        JSONObject jsonObj = null;
        try {
            file = URLDecoder.decode(getClass().getResource("/UserData/" + path).getFile(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        try {
            jsonObj = (JSONObject) new JSONParser().parse(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return jsonObj;

    }

    protected void saveJsonObj(String path, JSONObject jsonObj) {

        PrintWriter pw = null;
        String file = "";
        try {
            file = URLDecoder.decode(getClass().getResource("/UserData/" + path).getFile(), "utf-8");
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

//    protected static double setPrefWidth(Region element,relativeVal){
//
//        //element.setPrefWidth();
//
//    }






}
