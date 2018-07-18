package Jowil;

import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
//import org.jfree.io.FileUtilities;

import org.xhtmlrenderer.pdf.ITextRenderer;
import sun.plugin.dom.core.Element;

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
    private final String reportsPath=  "E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources\\reports\\";
    private final String gradeDistTemplatePath = reportsPath + "gradesDistributionReport\\gradesDistribution.html";
    private final String condensedTestTemplatePath = reportsPath+"condensedTestReport\\condensedTestTemplate.html";

    private final String report1TemplatePath = reportsPath + "report1\\report1Template.html";
    private final String report2TemplatePath = reportsPath + "report2\\report2Template.html";
    private final String report3TemplatePath = reportsPath + "report3\\report3Template.html";
    private final String report4TemplatePath = reportsPath + "report4\\report4Template.html";




    private  Map<String , String > parseCellData(String cellDataString) {
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

   private String createRowsHtml(ArrayList<ArrayList<String>> tableData , String rowClass , String commonDataClass){
        String tableHtml = ""  ;
        for(int i  = 0 ; i < tableData.size(); i ++ ) {
            tableHtml += "<tr class= '"+ rowClass +"' > \n" ;
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
//        String tableRows = createRowsHtml(tableDataAdapter(), "", "tg-l711");
//        doc.select("tr").last().after(tableRows);
//        doc.select("img").attr("src" , gradeDistHistogramChartPaht);

        writeHtmlFile(reportsPath + "gradesDistributionReport\\test.html", doc);
        generatePDF(reportsPath + "gradesDistributionReport\\test.html", reportsPath + "gradesDistributionReport\\test.pdf");
    }

    public  void  generateReport1() throws IOException, DocumentException {

        File file = new File(report1TemplatePath);
        Document doc = Jsoup.parse(file, "UTF-8");

        ArrayList<ArrayList<String>> statsTable = Statistics.report1Stats() ;

        String tableRowsHtml = createRowsHtml(statsTable , " " , "tg-l711") ;

        doc.select("tr.headerRow").after(tableRowsHtml) ;



        writeHtmlFile(reportsPath+"report1\\test.html" , doc);
        generatePDF(reportsPath + "report1\\test.html", reportsPath + "report1\\test.pdf");

    }

    private void fillGeneralStatsReport2 (Document doc , Map<String , String> generalStatsMap) {
        doc.select("td.NumberOfStudents").first().text(generalStatsMap.get("Number Of Students")) ;
        doc.select("td.MaxPossibleScore").first().text(generalStatsMap.get("Maximum Possible Score")) ;

        //Basic Statistics
        doc.select("td.Mean").first().text(generalStatsMap.get("Mean")) ;
        doc.select("td.HighestScore").first().text(generalStatsMap.get("Highest Score")) ;
        doc.select("td.LowestScore").first().text(generalStatsMap.get("Lowest Score")) ;
        //Dispersion
        doc.select("td.StandardDeviation").first().text(generalStatsMap.get("Standard Deviation")) ;
        doc.select("td.ScoreRange").first().text(generalStatsMap.get("Range")) ;
        doc.select("td.Median").first().text(generalStatsMap.get("Median")) ;


        //Test Reliability
        doc.select("td.Kuder-RichardsonFormula20").first().text(generalStatsMap.get("Kuder-Richardson Formula 20")) ;

    }

    public void  generateReport2() throws IOException, DocumentException {
        File file = new File(report2TemplatePath);
        Document doc = Jsoup.parse(file, "UTF-8");

        fillGeneralStatsReport2(doc, Statistics.report2GeneralStats(0));


        String tableHtml = doc.select("table.t2").last().outerHtml() ;
//        doc.select("table.t2").remove() ;
        ArrayList<ArrayList<ArrayList<String>>> statsTables = Statistics.report2TableStats(0) ;
        int questionIndex = 0 ;
        for(ArrayList<ArrayList<String>> table : statsTables) {
            //create new table
            if(questionIndex!=0)
                doc.select("table").last().after(tableHtml) ;
            String questionChoicesHtml = "" ;
            ArrayList<String> questionChoices =  Statistics.getSpecificQuestionChoices(questionIndex) ;
            for(String qChoice: questionChoices )
                questionChoicesHtml+= "<th>" +qChoice+ "</th>\n";
            doc.select("th.total").last().before(questionChoicesHtml);
            doc.select("th.responseFreq").last().attr("colspan" , String.valueOf(questionChoices.size())) ;

            String rowsHtml = createRowsHtml(table ,"" , "") ;
            doc.select("tr.bottom-header-row").last().after(rowsHtml);
            questionIndex += table.size() ;
//            break;
        }
//
//        String generalStatsHtml = doc.select("table.t").last().outerHtml() ;
//        System.out.println(generalStatsHtml);

//        doc.select("td.numberOfStudents").last().text("hi");

//        doc.select("table.t2").last().after(generalStatsHtml) ;
//        doc.select("td.numberOfStudents").last().text("man");

//        String answerNamesHtml = "<th>A</th>\n" +
//                "            <th>B</th>\n" +
//                "            <th>C</th>\n" +
//                "            <th>D</th>\n" +
//                "            <th>E</th>" ;
//        doc.select("th.total").last().before(answerNamesHtml) ;
//        System.out.println(doc.outerHtml());
        writeHtmlFile(reportsPath+"report2\\test.html" , doc);
        generatePDF(reportsPath+"report2\\test.html" , reportsPath + "report2\\test.pdf");

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

   private  ArrayList<ArrayList<String>> generateFakeTable(int numberOfRows , int numberOfCols) {
       ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
       for (int i = 0 ; i< numberOfRows ; i++) {
           ArrayList<String>outRow = new ArrayList<String>() ;
           for (int j = 0 ; j < numberOfCols ; j ++)
               outRow.add("R"+(i+1)+" C"+(j+1)) ;
           out.add(outRow) ;
       }
       return out ;
   }

   public void generateReport4() throws IOException, DocumentException {

       final String dataCellCommonClass = "tg-l711" ;

       File file = new File(report4TemplatePath);

       Document doc =  Jsoup.parse(file , "UTF-8") ;

       String headerHtml = doc.select("tr.headerRow").outerHtml();


       // get the table data from statistics class
       ArrayList<ArrayList<String>> statsTable = Statistics.report4Stats() ;

//       to test large number of rows
//       ArrayList<ArrayList<String>> statsTable = generateFakeTable(1000 , 4) ;

       // separate the maean row (last row)
       ArrayList<ArrayList<String>> meanRow =new ArrayList<ArrayList<String>> ( statsTable.subList(statsTable.size()-1 , statsTable.size()));

       // adding colspan attribute to first element in each row
       for(ArrayList<String> tableRow:statsTable) {
           tableRow.set(0,tableRow.get(0) + "#colspan='2'" ) ;
       }

       int startIndex = 0 ;
       int endIndex = (int)Utils.getNumberWithinLimits(statsTable.size() , 0 , 21) ;

       do  {
           ArrayList<ArrayList<String>> pageTable ;
           if(endIndex == statsTable.size()) {
               pageTable = new ArrayList<ArrayList<String>>(statsTable.subList(startIndex, endIndex - 1));
               String rowsHtml = createRowsHtml(pageTable , "" ,dataCellCommonClass );
               doc.select("tr.headerRow").last().after(rowsHtml) ;
           }
           else {
               pageTable = new ArrayList<ArrayList<String>>(statsTable.subList(startIndex, endIndex));
               String rowsHtml = createRowsHtml(pageTable , "" ,dataCellCommonClass );
               doc.select("tr.headerRow").last().after(rowsHtml + headerHtml);
           }
           startIndex = endIndex ;
           endIndex = (int)Utils.getNumberWithinLimits(endIndex+25 , 0 , statsTable.size())  ;
       }while ((endIndex != startIndex));


       String rowsHtml = createRowsHtml(meanRow , "" ,"MeanRow" );
       doc.select("tr").last().after(rowsHtml) ;

       //remove the header class from the first header so as not to put a page break before it
       doc.select("tr.headerRow").first().removeClass("headerRow") ;


       writeHtmlFile(reportsPath+"report4\\test.html" , doc);
       generatePDF(reportsPath + "report4\\test.html", reportsPath + "report4\\test.pdf");

   }
   public void createCondensedTestReport() throws IOException, DocumentException {
       File file = new File(condensedTestTemplatePath);

       Map<String , Double> MainStatistics = new HashMap<String, Double>() ;

       MainStatistics.put("mean" , 50.0) ;


       Document doc =  Jsoup.parse(file , "UTF-8") ;
       DecimalFormat format = new DecimalFormat("0.#");


       doc.select("td#numberOfStudents").first().text(format.format(MainStatistics.get("mean"))) ;
       doc.select("") ;
       writeHtmlFile("test.html" , doc);
       generatePDF(reportsPath + "report4\\test.html", reportsPath + "report4\\test.pdf");

//       System.out.println(td) ;
   }
}
