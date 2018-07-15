package Jowil;

import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class Controller {

    ///fields

    @FXML
    protected Pane rootPane;
    protected Scene scene;
    protected Stage stage;
    static public double resX;
    static public double resY;
    protected double rootWidth;
    protected double rootHeight;
    protected static final Logger logger = Logger.getLogger( FileConfigController.class.getName());
    final String myFXML;
    final String myTitle;
    final double XSCALE,YSCALE;
    final boolean isResizable;
    protected Controller back;
    protected Controller next;
    JFXButton backButton= new JFXButton("Back");

    protected boolean isContentEdited=false;
    protected HBox buttonsHbox= new HBox();




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
        initButtonsHBox();
        initComponents();
        updateSizes();




//        BackgroundFill[] fills={new BackgroundFill(Paint.valueOf("949797"),null,null)};
//        headersButton.setBackground(new Background(fills));
    }

    public void showWindow(){
        stage.show();
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void updateSizes(){
        rootWidth=rootPane.getPrefWidth();
        rootHeight=rootPane.getPrefHeight();
        backButton.setPrefWidth(resX/15);
        backButton.setPrefHeight(resX/250);
        backButton.setLayoutY(rootHeight/1.17);
        backButton.setLayoutX(rootWidth/1.35);
        buttonsHbox.setLayoutX(rootWidth/20);
        if(rootHeight<=resY/1.25)
            buttonsHbox.setLayoutY(rootHeight/1.14);
        else
            buttonsHbox.setLayoutY(rootHeight-resY*(1-1/1.14));
        buttonsHbox.setSpacing(resX/100);
        buttonsHbox.setPrefWidth(rootWidth/1.11);
        buttonsHbox.setPadding(new Insets(resY/100, 0, 0, 0));
        buttonsHbox.setAlignment(Pos.BASELINE_RIGHT);

    }




    //helper methods
    protected static void showAlert(Alert.AlertType alertType, javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }


    protected boolean isValidDouble(String s){
        try
        {
            Double.parseDouble(s);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;

    }

    private void initBackButton(){
        backButton.setStyle("-fx-border-width:1;-fx-border-color:#949797");
        rootPane.getChildren().add(backButton);
        backButton.setOnMouseClicked(t->{
            back.showWindow();
            stage.close();
        });

        backButton.setOnMouseEntered(t->backButton.setStyle("-fx-background-color:#878a8a;"));
        backButton.setOnMouseExited(t->backButton.setStyle("-fx-background-color:transparent;-fx-border-color:#949797"));
        buttonsHbox.getChildren().add(backButton);
    }

    private void initButtonsHBox(){
        buttonsHbox.setStyle("-fx-border-width: 1 0 0 0;-fx-border-color:#A9A9A9");
        rootPane.getChildren().add(buttonsHbox);
    }





}
