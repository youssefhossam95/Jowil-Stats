package Jowil.Reports.Utils;

import Jowil.Reports.ReportsHandler;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XlsUtils {

    public static void writeXlsFile(HSSFWorkbook workbook , String filePath) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(filePath));
        workbook.write(out);
        out.close();
    }


}
