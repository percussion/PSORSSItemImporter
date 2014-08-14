package com.percussion.pso.rssimport;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.percussion.server.IPSRequestContext;



public class PSOFeedUploadController extends SimpleFormController {
	private Log m_log = LogFactory.getLog(this.getClass());
	
	public static final String REQUEST_ATTRIBUTE = "RX_REQUEST_CONTEXT";  //  @jve:decl-index=0:

	
	protected ModelAndView onSubmit(
	        HttpServletRequest request,
	        HttpServletResponse response,
	        Object command,
	        BindException errors)throws Exception {

		IPSRequestContext req = (IPSRequestContext) request
		.getAttribute(REQUEST_ATTRIBUTE);
		
	         // cast the bean
	        PSOFileUploadBean file = (PSOFileUploadBean) command;
	        PSORSSImportStatus status = new PSORSSImportStatus();
	        
	    	InputStream in = file.getInputStream();
			
	    	if (file==null){
	    		m_log.debug("PSOFileUploadBean object is null");
	    	}
	    	if (in==null) {
	    		status.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);
				status.setMessage(IPSORSSImportStatusMessages.MSG_CHANNEL_LOAD_FAILURE);
				m_log.error("Cannot get input stream for rss");
	    	}
	    	
	    	PSORSSChannelManager channelMgr = new PSORSSChannelManager();
			if(in != null)
			{
				m_log.debug("got file loading channel");
				status = channelMgr.loadChannel(in, req);
				req.setParameter("channelName", channelMgr.getChannelTitle());			
			}
			else
				req.setParameter("channelName", "--EMPTY_FEED--");
			
			// Output the status to the log
			m_log.info(status.toString());

			
				//Write status
			m_log.debug("finished");
			response.getWriter().write(status.toString());
				
			
	      
	         // well, let's do nothing with the bean for now and return
			 String now = (new java.util.Date()).toString(); 
		        logger.info("returning hello view with " + now);

		  return new ModelAndView("form", "file", null);
	 
	 }
	
	
	 
}
