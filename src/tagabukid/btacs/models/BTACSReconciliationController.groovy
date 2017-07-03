import com.rameses.rcp.annotations.*
import com.rameses.rcp.common.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import java.rmi.server.*;
import com.rameses.util.*;
import com.rameses.osiris2.reports.*;
import java.text.*;
import tagabukid.btacs.models.*;

public class BTACSReconciliationController {
    @Binding
    def binding;
    
    @Service("BTACSReconciliationService")
    def svc
    
    @Service("DateService")
    def dtsvc
    
    def entity;
    def mode = "INIT"
    def reconciliationitems;
    def employees;
    def title;
    @FormTitle
    def completed;
    def task;
    def loghandler = new TextWriter();
    def selectedEmployee;
    def searchText;
    def df = new SimpleDateFormat("yyyy-MM-dd");
    def formatDate = { o->
        if(o instanceof java.util.Date) {
            return df.parse( df.format( o ));
        }
        return df.parse( o );     
    }
    
    void init(){
        mode= "INIT";
        title="Select Office";
        entity = [:];
        entity.txndate = dtsvc.getServerDate();
        entity.year = dtsvc.getYear(entity.txndate);
        entity.recordlog = [
            createdbyuserid : OsirisContext.env.USERID,
            createdbyuser : OsirisContext.env.FULLNAME,
            datecreated : entity.txndate,
        ]
        entity.txnno = " ";
        employees = [];
        reconciliationitems = [];
        completed = false;
//        return 'default';
    }
    
    def createnew(){
        init();
        return "default";
    }
    
    def getLookupOffice(){
        return Inv.lookupOpener('btacsoffice:lookup',[
                onselect :{
                    entity.office = it;
                },
            ])
    }
    
    def getLeaveClass(){
        return svc.getLeaveClass().collect{it.LeaveName}
    }
    
    def getPenaltyList(){
        return svc.getPenaltyList().collect{it.name}
    }
    
    def getMonthList(){
        return dtsvc.getMonths();
    }
    def listHandler = [
        fetchList : { return selectedEmployee?.items },
        onRemoveItem : {
//            if (MsgBox.confirm('Delete item?')){                
//                document.remove(it)
//                listHandler.reload();
//                return true;
//            }
            return false;
        }
//        ,
//        onColumnUpdate:{item,colName ->
//            reconciliationitems.each{y ->
//                if (item.din == y.din){
//                    y.message = item.remarks
//                }
//            }
//        }
    ] as EditorListModel
    
    def employeeListHandler = [
        fetchList : { return employees },
        onRemoveItem : {
//            if (MsgBox.confirm('Delete item?')){                
//                document.remove(it)
//                listHandler.reload();
//                return true;
//            }
            return false;
        }
//        ,
//        onColumnUpdate:{item,colName ->
//            reconciliationitems.each{y ->
//                if (item.din == y.din){
//                    y.message = item.remarks
//                }
//            }
//        }
    ] as EditorListModel
    
    def loadreconciliationinfo(){
        mode = "PROCESS";
        title = "Reconcile blank logs"
        reconciliationitems = svc.getReconciliationItems(entity);
        
        if(reconciliationitems.size == 0)
        throw new Exception("No items for reconciliation");
        
        employees = reconciliationitems.groupBy({[userid:it.userid,name:it.name,gender:it.gender,jobtitle:it.jobtitle]}).collect{k,v->
            [
                userid:k.userid,
                name:k.name,
                gender:k.gender,
                jobtitle:k.jobtitle,
                items:v
            ]
        }
       
        
        employeeListHandler.reload();
        listHandler.reload();
        return "process";
    }
    
    def save(){
        if(!reconciliationitems.find{it.approved == true})
        throw new Exception("No approved item(s) for reconciliation.");
        
        reconciliationitems.each{
            svc.verifyitem(entity,it)
        }
        
        if( MsgBox.confirm( "You are about to reconcile this items. Proceed?")) {
            reconciliationitems = reconciliationitems.findAll{it.approved == true};
            task = new OnlineTransactionTask();
            task.txnModel = this;
            task.txnSvc   = svc; 
            task.entity   = entity;
            task.reconciliationitems = reconciliationitems;
            task.oncomplete  = oncomplete;
            task.onerror     = onerror;
            task.loghandler = loghandler;
            Thread t = new Thread(task);
            t.start();
            mode = 'processing';
            return "completed";
        }
        
    }
    
    def showinfo = {msg ->
        loghandler.writeln(msg);
        binding?.refresh('.*');
    }
    
    def oncomplete = {msg ->
        completed = true;
        mode = 'completed';
        task = null;
        loghandler.writeln(msg);
        binding?.refresh('.*');
        MsgBox.alert("Transaction Successfull");
    }    
    
    def onerror = {msg ->
        task = null;
        
        loghandler.writeln(msg);
        mode = "PROCESS"
        binding?.refresh('.*');
        MsgBox.alert("msg");
        return "process"
      
    }   
    
    void searchEmployee(){
        if(searchText){
            employees = employees.findAll{it.name.contains(searchText)}
//            println searchText
            
        }else{
            employees = reconciliationitems.groupBy({[userid:it.userid,name:it.name,gender:it.gender,jobtitle:it.jobtitle]}).collect{k,v->
            [
                userid:k.userid,
                name:k.name,
                gender:k.gender,
                jobtitle:k.jobtitle,
                items:v
            ]
            }
        }
        employeeListHandler.reload();
        
    }
    
    def close(){
        return '_close'
    }
}