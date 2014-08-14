package com.percussion.pso.rssimport;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.percussion.cms.PSCmsException;
import com.percussion.cms.objectstore.PSInvalidContentTypeException;
import com.percussion.cms.objectstore.PSItemDefinition;
import com.percussion.cms.objectstore.PSItemField;
import com.percussion.cms.objectstore.server.PSServerItem;
import com.percussion.security.PSSecurityToken;

public class PSORSSItem extends PSServerItem {

	/**
	 * Log for debugging.
	 */
	private Logger m_log = Logger.getLogger("rssimporter");	
	
	/**
	 * Creates a PSServerItem object
	 * @param itemDef The content type definition
	 * @param token The security token
	 * @throws PSCmsException
	 * @throws PSInvalidContentTypeException
	 */
	public PSORSSItem(PSItemDefinition itemDef, PSSecurityToken token) 
		throws PSCmsException, PSInvalidContentTypeException
	{
		super(itemDef, null, token);
	}

	/**
	 * Sets the server item's fields, based on a map that stores the field-value pairs
	 * for all Rhythmyx fields to be set.
	 * @param dataMap The data mapping object that stores the mapping between the 
	 * Rhythmyx fields and the values for each that were retrieved from the incoming feed
	 */
	public void setFields(HashedMap dataMap) 
	{
		MapIterator it = dataMap.mapIterator();
		
		m_log.debug("Setting fields...");
		while(it.hasNext())
		{
			String fieldName = (String)it.next();
			String fieldValue = (String)it.getValue();

			m_log.debug(fieldName + " = " + fieldValue);
			PSItemField itemField = this.getFieldByName(fieldName);
			if(itemField != null)
				itemField.addValue(itemField.createFieldValue(fieldValue));
		}
	}
}