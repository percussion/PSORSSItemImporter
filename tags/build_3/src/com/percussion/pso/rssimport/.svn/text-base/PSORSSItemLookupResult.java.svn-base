package com.percussion.pso.rssimport;

import com.percussion.design.objectstore.PSLocator;

public class PSORSSItemLookupResult {

	private PSLocator m_loc;
	private String m_version;
	
	public PSORSSItemLookupResult(String contentid, String revision){
		m_loc = new PSLocator(contentid, revision);
	}
	
	public PSLocator getLocator(){
		return m_loc;
	}
	
	public void setVersion(String version){
		m_version = version;
	}

	public String getVersion(){
		return m_version;
	}
}