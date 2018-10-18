package Jowil.Reports;

import Jowil.*;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class Report {


    public final static String [] reportsTitles={"Grades Distribution Report","Test Summary Report",
            "Test Statistics Report","Students Grades Report","Questions Analysis Report","Groups Insights Report",
    "Questions Insights Report","Test Difficulty Report" , "Questionnaire Report"};

    protected String templatePath ;
    protected  String resourcesPath;
    protected  String reportsPath;
//    protected final String ReportsPath = "\\reports" ;
//    URLDecoder.decode(getClass().getResource("/GradeConfigs").getFile(),"utf-8")
    protected static String [] outputFormatsFolderPaths;  //same order as ReportsHandler formats Constants
    protected static String outPath;
    protected String workSpacePath;
    protected String outputFileName ;
    protected String pdfHtmlPath ;


    protected String reportTitle ;

    public final static int REPORTS_COUNT=8;


    volatile boolean arabicTextReady = false ;
    protected Map<String,String> defaultGradesTranslation ;

    Report() {
        try {
            resourcesPath= ".\\src\\main\\resources\\" ;
//                    URLDecoder.decode(Report.class.getResource("../../").getPath(),"utf-8");
//            resourcesPath= resourcesPath.substring(0 , resourcesPath.length()-8) ;
//            System.out.println("fuck this fucken shit"+resourcesPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        reportsPath=  resourcesPath+ "reports\\";
        init();
        int reportIndex=Integer.parseInt(this.getClass().getSimpleName().substring("Report".length()))-1;
        this.reportTitle=reportsTitles[reportIndex];
        this.outputFileName=this.getClass().getSimpleName()+" - "+this.reportTitle;
        initTranslationMap();
    }

    private void initTranslationMap() {
        defaultGradesTranslation = new HashMap<>();
        defaultGradesTranslation.put("ضعيفجدا" , "Very Weak") ;
        defaultGradesTranslation.put("ضعيف" , "Weak") ;
        defaultGradesTranslation.put("مقبول" , "Acceptable") ;
        defaultGradesTranslation.put("جيد" , "Good") ;
        defaultGradesTranslation.put("جيدجدا" , "Very Good") ;
        defaultGradesTranslation.put("امتياز" , "Excellent") ;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public static void initOutputFolderPaths(String outPath){

        outputFormatsFolderPaths=new String[ReportsHandler.FORMATS_NAMES.length];
        Report.outPath=outPath;

        for(int i=0;i<outputFormatsFolderPaths.length;i++)
            outputFormatsFolderPaths[i]=outPath+"\\"+ReportsHandler.FORMATS_NAMES[i]+" Reports\\";

    }

    public void updateTemplateFooter(Document doc) {

        String reportTitle = doc.select("div#footerCenter").text() ;
        String projectName= Controller.getProjectName();
        String centerFooter = reportTitle + "... " + projectName ;
        doc.select("div#footerCenter").last().text(centerFooter) ;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println();
        doc.select("div#footerLeft").last().text(dtf.format(now));
    }


    protected void generatePDF(String inputHtmlPath, String outputPdfPath) throws IOException, com.lowagie.text.DocumentException {

        String url = new File(inputHtmlPath).toURI().toURL().toString();
//        System.out.println("URL: " + url);



        OutputStream out = new FileOutputStream(outputPdfPath);



        //Flying Saucer part
        ITextRenderer renderer = new ITextRenderer(ReportsHandler.isTestMode);

        renderer.getFontResolver().addFont(resourcesPath+"font\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.setDocument(url);
        renderer.layout();
        renderer.createPDF(out);

        out.close();

//        PdfReader pdfReader = new PdfReader(outputPdfPath);
//        pdfReader.selectPages("1");
//
//        PdfStamper pdfStamper = new PdfStamper(pdfReader,
//                new FileOutputStream(outputPdfPath));
//
//        pdfStamper.close();


    }

    protected void writeHtmlFile(String filePath  , Document doc) throws IOException {
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        out.write(doc.outerHtml());
        out.close();
    }

    protected void styleTitlePrintable (Document doc) {
        String addedDivStyle = "background-color: white;\n" +
                "color: black;\n" +
                "border: 2px solid #08436b;";
        // change div title
        doc.select("div.divTitle").attr("style" , addedDivStyle) ;

    }

    protected Map<String , String > parseCellData(String cellDataString) {
        Map<String , String > cellDataMap = new HashMap<String , String>() ;

        Pattern pattern = Pattern.compile("#(.+)");
        Matcher matcher = pattern.matcher(cellDataString);
        if (matcher.find())
            cellDataMap.put("attributes" , matcher.group(1)) ;
        else
            cellDataMap.put("attributes","") ;

        pattern = Pattern.compile(";([^#]+)#?");
        matcher = pattern.matcher(cellDataString);
        if (matcher.find())
            cellDataMap.put("class" , matcher.group(1)) ;
        else
            cellDataMap.put("class" , "") ;


        pattern = Pattern.compile("([^;#]+)[#|;]?");
        matcher = pattern.matcher(cellDataString);
        if (matcher.find())
            cellDataMap.put("data" , matcher.group(1)) ;
        else
            cellDataMap.put("data" , "") ;

        return cellDataMap ;
    }

    /**
     *
     * @param tableData 2D ArrayList<String>  data in each cell ... Convention each data in the 2D list shold be in the form
     *                  : data;classes#attributes  example:  "20;red header#colspan='2'"
     * @param rowClasses String containing class to be added to table rows ... Convention:  All Rows Class; on Off Class; last Row Class
     *                   examples:  red; grayRow ; underLine if you only want to apply one type of classes you can do
     *                   red OR ;grayRow  OR ;;underLine
     * @param commonDataClass class that will be applied to each data cell
     * @return String containg the html of the table
     */
    protected String createRowsHtml(ArrayList<ArrayList<String>> tableData , String rowClasses , String commonDataClass){
        String tableHtml = ""  ;

        String[] rowClassesArray = rowClasses.split(";") ;
        String allRowsClass =rowClassesArray.length>0?rowClassesArray[0] +" ":"";
        String onOffRowClass = rowClassesArray.length>1?rowClassesArray[1] +" ":"";
        String lastRowClass = rowClassesArray.length>2?rowClassesArray[2] +" ":"";


        for(int i  = 0 ; i < tableData.size(); i ++ ) {
            String thisRowClasses = "" ;
            thisRowClasses += allRowsClass ;
            thisRowClasses += i%2==1?onOffRowClass:""  ;
            thisRowClasses += i==tableData.size()-1? lastRowClass:"" ;

            tableHtml += "<tr class= '"+ thisRowClasses +"' > \n" ;
            ArrayList<String> tableRow = tableData.get(i);
            for(int cellIndex = 0 ; cellIndex <tableData.get(0).size() ; cellIndex ++ ) {
                Map<String , String > cellData = parseCellData(tableRow.get(cellIndex));
                tableHtml += "   <td "+ cellData.get("attributes")+" class='" + commonDataClass + " " +cellData.get("class")+"'>"
                        +cellData.get("data") + "</td> \n";
            }
            tableHtml+= "</tr>" ;
        }
        return tableHtml ;
    }

    public static String getOutPath(){
        return outPath;
    }

    /**
     * remove classes and atrributes (for html docs) from table
     * @param table table to be cleaned
     * @return the cleaned version of the table
     */
    protected ArrayList<ArrayList<String>> cleanTable (ArrayList<ArrayList<String>> table) {
        ArrayList<ArrayList<String>> cleanedTable = Utils.cloneTable(table);
        for (int rowIndex = 0; rowIndex < cleanedTable.size(); rowIndex++) {
            ArrayList<String> tableRow = cleanedTable.get(rowIndex);
            for (int colIndex = 0; colIndex < tableRow.size(); colIndex++) {
                String data = tableRow.get(colIndex) ;
                if(data.contains(";")){
                    tableRow.set(colIndex, data.split(";")[0]) ;
                }
            }
        }
        return cleanedTable ;
    }

    protected void changeImgPath (Document doc , String imagesFullPath) {
        Elements images =  doc.select("img") ;
        for (Element image: images) {
            String imgName = image.attr("src");
            image.attr("src" , "file://"+imagesFullPath + imgName) ;
        }
    }




    protected void handleArabicPdf(ArrayList<ArrayList<String>> table , int arabicColIndex ) throws IOException {
        System.out.println("handling Arabic Pdf");
        if(Utils.checkListContainArabic(Statistics.getGrades())){ // check if any grade is arabic
            if(!arabicTextReady) {
                    generateGradesTextImgs();
                    while (!arabicTextReady) ; // wait for the imgs to be created
            }
            for (int i = 0; i < table.size(); i++) {  // replace each grade in the table with it's img
                    ArrayList<String> tableRow = table.get(i);
                    String tableGrade = tableRow.get(arabicColIndex).replace(" " , "%20");;
                    tableRow.set(arabicColIndex, "<img class='text-img'  src='" + tableGrade + ".png'> </img>");
            }
       }
    }

    // this function tries to translate the given col and returns the translated table
    // if it couldn't translate any one of its entries it returns the original table
    protected ArrayList<ArrayList<String>> translateTableCol(ArrayList<ArrayList<String>> table , int colIndex) {
        ArrayList<ArrayList<String>> translatedTable = Utils.cloneTable(table) ;
        for(int rowIndex = 0 ; rowIndex<translatedTable.size(); rowIndex++) {
            String tableCellText = translatedTable.get(rowIndex).get(colIndex).replace(" " , "");
            if(defaultGradesTranslation.containsKey(tableCellText))
                translatedTable.get(rowIndex).set(colIndex , defaultGradesTranslation.get(tableCellText)) ;
        }
        boolean allColTranslated = !Utils.checkListContainArabic(Utils.transposeStringList(translatedTable).get(colIndex));
        if(allColTranslated)
            return translatedTable;
        else
            return table ;
    }

    protected void generateGradesTextImgs () throws IOException {
        ArrayList<String> grades = Statistics.getGrades();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                Stage stage = new Stage() ;
                Pane pane = new Pane() ;
                pane.setStyle("-fx-background-color:white");
                Scene scene  = new Scene(pane, Color.WHITE);
                stage.setScene(scene);
                Label label = new Label("man");
                pane.getChildren().add(label);
                for (int i =0 ; i < grades.size(); i ++) {
                    Label label2 = new Label(grades.get(i));
                    label2.setStyle("-fx-font-weight: bold");
                    pane.getChildren().set(0, label2);

//        scene.getStylesheets().add("reports/report1/style.css");

                    WritableImage snapShot = label2.snapshot(new SnapshotParameters(), null);
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", new File(workSpacePath + grades.get(i)+".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                arabicTextReady = true ;
            }
        });


    }

    abstract  public  void generateHtmlReport() throws IOException;
    abstract public void generatePdfReport() throws IOException, DocumentException;
    abstract public void generateTxtReport() ;
    abstract public void generatePrintablePdfReport() throws IOException, DocumentException;
    abstract public void generateCsvReport() throws IOException;
    abstract public void generateTsvReprot() ;
    abstract public void generateWordReport() throws FileNotFoundException, IOException, InvalidFormatException;
    abstract public void generateXlsReport() throws IOException;
    abstract public void init(); // function to get the abrobriate statistics from the statistics class


}
