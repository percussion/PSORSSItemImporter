package com.percussion.pso.rssimport;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.percussion.cms.PSCmsException;
import com.percussion.cms.objectstore.IPSFieldValue;
import com.percussion.cms.objectstore.PSBinaryValue;
import com.percussion.cms.objectstore.PSComponentSummary;
import com.percussion.cms.objectstore.PSCoreItem;
import com.percussion.cms.objectstore.PSDateValue;
import com.percussion.cms.objectstore.PSInvalidContentTypeException;
import com.percussion.cms.objectstore.PSItemDefinition;
import com.percussion.cms.objectstore.PSItemField;
import com.percussion.data.PSInternalRequestCallException;
import com.percussion.design.objectstore.PSLocator;
import com.percussion.design.objectstore.PSUnknownNodeTypeException;
import com.percussion.error.PSException;
import com.percussion.extension.PSExtensionProcessingException;
import com.percussion.pso.utils.PSOItemSummaryFinder;
import com.percussion.pso.workflow.PSOWorkflowInfoFinder;
import com.percussion.server.IPSRequestContext;
import com.percussion.services.content.data.PSItemStatus;
import com.percussion.services.contentmgr.IPSContentMgr;
import com.percussion.services.contentmgr.PSContentMgrLocator;
import com.percussion.services.guidmgr.IPSGuidManager;
import com.percussion.services.guidmgr.PSGuidManagerLocator;
import com.percussion.services.legacy.IPSCmsContentSummaries;
import com.percussion.services.legacy.PSCmsContentSummariesLocator;
import com.percussion.services.workflow.data.PSState;
import com.percussion.services.workflow.data.PSTransition;
import com.percussion.util.PSPurgableTempFile;
import com.percussion.utils.guid.IPSGuid;
import com.percussion.webservices.PSErrorException;
import com.percussion.webservices.PSErrorResultsException;
import com.percussion.webservices.PSErrorsException;
import com.percussion.webservices.content.IPSContentWs;
import com.percussion.webservices.content.PSContentWsLocator;

import com.percussion.webservices.system.IPSSystemWs;
import com.percussion.webservices.system.PSSystemWsLocator;

public class PSORSSItemManager {

	/**
	 * ContentWS Service
	 */
	private static IPSContentWs cws = null;

	/**
	 * ContentMgr Service
	 */
	private static IPSContentMgr cmgr = null;

	/**
	 * GUID Manager Service
	 */
	private static IPSGuidManager gmgr = null;

	/**
	 * SystemWs Service
	 */
	private static IPSSystemWs sws = null;

	/**
	 * Summaries Config
	 */
	private static IPSCmsContentSummaries summaries = null;

	/**
	 * Initializes the Rhythmyx services pointers. Used to prevent calls to
	 * these services during extension registration.
	 */
	private static void initServices() {
		if (gmgr == null) {
			gmgr = PSGuidManagerLocator.getGuidMgr();
			cmgr = PSContentMgrLocator.getContentMgr();
			sws = PSSystemWsLocator.getSystemWebservice();
			cws = PSContentWsLocator.getContentWebservice();
			summaries = PSCmsContentSummariesLocator.getObjectManager();
		}

	}

