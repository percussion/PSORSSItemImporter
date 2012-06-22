package com.percussion.pso.rssimport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

public class PSORSSImportConfig extends XMLConfiguration {

	private static final long serialVersionUID = -252832830841184074L;
	private static Logger m_log = Logger.getLogger("rssimporter");	
	private Map m_channelConfigMap = new HashMap();

	/**
	 * Singleton instance of the config
	 */
	private static PSORSSImportConfig config;
	
	/**
	 * Builds an PSORSSImportConfig object from an XML file
	 * @param configXml The config file
	 * @throws ConfigurationException
	 */
	private PSORSSImportConfig(File configXml) 
		throws ConfigurationException
	{
		super(configXml);
	}
	
	/**
	 * Gets the instance of the config object
	 * @return The config object
	 */
	public static synchronized PSORSSImportConfig getInstance() 
	{
	      if (config == null)
	      {
	          m_log.debug("Creating new config");
	          File configXml = new File("rssimport-config.xml");
	          try {
	        	// Create the config and load each "channel" configuration instance
				config = new PSORSSImportConfig(configXml);
				config.loadChannels();
	  		} catch (ConfigurationException e) {
				m_log.error("Unable to load configuration file.  Please contact Rhythmyx administrator", e);
			}
	      }
	      return config;
	}
	
	public PSORSSChannelConfig getChannelConfig(String channelName)
	{
		Object configObj = m_channelConfigMap.get(channelName);
		if(configObj == null)
			return null;
		else
			return (PSORSSChannelConfig)configObj;
	}
	
	public void loadChannels()
	{
		Collection channelSet = config.getList("channels.channel[@name]");
		
		if(!channelSet.isEmpty())
		{
			Iterator it = channelSet.iterator();
			int index = 0;
			while(it.hasNext())
			{
				m_log.debug("Loading channel: " + (this.getString("channels.channel(" + index
						+ ")[@name]")) + " (config index=" + index + ")");
				String name = (String)it.next(); 
				PSORSSChannelConfig config = new PSORSSChannelConfig();
				config.setContentTypeName(this.getString("channels.channel(" + index
						+ ").contentType"));
				loadFieldMappingSet(index, config);
				if(this.getString("channels.channel(" + index
						+ ").syncSupport.alwaysUpdate", "").toLowerCase().trim().equals("yes"))
					config.setAlwaysUpdate(true);
				config.setLookupQueryUrl(this.getString("channels.channel(" + index 
						+ ").syncSupport.lookupQuery[@resource]", ""));
				loadLookupQueryParams(index, config);
				config.setSyncIdElement(this.getString("channels.channel(" + index
						+ ").syncSupport.syncId[@itemElement]", ""));
				config.setSyncIdAttribute(this.getString("channels.channel(" + index
						+ ").syncSupport.syncId[@itemAttribute]", ""));
				config.setVersionElement(this.getString("channels.channel(" + index
						+ ").syncSupport.version[@itemElement]", ""));
				config.setVersionAttribute(this.getString("channels.channel(" + index
						+ ").syncSupport.version[@itemAttribute]", ""));
				config.setDeleteFlagElement(this.getString("channels.channel(" + index
						+ ").syncSupport.deleteFlag[@itemElement]", ""));
				config.setDeleteFlagAttribute(this.getString("channels.channel(" + index
						+ ").syncSupport.deleteFlag[@itemAttribute]", ""));		
				loadWorkflowTransitions(index, config);
				if(this.getString("channels.channel(" + index
						+ ").workflow.doBeforeEdit[@editPublic]", "").toLowerCase().trim().equals("yes"))
					config.setEditPublic(true);
				if(this.getString("channels.channel(" + index
						+ ").workflow.doBeforeEdit[@overrideCheckout]", "").toLowerCase().trim().equals("yes"))
					config.setBeforeEditOverride(true);
				if(this.getString("channels.channel(" + index
						+ ").workflow.doAfterEdit[@returnToPublic]", "").toLowerCase().trim().equals("yes"))
					config.setReturnToPublic(true);
				if(this.getString("channels.channel(" + index
						+ ").workflow.doUnpublish[@overrideCheckout]", "").toLowerCase().trim().equals("yes"))
					config.setUnpublishEditOverride(true);
				config.setDefaultFolderPath(this.getString("channels.channel(" + index
						+ ").defaultFolder", ""));
				loadPostImportExtensionCalls(index, config);
				m_channelConfigMap.put(name, config);
				index++;
			}
		}
	}
	
