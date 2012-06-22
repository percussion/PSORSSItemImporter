package com.percussion.pso.rssimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.percussion.pso.rssimport.PSORSSImportConfig.LookupQueryParam;

import de.nava.informa.core.ItemIF;

public class PSORSSItemData {

	private ItemIF m_item = null;
	private PSORSSChannelConfig m_config;
	private PSORSSItemDataMap m_dataMap;
	private String m_syncId;	
	private String m_version;
	private boolean m_deletedFlag = false;
	private Map<String,String> m_lookupParamValueMap = new HashMap<String,String>();
	private List m_doAfterNewTransitions = new ArrayList();
	private String m_folderPath;

	public PSORSSItemData(PSORSSChannelConfig config, ItemIF item)
	{
		m_item = item;
		m_config = config;
		// JDL 7/8/06... data map now loaded by channel manager; 
		//loadDataMap();
		loadLookupParamValues();
		loadSyncId();
		loadVersion();
		loadDeleteFlag();
		loadDoAfterNewTransitions();		
		loadFolderPath();
	}
	
	public PSORSSItemDataMap getDataMap()
	{
		return m_dataMap;
	}
	
	public String getSyncId()
	{	
		return m_syncId;
	}

	public String getVersion()
	{	
		return m_version;
	}

	public boolean isDeleted()
	{
		return m_deletedFlag;
	}

	public Map<String,String> getLookupParamMap()
	{
		return m_lookupParamValueMap;
	}
	
	public List getDoAfterNewTransitions()
	{
		return m_doAfterNewTransitions;
	}
	
	public String getFolderPath()
	{
		return m_folderPath;
	}
	
	public List loadDataMap()
	{
		m_dataMap = new PSORSSItemDataMap(m_item, m_config);
		return m_dataMap.loadFieldData(m_config.getFieldMappingSet());
	}

	private void loadSyncId()
	{
		String syncIdElement = m_config.getSyncIdElement();
		String syncIdAttribute = m_config.getSyncIdAttribute();
		if(syncIdAttribute.equals(""))
			m_syncId = m_item.getElementValue(syncIdElement);
		else
			m_syncId = m_item.getAttributeValue(syncIdElement, syncIdAttribute);
	}

	private void loadVersion()
	{
		String versionElement = m_config.getVersionElement();
		String versionAttribute = m_config.getVersionAttribute();
		if(versionAttribute.equals(""))
			m_version = m_item.getElementValue(versionElement);
		else
			m_version = m_item.getAttributeValue(versionElement, versionAttribute);
	}
	
	private void loadDeleteFlag()
	{
		String deleteFlagElement = m_config.getDeleteFlagElement();
		String deleteFlagAttribute = m_config.getDeleteFlagAttribute();
		String flag;
		if(deleteFlagAttribute.equals(""))
			flag = m_item.getElementValue(deleteFlagElement);
		else
			flag = m_item.getAttributeValue(deleteFlagElement, deleteFlagAttribute);
		
		if(flag != null && flag.equalsIgnoreCase("true"))
			m_deletedFlag = true;
	}
	
	private void loadLookupParamValues()
	{
		Set lookupParams = m_config.getLookupQueryParams();
		if(!lookupParams.isEmpty())
		{
			Iterator it = lookupParams.iterator();
			while(it.hasNext())
			{
				LookupQueryParam param = (LookupQueryParam)it.next();
				String element = param.getElement();
				String attribute = param.getAttribute();
				if(attribute.equals(""))
					m_lookupParamValueMap.put(param.getName(), m_item.getElementValue(element));
				else
					m_lookupParamValueMap.put(param.getName(), m_item.getAttributeValue(element, attribute));			
			}
		}
	}
	
	private void loadDoAfterNewTransitions()
	{
		String transitions = m_item.getElementValue("psx:doAfterNewTransitions");
		
		if(transitions != null && !transitions.equals(""))
		{
			StringTokenizer tokenizer = new StringTokenizer(transitions, ",");
			while(tokenizer.hasMoreTokens())
			{
				m_doAfterNewTransitions.add(tokenizer.nextToken().trim());				
			}
		}
	}	
	
	private void loadFolderPath()
	{
		m_folderPath = m_item.getElementValue("psx:folderPath");
	}

	public String getLookupJexl(String url) {
		
		Iterator it = m_lookupParamValueMap.keySet().iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			url.replaceAll("{"+key+"}", m_lookupParamValueMap.get(key));
		}
		return url;
	}
}
