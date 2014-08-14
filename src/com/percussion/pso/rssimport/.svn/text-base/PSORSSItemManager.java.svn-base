package com.percussion.pso.rssimport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.percussion.cms.PSCmsException;
import com.percussion.cms.objectstore.PSFolder;
import com.percussion.cms.objectstore.PSInvalidContentTypeException;
import com.percussion.cms.objectstore.PSItemDefinition;
import com.percussion.cms.objectstore.PSKey;
import com.percussion.consulting.utils.PSORelationshipHelper;
import com.percussion.consulting.utils.PSOSimpleSqlQuery;
import com.percussion.data.PSInternalRequestCallException;
import com.percussion.design.objectstore.PSLocator;
import com.percussion.design.objectstore.PSRelationshipConfig;
import com.percussion.design.objectstore.PSUnknownNodeTypeException;
import com.percussion.extension.PSExtensionProcessingException;
import com.percussion.pso.workflow.IPSOStateInfo;
import com.percussion.pso.workflow.IPSOWorkflowItemEditContext;
import com.percussion.pso.workflow.IPSOWorkflowItemInfo;
import com.percussion.pso.workflow.IPSOWorkflowManager;
import com.percussion.pso.workflow.PSOWorkflowException;
import com.percussion.pso.workflow.PSOWorkflowManagerFactory;
import com.percussion.security.PSSecurityToken;
import com.percussion.server.IPSInternalRequest;
import com.percussion.server.IPSRequestContext;

public class PSORSSItemManager {

	/**
	 * Log for debugging.
	 */
	private Logger m_log = Logger.getLogger("rssimporter");
	
	/**
	 * The configuration object for the channel being processed
	 */
	private PSORSSChannelConfig m_config;
	private boolean m_editPublic = false;
	private boolean m_beforeEditOverrideCheckout = false;
	private boolean m_returnToPublic = false;
	private boolean m_unpublishOverrideCheckout = false;
	private List m_transitionList = new ArrayList();
	private String m_defaultFolderPath;

	/**
     * Constructor
     */
    public PSORSSItemManager(PSORSSChannelConfig config){
    	m_config = config;
    	m_editPublic = config.getEditPublic();
    	m_beforeEditOverrideCheckout = config.getBeforeEditOverride();
    	m_returnToPublic = config.getReturnToPublic();
    	m_unpublishOverrideCheckout = config.getUnpublishOverride();
    	m_transitionList = config.getWorkflowTransitionList();
    	m_defaultFolderPath = config.getDefaultFolderPath();
    }
    
	/**
	 * Processes an RSS &lt;item&gt; for import into Rhythmyx. 
	 * @param itemData The data object for the &lt;item&gt;
	 * @param itemDef The PSItemDefinition for the content type
	 * @param req The Rhythmyx request context
	 * @return A PSORSSItemStatus object, containing the status of the item import
	 */
    public PSORSSItemStatus processItem(PSORSSItemData itemData, PSItemDefinition itemDef, IPSRequestContext req) 

