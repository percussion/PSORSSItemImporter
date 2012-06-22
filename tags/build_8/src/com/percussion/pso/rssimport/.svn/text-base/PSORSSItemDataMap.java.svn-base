package com.percussion.pso.rssimport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.percussion.util.PSPurgableTempFile;

import de.nava.informa.core.ItemIF;

public class PSORSSItemDataMap extends HashedMap {

	private static final long serialVersionUID = 2126851311232536093L;
	private Logger m_log = Logger.getLogger("rssimporter");
	private ItemIF m_item = null;
	PSORSSChannelConfig m_config = null;
	//private SimpleDateFormat m_defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
	
	public PSORSSItemDataMap(ItemIF item, PSORSSChannelConfig config)
	{
		super();
		m_item = item;
		m_config = config;
	}
	
	public List loadFieldData(Set fieldMap)
	{
		/* If any exceptions are caught during the loading of the
		 * data from the feed, the List below will be used for 
		 * storing of the errors and will ultimately be returned back 
		 * to the ChannelManager.  This way, it is possible for items
		 * with data issues to still import, and the errors will be
		 * returned to the application performing the import.
		 */
		List dataIssueList = new ArrayList();	
		
		Iterator it = fieldMap.iterator();
		while (it.hasNext()) 
		{
			PSORSSFieldMapping field = (PSORSSFieldMapping)it.next();
			String rxFieldName = field.getRxFieldName();
			m_log.debug("Getting value from feed for field: " + rxFieldName);
			String rssDataValue = getDataFromRssItem(field);

			if(rssDataValue == null || rssDataValue.equals(""))
			{
				m_log.warn("No value found in feed for field \"" + rxFieldName + "\"");	
			}
			
			m_log.debug("Data type is " + field.getDataType());
			
			if(rssDataValue != null && !rssDataValue.equals("") && field.getDataType().toLowerCase().trim().equals("binary"))
			{
				m_log.debug("Field \"" + rxFieldName + "\" identified as binary.  Locating file...");

				try {
					mapBinaryFields(rxFieldName, new URL(rssDataValue));
					
				} catch (Exception e) {
					String msg = "Issue encountered loading data for field: \"" + rxFieldName + "\"";
					dataIssueList.add(msg);
					m_log.error(msg + "\n", e);
				}
			}
			
			/*
			 * It was initially thought that date values may need to be
			 * formatted prior to import, but the code below never worked
			 * properly and was removed due to the fact that most date
			 * manipulations can easily be handled (and probably already are) 
			 * through a UDF on the content editor field. 
			 * 
			 * if(field.getDataType().equals("date"))
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
				
			else
				this.put(rxFieldName, rssDataValue); 
		}
		
		return dataIssueList;
	}
	
	/**
	 * Gets the data for a given field from the feed.
	 * @param field The PSORSSFieldMapping object containing the
	 * field properties defined in the importer configuration.
	 * @return The String value from the feed item element or attribute
	 */
	private String getDataFromRssItem(PSORSSFieldMapping field)
	{
		String rssFieldName = field.getItemElementName();
		String rssAttrName = field.getItemAttributeName();
	    String rssFieldValue = m_item.getElementValue(rssFieldName);
	    String rssAttrValue = m_item.getAttributeValue(rssFieldName, rssAttrName);
		
		/* Note: if the config does not define an RSS <item> element, then
		 * return whatever is defined in the defaultValue attribute;  If 
		 * this is blank, then the field value will simply be an empty string.
		 * Also, if an element (or element & attribute) is specified, and the 
		 * values are empty in the feed, the default value should be used.
		 */
	    
		if(rssFieldName.equals("")
				|| (!rssFieldName.equals("") && rssAttrName.equals("") && (rssFieldValue == null || rssFieldValue.equals("")))
				|| (!rssFieldName.equals("") && !rssAttrName.equals("") && (rssAttrValue == null || rssAttrValue.equals(""))))
			return field.getDefaultValue();

		if(rssAttrName.equals(""))
			return m_item.getElementValue(rssFieldName);
		else
			return m_item.getAttributeValue(rssFieldName, rssAttrName);
	}
	
	/**
	 * Prints the data mappings
	 */
	public void print() {
		m_log.debug("Printing data mappings:\n");
		MapIterator it = this.mapIterator();
		while (it.hasNext()) {
		   String key = (String)it.next();
		   String value = (String)it.getValue();
		 
		   m_log.debug("Rhythmyx field: " + key + " will have a value of: " + value + ".\n");
		}
	}	
	
	/**
	 * This is responsible for loading all data for a field that was specified
	 * as binary.  The field name in the feed is expected to contain a url 
	 * string which can be used to retrieve the file, and all metadata 
	 * (file name, extension, size, etc.) will be retrieved a la sys_FileInfo, 
	 * which is the Java Exit that normally handles this extraction during a
	 * normal file upload from a content editor.
	 * 
	 * @param rxFieldName The Rhythmyx field name to contain the binary file.
	 * @param url A URL object (which is constructed from value in the feed
	 * that references the location of the file to upload)
	 * @throws IOException 
	 * @throws IOException 
	 * @throws IOException
	 */
	private void mapBinaryFields(String rxFieldName, URL url) throws IOException
	{
		InputStream in;

		// Read in the stream from the url and write out a temp file
		m_log.debug("Retrieving binary file from url: " + url.toExternalForm());

		String proxyHost = m_config.getProxyServerHost();
		String proxyPort = m_config.getProxyServerPort();
		
		// If proxy server settings are in the configuration, they are set here.
		if(!proxyHost.equals(""))
		{
			m_log.debug("Attempting to set proxy server properties... Host: "
					+ proxyHost + ", Port: " + proxyPort);
			
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("proxyHost", proxyHost);
			System.getProperties().put("proxyPort", proxyPort);
		}
		
		URLConnection conn = url.openConnection();
		String contentType = conn.getContentType();
		in = conn.getInputStream();
		
		m_log.debug("Retrieved input stream, writing temp file...");
		
		PSPurgableTempFile tmpFile = new PSPurgableTempFile("rxtmp", ".tmp", null);
		m_log.debug("Created temp file: " + tmpFile.getAbsolutePath());
        OutputStream fout = new FileOutputStream(tmpFile);
        
        int c;
        while ((c = in.read()) != -1)
           fout.write(c);

        in.close();
        fout.close();
        
        m_log.debug("Completed file read.");
        // Adds the temp file object to the data mapping
        this.put(rxFieldName, tmpFile);
				
		String srcPath = url.toExternalForm();
		
		/* There should be no intelligence needed below for the path 
		 * separator-- if "\" is used for local files, so long as the
		 * appropriate "file:/" protocol is provided, when the URL object
		 * is constructed, it normalizes the separator.
		 */
		String fName = getFilename(srcPath, "/");
		int pos = srcPath.lastIndexOf(".");
		long filesize = tmpFile.length();
		String extension = srcPath.substring(pos);
		String mimeType = determineMimeType(contentType, fName);
		String encoding = tmpFile.getCharacterSetEncoding();
		
		// Now set each of the metadata fields
		m_log.debug("Metadata values are: ");
		m_log.debug("	" + rxFieldName + "_fullFilepath: " + srcPath);
		m_log.debug("	" + rxFieldName + "_filename: " + fName);
		m_log.debug("	" + rxFieldName + "_ext: " + extension);
		m_log.debug("	" + rxFieldName + "_type: " + mimeType);
		m_log.debug("	" + rxFieldName + "_encoding: " + encoding);
		m_log.debug("	" + rxFieldName + "_size: " + tmpFile.length());
		
		this.put(rxFieldName + "_fullFilepath", srcPath);
		this.put(rxFieldName + "_filename", fName);
		this.put(rxFieldName + "_ext", extension);
		this.put(rxFieldName + "_type", mimeType);
		this.put(rxFieldName + "_encoding", encoding);
		if(filesize > 0)
			this.put(rxFieldName + "_size", String.valueOf(tmpFile.length()));
	}	
	
	
	/**
	 * FROM sys_FileInfo
	 * 
	 * Returns the filename portion of the provided fully qualified path.
	 * 
	 * @param fullPathname The full path of the file, assumed not
	 * <code>null</code> or empty.
	 *
	 * @param pathSep The path separator to use, assumed not <code>null
	 * </code>.
	 *
	 * @return The filename portion of the full path, based on the pathSep, or
	 * the fullPathname if the provided pathSep is not found in the provided
	 * fullPathname.  Never <code>null</code>, may be emtpy if the fullPathname
	 * ends in the pathSep.
	 */
	private static String getFilename(String fullPathname, String pathSep)
	{
		String fileName = "";

		// add 1 to the index so that we do not include the separator in the
		// filename string
		int startOfFilename = fullPathname.lastIndexOf(pathSep) + 1;
		if (startOfFilename < fullPathname.length())
			fileName = fullPathname.substring(startOfFilename);
		return fileName;
	}
	
	/**
	 * FROM sys_FileInfo
	 * 
	 * This method tries to make a more intelligent decision to determine
	 * the appropriate Mime type by looking at both the type guess made
	 * by the browser and the file extension. Some browser do not always 
	 * correctly determine an uploaded files Mime type for HTML files. If the
	 * type guessed by the browser is text or octet-stream and the file extension
	 * is one of the well known extensions then we use that extensions 
	 * Mime type.
	 * 
	 * @param type the Mime type guessed by the browser, cannot be <code>
	 * null</code> or empty.
	 * @param filename the filename for the uploaded file, cannot be
	 * <code>null</code> or empty.
	 * @return the Mime type, never <code>null</code> or empty.
	 */
	private static String determineMimeType(String type, String filename)
	{
		int pos = filename.indexOf('.');
		String ext = pos == -1 ? "" : filename.substring(pos + 1).toLowerCase();

		if(ms_wellKnownExts.containsKey(ext) && (type.toLowerCase().equals("application/octet-stream") || type.toLowerCase().equals("text/plain")))
		{
			return (String)ms_wellKnownExts.get(ext);  
		}
		return type;
	}
	
   /**
    * FROM sys_FileInfo
    * 
    * These are a map of well know file extensions that the browser should not guess
    * as either text or octet-stream Mime types, but some browser like IE can make
	* a mistake with these. This list should be expanded as we find other problem
	* extension types.
	*/
	private static final Map ms_wellKnownExts = new HashMap(2);   
	static
	{
		ms_wellKnownExts.put("htm", "text/html");
		ms_wellKnownExts.put("html", "text/html");     
	}
	
}
