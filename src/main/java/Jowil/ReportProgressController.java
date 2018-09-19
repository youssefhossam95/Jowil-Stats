package Jowil;

import Jowil.Reports.*;
import com.lowagie.text.DocumentException;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.pdfsam.ui.FillProgressIndicator;
import org.pdfsam.ui.RingProgressIndicator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ReportProgressController {

    private final ArrayList<Boolean> isGenerateReports;
    Scene scene;
    Stage stage;

    volatile static SimpleIntegerProperty progressCount;
    int reportsCount;
    double resX=Controller.resX;
    double resY=Controller.resY;
    Thread th;
    double rootWidth=1280/2;
    double rootHeight=680/2;

    private static volatile SimpleDoubleProperty reportProgress;

    Pane root;

    ArrayList<Integer> formatsOut;




    RingProgressIndicator counterIndicator;

    @FXML
    FillProgressIndicator reportProgressIndicator;


    @FXML
    VBox counterVBox;

    @FXML
    HBox mainHBox;

    @FXML
    Label titleLabel;


    ReportProgressController(int reportsCount,ArrayList<Boolean> isGenerateReports,ArrayList<Integer>formatsOut){

        reportProgress=new SimpleDoubleProperty();
        progressCount=new SimpleIntegerProperty();

        this.reportsCount=reportsCount;
        this.formatsOut=formatsOut;
        this.isGenerateReports=isGenerateReports;



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

            if(progressValue==100) { //all reports finished -> animate finish and open explorer
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> showFolderInExplorer());
            }

        });

    }


    public void initialize() {



        mainHBox.setLayoutX((int)(rootWidth*0.18));
        mainHBox.setLayoutY((int)(rootHeight*0.25));
        mainHBox.setSpacing((int)(1280/20));

        counterIndicator=new RingProgressIndicator(reportsCount);

        counterVBox.getChildren().add(counterIndicator);

        counterIndicator.setStyle("-fx-background-color:transparent");

        titleLabel.setLayoutY((int)(680/18));
        titleLabel.setLayoutX((int)(1280/70));

        stage.setOnCloseRequest(event->{
            boolean isQuit=Controller.showConfirmationDialog("Quit Reports Generation","Are you sure you want to quit reports generation?",stage.getOwner());
            if(isQuit)
                th.interrupt();
            else
                event.consume();
        });


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



        generateReports();

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


    private void generateReports(){

        Runnable task =()->{

            ArrayList<Report> reportsOut=new ArrayList<>();

            for(int i=0;i<isGenerateReports.size();i++){
                if(isGenerateReports.get(i))
                    reportsOut.add(getReport(i+1));
            }


            ReportsHandler reportsHandler = new ReportsHandler();

            try {
                reportsHandler.generateReports(reportsOut, formatsOut);
            } catch (IOException e) {
                showReportsErrorMessage(" Make sure that the file "+"\""+reportsOut.get(progressCount.get()).getOutputFileName()+".pdf\" is not opened in another application");


            } catch (DocumentException e) {
                showReportsErrorMessage("");
                e.printStackTrace();
            }
            catch(RuntimeException e) {
                if(!Thread.currentThread().isInterrupted()) //not caused by interruption
                    showReportsErrorMessage("");
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }

        };



        th = new Thread(task);
        th.setDaemon(false);
        th.start();

    }

    private void deleteLastReport(){
//        String fileName = Report.getPDFPath() + "Report" + (progressCount.get() + 1) + ".pdf";
//        System.out.println(fileName);
//        System.out.println("Delete: " + new File(fileName).delete());
    }

    private void showReportsErrorMessage(String extraStatement){

        Platform.runLater(()->{

        Alert alert = new Alert(Alert.AlertType.ERROR);
        //alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());


        alert.getButtonTypes().add(ButtonType.CLOSE);
        Button closeButt=(Button)alert.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButt.setText("Close");

        Button yessButt=(Button)alert.getDialogPane().lookupButton(ButtonType.OK);
        yessButt.setText("Retry");

        alert.setTitle("Reports Generation Error");
        alert.setHeaderText(null);
        alert.setContentText("An error has occurred during report generation."+extraStatement);
        alert.initOwner(stage.getOwner());
        Optional<ButtonType> option = alert.showAndWait();

        stage.hide();

        if(option.get()==ButtonType.OK)
            new ReportProgressController(reportsCount,isGenerateReports,formatsOut).startWindow();

        stage.close();
        });

    }

    private void showFolderInExplorer(){

        stage.hide();

        File file = new File (Report.getOutPath());
        Desktop desktop = Desktop.getDesktop();

        int i=0; //try to open 5 times at most -> if failed display error message

        while(i<5) {
            try {
                desktop.open(file);
                break;
            } catch (IOException e) {
                Controller.showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Directory Error", "Cannot open reports directory.");
                i++;
            }
            catch(IllegalArgumentException e){
                break; //check m3a william
            }
        }

        stage.close();

    }


    private Report getReport(int reportNumber){

        switch (reportNumber){
            case 1:
                return new Report1();
            case 2:
                return new Report2();
            case 3:
                return new Report3();
            case 4:
                return new Report4();
            case 5:
                return new Report5();
            case 6:
                return new Report6();
            case 7:
                return new Report7();
            case 8:
                return new Report8();
        }
        return null;
    }


}
