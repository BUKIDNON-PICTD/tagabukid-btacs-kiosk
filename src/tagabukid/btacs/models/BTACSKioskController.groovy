import com.rameses.osiris2.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.util.*;
import java.text.*;
            
class BTACSKioskController{
    @Binding
    def binding;
        
    @Service("BTACSKioskService")
    def svc
    
    @Service("DateService")
    def dtsvc
    
    @FormTitle
    def title
    
    def items = [];
    def dtr;
    def homeicon = 'home/icons/folder.png'; 
   
    
    def listModel = [
        fetchList: {items},
        onOpenItem: { o ->
            if(o.txntype == "END"){
                dtr.Supervisor = o.x
                return viewDTR()
            }else{
                processTransaction(o)
            }
           
        }
    ] as TileViewModel;
            
    void init(){
        dtr = [:]
//        dtr.txntype = "INIT"
//        items = [
//            [caption:"Month",icon:homeicon,txntype:"M",index:"1"],
//            [caption:"First Half",icon:homeicon,txntype:"FH",index:"2"],
//            [caption:"Second Half",icon:homeicon,txntype:"SH",index:"3"],
//            [caption:"Select Date",icon:homeicon,txntype:"SD",index:"4"]
//        ];
//        title = "Select DTR Type"
        getMonthList()
    }
    def back(){
        switch (dtr.back) {
        case ["MONTHSELECT"]:
            init()
            dtr.back = "MAIN"
            binding.refresh('listModel');
            break
        case "OFFICESELECT":
            dtr.back = "MONTHSELECT"
            getOffice()
            break
        case "EMPSELECT":
            dtr.back = "OFFICESELECT"
            getEmployessFromOffice(dtr.DEPARTMENT_ID)
            break
        default:
            return '_close'
            break
        }   
    }
    
    def getMonthList(){
        items = dtsvc.getMonths().each{
            it.caption = it.caption;
            it.icon = homeicon;
            it.txntype = "MONTHSELECT";
        }
        title = "Select Month" 
//        binding.refresh('listModel');
    }
        
    void getOffice(){
        items = svc.GetOfficeList().each{
            it.caption = it.DEPTNAME;
            it.icon = homeicon;
            it.txntype = "OFFICE";
        }
        title = "Select Office for DTR Type : " + dtr.SelectedDTRType
        binding.refresh('listModel');
    }
        
    void getEmployessFromOffice(param){
        items = svc.GetEmployeeList(param).each{
            it.caption = it.NAME;
            it.icon = homeicon;
            it.txntype = "SIG";
        }
        title = "Select employees from " + dtr.SelectedOffice
        binding.refresh('listModel');
    }
        
    void getSignatory(){
        items = svc.GetSignatoryList().each{
            it.caption = it.NAME;
            it.icon = homeicon;
            it.txntype = "END";
        }
        title = "Select signatory for " + dtr.SelectedEmp
        binding.refresh('listModel');
    }
    
    void getType(){
         items = [
            [caption:"Month",icon:homeicon,txntype:"XM",index:"1"],
            [caption:"First Half",icon:homeicon,txntype:"XFH",index:"2"],
            [caption:"Second Half",icon:homeicon,txntype:"XSH",index:"3"]
        ];
        title = "Select DTR Type"
        binding.refresh('listModel');
    }
        
    void processTransaction(item){
        switch (item.txntype) {
        case "M":
            dtr.DTR_MONTH = dtsvc.parseCurrentDate().month.toString()
            dtr.DTR_YEAR = dtsvc.parseCurrentDate().year.toString()
            dtr.Type = "1"
            dtr.SelectedDTRType = "Monthly"
            dtr.txntype = item.txntype
            dtr.back = "MONTHSELECT"
            getOffice()
            break
        case "FH":
            dtr.DTR_MONTH = dtsvc.parseCurrentDate().month.toString()
            dtr.DTR_YEAR = dtsvc.parseCurrentDate().year.toString()
            dtr.Type = "2"
            dtr.SelectedDTRType = "First Half"
            dtr.txntype = item.txntype
            dtr.back = "MONTHSELECT"
            getOffice()
            break
        case "SH":
            dtr.DTR_MONTH = dtsvc.parseCurrentDate().month.toString()
            dtr.DTR_YEAR = dtsvc.parseCurrentDate().year.toString()
            dtr.Type = "3"
            dtr.SelectedDTRType = "Second Half"
            dtr.txntype = item.txntype
            dtr.back = "MONTHSELECT"
            getOffice()
            break
        case "SD":
            getMonthList()
            break
        case "MONTHSELECT":
            dtr.DTR_MONTH = item.index.toString()
            dtr.DTR_YEAR = dtsvc.parseCurrentDate().year.toString()
            dtr.Type = "1"
            dtr.SelectedDTRType = item.caption
            dtr.txntype = item.txntype
            dtr.back = "MONTHSELECT"
            getType()
            break
        case "XM":
            dtr.Type = "1"
            dtr.SelectedDTRType = "Monthly"
            dtr.txntype = item.txntype
            dtr.back = "MONTHSELECT"
            getOffice()
            break
        case "XFH":
            dtr.Type = "2"
            dtr.SelectedDTRType = "First Half"
            dtr.txntype = item.txntype
            dtr.back = "MONTHSELECT"
            getOffice()
            break
        case "XSH":
            dtr.Type = "3"
            dtr.SelectedDTRType = "Second Half"
            dtr.txntype = item.txntype
            dtr.back = "MONTHSELECT"
            getOffice()
            break
        case "OFFICE":
            dtr.DEPARTMENT_ID = item.DEPTID.toString()
            dtr.txntype = item.txntype
            dtr.back = "OFFICESELECT"
            dtr.SelectedOffice = item.caption
            getEmployessFromOffice(item.DEPTID)
            break
        case "SIG":
            dtr.UserId = item.BADGENUMBER.toString()
            dtr.txntype = item.txntype
            dtr.Name = item.NAME
            dtr.back = "EMPSELECT"
            dtr.SelectedEmp = item.caption
            getSignatory()
            break
            default:
            MsgBox.alert(txntype)
            break
        }    
       
    }
    def viewDTR()
    {
        return InvokerUtil.lookupOpener('attachmentpdf:view', [
                entity : dtr,
            ]); 
    }
    }