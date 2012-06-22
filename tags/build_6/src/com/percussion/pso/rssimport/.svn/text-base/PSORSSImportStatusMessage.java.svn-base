package com.percussion.pso.rssimport;

public class PSORSSImportStatusMessage {

	private String msg = null;
	private static PSORSSImportStatusMessage msgInstance = null;

	/**
	 * Constructor
	 */
	private PSORSSImportStatusMessage()
	{
		msgInstance = PSORSSImportStatusMessage.getInstance();		
	}
	
    public static synchronized PSORSSImportStatusMessage getInstance() 
    {
      if (msgInstance == null)
    	  msgInstance = new PSORSSImportStatusMessage();
      return msgInstance;
    }
    
    public void setMessage(String message)
    {
    	msg = message;
    }
    
    public String getMessage()
    {
    	return msg;
    }
}
