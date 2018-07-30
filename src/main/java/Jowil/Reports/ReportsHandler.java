package Jowil.Reports;

import Jowil.Statistics;
import Jowil.Utils;
import com.lowagie.text.DocumentException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
//import org.jfree.io.FileUtilities;

import org.xhtmlrenderer.pdf.ITextRenderer;
import sun.plugin.dom.core.Element;

import javax.imageio.ImageIO;
import java.io.*;
import java.lang.reflect.Array;
import java.text.AttributedCharacterIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportsHandler {

    public final static int HTML=0,PDF=1,TXT=2,WORD=3,XLS=4 ;
    private final String reportsPath=  ".\\src\\main\\resources\\reports\\";


   public  void generateReport5Chart(Stage stage , ArrayList<String> responsePercentagesWithClasses  , ArrayList<String> questionChoices ) throws IOException {

       stage.setTitle("Bar Chart Sample");
       final NumberAxis yAxis = new NumberAxis(0 , 100 ,10);
       final CategoryAxis xAxis = new CategoryAxis();
       final BarChart<String,Number> bc =
               new BarChart<String,Number>(xAxis,yAxis);
//       bc.setTitle("Country Summary");
       bc.setLegendVisible(false);
//       xAxis.setLabel("Percentage");
//       xAxis.setTickLabelRotation(0);
//       yAxis.setLabel("");
       XYChart.Series series1 = new XYChart.Series();
       for(int choiceIndex =0 ; choiceIndex<questionChoices.size() ; choiceIndex++) {
           double responsePercent = Double.valueOf(responsePercentagesWithClasses.get(choiceIndex).split(";")[0])  ;
           series1.getData().add(new XYChart.Data( questionChoices.get(choiceIndex),responsePercent));
       }
       bc.getData().add(series1);


       for(int choiceIndex =0 ; choiceIndex<questionChoices.size() ; choiceIndex++) {
           String responseClass = responsePercentagesWithClasses.get(choiceIndex).split(";")[1] ;
           Node n = bc.lookup(".data"+choiceIndex+".chart-bar");
           if(responseClass.length()<2)
               responseClass = "normal";
           System.out.println(responseClass);
           n.getStyleClass().add(responseClass);
       }

       bc.setAnimated(false);
       Scene scene  = new Scene(bc,450,350);

       scene.getStylesheets().add("reports/report5/style.css");
       bc.applyCss();
       bc.layout();
       stage.setScene(scene);
       stage.show();

       WritableImage snapShot = bc.snapshot(new SnapshotParameters() , null);
       ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", new File(reportsPath+"report5\\Q1stats.png"));

   }


}
