package Jowil;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pdfsam.ui.FillProgressIndicator;
import org.pdfsam.ui.RingProgressIndicator;

import java.io.IOException;

public class ReportProgressController {

    Scene scene;
    Stage stage;

    volatile static SimpleIntegerProperty progressCount=new SimpleIntegerProperty();
    int reportsCount;
    double resX=Controller.resX;
    double resY=Controller.resY;

    double rootWidth=resX/2;
    double rootHeight=resY/2;

    private static volatile SimpleDoubleProperty reportProgress=new SimpleDoubleProperty();

    Pane root;




    RingProgressIndicator counterIndicator;

    @FXML
    FillProgressIndicator reportProgressIndicator;

    @FXML
    VBox reportProgressVBox;

    @FXML
    VBox counterVBox;

    @FXML
    HBox mainHBox;

    @FXML
    Label titleLabel;


    ReportProgressController(int reportsCount){


        this.reportsCount=reportsCount;

        reportProgress.addListener(t -> {

            Platform.runLater(() -> {
                reportProgressIndicator.setProgress((int)(reportProgress.get()*100));
                //counterIndicator.setProgress((int)((reportProgress.get()+progressCount.get())/(double)reportsCount*100));
            });
        });

        progressCount.addListener(t -> {

            int progressValue = (int) ((double) progressCount.get() / reportsCount * 100);
            Platform.runLater(() -> {
                counterIndicator.setProgress(progressValue);
            });
        });

    }

    public void initialize() {



        mainHBox.setLayoutX(rootWidth*0.18);
        mainHBox.setLayoutY(rootHeight*0.25);
        mainHBox.setSpacing(resX/20);

        counterIndicator=new RingProgressIndicator(reportsCount);

        counterVBox.getChildren().add(counterIndicator);

        counterIndicator.setStyle("-fx-background-color:transparent");

        titleLabel.setLayoutY((int)(resY/18));
        titleLabel.setLayoutX((int)(resX/70));


    }

    public void startWindow(){
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/reportProgress.fxml"));
            stage = new Stage();
            loader.setController(this);
            root= loader.load();
            scene = new Scene(root,rootWidth,rootHeight);
            stage.setTitle("Reports Generation Progress");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCounterIndicator(RingProgressIndicator counterIndicator) {
        this.counterIndicator = counterIndicator;
    }

    public void setReportProgressIndicator(FillProgressIndicator reportProgressIndicator) {
        this.reportProgressIndicator = reportProgressIndicator;
    }

    public static void incrementProgressCount(){
        progressCount.setValue(progressCount.get()+1);
    }


    public static void setReportProgress(double reportProgress) {
        ReportProgressController.reportProgress.set(reportProgress);
    }

}
