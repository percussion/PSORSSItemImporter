package com.percussion.pso.rssimport;

import java.util.Map;

import com.percussion.server.IPSRequestContext;

/**
 * <p>
 * This interface is intended to be used for creating classes that
 * can be run immediately after an item is imported, prior to any 
 * workflow transitions occur (if any).  These classes can be "plugged in"
 * to the rssimport-config.xml file in a nodeset such as the one below:
 * </p>
 * <p>
 * &lt;postImportExtensions&gt;<br/>
 *     &lt;extension className="com.percussion.pso.nw.rssimport.postprocessing.InsertAuthorAfterImport"&gt;<br/>
 *			&lt;param name="childIdFieldName" value="primeauthor"/&gt;<br/>
 *			&lt;param name="relationshipType" value="Active Assembly"/&gt;<br/>
 *			&lt;param name="slotId" value="5004"/&gt;<br/>
 *			&lt;param name="variantId" value="312"/&gt;<br/>
 *		&lt;/extension&gt;<br/>
 * &lt;/postImportExtensions&gt;<br/>
 * </p> 
 * <p>
 * These extensions are, among other things, intended to replace "post" exits 
 * that are typically added to Rhythmyx content editors, as the server will not
 * execute these following the internal request that the importer makes to the 
 * resource.
 * </p>
 * 
 * @author JeffLarimer
 */
public interface IPSORSSImportPostProcessor {

	/**
	 * Method for processing an item after the importer has successfully inserted
	 * or updated it
	 * @param item The ojbect that was used to create or update the item (via
	 * its super class' PSServerItem.save() method.  This has all fields available
	 * for processing
	 * @param itemStatus The object storing the status of the import.
	 * @param params The parameters that were registered for an extension that
	 * implements this interface
	 * @param req The Rhythmyx request context
	 */
	public void processItem(PSORSSItem item, 
			PSORSSItemStatus itemStatus, Map params, IPSRequestContext req);
}
