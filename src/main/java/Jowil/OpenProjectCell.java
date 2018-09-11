package Jowil;

import com.jfoenix.controls.JFXListCell;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import static Jowil.Controller.resX;

class OpenProjectCell extends JFXListCell {


    private long lastClick;
    String myText;
    Dialog dialog;
    private final  Node removeIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.TRASH_ALT).size(Double.toString(resX/80)).styleClass("gradesMinusIcon").build();


    StartController parentController;

    OpenProjectCell(Dialog dialog,StartController parentController) {
        super();
        this.dialog = dialog;
        this.parentController=parentController;



    }
    @Override
    public void updateItem(Object item,boolean empty){

//        if(item!=null ) {
//            if(((String)item).isEmpty())
//                this.setGraphic(null);
//            else {
//                label.setText((String) item);
//                myText = (String) item;
//            }
//        }

        super.updateItem("",empty);
        if(item!=null){
            AnchorPane cellPane=new AnchorPane();
            cellPane.setStyle("-fx-background-color:transparent");
            HBox hbox = new HBox(5);
            hbox.setId("hBox");
            this.setOnMouseClicked(event -> {

                if (Math.abs(lastClick - (lastClick = System.currentTimeMillis())) < 700)
                    parentController.startOpenMode(myText,dialog);

            });

            ImageView imageView = new ImageView();
            imageView.setImage(new Image("Images/Folder_96px.png"));
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);

            Label label = new Label(myText=(String)item);
            label.setId("label");
            label.setFont(new Font(15));
            label.setAlignment(Pos.CENTER);

            StackPane deleteButton=new StackPane();
            deleteButton.getChildren().add(removeIcon);
            Tooltip tooltipRemove = new Tooltip("Delete Project");
            Tooltip.install(deleteButton, tooltipRemove);
            deleteButton.setOnMouseClicked(event -> parentController.deleteProject(myText));

            hbox.getChildren().addAll(imageView, label);
            cellPane.getChildren().addAll(hbox,deleteButton);
            AnchorPane.setLeftAnchor(hbox,0.0);
            AnchorPane.setRightAnchor(deleteButton,5.0);
            this.setGraphic(cellPane);
        }


    }

    public static OpenProjectCell createOpenProjectCell(Dialog dialog,StartController parentController) {
        return new OpenProjectCell(dialog,parentController);
    }
}
