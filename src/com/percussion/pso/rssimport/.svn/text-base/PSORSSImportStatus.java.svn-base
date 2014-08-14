package com.percussion.pso.rssimport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;

import com.percussion.design.objectstore.PSLocator;

public class PSORSSImportStatus {

	/**
	 * Log for debugging.
	 */
	private Logger m_log = Logger.getLogger("rssimporter");	
	private String msg;
	private String status;
	private List itemStatusList = new ArrayList();

	public PSORSSImportStatus() {
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public void setMessage(String msg) {
		this.msg = msg;
	}

	public List getItemStatusList() {
		return itemStatusList;
	}

	public void addItemStatusList(List itemStatusList) {
		this.itemStatusList = itemStatusList;
	}
	
	public String toString(){
		m_log.debug("Printing import status: \n");
		StringBuffer sb = new StringBuffer();
		sb.append("Channel import status:");
		sb.append("\n" + this.getStatus());
		sb.append("\n" + this.getMessage());
		
		List items = this.getItemStatusList();
		m_log.debug("Found " + items.size() + " item status objects");		
		if(!items.isEmpty())
		{
			sb.append("\nItems: ");
			Iterator it = items.iterator();
			while(it.hasNext())
			{
				PSORSSItemStatus itemStatus = (PSORSSItemStatus)it.next();

				sb.append("\nItem >>");
				sb.append("\n  ID: " + itemStatus.getSyncId() + "; Version: " + itemStatus.getSyncVersion());
				sb.append("\n  Status: " + itemStatus.getStatus());
				sb.append("\n  Message: " + itemStatus.getMessage());
			
				PSLocator loc = itemStatus.getLocator();
				if(loc != null)
				{
					sb.append("\n  Content ID:" + loc.getId() + "; Revision ID: " + loc.getRevision());
				}
			}
		}
		sb.append("\n********************************");
		return sb.toString();
	}
	
	public org.w3c.dom.Document toXml() throws JDOMException 
	{
		m_log.debug("Building status XML...");
		Element root = new Element("channelStatus");
		
		// Add channel status element
		Element channelStatus = new Element("status");
		channelStatus.addContent(this.getStatus());
		
		// Add message
		Element channelMsg = new Element("message");
		channelMsg.addContent(this.getMessage());
		
		Element itemsEl = new Element("items");
		
		List items = this.getItemStatusList();
		m_log.debug("Found " + items.size() + " item status objects");
		if(!items.isEmpty())
		{
			Iterator it = items.iterator();
			while(it.hasNext())
			{
				PSORSSItemStatus itemStatus = (PSORSSItemStatus)it.next();

				Element item = new Element("item");
				Attribute id = new Attribute("id", itemStatus.getSyncId());
				Attribute version = new Attribute("version", itemStatus.getSyncVersion());
				item.setAttribute(id);
				item.setAttribute(version);

				Element status = new Element("status");
				status.addContent(itemStatus.getStatus());
				item.addContent(status);
				
				Element message = new Element("message");
				message.addContent(itemStatus.getMessage());
				item.addContent(message);
				
				PSLocator loc = itemStatus.getLocator();
				if(loc != null)
				{
					Element locator = new Element("locator");

					Attribute contentid = new Attribute("sys_contentid", String.valueOf(loc.getId()));
					Attribute revision = new Attribute("sys_revision", String.valueOf(loc.getRevision()));					
					locator.setAttribute(contentid);
					locator.setAttribute(revision);
					
					item.addContent(locator);
				}
				
				itemsEl.addContent(item);
			}
		}

		root.addContent(channelStatus);
		root.addContent(channelMsg);
		root.addContent(itemsEl);		
		
		Document jdoc = new Document(root);
		DOMOutputter dop = new DOMOutputter();
		return dop.output(jdoc);
	}
}
