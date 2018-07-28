package Jowil;


import com.jfoenix.controls.JFXCheckBox;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

public class TreeViewCustomCell extends TreeCell<String> {


    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        this.selectedProperty().addListener((observable,oldValue,newValue)->{if(newValue) this.updateSelected(false);});
        this.focusedProperty().addListener((observable,oldValue,newValue)->{if(newValue) this.setFocused(false);});



        if (isEmpty()) {
            setGraphic(null);
            setText(null);
        } else {

            if (this.getTreeItem().isLeaf()) {

                this.setStyle("-fx-font-size:12");

                HBox cellBox = new HBox(0);
                JFXCheckBox checkBox = new JFXCheckBox();
                Label label = new Label(item);
                label.setPadding(new Insets(0,0,0,0));
                checkBox.setPadding(new Insets(0,0,0,0));
                checkBox.getStyleClass().add("smallCheckBox");
                checkBox.setSelected(GroupsController.isChoicePossible(this.getTreeItem().getParent().getParent().getValue(), this.getTreeItem().getValue()));
                checkBox.selectedProperty().addListener((observable,oldValue,newValue)->{
                    if(newValue) {
                        GroupsController.restoreToGroup(this.getTreeItem().getParent().getParent().getValue(), this.getTreeItem().getValue());
                        this.getTreeView().refresh();
                    }
                    else if(GroupsController.getLastPossible(this.getTreeItem().getParent().getParent().getValue())==GroupsController.getFirstPossible(this.getTreeItem().getParent().getParent().getValue()))
                        checkBox.setSelected(true);
                    else{
                        GroupsController.deleteFromGroup(this.getTreeItem().getParent().getParent().getValue(), this.getTreeItem().getValue());
                        this.getTreeView().refresh();
                    }
                });
                //Button button = new Button("Press!");
                // Here we bind the pref height of the label to the height of the checkbox. This way the label and the checkbox will have the same size.
                label.prefHeightProperty().bind(checkBox.heightProperty());
                cellBox.getChildren().addAll(checkBox, label);

                // We set the cellBox as the graphic of the cell.
                setGraphic(cellBox);
                setText(null);
            } else if(isGroupNameNode()) {


                HBox cellBox = new HBox(4);
                cellBox.setAlignment(Pos.CENTER_LEFT);
                Label label = new Label(item);
                this.setStyle("-fx-font-size:15;-fx-font-weight: bold;-fx-text-fill:#5e5c5c");
                Node plusIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.PLUS_CIRCLE).size("1.1em").styleClass("error").build();
                plusIcon.getStyleClass().add("groupPlusIcon");
                StackPane iconPane=new StackPane();
                iconPane.getChildren().add(plusIcon);

                this.setId(this.getTreeItem().getValue());
                iconPane.setOnMouseClicked(t->{
                    GroupsController.addToGroup(this.getTreeItem().getValue());
                    System.out.println(this.getTreeItem().getValue());
                    GroupsController.constructChoicesTreeView(this.getTreeView());
                });


                //Node d=this.getTreeItem().getGraphic().lookup("habeby");


                cellBox.getChildren().addAll(label,iconPane);

                setGraphic(cellBox);
                setText(null);
            }else{ //range node

                HBox cellBox = new HBox(3);
                cellBox.setAlignment(Pos.CENTER_LEFT);
                String min=this.getTreeItem().getChildren().get(GroupsController.getFirstPossible(this.getTreeItem().getParent().getValue())).getValue();
                String max=this.getTreeItem().getChildren().get(GroupsController.getLastPossible(this.getTreeItem().getParent().getValue())).getValue();
                Label minLabel=new Label(min);
                Label maxLabel=new Label(max);
                minLabel.setPadding(new Insets(0,0,0,0));
                maxLabel.setPadding(new Insets(0,0,0,0));
                Node rightArrowIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.ARROW_RIGHT).size("0.9em").styleClass("error").build();
                this.selectedProperty().addListener((observable,oldValue,newValue)->{
                    if(newValue)
                        rightArrowIcon.setStyle("-fx-fill:white");
                    else
                        rightArrowIcon.setStyle("-fx-fill:#3184c9");
                });
                rightArrowIcon.setStyle("-fx-fill:#3184c9");

                this.setStyle("-fx-font-size:14");
                cellBox.getChildren().addAll(minLabel,rightArrowIcon,maxLabel);
                setGraphic(cellBox);
                setText(null);
            }
        }
    }

    protected boolean isGroupNameNode(){
        return this.getTreeItem().getParent()==this.getTreeView().getRoot();
    }
}