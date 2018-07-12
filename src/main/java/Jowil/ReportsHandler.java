package Jowil;

import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
//import org.jfree.io.FileUtilities;

import org.xhtmlrenderer.pdf.ITextRenderer;
import sun.plugin.dom.core.Element;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsHandler {

    public final static int HTML=0,PDF=1,TXT=2,WORD=3,XLS=4 ;
    private final String reportsPath=  "E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources\\reports\\";
    private final String gradeDistTemplatePath = reportsPath + "gradesDistributionReport\\gradesDistribution.html";
    private final String condensedTestTemplatePath = reportsPath+"condensedTestReport\\condensedTestTemplate.html";

    private final String report3TemplatePath = reportsPath + "report3\\report3Template.html";

   private String createRowsHtml(List<List<String>> tableData , String rowClass , String dataClass){
       String tableHtml = "<tr class= '"+ rowClass +"' > \n" ;
       for(int i  = 0 ; i < tableData.size(); i ++ ) {
           for(int j = 0 ; j <tableData.get(0).size() ; j ++ ) {
               tableHtml += "   <td class='" + dataClass + "'>" + tableData.get(i).get(j) + "</td> \n";
           }
       }
        tableHtml+= "</tr>" ;
       return tableHtml ;
   }

   private List<List<String>> tableDataAdapter(){
       List<List<String>> tableData = new ArrayList<List<String>>() ;
       List<String>row = new ArrayList<String>() ;
       row.add("F") ;
       row.add("93-100") ;
       row.add("26-28");
       row.add("5") ;
       row.add("10.5") ;
       tableData.add(row) ;
       return  tableData ;
   }


    public void generatePDF(String inputHtmlPath, String outputPdfPath) throws IOException, com.lowagie.text.DocumentException {

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

    private void writeHtmlFile(String filePath  , Document doc) throws IOException {
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        out.write(doc.outerHtml());
        out.close();
    }
    public void createGradeDist () throws IOException, DocumentException {
        System.out.println(gradeDistTemplatePath);

        File file = new File(gradeDistTemplatePath);
        Document doc = Jsoup.parse(file, "UTF-8");
        System.out.println(doc.body().html());
        String tableRows = createRowsHtml(tableDataAdapter(), "", "tg-l711");
        doc.select("tr").last().after(tableRows);
//        doc.select("img").attr("src" , gradeDistHistogramChartPaht);

        writeHtmlFile(reportsPath + "gradesDistributionReport\\test.html", doc);
        generatePDF(reportsPath + "gradesDistributionReport\\test.html", reportsPath + "gradesDistributionReport\\test.pdf");


    }
   public void generateReport3() throws IOException {
       File file = new File(report3TemplatePath);
       Document doc = Jsoup.parse(file, "UTF-8");
//       System.out.println(doc.body().html());

       Map<String, String > report3Stats = Statistics.report3Stats() ;

       System.out.println("hello " + doc.select("td#90").first().text()) ;

       doc.select("td#NumberOfGradedQuestions").first().text(report3Stats.get("Number Of Graded Questions")) ;
       doc.select("td#MaximumPossibleScore").first().text(report3Stats.get("Maximum Possible Score")) ;
       doc.select("td#Benchmark").first().text(report3Stats.get("Benchmark")) ;

       //Basic Statistics
       doc.select("td#Mean").first().text(report3Stats.get("Mean")) ;
       doc.select("td#MeanPercentScore").first().text(report3Stats.get("Mean Percent Score")) ;
       doc.select("td#HighestScore").first().text(report3Stats.get("Highest Score")) ;
       doc.select("td#LowestScore").first().text(report3Stats.get("Lowest Score")) ;
       //Dispersion
       doc.select("td#StandardDeviation").first().text(report3Stats.get("Standard Deviation")) ;
       doc.select("td#Variance").first().text(report3Stats.get("Variance")) ;
       doc.select("td#Range").first().text(report3Stats.get("Range")) ;
       doc.select("td#Median").first().text(report3Stats.get("Median")) ;
       doc.select("td#25thPercentile").first().text(report3Stats.get("25th Percentile")) ;
       doc.select("td#75thPercentile").first().text(report3Stats.get("75th Percentile")) ;
       doc.select("td#InterquartileRange").first().text(report3Stats.get("Interquartile Range")) ;

       //Confidence Intervals
       doc.select("td#90").first().text(report3Stats.get("90")) ;
       doc.select("td#95").first().text(report3Stats.get("95")) ;
       doc.select("td#99").first().text(report3Stats.get("99")) ;

       //Test Reliability
       doc.select("td#Kuder-RichardsonFormula20").first().text(report3Stats.get("Kuder-Richardson Formula 20")) ;
       doc.select("td#Kuder-RichardsonFormula21").first().text(report3Stats.get("Kuder-Richardson Formula 21")) ;

       writeHtmlFile(reportsPath + "report3\\test.html", doc);

   }

   public void generateReport4() {
       ArrayList<ArrayList<String>> statsTable = Statistics.report4Stats() ;
   }
   public void createCondensedTestReport() throws IOException {
       File file = new File(condensedTestTemplatePath);

       Map<String , Double> MainStatistics = new HashMap<String, Double>() ;

       MainStatistics.put("mean" , 50.0) ;


       Document doc =  Jsoup.parse(file , "UTF-8") ;
       DecimalFormat format = new DecimalFormat("0.#");


       doc.select("td#numberOfStudents").first().text(format.format(MainStatistics.get("mean"))) ;
       doc.select("") ;
       writeHtmlFile("test.html" , doc);
//       System.out.println(td) ;
   }
}
