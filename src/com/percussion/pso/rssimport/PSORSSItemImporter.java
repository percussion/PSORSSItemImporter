package com.percussion.pso.rssimport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.w3c.dom.Document;

import com.percussion.error.PSRuntimeException;
import com.percussion.extension.IPSExtensionDef;
import com.percussion.extension.IPSRequestPreProcessor;
import com.percussion.extension.PSExtensionException;
import com.percussion.extension.PSExtensionProcessingException;
import com.percussion.extension.PSParameterMismatchException;
import com.percussion.security.PSAuthorizationException;
import com.percussion.server.IPSRequestContext;
import com.percussion.server.PSRequestValidationException;
import com.percussion.xml.PSXmlDocumentBuilder;

/**
 * Imports a content item from an incoming document (represented by a
 * literal, String, HTTP parameter value) that conforms to the RSS 2.0 
 * specification with elements in the "psx:" namespace for Rhythmyx field
 * values and the "sx:" namespace for synchronization support 
 * (see http://msdn.microsoft.com/xml/rss/sse/).
 * 
 * <p>
 * This exit should be added as a pre-exit to an assembly "support" resource.
 * There are 4 parameters: 
 * <ol>
 * <li>rssImportParam - The name of the incoming request's HTML parameter 
 * that contains the RSS 2.0 document.</li>
 * <li>statusParam - The name of the private request object that will be 
 * added to the incoming request which contains the import status document (Defaults to importStatusDoc).</li>
 * </ol>
 * <p>
 *
 * <p>For more information, see supporting specification document.</p>
 * 
 * @author JeffLarimer
 *
 */
public class PSORSSItemImporter implements IPSRequestPreProcessor {

	/**
	 * Log for debugging.
	 */
	private Log m_log = LogFactory.getLog(this.getClass());
	
	/**
	 * Process the request. See the class header above.
	 * 
	 * @see com.percussion.extension.IPSRequestPreProcessor#preProcessRequest(java.lang.Object[],
	 *      com.percussion.server.IPSRequestContext)
	 */
	public void preProcessRequest(Object[] params, IPSRequestContext req)
			throws PSAuthorizationException, PSRequestValidationException,
			PSParameterMismatchException, PSExtensionProcessingException {

		// Load exit parameters into a map; Instantiate the import status object
		Map paramMap = argParser(params);
		PSORSSImportStatus status = new PSORSSImportStatus();
		
		InputStream in = null;
		
		/* If the parameter specified is "InputDocument" then the feed should
		 * be retrieved from the Input Document */
		if(paramMap.get(RSSPARAM_MAP_KEY).equals("InputDocument"))
		{
			m_log.debug("Getting feed from InputDocument");
			Document inDoc = req.getInputDocument();
			if(inDoc == null)
			{
				m_log.error("InputDocument specified as source for feed, but InputDocument is null.");
				status.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);
				status.setMessage(IPSORSSImportStatusMessages.MSG_CHANNEL_LOAD_EMPTY_FEED);		
			}
			else
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try {
					PSXmlDocumentBuilder.write(inDoc, out);
					in = new ByteArrayInputStream(out.toByteArray());
				} catch (IOException e) {
					m_log.error(e);
					status.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);
					status.setMessage(IPSORSSImportStatusMessages.MSG_CHANNEL_LOAD_FAILURE);
				}
			}
		}
		/* If the InputDocument is not used, then the feed may be represented as the 
		 * String value of an HTTP parameter.*/
		else 
		{	
			// Get the value of the incoming parameter
			String rssDocString = req.getParameter(paramMap.get(RSSPARAM_MAP_KEY).toString());
			if (rssDocString == null || rssDocString.equals(""))
			{
				m_log.error("Parameter " + paramMap.get(RSSPARAM_MAP_KEY) + " specified as source for feed, but it is empty.");
				status.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);
				status.setMessage(IPSORSSImportStatusMessages.MSG_CHANNEL_LOAD_EMPTY_FEED);
			}
			else
			{
				m_log.debug("Getting feed from parameter: ");			
				in = new ByteArrayInputStream(rssDocString.getBytes());
			}
		}			

		PSORSSChannelManager channelMgr = new PSORSSChannelManager();
		if(in != null)
		{
			status = channelMgr.loadChannel(in, req);
			req.setParameter("channelName", channelMgr.getChannelTitle());			
		}
		else
			req.setParameter("channelName", "--EMPTY_FEED--");
		
		// Output the status to the log
		m_log.info(status.toString());

		try {
			req.setPrivateObject(paramMap.get(STATUS_MAP_KEY).toString(), status.toXml());
		} catch (PSRuntimeException e) {
			m_log.error(e);
		} catch (JDOMException e) {
			m_log.error(e);
		}
	}

	/**
	 * Builds a HashMap for the exit's parameter values. 
	 * @param params The exit's parameters
	 * @return a HashMap containing the parameter values.
	 * @throws PSParameterMismatchException
	 */
	private static Map argParser(Object[] params)
			throws PSParameterMismatchException {
		Map paramMap = new HashMap();

		/* 
		 * First param is the name of the incoming HTTP parameter containing the
		 * RSS doc
		 */
		if (params[0] != null || params[0].toString().trim().length() != 0)
			paramMap.put(RSSPARAM_MAP_KEY, params[0].toString().trim());
		else
			throw new PSParameterMismatchException(0,
					"The rssImportParam was not specified.");

		/*
		 * Second param is the name of the HTTP parameter to set that contains
		 * the status of the import
		 */
		if (params[1] != null || params[1].toString().trim().length() != 0)
			paramMap.put(STATUS_MAP_KEY, params[1].toString().trim());
		else
			paramMap.put(STATUS_MAP_KEY, "importStatusDoc");

		return paramMap;
	}

	/**
	 * Key identifying the parameter storing the name of the incoming RSS feed
	 * parameter (Args are stored in a Map).
	 */
	private static final String RSSPARAM_MAP_KEY = "RSSImportParam";

	/**
	 * Key identifying the parameter storing the XML document built from the Import Status
	 * object (Args are stored in a Map).
	 */
	private static final String STATUS_MAP_KEY = "statusDocObjName";
	
	/**
	 * Required by interface
	 */
	public void init(IPSExtensionDef arg0, File arg1)
			throws PSExtensionException	{
	}
}
