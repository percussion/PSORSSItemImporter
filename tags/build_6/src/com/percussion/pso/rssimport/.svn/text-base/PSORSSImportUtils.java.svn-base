package com.percussion.pso.rssimport;

import org.apache.log4j.Logger;

public class PSORSSImportUtils {

	/**
	 * Log for debugging.
	 */
	private static Logger m_log = Logger.getLogger("rssimporter");

	/**
	 * Logs an exception (as a Log4j ERROR), as well as
	 * updates a status object's status and its message with the exception text.
	 * @param e The exception
	 * @param statusItem The status object to update
	 */
	public static void logException(Exception e, PSORSSImportStatus statusItem)
    {
    	statusItem.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);
    	statusItem.setMessage(e.toString());
    	m_log.error(e);    	
    }
	
	/**
	 * Logs an exception (as a Log4j ERROR), as well as
	 * updates a status object's status and its message with the exception text.
	 * @param e The exception
	 * @param statusItem The status object to update
	 */	
	public static void logException(Exception e, PSORSSItemStatus statusItem)
    {
    	statusItem.setStatus(IPSORSSImportStatusMessages.STATUS_FAILURE);
    	statusItem.setMessage(e.toString());
    	m_log.error(e);    	
    }	
}
