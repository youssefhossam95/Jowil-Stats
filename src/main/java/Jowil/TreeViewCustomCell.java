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
import javafx.scene.layout.StackPane;

import static Jowil.Controller.resX;


public class TreeViewCustomCell extends TreeCell<String> {

    private final double CHECK_BOXES_SIZE=resX*14/1280;

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



                //HBox cellBox = new HBox(0);
                JFXCheckBox checkBox = new JFXCheckBox(item,CHECK_BOXES_SIZE);
                this.setStyle("-fx-font-size:"+resX*12/1280);
//                Label label = new Label(item);
//                label.setPadding(new Insets(0,0,0,0));
                checkBox.setPadding(new Insets(0,0,0,0));
                checkBox.setSelected(GroupsController.isChoicePossible(this.getTreeItem().getParent().getParent().getValue(), this.getTreeItem().getValue()));

                checkBox.selectedProperty().addListener((observable,oldValue,newValue)->{

                    String groupName=this.getTreeItem().getParent().getParent().getValue();
                    if(newValue) {
                        GroupsController.restoreToGroup(groupName, this.getTreeItem().getValue());
                        this.getTreeView().refresh();
                    }
                    else if(GroupsController.getLastPossible(groupName)==GroupsController.getFirstPossible(groupName))
                        checkBox.setSelected(true);
                    else{
                        GroupsController.deleteFromGroup(groupName, this.getTreeItem().getValue());
                        this.getTreeView().refresh();
                    }

                });


//                label.prefHeightProperty().bind(checkBox.heightProperty());
                //cellBox.getChildren().addAll(checkBox,label);

                setGraphic(checkBox);
                setText(null);

            } else if(isGroupNameNode()) {


                HBox cellBox = new HBox(4);
                cellBox.setAlignment(Pos.CENTER_LEFT);
                Label label = new Label(item);
                this.setStyle("-fx-font-size:"+resX*15/1280+";-fx-font-weight: bold;-fx-text-fill:#5e5c5c");
                Node plusIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.PLUS_CIRCLE).size(Double.toString(resX/95)).styleClass("groupPlusIcon").build();
                StackPane iconPane=new StackPane();
                iconPane.getChildren().add(plusIcon);

                this.setId(this.getTreeItem().getValue());
                iconPane.setOnMouseReleased(t->{
                    GroupsController.addToGroup(this.getTreeItem().getValue());
                    System.out.println(this.getTreeItem().getValue());
                    this.getTreeView().refresh();
                });


                //Node d=this.getTreeItem().getGraphic().lookup("habeby");


                cellBox.getChildren().addAll(label,iconPane);

                setGraphic(cellBox);
                setText(null);
            }else{ //range node

                HBox cellBox = new HBox(resX/400);
                cellBox.setAlignment(Pos.CENTER_LEFT);
                String min=this.getTreeItem().getChildren().get(GroupsController.getFirstPossible(this.getTreeItem().getParent().getValue())).getValue();
                String max=this.getTreeItem().getChildren().get(GroupsController.getLastPossible(this.getTreeItem().getParent().getValue())).getValue();
                Label minLabel=new Label(min);
                Label maxLabel=new Label(max);
                minLabel.setPadding(new Insets(0,0,0,0));
                maxLabel.setPadding(new Insets(0,0,0,0));
                Node rightArrowIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.ARROW_RIGHT).size(Double.toString(resX/111)).styleClass("error").build();
                this.selectedProperty().addListener((observable,oldValue,newValue)->{
                    if(newValue)
                        rightArrowIcon.setStyle("-fx-fill:white");
                    else
                        rightArrowIcon.setStyle("-fx-fill:#07446b");
                });
                rightArrowIcon.setStyle("-fx-fill:#07446b");

                this.setStyle("-fx-font-size:"+resX*14/1280);
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