	private void loadFieldMappingSet(int index, PSORSSChannelConfig config) 
	{
		int fieldMappingIndex = 0;
		Set fieldMappingSet = new HashSet();
		m_log.debug("Loading field mappings");
		Collection fieldMappings = this.getList("channels.channel(" + index + ").itemFieldMap.field[@rxField]");
		if (!fieldMappings.isEmpty()) {
			Iterator it = fieldMappings.iterator();
			while (it.hasNext()) {
				String rxFieldName = (String)it.next();
				if(rxFieldName.equals(""))
				{
					m_log.info("The field at index " + fieldMappingIndex + " is not mapped properly;" +
							"check config file");
					fieldMappingIndex++;
					continue;
				}

				m_log.debug("Creating ItemField mapping object for rx field: " + rxFieldName);
				PSORSSFieldMapping mapping = new PSORSSFieldMapping();
				mapping.setRxFieldName(rxFieldName);
				mapping.setItemElementName(getFieldAttributeValue(index, fieldMappingIndex, "itemElement"));
				mapping.setItemAttributeName(getFieldAttributeValue(index, fieldMappingIndex, "itemAttribute"));
				mapping.setDataType(getFieldAttributeValue(index, fieldMappingIndex, "dataType"));
				mapping.setDateFormat(new SimpleDateFormat(getFieldAttributeValue(index, fieldMappingIndex, "dateFormat")));
				mapping.setDefaultValue(getFieldAttributeValue(index, fieldMappingIndex, "defaultValue"));

				fieldMappingSet.add(mapping);
				fieldMappingIndex++;
			}
			config.addFieldMapping(fieldMappingSet);
		}
	}

	private String getFieldAttributeValue(int index, int fieldMappingIndex, String attributeName){
		return this.getString("channels.channel(" + index + 
				").itemFieldMap.field(" + fieldMappingIndex + ")[@" 
				+ attributeName + "]", "");
	}
	
	
	private void loadLookupQueryParams(int index, PSORSSChannelConfig config)
	{
		int lookupParamIndex = 0;
		Collection params = this.getList("channels.channel(" + index
				+ ").syncSupport.lookupQuery.params.param[@name]");
		if(!params.isEmpty())
		{
			Iterator it = params.iterator();
			while(it.hasNext())
			{
				String name = (String)it.next();
				if(!name.equals(""))
				{
					m_log.debug("Adding query param to map: " + name);
					LookupQueryParam param = new LookupQueryParam();
					param.setName(name);
					param.setElement(getLookupParamAttribute(index, lookupParamIndex, "itemElement"));
					param.setAttribute(getLookupParamAttribute(index, lookupParamIndex, "itemAttribute"));
					config.addLookupQueryParam(param);
				}
				lookupParamIndex++;
			}
		}
	}
	
	private String getLookupParamAttribute(int index, int lookupParamIndex, String attributeName){
		return this.getString("channels.channel(" + index
				+ ").syncSupport.lookupQuery.params.param(" + lookupParamIndex
				+ ")[@" + attributeName + "]", "");
	}
	
	private void loadWorkflowTransitions(int index, PSORSSChannelConfig config){
		Collection transitions = this.getList("channels.channel(" + index
				+ ").workflow.doAfterNew.transition");
		config.setWorkflowTransitionList((List)transitions);
	}
	
	public void loadPostImportExtensionCalls(int index, PSORSSChannelConfig config){
		Collection extCalls = this.getList("channels.channel(" + index
				+ ").postImportExtensions.extension[@className]");
		
		if(!extCalls.isEmpty())
		{
			Iterator it = extCalls.iterator();
			int extCallIndex = 0;
			while(it.hasNext())
			{
				String className = (String)it.next();
				if(!className.equals(""))
				{
					m_log.debug("Adding extension call for " + className);
					PSORSSImportExtensionCall extCall = new PSORSSImportExtensionCall(className);
					loadPostImportExtensionCallParams(index, extCallIndex, extCall);
					config.addExtensionCall(extCall);
				}
				extCallIndex++;
			}
		}
	}
	
	public void loadPostImportExtensionCallParams(int index, int extCallIndex, PSORSSImportExtensionCall extCall)
	{
		Collection params = this.getList("channels.channel(" + index + 
				").postImportExtensions.extension(" + extCallIndex + 
				").param[@name]");
		
		if(!params.isEmpty())
		{
			Iterator it = params.iterator();
			int paramIndex=0;
			while(it.hasNext())
			{
				String name = (String)it.next();
				if(!name.equals(""))
				{
					String value = this.getString("channels.channel(" + index + 
							").postImportExtensions.extension(" + extCallIndex + 
							").param(" + paramIndex + ")[@value]", "");
					m_log.info("...with param " + name + " = " + value);
					extCall.setParam(name,value);
				}
				paramIndex++;
			}
		}
	}
	
	public class LookupQueryParam {
		private String name;
		private String element;
		private String attribute;
		
		public LookupQueryParam(){
		}
		
		public String getName(){
			return name;
		}
		
		public void setName(String s){
			name = s;
		}
		
		public String getElement(){
			return element;
		}
		
		public void setElement(String s){
			element = s;
		}
		
		public String getAttribute(){
			return attribute;
		}
		
		public void setAttribute(String s){
			attribute = s;
		}
	}
}