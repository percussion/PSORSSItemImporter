package com.percussion.pso.rssimport;

import java.io.FileInputStream;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.percussion.cms.PSCmsException;
import com.percussion.cms.objectstore.IPSFieldValue;
import com.percussion.cms.objectstore.PSBinaryValue;
import com.percussion.cms.objectstore.PSInvalidContentTypeException;
import com.percussion.cms.objectstore.PSItemDefinition;
import com.percussion.cms.objectstore.PSItemField;
import com.percussion.cms.objectstore.server.PSServerItem;
import com.percussion.security.PSSecurityToken;
import com.percussion.util.PSPurgableTempFile;

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
			PSItemField itemField = this.getFieldByName(fieldName);

			if(itemField != null)
			{
				Object valueObj = (Object)it.getValue();
				
				if(valueObj == null)
					m_log.debug("Field value for \"" + fieldName + "\" is null");
			
				else{
	
					IPSFieldValue value = null;
					
					if(valueObj instanceof PSPurgableTempFile)
					{
						m_log.debug("PurgableTempFile found.  Loading binary data.");
						PSPurgableTempFile tmpFile = (PSPurgableTempFile)valueObj;
						try {
							value = new PSBinaryValue(new FileInputStream(tmpFile));
						} catch (Exception e) {
							m_log.error("Error reading and setting binary value.");
						}
					}
					
					else
					{
						m_log.debug("Adding field value for " + fieldName + ": " + (String)valueObj + ".");
						value = itemField.createFieldValue((String)valueObj);
					}
					
					if(value != null)
						itemField.addValue(value);
				}
			}
		}
		m_log.debug("Done setting fields.");
	}
}