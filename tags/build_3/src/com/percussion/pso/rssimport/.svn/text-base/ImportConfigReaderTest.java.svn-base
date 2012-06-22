package com.percussion.pso.rssimport;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class ImportConfigReaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try
		{
			File configXml = new File("rssimport-config.xml");
	        XMLConfiguration config = new XMLConfiguration(configXml);
	        
			Collection cTypes = config.getList("channels.channel[@name]");
			if(cTypes != null)
			{
				int i = 0;
				System.out.println("Number of content types: " + cTypes.size());
				Iterator it = cTypes.iterator();
				while(it.hasNext())
				{
					String cTypeName = (String)it.next();
					System.out.println("Name: " + cTypeName);
					String itemEl = "channels.channel(" + i + ").itemFieldMap.itemElement(1)";
					System.out.println(itemEl + " : " + config.getString(itemEl));
					i++;
				}
			}
		}
		catch(ConfigurationException cex)
		{
			cex.printStackTrace();
		}
	}
}
