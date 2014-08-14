package com.percussion.pso.rssimport;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemGuidIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;

public class RSSReaderTest {

	public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException
	{
		File inpFile = new File("C://CustomerProjects//IMF//03 - Planning Phase Fall 2005//Docs//Deliverables//Systems Integration//RSSContentItemImporter//SampleRSSFeed.xml");
		System.out.println("Loaded file: " + inpFile.isFile());
		ChannelIF channel = FeedParser.parse(new ChannelBuilder(), inpFile);
		
		System.out.println("Title is: " + channel.getTitle());
		System.out.println("Description is: " + channel.getDescription());
		
		Collection items = channel.getItems();
		if(!(items.isEmpty()))
		{
			Iterator itemIt = items.iterator();
			while(itemIt.hasNext())
			{
				ItemIF item = (ItemIF)itemIt.next();
				ItemGuidIF guid = item.getGuid();

				System.out.println("GUID: " + guid.getLocation());
				System.out.println("Displaytitle: " + item.getElementValue("psx:displaytitle"));
			}
		}		
	}
}
