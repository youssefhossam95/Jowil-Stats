package Jowil;

import java.io.File;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author blj0011
 */
//public class PDFRenderingTest extends Application
//{
//
//    @Override
//    public void start(Stage primaryStage)
//    {
//        File file = new File("C:\\Users\\Youssef Hossam\\Desktop\\stack.html");
//
//        WebView webView = new WebView();
//        WebEngine webEngine = webView.getEngine();
//        webEngine.load(file.toURI().toString());
//
//        StackPane root = new StackPane();
//        root.getChildren().add(webView);
//
//        Scene scene = new Scene(root, 700, 600);
//
//        primaryStage.setTitle("Hello World!");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args)
//    {
//        launch(args);
//    }
//
//}

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
//import javafx.application.Application;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.embed.swing.SwingNode;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//
//import javax.swing.*;
//
//import com.qoppa.pdf.PDFException;
//import com.qoppa.pdfNotes.PDFNotesBean;
//
//public class JavaFXNotes extends Application {
//
//    private ObjectProperty pdf = new SimpleObjectProperty&lt;&gt;();
//
//    public static void main(String[] args) {
//        Application.launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        Button btnEmbedded = new Button("Embedded");
//        btnEmbedded.disableProperty().bind(pdf.isNull());
//        btnEmbedded.setOnAction(event2 -&gt; openEmbedded());
//        Button btnJframe = new Button("JFrame");
//        btnJframe.setOnAction(event1 -&gt; openJFrame());
//        btnJframe.disableProperty().bind(pdf.isNull());
//        Button pickFile = new Button("Pick file");
//
//        pickFile.setOnAction(event -&gt; {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PDF", ".pdf"));
//            pdf.set(fileChooser.showOpenDialog(pickFile.getScene().getWindow()));
//        });
//
//        Scene primaryScene = new Scene(new VBox(pickFile, new HBox(10, btnEmbedded, btnJframe)));
//        primaryStage.setScene(primaryScene);
//        primaryStage.show();
//    }
//
//    private void openEmbedded() {
//        Stage embeddedStage = new Stage();
//        SwingNode sw = new SwingNode();
//        sw.setContent(createAndLoad());
//        Scene embeddedScene = new Scene(new StackPane(sw), 500, 500);
//        embeddedStage.setScene(embeddedScene);
//        embeddedStage.show();
//    }
//
//    private void openJFrame() {
//        JFrame jframe = new JFrame();
//        jframe.setContentPane(createAndLoad());
//        jframe.setSize(500, 500);
//        jframe.setVisible(true);
//        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//    }
//
//    public PDFNotesBean createAndLoad() {
//        long before = System.currentTimeMillis();
//        PDFNotesBean notesBean = new PDFNotesBean();
//        System.out.println("After: " + (System.currentTimeMillis() - before));
//        new Thread(() -&gt; {
//            try {
//                notesBean.loadPDF(new FileInputStream(pdf.get()));
//            } catch (PDFException | FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }).run();
//        return notesBean;
//    }
//
//}