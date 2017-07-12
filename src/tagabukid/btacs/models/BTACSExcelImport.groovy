
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class BTACSKioskController {
    
    @Binding
    def binding;
    
    def file;
    def mode;
    
    
    String getTitle(){
        return 'Import Reconciliation Form';
    }
    
    void init(){
        mode = 'init';
        
    }
    
  
    def doNext(){
        if (!file)
            throw new Exception('File is required.');
      
        
       try {

            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                   
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        System.out.print(currentCell.getStringCellValue() + "--");
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        System.out.print(currentCell.getNumericCellValue() + "--");
                    }

                }
                System.out.println();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mode = 'read';
        return 'default';
    }
    
    void doImport(){
//        if (MsgBox.confirm('Import transmittal?')){
//            task = new TransmittalImportTask();
//            task.importModel = this;
//            task.importSvc   = importSvc; 
//            task.file        = file;
//            task.updateItem  = updateItem;
//            task.oncomplete  = oncomplete;
//            task.onerror     = onerror;
//            Thread t = new Thread(task);
//            t.start();
//            mode = 'processing'
//        }
    }
    
    def updateItem = {updateditem ->
        def item = entity.items.find{it.objid == updateditem.objid}
        item.putAll(updateditem);
        listHandler.reload();
    }
    
    def oncomplete = {
        mode = 'completed'
        task = null;
        binding.refresh();
        MsgBox.alert('Transmittal has been successfully imported.');
    }    
    
    def onerror = {errmsg ->
        mode = 'read'
        task = null;
        binding.refresh();
        MsgBox.alert(errmsg);
    }   
   
}

