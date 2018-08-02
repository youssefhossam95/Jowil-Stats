package Jowil.Reports;

import com.lowagie.text.DocumentException;
import org.jsoup.nodes.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class Report {

    protected String templatePath ;
    protected final String reportsPath=  ".\\src\\main\\resources\\reports\\";
    protected final String outputPdfFolderPath  = ".\\src\\main\\resources\\pdfReports\\" ;
    protected final String outputHtmlFolderPath =".\\src\\main\\resources\\htmlReports\\" ;
    protected String workSpacePath;
    protected String outputFileName ;
    protected String pdfHtmlPath ;

    public void updateTemplateDate(Document doc) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println();
            doc.select("div#footerLeft").last().text(dtf.format(now));
    }


    protected void generatePDF(String inputHtmlPath, String outputPdfPath) throws IOException, com.lowagie.text.DocumentException {

        String url = new File(inputHtmlPath).toURI().toURL().toString();
        System.out.println("URL: " + url);

        OutputStream out = new FileOutputStream(outputPdfPath);

        //Flying Saucer part
        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocument(url);
        renderer.layout();
        renderer.createPDF(out);

        out.close();

    }

    protected void writeHtmlFile(String filePath  , Document doc) throws IOException {
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        out.write(doc.outerHtml());
        out.close();
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


    abstract  public  void generateHtmlReport() throws IOException;
    abstract public void generatePdfReport() throws IOException, DocumentException;
    abstract public void generateTxtReport() ;


}