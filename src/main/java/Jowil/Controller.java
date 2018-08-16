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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
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


    protected boolean isContentEdited=false;
    protected HBox buttonsHbox= new HBox();
    protected final double headersFontSize=resX/64;



    //Main methods
    Controller(String fxmlName, String myTitle, double XSCALE , double YSCALE, boolean isResizable,Controller back){

        this.myFXML="/FXML/"+fxmlName;
        this.myTitle=myTitle;
        this.XSCALE=XSCALE;
        this.YSCALE=YSCALE;
        this.isResizable = isResizable;
        if(back==null)
            backButton.setVisible(false);
        this.back=back;
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
        rootPane.requestFocus();
    }

    public void startWindow(){
        try {

            // Hide this current window (if this is what you want)

            FXMLLoader loader = new FXMLLoader(getClass().getResource(myFXML));
            stage = new Stage();
            loader.setController(this);
            Pane root = loader.load();
            scene = new Scene(root,resX/XSCALE,resY/YSCALE);
            stage.setTitle(myTitle);
            scene.getStylesheets().add(getClass().getResource("/FXML/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(isResizable);
            stage.widthProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                    updateSizes();
                }
            });
            stage.heightProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                    updateSizes();
                }
            });
            stage.show();
            rootPane.requestFocus();
            rootPane.setOnMouseClicked(t->rootPane.requestFocus());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void updateSizes(){

        Insets navPadding=new Insets(0,0,0,0);
        navWidth=resX/15;
        navHeight=resX/47.5;

        rootWidth=rootPane.getPrefWidth();
        rootHeight=rootPane.getPrefHeight();

        backButton.setPrefWidth(navWidth);
        backButton.setPrefHeight(navHeight);
        backButton.setPadding(navPadding);

        nextButton.setPrefWidth(navWidth);
        nextButton.setPrefHeight(navHeight);
        nextButton.setPadding(navPadding);
        buttonsHbox.setLayoutX(rootWidth/20);

        buttonsHbox.setPrefHeight(resY*680/35);

        if(rootHeight<=resY/1.25)
            buttonsHbox.setLayoutY(rootHeight/1.14);
        else
            buttonsHbox.setLayoutY(rootHeight-resY*(1-1/1.14));

        buttonsHbox.setSpacing(resX/100);
        buttonsHbox.setPrefWidth(rootWidth*0.9);
        buttonsHbox.setPadding(new Insets(resY/100, 0, 0, 0));


        buttonsHbox.setAlignment(Pos.TOP_RIGHT);

    }




    //helper methods
    protected static void showAlert(Alert.AlertType alertType, javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    protected static void showAlertAndWait(Alert.AlertType alertType, javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());

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


    private void initNextButton(){
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
        Controller controller;
        if(next==null || isContentEdited) { //if first time or edit manually has been pressed
            next = controller = getNextController();
            controller.startWindow();
        }
        else {
            controller = next;
            controller.showWindow();
        }
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
        alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());
        Optional<ButtonType> option = alert.showAndWait();

        return option.get() == ButtonType.OK;
    }

//    protected static double setPrefWidth(Region element,relativeVal){
//
//        //element.setPrefWidth();
//
//    }






}
