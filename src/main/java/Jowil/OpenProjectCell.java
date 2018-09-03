package Jowil;

import com.jfoenix.controls.JFXListCell;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

class OpenProjectCell extends JFXListCell {


    private long lastClick;
    String myText;
    Dialog dialog;

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
            hbox.getChildren().addAll(imageView, label);
            this.setGraphic(hbox);
        }


    }

    public static OpenProjectCell createManualModeCell(Dialog dialog,StartController parentController) {
        return new OpenProjectCell(dialog,parentController);
    }
}