	/**
	 * Log for debugging.
	 */
	private Log m_log = LogFactory.getLog(this.getClass());

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
	public PSORSSItemManager(PSORSSChannelConfig config) {
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
	 * 
	 * @param itemData
	 *            The data object for the &lt;item&gt;
	 * @param itemDef
	 *            The PSItemDefinition for the content type
	 * @param req
	 *            The Rhythmyx request context
	 * @return A PSORSSItemStatus object, containing the status of the item
	 *         import
	 */
	public PSORSSItemStatus processItem(PSORSSItemData itemData,
			PSItemDefinition itemDef, IPSRequestContext req)

	{

		initServices();

		boolean newItem = true;
		// IPSOWorkflowItemEditContext workEditCtx = null;
		PSORSSItemStatus status = new PSORSSItemStatus(itemData.getSyncId(),
				itemData.getVersion());

		PSORSSItemLookupResult result;
		PSCoreItem importItem;

		try {

			result = findItem(itemData, req);

			importItem = new PSCoreItem(itemDef);
			// workMgr = PSOWorkflowManagerFactory.getWorkflowManager();

			if ((result == null) && itemData.isDeleted()) {
				status.setStatus(IPSORSSImportStatusMessages.STATUS_SKIPPED);
				status
				.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_NOT_FOUND);
				m_log
				.debug("Item "
						+ itemData.getSyncId()
						+ " designated for deletion (archival), but not found in system");
				return status;
			}
			List<PSItemStatus> itemStatus = null;
			ArrayList<IPSGuid> guids = null;
			if (result != null) {
				status.setLocator(result.getLocator());

				m_log.debug("version of existing item is: "
						+ result.getVersion() + " "
						+ " and the version from feed is: "
						+ itemData.getVersion());
				if (!m_config.alwaysUpdate()
						&& result.getVersion().equals(itemData.getVersion())) {
					status
					.setStatus(IPSORSSImportStatusMessages.STATUS_SKIPPED);
					status
					.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_SAME_VERSION);
					m_log
					.debug(IPSORSSImportStatusMessages.MSG_ITEM_SAME_VERSION);
					return status;
				}

				m_log.debug("loading existing item");

				PSLocator loc = result.getLocator();
				IPSGuid guid = gmgr.makeGuid(loc);

				guids = new ArrayList<IPSGuid>(1);
				guids.add(guid);

				/*
				 * If this is marked for deletion, transition out of public.
				 * This will only happen if the item is public.
				 */
				if (itemData.isDeleted()) {

					m_log
					.debug("Item marked for deletion, checking workflow information");

					PSOWorkflowInfoFinder workflowInfo = new PSOWorkflowInfoFinder();
					PSState itemState = workflowInfo.findWorkflowState(String
							.valueOf(loc.getId()));
					String published = itemState.getContentValidValue();
					PSComponentSummary sum = PSOItemSummaryFinder
					.getSummary(String.valueOf(loc.getId()));
					int wfapp = sum.getWorkflowAppId();
					m_log.debug("Workflow appid = " +wfapp);

					/*
					 * IPSOWorkflowItemInfo wfInfo =
					 * workMgr.getWorkflowItemInfo(req, loc); IPSOStateInfo
					 * stateInfo = wfInfo.getState(); String published =
					 * stateInfo.getContentvalid();
					 */
					m_log.debug("Published?  " + published);

					if (published.equalsIgnoreCase("y")) {
						m_log
						.debug("Item in public state, attempting unpublish");

						String transitionName = null;
						Map<String,String> allowedTrans = sws.getAllowedTransitions(guids);
						

						m_log.debug("Got Transitions for workflow: " +wfapp);
						for (String transName: allowedTrans.keySet()) {
						
							
							m_log.debug("Checking transition:" + transName);
						
							if (transName.toUpperCase().startsWith("UNPUBLISH")) {
								transitionName = transName;
								m_log.debug("Found transition starting with Unpublish");
								break;
							} 

						}

						if (transitionName != null) {
							m_log.debug("Doing transition");
							// Make guid not revision dependent.
							PSLocator newLoc = gmgr.makeLocator(guids.get(0));
							newLoc.setRevision(-1);
							ArrayList<IPSGuid> newGuids = new ArrayList<IPSGuid>(1);
							newGuids.add(gmgr.makeGuid(newLoc));
							m_log.debug("made new guids");
							List<String> transitions =sws.transitionItems(newGuids, transitionName, req
									.getUserName());
							m_log.debug("Transitioned item to state " + transitions.get(0));
							
							status
							.setStatus(IPSORSSImportStatusMessages.STATUS_SUCCESS);
						
							m_log
							.debug(IPSORSSImportStatusMessages.STATUS_SUCCESS);
						} else {
							status
							.setStatus(IPSORSSImportStatusMessages.STATUS_SKIPPED);
							status
							.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_NOT_UNPUBLISHED);
							m_log
							.debug(IPSORSSImportStatusMessages.MSG_ITEM_NOT_UNPUBLISHED);

						}
						// Items should not be checked out in a public state.

						// workMgr.doUnpublish(req, result.getLocator(),
						// m_unpublishOverrideCheckout);

						return status;
					} else {
						status
						.setStatus(IPSORSSImportStatusMessages.STATUS_SKIPPED);
						status
						.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_NOT_PUBLIC);
						m_log
						.debug(IPSORSSImportStatusMessages.MSG_ITEM_NOT_PUBLIC);
						return status;
					}
				}

				// Otherwise, move

				// sum =
				// PSOItemSummaryFinder.getSummary(String.valueOf(loc.getId()));
				
				
				if (guids != null) {
					itemStatus = cws.prepareForEdit(guids, req.getUserName());
					guids.set(0, gmgr.makeGuid((summaries.loadComponentSummary(loc.getId()).getEditLocator())));
				}
				
				List<PSCoreItem> items = cws
				.loadItems(guids, false, false, false, false, req
						.getUserSessionId(), req.getUserName());
				m_log.debug("loaded Items");
				
				
				importItem = items.get(0);
				
				
				