    {
    	PSSecurityToken token = req.getSecurityToken();
    	boolean newItem = true;
    	IPSOWorkflowItemEditContext workEditCtx = null;
    	PSORSSItemStatus status = new PSORSSItemStatus(itemData.getSyncId(), itemData.getVersion());
    	
    	PSORSSItemLookupResult result;
    	PSORSSItem importItem;
    	IPSOWorkflowManager workMgr;
    	
    	try {
			result = findItem(itemData, req);
			importItem = new PSORSSItem(itemDef, token);
			workMgr = PSOWorkflowManagerFactory.getWorkflowManager();
			
			if(result == null && itemData.isDeleted())
			{
				status.setStatus(IPSORSSImportStatusMessages.STATUS_SKIPPED);
				status.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_NOT_FOUND);
				m_log.debug("Item " + itemData.getSyncId() + " designated for deletion (archival), but not found in system");
				return status;
			}
			
	    	if(result != null)
	    	{
	            status.setLocator(result.getLocator());
	            
	    		m_log.debug("version of existing item is: " + result.getVersion() + " "
	    				+ " and the version from feed is: " 
	    				+ itemData.getVersion());
	    		if(!m_config.alwaysUpdate() && result.getVersion().equals(itemData.getVersion()))
	    		{
	    			status.setStatus(IPSORSSImportStatusMessages.STATUS_SKIPPED);
	    			status.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_SAME_VERSION);
	    			m_log.debug(IPSORSSImportStatusMessages.MSG_ITEM_SAME_VERSION);
		            return status;
	    		}
	    		
	            m_log.debug("loading existing item"); 
	            
	            PSLocator loc = result.getLocator();
	            
	            /* If this is marked for deletion, transition out of public.
	             * This will only happen if the item is public.
	             */
		    	if(itemData.isDeleted())
		    	{
		    		m_log.debug("Item marked for deletion, checking workflow information");
		    		IPSOWorkflowItemInfo wfInfo = workMgr.getWorkflowItemInfo(req, loc);
		    		IPSOStateInfo stateInfo = wfInfo.getState();
		    		String published = stateInfo.getContentvalid();
		    		m_log.debug("Published?  " + published);
		    		
		    		if(published.equalsIgnoreCase("y"))
		    		{
		    			m_log.debug("Item in public state, attempting unpublish");
		    			workMgr.doUnpublish(req, result.getLocator(), m_unpublishOverrideCheckout);
			    		status.setStatus(IPSORSSImportStatusMessages.STATUS_SUCCESS);
			    		status.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_UNPUBLISHED);
			    		m_log.debug(IPSORSSImportStatusMessages.MSG_ITEM_UNPUBLISHED);
			    		return status;
		    		}
		    		else
		    		{
			    		status.setStatus(IPSORSSImportStatusMessages.STATUS_SKIPPED);
			    		status.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_NOT_PUBLIC);
			    		m_log.debug(IPSORSSImportStatusMessages.MSG_ITEM_NOT_PUBLIC);
			    		return status;	
		    		}
		    	}	    	
           
		    	// Otherwise, move 
	            workEditCtx = workMgr.doBeforeEdit(req, loc, m_editPublic, m_beforeEditOverrideCheckout);
	            loc = workEditCtx.getIteminfo().getSummary().getEditLocator();
	            importItem.load(loc, token);
	            newItem = false;
	    	}
			
	    	importItem.setFields(itemData.getDataMap());
			importItem.save(token);
			m_log.debug("Saved item; setting item status to success");
			status.setStatus(IPSORSSImportStatusMessages.STATUS_SUCCESS);
			
			// Set the status locator
			status.setLocator(importItem.getKey());
			
			// Perform Transitions
			if(newItem)
			{  
				status.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_CREATED);
			
				// Add item to folder
				addItemToFolder(importItem, itemData.getFolderPath(), req);
				// execute "post-processing" extensions
				executePostImportExtensions(importItem, status, req);
				
				// Perform transitions, if required
				List itemTransitionList = itemData.getDoAfterNewTransitions();				
				if(!itemTransitionList.isEmpty())
				{
					m_log.debug("Item overrides config's doAfterNew transitions: " + itemTransitionList.toString());
					workMgr.doAfterNew(req, importItem.getKey(), itemTransitionList);
				}
				else if(!m_transitionList.isEmpty())
				{
					m_log.debug("Attempting workflow doAfterNew actions.");
					workMgr.doAfterNew(req, importItem.getKey(), m_transitionList);
				}
			}
			else
			{
	            status.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_UPDATED);
				executePostImportExtensions(importItem, status, req);
				
				// Do after edit
				m_log.debug("Item updated, attempting workflow doAfterEdit actions.");
				workMgr.doAfterEdit(req, workEditCtx, m_returnToPublic);
			}
			
    	} catch (PSExtensionProcessingException e) {
    		PSORSSImportUtils.logException(e, status);
		} catch (PSCmsException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSInvalidContentTypeException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSOWorkflowException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSUnknownNodeTypeException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSInternalRequestCallException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (SQLException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (SecurityException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (IllegalArgumentException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (ClassNotFoundException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (InstantiationException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (IllegalAccessException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (NoSuchMethodException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (InvocationTargetException e) {
			PSORSSImportUtils.logException(e, status);
		}
		return status;
	}
    
    private PSORSSItemLookupResult findItem(PSORSSItemData itemData, IPSRequestContext req) 
		throws PSExtensionProcessingException, PSInternalRequestCallException, SQLException
	{
		m_log.debug("Looking for item in Rhythmyx");
		String url = m_config.getLookupQueryUrl();
		Map paramMap = itemData.getLookupParamMap();
		
		m_log.debug("Getting internal request for: " + url);
		IPSInternalRequest irq = req.getInternalRequest(url, paramMap, false);
		if(irq == null)
		{
			m_log.error("Application Query " + url + " not found");
			String msg = "Application Query " + url + " not found"; 
				throw new PSExtensionProcessingException(0, msg); 
		}
		Object[] results;

		m_log.debug("Calling PSOSimpleSqlQuery");
		results = PSOSimpleSqlQuery.doSingleRowQuery(irq);
		if(results == null)
		{
			m_log.debug("No existing item found.");
			return null;
		}
			
		m_log.debug("Got results...");
		String contentid = results[0].toString();
		String revision = results[1].toString();
		String version = results[2].toString();
		m_log.debug("Found existing item: " + "\ncontentid: " + contentid
				+ "\nrevision: " + revision + "\nversion: " + version);
		PSORSSItemLookupResult result = new PSORSSItemLookupResult(contentid, revision);
		result.setVersion(version);
		return result;
	}
    
    private void addItemToFolder(PSORSSItem importItem, String folderPath, IPSRequestContext req) 
    	throws PSUnknownNodeTypeException, PSCmsException, PSExtensionProcessingException
    {
		m_log.debug("Adding item to a folder...");
		PSORelationshipHelper helper = new PSORelationshipHelper(req);
    	PSFolder folder = null;
    	// Use the folder path from the feed.  If empty, use default from config.
		if(folderPath != null && !folderPath.equals(""))
		{
			m_log.debug("Folder specified in RSS item is: " + folderPath);
			folder = helper.getFolderByPath(folderPath);
		}
		if(folder == null && !m_defaultFolderPath.equals(""))
		{
			m_log.debug("Folder not specified by RSS item, or it does not " +
					"exist; checking default folder: " + m_defaultFolderPath);
			folder = helper.getFolderByPath(m_defaultFolderPath);
		}
		if(folder == null)
		{
			m_log.debug("Folder not found.  Verify <psx:folderPath> element in feed, " +
					"or check <defaultFolder> element in config file.");
			return;
		}
		
		PSKey folderKey = folder.getLocator();
		List children = new ArrayList(1);
		children.add(importItem.getKey());
		helper.getRelationshipProxy().add(PSRelationshipConfig.TYPE_FOLDER_CONTENT,children,(PSLocator)folderKey);
		
		if(folder != null)
			m_log.debug("Item added to folder: " + folder.getName());
    }
    
    private void executePostImportExtensions(PSORSSItem item, PSORSSItemStatus status, IPSRequestContext req)
    	throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, 
    	NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
    	List extensionCalls = m_config.getExtensionCallSet();
    	
    	if(!extensionCalls.isEmpty())
    	{
    		Iterator it = extensionCalls.iterator();
    		while(it.hasNext())
    		{
    			PSORSSImportExtensionCall extCall = (PSORSSImportExtensionCall)it.next();
    			String className = extCall.getClassName();

    			m_log.debug("Attempting call to extension: " + className);
    			
    			// Instantiate an object of the named class
    			Class def = Class.forName(className);    			
    			Object object = def.newInstance();
    			
    			// Initialize the parameters and their values
    			Class[] types = new Class[] {PSORSSItem.class,	PSORSSItemStatus.class, Map.class, IPSRequestContext.class};
    			Object args[] = new Object[] {item, status, extCall.getParamMap(), req};
   				
    			/* Call the "processItem" method, which should be inherited by the class
    			from the IPSORSSImportPostProcessor Interface */
   				Method processItem = def.getMethod("processItem", types);
   				processItem.invoke(object, args);
    		}
    	}
    }
}
