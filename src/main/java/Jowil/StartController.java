package Jowil;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.Stack;


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
        lowerAncPane.setStyle("-fx-background-color:transparent");
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

        newStack.setOnMouseClicked(event -> new FileConfigController().startWindow());

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
        double upperHeight=rootHeight*0.245;
        double lowerHeight=1-upperHeight;


        upperImageView.setFitWidth(rootWidth);
        upperImageView.setFitHeight(upperHeight);

        lowerAncPane.setPrefWidth(rootWidth);
        lowerAncPane.setPrefHeight(lowerHeight);

        logoImageView.setFitWidth(0.08*resX);
        logoImageView.setFitHeight(0.11*resY);
        logoImageView.setLayoutX(0.47*rootWidth);
        logoImageView.setLayoutY(0.12*rootHeight);



        jowilLabel.setLayoutX(0.4*rootWidth);
        jowilLabel.setLayoutY(0.03*rootHeight);


        openStack.setLayoutX(0.24*rootWidth);
        openStack.setLayoutY(0.24*rootHeight);

        openRect.setWidth(0.173*resX);
        openRect.setHeight(0.4*resY);
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



}
