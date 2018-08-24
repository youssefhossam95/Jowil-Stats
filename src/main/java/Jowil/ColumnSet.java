package Jowil;

import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import static Jowil.Controller.resX;

public class ColumnSet extends HBox {


    private String name;
    private String type;
    private String color;
    private int startIndex;
    private int mySize;
    private int endIndex;




    private JFXTextField nameTextField;
    private Label typeTextField;
    private HBox colorPane;
    private Pane innerColorPane;
    private HBox rangeHBox;
    StackPane removeButton=new StackPane();
    ManualModeController parentController;

    private Font innerLabelsFont;
    private GlyphIcon rightArrowIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.ARROW_RIGHT).styleClass("error").build();
    private final  GlyphIcon removeIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.TRASH_ALT).styleClass("gradesMinusIcon").build();


    ColumnSet(String name,String color,int startIndex,int mySize,ManualModeController parentController,String type){

        this.name=name;
        this.type=type;
        this.color=color;
        this.startIndex=startIndex;
        this.mySize=mySize;
        this.endIndex=startIndex+mySize-1;
        this.parentController=parentController;
        this.innerLabelsFont=new Font("Arial", Controller.resX/100);



        this.setAlignment(Pos.CENTER);

        nameTextField=new JFXTextField();
        nameTextField.setFont(innerLabelsFont);
        nameTextField.setText(this.name);
        nameTextField.setStyle("-jfx-focus-color:#3184c9;-jfx-unfocus-color: #989898");
        nameTextField.setEditable(false);
        nameTextField.setAlignment(Pos.CENTER);


        typeTextField=new Label();
        typeTextField.setFont(innerLabelsFont);
        typeTextField.setText(getCroppedType());
        typeTextField.setStyle("-jfx-focus-color:#3184c9;-jfx-unfocus-color: #989898");
        //typeTextField.setEditable(false);
        typeTextField.setAlignment(Pos.CENTER);


        rangeHBox=new HBox(resX/400);
        Font rangeLabelsFont=new Font("Arial",Controller.resX/100);
        Label startLabel=new Label(Integer.toString(startIndex+1));
        Label endLabel=new Label(Integer.toString(startIndex+mySize));
        startLabel.setFont(rangeLabelsFont);
        endLabel.setFont(rangeLabelsFont);
        rightArrowIcon.setStyle("-fx-fill:#3184c9");
        rangeHBox.getChildren().addAll(startLabel,rightArrowIcon,endLabel);
        rangeHBox.setAlignment(Pos.CENTER);

        colorPane=new HBox();
        innerColorPane=new Pane();
        innerColorPane.setStyle("-fx-background-color:"+this.color);
        colorPane.getChildren().addAll(new Pane(),innerColorPane,new Pane());
        colorPane.setAlignment(Pos.CENTER);


        removeButton.getChildren().add(removeIcon);
        removeButton.setOnMouseClicked(t-> {
            if(Controller.showConfirmationDialog("Confirm Column Set Deletion","Are you sure you want to " +
                    "delete this column set?",parentController.stage.getOwner()))
                parentController.deleteColumnSet(this);
        });
        Tooltip tooltipRemove = new Tooltip("Delete Column Set");
        Tooltip.install(removeButton, tooltipRemove);

        this.getChildren().addAll(nameTextField,typeTextField,rangeHBox,colorPane,removeButton);

    }


    public void updateSizes(double scrollPaneWidth,double scrollPaneHeight){
        removeIcon.setGlyphSize((int)(parentController.rootWidth/64));
        rightArrowIcon.setGlyphSize((int)(parentController.rootWidth/75));
        nameTextField.setPrefWidth((int)(scrollPaneWidth*0.21));
        typeTextField.setPrefWidth((int)(scrollPaneWidth*0.21));
        rangeHBox.setPrefWidth((int)(0.2*scrollPaneWidth));
        colorPane.setPrefWidth((int)(0.15*scrollPaneWidth));
        nameTextField.setPrefHeight((int)(scrollPaneHeight/12));
        typeTextField.setPrefHeight((int)(scrollPaneHeight/12));
        innerColorPane.setPrefWidth((int)(nameTextField.getPrefHeight()));
        this.setSpacing((int)(scrollPaneWidth*0.03));

    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getMySize() {
        return mySize;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }



    public String getCroppedType(){

        String obj="Objective",id="ID",form="Form",subj="Subjective";
        if(type.contains(obj))
            return obj;
        if(type.contains(id))
            return id;
        if(type.contains(form))
            return form;
        if(type.contains(subj))
            return subj;

        return "";
    }



    public String getColor() {
        return color;
    }
//    @Override
//    public boolean equals(Object object) {
//        ColumnSet other=(ColumnSet)object;
//        return other.color.equals(this.color);
//    }







}