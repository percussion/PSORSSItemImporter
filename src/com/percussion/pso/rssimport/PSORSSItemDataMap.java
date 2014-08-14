package com.percussion.pso.rssimport;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import de.nava.informa.core.ItemIF;

public class PSORSSItemDataMap extends HashedMap {

	private static final long serialVersionUID = 2126851311232536093L;
	private Logger m_log = Logger.getLogger("rssimporter");
	private ItemIF m_item = null;
	//private SimpleDateFormat m_defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
	
	public PSORSSItemDataMap(ItemIF item)
	{
		super();
		m_item = item;
	}
	
	public void loadFieldData(Set fieldMap)
	{
		Iterator it = fieldMap.iterator();
		while (it.hasNext()) 
		{
			PSORSSFieldMapping field = (PSORSSFieldMapping)it.next();
			String rxFieldName = field.getRxFieldName();
			String rssDataValue = getDataFromRssItem(field);
		
			/*if(field.getDataType().equals("date"))
			{
				DateFormat f = DateFormat.getDateInstance();
				f.setLenient(true);
				SimpleDateFormat importFormat = (SimpleDateFormat)field.getDateFormat();
				Date dateValue;				
				try {
					dateValue = f.parse(rssDataValue);
					if(!importFormat.toPattern().equals(""))
						rssDataValue = importFormat.format(dateValue);
					else
						rssDataValue = m_defaultDateFormat.format(dateValue);

				} catch (ParseException e) {
					m_log.debug("Could not parse date string: " + rssDataValue + "\n" + e);
				}
			}*/
				
			if(rssDataValue != null && !rssDataValue.equals(""))
				this.put(rxFieldName, rssDataValue); 
		}
	}
	
	private String getDataFromRssItem(PSORSSFieldMapping field)
	{
		String rssFieldName = field.getItemElementName();
		/* Note: if the config does not define an RSS <item> element, then
		 * return whatever is defined in the defaultValue attribute;  If 
		 * this is blank, then the field value will simply be an empty string.
		 */ 
		if(rssFieldName.equals(""))
			return field.getDefaultValue();
		if(field.getItemAttributeName().equals(""))
			return m_item.getElementValue(rssFieldName);
		else
			return m_item.getAttributeValue(rssFieldName, field.getItemAttributeName());
	}
	
	public void print() {
		m_log.debug("Printing data mappings:\n");
		MapIterator it = this.mapIterator();
		while (it.hasNext()) {
		   String key = (String)it.next();
		   String value = (String)it.getValue();
		 
		   m_log.debug("Rhythmyx field: " + key + " will have a value of: " + value + ".\n");
		}
	}	
}
