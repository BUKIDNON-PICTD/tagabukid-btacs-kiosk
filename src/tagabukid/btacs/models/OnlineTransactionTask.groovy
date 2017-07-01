package tagabukid.btacs.models;

import com.rameses.rcp.annotations.* 
import com.rameses.rcp.common.* 
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import com.rameses.util.*;
import com.rameses.common.*;
import com.rameses.gov.etracs.rpt.util.*;


public class OnlineTransactionTask implements Runnable{
    def entity;
    def reconciliationitems;
//    def reader;
//    def updateItem;
    def oncomplete;
    def onerror;
    def txnSvc; 
    def txnModel;
    def loghandler;
    
    public void run(){
        loghandler.writeln('Initializing...' + reconciliationitems.size());
        loghandler.writeln('Start Processing Reconciliation ');
        loghandler.writeln('Date\t:' + entity.txndate);
        loghandler.writeln('Prepared By\t:' + entity.preparedbyname);

        txnSvc.processReconciliation(entity,reconciliationitems,processreconciliationAsyncHandler);


    }
    
      def processreconciliationAsyncHandler = [
            onError: {o-> 
               onerror(o.message); 
            }, 
            onTimeout: {
                //MsgBox.err(o.message); 
                processreconciliationAsyncHandler.retry(); 
            },
            onMessage: {o-> 
                if(o == "OK"){
                   oncomplete('Transaction was successful.');
                }
                
//                if (o == com.rameses.common.AsyncHandler.EOF) {
//                   if( has_error ){
//                         MsgBox.err(o.message); 
//                         processdocumentAsyncHandler.cancel();
//                   }
//                   else {
//                        oncomplete('Transaction is successfully imported.');
//                   }
//                } 
            } 
        ] as com.rameses.common.AbstractAsyncHandler 
//    def handler = [
//        onMessage : { msg ->
//            if (msg != AsyncHandler.EOF && msg.msgtype == '_ERROR_' ){
//                loghandler.writeln('');
//                loghandler.writeln(msg.msg);
//            }
//            else if (msg != AsyncHandler.EOF){
//                service.syncData(msg);
//                loghandler.writeln('Processing ' + msg.msgtype + ' data.');
//            }
//            else{
//                try{ caller?.search(); } catch(e){}
//                loghandler.writeln('\nTransaction is successfully imported.');
//            }
//           
//        },
//        
//        onError : { e ->
//            println '='*50 ;
//            println e.printStackTrace();
//            loghandler.writeln('\n\n');
//            loghandler.writeln('ERROR: ' + e.message);
//        }
//    ] as AsyncHandler
    
//    private void processdocument(){
//       
//        processdocumentAsyncHandler = [
//            onError: {o-> 
//                onerror(o.message);
//                has_error = true
//            }, 
//            onTimeout: {
//                processdocumentAsyncHandler.retry(); 
//            },
//            onMessage: {o-> 
//                if (o == com.rameses.common.AsyncHandler.EOF) {
//                   if( has_error ){
//                         MsgBox.err(o.message); 
//                         processdocumentAsyncHandler.cancel();
//                   }
//                   else {
//                        oncomplete('Transaction is successfully imported.');
//                   }
//                } 
//            } 
//        ] as com.rameses.common.AbstractAsyncHandler 
//        
//        showinfo('Initializing...');
//        showinfo(' Documents .... Done\n');
//        showinfo('Start Processing Transaction \n');
//        def initialmessage = 'Mode\t:' + entity.mode.toUpperCase() + ' \nDate\t:' + entity.txndate + ' \nPrepared By\t:' + entity.preparedbyname + ' \n';
//        showinfo(initialmessage);
//        document.each{
//            if (!it.message){
//                switch(mode){
//                    case "receive":
//                        it.message =  "DOCUMENT RECEIVED AT " + entity.currentorg.name.toUpperCase() + " BY " + OsirisContext.env.FULLNAME.toUpperCase();
//                    break;
//                    case "send":
//                        def dest = entity.org.code;
//                        if(entity.destinations){
//                           dest =  entity.destinations.code.join(", ");
//                        }
//                        it.message =  "DOCUMENT SENT TO " + dest + " BY " + OsirisContext.env.FULLNAME.toUpperCase();
//                    break;
//                    case "outgoing":
//                        it.message =  "DOCUMENT IS FOR PICKUP AT " + entity.currentorg.code.toUpperCase();
//                    break;
//                    case "archived":
//                        it.message =  "DOCUMENT ARCHIVED AT " + entity.currentorg.name.toUpperCase() + ". " + entity.cabinet.type.toUpperCase() + " " +  entity.cabinet.title + " BY " + OsirisContext.env.FULLNAME.toUpperCase();
//                    break;
//                    default:
//                    break;
//                }
//
//            }
//            showinfo(it?.din + ' - ' + it?.title);
//            txnSvc.processDocument(entity,it,processdocumentAsyncHandler);
//            showinfo(' .... Done\n');
//        }
//            
//     
//    }
       
}