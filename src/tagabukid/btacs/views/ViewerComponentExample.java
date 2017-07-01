package tagabukid.btacs.views;

/*
* Copyright 2006-2016 ICEsoft Technologies Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the
* License. You may obtain a copy of the License at
*
*        http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an "AS
* IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied. See the License for the specific language
* governing permissions and limitations under the License.
*/


import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.ReportOutputFormat;
import com.jaspersoft.jasperserver.jaxrs.client.core.JasperserverRestClient;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.AuthenticationType;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.JRSVersion;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.MimeType;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.BorderLayout;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import javax.swing.*;
import org.icepdf.ri.common.ComponentKeyBinding;


/**
 * The <code>ViewerComponentExample</code> class is an example of how to use
 * <code>SwingController</code> and <code>SwingViewBuilder</code>
 * to build a PDF viewer component.  A file specified at the command line is
 * opened in a JFrame which contains the viewer component.
 *
 * @since 2.0
 */



public class ViewerComponentExample extends JPanel implements UIControl {
    private Binding binding;
    private String[] depends;
    private int index;
    private int stretchWidth;
    private int stretchHeight;
    public ViewerComponentExample() {
        Object value = UIControlUtil.getBeanValue(this);
// build a controller
        SwingController controller = new SwingController();
        
// Build a SwingViewFactory configured with the controller
        SwingViewBuilder factory = new SwingViewBuilder(controller);
        
// Use the factory to build a JPanel that is pre-configured
//with a complete, active Viewer UI.
        JPanel viewerComponentPanel = factory.buildViewerPanel();
        
// add copy keyboard command
        ComponentKeyBinding.install(controller, viewerComponentPanel);
        
// add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));
        
// Create a JFrame to display the panel in
//        JFrame window = new JFrame("Using the Viewer Component");
//        window.getContentPane().add(viewerComponentPanel);
//        window.pack();
//        window.setVisible(true);
        setLayout(new BorderLayout());
        add(viewerComponentPanel, BorderLayout.CENTER);
// Open a PDF document to view
        RestClientConfiguration configuration = new RestClientConfiguration("http://172.16.2.50:8060/jasperserver");
        configuration.setAcceptMimeType(MimeType.JSON).setContentMimeType(MimeType.JSON).setJrsVersion(JRSVersion.v5_1_0).setLogHttp(true);
        configuration.setAuthenticationType(AuthenticationType.BASIC);
        JasperserverRestClient client = new JasperserverRestClient(configuration);
        Map items = new HashMap();
        items.putAll((Map)value);
        OperationResult<InputStream> result = client
                .authenticate("jasperdtr", "jasperdtr")
                .reportingService()
                .report("/dtr/dtr1_3_2_1_1_1_4_4")
                .prepareForRun(ReportOutputFormat.PDF, 1)
                .parameter("DEPARTMENT_ID",(String)items.get("DEPARTMENT_ID"))
                .parameter("UserId",(String)items.get("UserId"))
                .parameter("DTR_MONTH",(String)items.get("DTR_MONTH"))
                .parameter("DTR_YEAR",(String)items.get("DTR_YEAR"))
                .parameter("Supervisor",(String)items.get("Supervisor"))
                .parameter("Type",(String)items.get("Type"))
                .run();
        InputStream report = result.getEntity();
        controller.openDocument(report,"","");
    }
    public void load() {
        
    }
    
    
    public void refresh() {
        
    }
    
    @Override
    public Binding getBinding() {
        return this.binding; //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void setBinding(Binding bndng) {
        this.binding = binding; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public String[] getDepends() {
        return this.depends = depends; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public int getIndex() {
        return this.index = index; //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
        
    }
    
    
    public int getStretchWidth() {
        return this.stretchWidth; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public int getStretchHeight() {
        return this.stretchHeight; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getVisibleWhen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setVisibleWhen(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
