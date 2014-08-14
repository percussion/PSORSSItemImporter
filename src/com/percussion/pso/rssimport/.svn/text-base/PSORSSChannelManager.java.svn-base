package com.percussion.pso.rssimport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.percussion.cms.objectstore.PSInvalidContentTypeException;
import com.percussion.cms.objectstore.PSItemDefinition;
import com.percussion.cms.objectstore.server.PSItemDefManager;
import com.percussion.security.PSSecurityToken;
import com.percussion.server.IPSRequestContext;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;

public class PSORSSChannelManager {
	
	/**
	 * Log for debugging.
	 */
	private Log m_log = LogFactory.getLog(this.getClass());
	
	
	/**
	 * Config instance
	 */
	private PSORSSImportConfig m_config;
	
	/**
	 * Channel Instance
	 */
	private ChannelIF m_channel = null;
	
	private PSORSSImportStatus m_status = null;
	
    /**
     * Constructor
     */
    public PSORSSChannelManager()
    {
		// Get the config
    	m_config = PSORSSImportConfig.getInstance();
    	m_status = new PSORSSImportStatus();
    }
    
    /**
     * Builds a Channel from an InputStream, determines which channel configuration
     * to load, then calls the importItems method.
     * @param in the InputStream to parse
     * @param req the Rhythmyx request context
     * @return The PSORSSImportStatus object
     */
    public PSORSSImportStatus loadChannel(InputStream in, IPSRequestContext req)
    {
    	m_log.debug("Loading channel...");
		try
		{    	
	    	m_channel = FeedParser.parse(new ChannelBuilder(), in);
	    	String channelTitle = m_channel.getTitle();
	    	if(channelTitle == null || channelTitle.trim().equals(""))
	    	{
	    		m_log.error("Incoming channel has no title.  Unable to load channel.");
	    		m_status.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);
	    		m_status.setMessage(IPSORSSImportStatusMessages.MSG_CHANNEL_LOAD_FAILURE);
	    		return m_status;
	    	}
	    	
	    	// load the channel configuration
	    	PSORSSChannelConfig config = m_config.getChannelConfig(channelTitle);
	    	if(config == null)
	    	{
	    		m_log.error("No configuration found for channel: " + channelTitle + ".  Check configuration file.");
	    		m_status.setMessage(IPSORSSImportStatusMessages.MSG_CHANNEL_LOAD_FAILURE);
	    		m_status.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);	    		
	    		return m_status;
	    	}
	    	
	    	// for testing...
	    	m_log.debug(config.toString());
	   		String contentTypeName = config.getContentTypeName();
	   		PSItemDefManager itemDefMgr = PSItemDefManager.getInstance();
	    	PSItemDefinition itemDef;

    		// Create the itemDef object for the content type
    		m_log.debug("Creating itemDef for contentTypeName: " + contentTypeName);
    		itemDef = itemDefMgr.getItemDef(contentTypeName, req.getSecurityToken());
  
    		
    		m_log.debug("Content type ID is: " + itemDef.getTypeId());
    		m_status.addItemStatusList(importItems(config, itemDef, req));		
		
		} catch (PSInvalidContentTypeException e) {
			PSORSSImportUtils.logException(e, m_status);
		} catch (IOException e) {
			PSORSSImportUtils.logException(e, m_status);
		} catch (ParseException e) {
			PSORSSImportUtils.logException(e, m_status);
		}
		m_status.setStatus(IPSORSSImportStatusMessages.STATUS_SUCCESS);
		m_status.setMessage(IPSORSSImportStatusMessages.MSG_CHANNEL_LOAD_SUCCESS);
		return m_status;
    }
    
    /**
     * Gets the title of the loaded channel
     * @return the channel title
     */
    public String getChannelTitle()
    {
    	return m_channel.getTitle();
    }
    
    /**
	 * Gets all the items from the channel, constructs an Item Manager instance,
	 * and loops through the list of items, passing them to the Item Manager.
	 * @param itemDef The PSItemDefinition for the content type that pertains to the channel
	 * @param req the Rhythmyx request context
	 * @return A list of PSORSSItemStatus objects, each of which describes the status of the
	 * import attempt.
	 */
    private List importItems(PSORSSChannelConfig config, PSItemDefinition itemDef, IPSRequestContext req) 
    {
    	List statusObjs = new ArrayList();
    	Collection items = m_channel.getItems();
		PSORSSItemManager itemMgr = new PSORSSItemManager(config);
		
		if(!items.isEmpty())
		{
			Iterator it = items.iterator();
			while(it.hasNext())
			{
				ItemIF item = (ItemIF)it.next();
		    	m_log.info("Loading data for: " + item.getElementValue("title"));				
				PSORSSItemData itemDataObj = new PSORSSItemData(config, item);

				/* It is possible that items with data loading issues could still 
				 * import, and these exceptions may be caught during the loading
				 * of the data from the feed, rather than creation of the item.
				 * 
				 * Any errors that are caught during data loading are returned in
				 * a List (below), and then added to the status object for return 
				 * to the application attempting the import.
				 */
				List dataErrors = itemDataObj.loadDataMap();
				PSORSSItemStatus status = itemMgr.processItem(itemDataObj, itemDef, req);
				
				if(!dataErrors.isEmpty())
				{
					Iterator dataErrorIt = dataErrors.iterator();
					while(dataErrorIt.hasNext())
						status.setMessage(status.getMessage() + "; " + (String)dataErrorIt.next());
					status.setMessage(status.getMessage() + "; see log for further details.");
				}
				
				statusObjs.add(status);
			}
		}
		return statusObjs;
    }
}