package com.percussion.pso.rssimport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.percussion.pso.rssimport.PSORSSImportConfig.LookupQueryParam;

public class PSORSSChannelConfig {

	private Log m_log = LogFactory.getLog(this.getClass());
	
	private String m_contentTypeName;
	private Set m_fieldMap = new HashSet();
	// for sync support
	private boolean m_alwaysUpdate = false;
	private String m_lookupQueryUrl;
	private Set m_lookupQueryParams = new HashSet();
	private String m_syncIdElement;
	private String m_syncIdAttribute;
	private String m_versionElement;
	private String m_versionAttribute;
	private String m_deleteFlagElement;
	private String m_deleteFlagAttribute;
	// for workflow
	private List m_newItemTransitions = new ArrayList();
	private boolean m_editPublic = false;
	private boolean m_beforeEditOverrideCheckout = false;
	private boolean m_returnToPublic = false;
	private boolean m_unpublishOverrideCheckout = false;
	// other
	private String m_defaultFolderPath;
	private List m_extensionCallSet = new ArrayList();
	private String m_proxyServerHost;
	private String m_proxyServerPort;
		
	public PSORSSChannelConfig(){
	}
	
	public void addFieldMapping(Set fieldMap){
		m_fieldMap = fieldMap;
	}
	
	public void setContentTypeName(String name){
		m_contentTypeName = name;
	}
	
	public void setLookupQueryUrl(String url){
		m_lookupQueryUrl = url;
	}
	
	public void addLookupQueryParam(LookupQueryParam param){
		m_lookupQueryParams.add(param);
	}
	
	public void setAlwaysUpdate(boolean alwaysUpdate){
		m_alwaysUpdate=alwaysUpdate;
	}
	
	public void setSyncIdElement(String element){
		m_syncIdElement = element;
	}
	
	public void setSyncIdAttribute(String attribute){
		m_syncIdAttribute = attribute;
	}
	
	public void setVersionElement(String element){
		m_versionElement = element;
	}
	
	public void setVersionAttribute(String attribute){
		m_versionAttribute = attribute;
	}
	
	public void setDeleteFlagElement(String element){
		m_deleteFlagElement = element;
	}
	
	public void setDeleteFlagAttribute(String attribute){
		m_deleteFlagAttribute = attribute;
	}
	
	public void setWorkflowTransitionList(List transitions){
		m_newItemTransitions = transitions;
	}
	
	public void setEditPublic(boolean b){
		m_editPublic = b;
	}
	
	public void setBeforeEditOverride(boolean b){
		m_beforeEditOverrideCheckout = b;
	}
	
	public void setReturnToPublic(boolean b){
		m_returnToPublic = b;
	}
	
	public void setUnpublishEditOverride(boolean b){
		m_unpublishOverrideCheckout = b;
	}
	
	public void setDefaultFolderPath(String folderPath){
		m_defaultFolderPath = folderPath;
	}

	public void addExtensionCall(PSORSSImportExtensionCall extCall){
		m_extensionCallSet.add(extCall);
	}
	
	public void setProxyServerHost(String host){
		m_proxyServerHost = host;
	}

	public void setProxyServerPort(String port){
		m_proxyServerPort = port;
	}
	
	public Set getFieldMappingSet(){
		return m_fieldMap;
	}

	public String getContentTypeName(){
		return m_contentTypeName;
	}
	
	public boolean alwaysUpdate(){
		return m_alwaysUpdate;
	}
		
	public String getLookupQueryUrl(){
		return m_lookupQueryUrl;
	}

	public Set getLookupQueryParams(){
		return m_lookupQueryParams;
	}

	public String getSyncIdElement(){
		return m_syncIdElement;
	}

	public String getSyncIdAttribute(){
		return m_syncIdAttribute;
	}
	
	public String getVersionElement(){
		return m_versionElement;
	}

	public String getVersionAttribute(){
		return m_versionAttribute;
	}
	
	public String getDeleteFlagElement(){
		return m_deleteFlagElement;
	}
	
	public String getDeleteFlagAttribute(){
		return m_deleteFlagAttribute;
	}
	
	public List getWorkflowTransitionList(){
		return m_newItemTransitions;
	}
	
	public boolean getEditPublic(){
		return m_editPublic;
	}
	
	public boolean getBeforeEditOverride(){
		return m_beforeEditOverrideCheckout;
	}
	
	public boolean getReturnToPublic(){
		return m_returnToPublic;
	}
	
	public boolean getUnpublishOverride(){
		return m_unpublishOverrideCheckout;	
	}
	
	public String getDefaultFolderPath(){
		return m_defaultFolderPath;
	}
	
	public List getExtensionCallSet(){
		return m_extensionCallSet;
	}
	
	public String getProxyServerHost(){
		return m_proxyServerHost;
	}
	
	public String getProxyServerPort(){
		return m_proxyServerPort;
	}
	
	public String toString()
	{
		m_log.debug("Loaded channel for content type: " + this.getContentTypeName() + ", printing config:\n");
		// For debugging, print out the map
		StringBuffer sb = new StringBuffer();		
		sb.append("Printing field mappings (# of fields in map is: " + m_fieldMap.size() + "\n");
		Iterator it = m_fieldMap.iterator();
		while (it.hasNext()) 
		{
			PSORSSFieldMapping field = (PSORSSFieldMapping)it.next();
			if(field.getItemAttributeName().equals(""))
				sb.append("\nMapping: " + field.getRxFieldName() + " <==> " + field.getItemElementName());
			else
				sb.append("\nMapping: " + field.getRxFieldName() + " <==> " + 
						field.getItemElementName() + "@" + field.getItemAttributeName());
			sb.append("\n");
		}
		sb.append("\nAlways update is set to: " + m_alwaysUpdate + "\n");
		sb.append("\nLookup Query: " + m_lookupQueryUrl + "\n");
		
		if(!m_lookupQueryParams.isEmpty())
		{
			sb.append("\nParams:\n");
			Iterator paramsIt = m_lookupQueryParams.iterator();
			while(paramsIt.hasNext())
			{
				LookupQueryParam param = (LookupQueryParam)paramsIt.next();
				sb.append("\nParam " + param.getName() + ": element - " 
						+ param.getElement() + "; attribute - " + param.getAttribute() + "\n"); 
			}
		}
		sb.append("\nSync ID element: " + m_syncIdElement);
		sb.append("\nSync ID attribute: " + m_syncIdAttribute);
		sb.append("\nVersion element: " + m_versionElement);
		sb.append("\nVersion attribute: " + m_versionAttribute);		
		sb.append("\nDelete flag element: " + m_deleteFlagElement);
		sb.append("\nDelete flag attribute: " + m_deleteFlagAttribute);
		
		if(!m_defaultFolderPath.equals(""))
			sb.append("\nDefault folder: " + m_defaultFolderPath);
		
		if(!m_extensionCallSet.isEmpty())
		{
			Iterator extCallsIt = m_extensionCallSet.iterator();
			while(extCallsIt.hasNext())
			{
				PSORSSImportExtensionCall extCall = (PSORSSImportExtensionCall)extCallsIt.next();
				sb.append("\nPost Import Extensions...\n" );
				sb.append("--" + extCall.getClassName() + " with params: \n");
				Map paramMap = extCall.getParamMap();
				sb.append(paramMap.toString());
			}
		}
		
		if(!m_proxyServerHost.equals(""))
			sb.append("\nProxy server host: " + m_proxyServerHost);
		if(!m_proxyServerPort.equals(""))
			sb.append("\nProxy server port: " + m_proxyServerPort);
		
		return sb.toString();
	}
}