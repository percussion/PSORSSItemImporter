package com.percussion.pso.rssimport;

import com.percussion.design.objectstore.PSLocator;

public class PSORSSItemStatus {

	private PSLocator locator;
	private String msg;
	private String status;
	private String syncId;
	private String syncVersion;
	
	public PSORSSItemStatus(String syncId, String syncVersion){
		this.syncId = syncId;
		this.syncVersion = syncVersion;
	}	
	
	public PSLocator getLocator() {
		return locator;
	}

	public String getMessage() {
		return msg;
	}

	public String getStatus() {
		return status;
	}

	public String getSyncId() {
		return syncId;
	}

	public String getSyncVersion() {
		return syncVersion;
	}

	public void setLocator(PSLocator locator) {
		this.locator = locator;
	}

	public void setMessage(String message) {
		this.msg = message;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSyncId(String syncId) {
		this.syncId = syncId;
	}

	public void setSyncVersion(String syncVersion) {
		this.syncVersion = syncVersion;
	}
	
	public void appendToMessage(String str){
		this.msg.concat(str);
	}
}