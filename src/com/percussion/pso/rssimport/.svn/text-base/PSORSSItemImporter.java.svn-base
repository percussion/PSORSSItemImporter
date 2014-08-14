package com.percussion.pso.rssimport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.percussion.error.PSRuntimeException;
import com.percussion.extension.IPSExtensionDef;
import com.percussion.extension.IPSRequestPreProcessor;
import com.percussion.extension.PSExtensionException;
import com.percussion.extension.PSExtensionProcessingException;
import com.percussion.extension.PSParameterMismatchException;
import com.percussion.security.PSAuthorizationException;
import com.percussion.server.IPSRequestContext;
import com.percussion.server.PSRequestValidationException;

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
 * <li>rssImportParam - The name of the incoming request’s HTML parameter 
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
	private static Logger m_log = Logger.getLogger("rssimporter");	

	/**
	 * Process the request. See the class header above.
	 * 
	 * @see com.percussion.extension.IPSRequestPreProcessor#preProcessRequest(java.lang.Object[],
	 *      com.percussion.server.IPSRequestContext)
	 */
	public void preProcessRequest(Object[] params, IPSRequestContext req)
			throws PSAuthorizationException, PSRequestValidationException,
			PSParameterMismatchException, PSExtensionProcessingException {
	
		Map paramMap = argParser(params);
		PSORSSImportStatus status = new PSORSSImportStatus();
		
		String rssDocString = req.getParameter(paramMap.get(RSSPARAM_MAP_KEY)
				.toString());
		if (rssDocString == null || rssDocString.equals("")) {
			m_log.error("Source RSS document is null; exiting.");
			status.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);
			status.setMessage(IPSORSSImportStatusMessages.MSG_CHANNEL_LOAD_FAILURE);
			return;
		}
		
		// Read in the channel from the feed
		InputStream in = new ByteArrayInputStream(rssDocString.getBytes());
		PSORSSChannelManager channelMgr = new PSORSSChannelManager();
		status = channelMgr.loadChannel(in, req);

		m_log.info(status.toString());

		try {
			req.setPrivateObject(paramMap.get(STATUS_MAP_KEY).toString(), status.toXml());
		} catch (PSRuntimeException e) {
			m_log.error(e);
		} catch (JDOMException e) {
			m_log.error(e);
		}
		
		req.setParameter("channelName", channelMgr.getChannelTitle());
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