				// workEditCtx = workMgr.doBeforeEdit(req, loc, m_editPublic,
				// m_beforeEditOverrideCheckout);
				// loc =
				// workEditCtx.getIteminfo().getSummary().getEditLocator();
				// importItem.load(loc, token);
				newItem = false;
			}

		
			
			setFields(importItem, itemData.getDataMap());

			ArrayList<PSCoreItem> saveItems = new ArrayList<PSCoreItem>(0);
			saveItems.add(importItem);

			m_log.debug("Added item to saveItems List");
			List<IPSGuid> savedGuids = cws.saveItems(saveItems, false, false,
					req.getUserSessionId(), req.getUserName());
			// importItem.save(token);
			m_log.debug("Saved item; setting item status to success");
			status.setStatus(IPSORSSImportStatusMessages.STATUS_SUCCESS);

			// Set the status locator
			status.setLocator(gmgr.makeLocator(savedGuids.get(0)));

			// Perform Transitions
			if (newItem) {
				status.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_CREATED);

				// Add item to folder
				String folderPath = itemData.getFolderPath();

				if ((folderPath != null) && !folderPath.equals("")) {
					m_log.debug("Folder specified in RSS item is: "
							+ folderPath);
				} else if (m_defaultFolderPath != "") {
					m_log
					.debug("Folder not specified by RSS item, or it does not "
							+ "exist; using default folder: "
							+ m_defaultFolderPath);
					folderPath = m_defaultFolderPath;
				} else {
					m_log
					.debug("Folder not found.  Verify <psx:folderPath> element in feed, "
							+ "or check <defaultFolder> element in config file.");
				}

				cws.addFolderChildren(folderPath, savedGuids);
				m_log.debug("Item added to folder " + folderPath);

				// addItemToFolder(importItem, itemData.getFolderPath(), req);

				// execute "post-processing" extensions
				executePostImportExtensions(importItem, status, req);

				// Perform transitions, if required

				List itemTransitionList = itemData.getDoAfterNewTransitions();
				if (!itemTransitionList.isEmpty()) {
					m_log
					.debug("Item overrides config's doAfterNew transitions: "
							+ itemTransitionList.toString());
					for (Object transitionName : itemTransitionList) {
						sws.transitionItems(savedGuids, (String) transitionName, req
								.getUserName());
					}
					// workMgr.doAfterNew(req, importItem.getKey(),
					// itemTransitionList);
				} else if (!m_transitionList.isEmpty()) {
					m_log.debug("Attempting workflow doAfterNew actions.");
					for (Object transitionName : m_transitionList) {
						sws.transitionItems(savedGuids,
								(String) transitionName, req.getUserName());
					}
					// workMgr.doAfterNew(req, importItem.getKey(),
					// m_transitionList);
				}

			} else {
				status.setMessage(IPSORSSImportStatusMessages.MSG_ITEM_UPDATED);
				executePostImportExtensions(importItem, status, req);

				// Do after edit
				m_log
				.debug("Item updated, attempting workflow doAfterEdit actions.");
				if (itemStatus != null) {
					cws.releaseFromEdit(itemStatus, !m_returnToPublic);
				}
				// workMgr.doAfterEdit(req, workEditCtx, m_returnToPublic);
			}

		} catch (PSExtensionProcessingException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSCmsException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSInvalidContentTypeException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSUnknownNodeTypeException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSInternalRequestCallException e) {
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
		} catch (PSErrorException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSErrorsException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (PSErrorResultsException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (InvalidQueryException e) {
			PSORSSImportUtils.logException(e, status);
		} catch (RepositoryException e) {
			PSORSSImportUtils.logException(e, status);
		}
		return status;
	}

	private PSORSSItemLookupResult findItem(PSORSSItemData itemData,
			IPSRequestContext req) throws InvalidQueryException,
			RepositoryException

			{

		// m_log.debug("Looking for item in Rhythmyx");
		String query = m_config.getLookupQueryUrl();
		PSORSSItemLookupResult result = null;

		m_log.debug("Getting internal request for: " + query);

		Query myQuery;

		myQuery = cmgr.createQuery(query, Query.SQL);

		Map<String, String> paramMap = itemData.getLookupParamMap();

		QueryResult results = cmgr.executeQuery(myQuery, 1, paramMap);

		if (results.getRows().hasNext()) {
			m_log.debug("Found existing item in Rhythmyx");
			Row item = results.getRows().nextRow();

			m_log.debug("Got results...");
			Long contentid_long = item.getValue("rx:sys_contentid").getLong();
			String contentid = contentid_long.toString();
			m_log.debug("Contentid = " + contentid);
			String version = item.getValue("rx:rss_versionid").getString();
			m_log.debug("Version = " + version);

			PSComponentSummary summ = summaries.loadComponentSummary(Integer
					.valueOf(contentid));
			m_log.debug("got component summary for item to get revision");
			String revision = String.valueOf(summ.getPublicOrCurrentRevision());
			m_log.debug("Revision = " + revision);
			result = new PSORSSItemLookupResult(contentid, revision);
			result.setVersion(version);

		}

		if (result == null) {
			m_log.debug("no existing item found");
		}

		return result;

			}

	/*
	 * private void addItemToFolder(PSORSSItem importItem, String folderPath,
	 * IPSRequestContext req) throws PSUnknownNodeTypeException, PSCmsException,
	 * PSExtensionProcessingException { m_log.debug("Adding item to a
	 * folder..."); PSORelationshipHelper helper = new
	 * PSORelationshipHelper(req); PSFolder folder = null; // Use the folder
	 * path from the feed. If empty, use default from config. if(folderPath !=
	 * null && !folderPath.equals("")) { m_log.debug("Folder specified in RSS
	 * item is: " + folderPath); folder = helper.getFolderByPath(folderPath); }
	 * if(folder == null && !m_defaultFolderPath.equals("")) {
	 * m_log.debug("Folder not specified by RSS item, or it does not " + "exist;
	 * checking default folder: " + m_defaultFolderPath); folder =
	 * helper.getFolderByPath(m_defaultFolderPath); } if(folder == null) {
	 * m_log.debug("Folder not found. Verify <psx:folderPath> element in feed, " +
	 * "or check <defaultFolder> element in config file."); return; }
	 * 
	 * PSKey folderKey = folder.getLocator(); List children = new ArrayList(1);
	 * children.add(importItem.getKey());
	 * helper.getRelationshipProxy().add(PSRelationshipConfig.TYPE_FOLDER_CONTENT,children,(PSLocator)folderKey);
	 * 
	 * if(folder != null) m_log.debug("Item added to folder: " +
	 * folder.getName()); }
	 */
	private void executePostImportExtensions(PSCoreItem item,
			PSORSSItemStatus status, IPSRequestContext req)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException, SecurityException, NoSuchMethodException,
	IllegalArgumentException, InvocationTargetException {
		List extensionCalls = m_config.getExtensionCallSet();

		if (!extensionCalls.isEmpty()) {
			Iterator it = extensionCalls.iterator();
			while (it.hasNext()) {
				PSORSSImportExtensionCall extCall = (PSORSSImportExtensionCall) it
				.next();
				String className = extCall.getClassName();
			
				m_log.debug("Attempting call to extension: " + className);

				// Instantiate an object of the named class
				Class def = Class.forName(className);
				Object object = def.newInstance();

				// Initialize the parameters and their values
				Class[] types = new Class[] { PSCoreItem.class,
						PSORSSItemStatus.class, Map.class,
						IPSRequestContext.class };
				Object args[] = new Object[] { item, status,
						extCall.getParamMap(), req };

				/*
				 * Call the "processItem" method, which should be inherited by
				 * the class from the IPSORSSImportPostProcessor Interface
				 */
				Method processItem = def.getMethod("processItem", types);
				processItem.invoke(object, args);
			}
		}
	}

	/**
	 * Sets the server item's fields, based on a map that stores the field-value
	 * pairs for all Rhythmyx fields to be set.
	 * 
	 * @param dataMap
	 *            The data mapping object that stores the mapping between the
	 *            Rhythmyx fields and the values for each that were retrieved
	 *            from the incoming feed
	 */
	public void setFields(PSCoreItem theItem, HashedMap dataMap) {
		MapIterator it = dataMap.mapIterator();

		m_log.debug("Setting fields...");
		while (it.hasNext()) {
			String fieldName = (String) it.next();
			PSItemField itemField = theItem.getFieldByName(fieldName);

			if (itemField != null) {
				Object valueObj = it.getValue();

				if (valueObj == null)
					m_log
					.debug("Field value for \"" + fieldName
							+ "\" is null");

				else {

					IPSFieldValue value = null;

					if (valueObj instanceof PSPurgableTempFile) {
						m_log
						.debug("PurgableTempFile found.  Loading binary data.");
						PSPurgableTempFile tmpFile = (PSPurgableTempFile) valueObj;
						try {
							value = new PSBinaryValue(new FileInputStream(
									tmpFile));
						} catch (Exception e) {
							m_log
							.error("Error reading and setting binary value.");
						}
					}
				
					else {

						m_log.debug("Adding field value for " + fieldName
								+ ": " + (String) valueObj + ".");

						value = itemField.createFieldValue((String) valueObj);
					}

					if (value != null)
						itemField.addValue(value);
					
					
				}
			}
			
		}
		if (!dataMap.containsKey("sys_contentstartdate")) {
			//Add Required contentstartdate if it does not exist
			m_log.debug("sys_contentstartdate not set.  Setting to current date");
			 PSItemField fld = theItem.getFieldByName("sys_contentstartdate");
			 IPSFieldValue val = new PSDateValue(new Date());
			  fld.clearValues(); 
			  fld.addValue(val);
		}
		m_log.debug("Done setting fields.");
	}
}
