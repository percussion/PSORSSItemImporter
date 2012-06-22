package com.percussion.pso.rssimport;

public interface IPSORSSImportStatusMessages {
	
	public static final String STATUS_SUCCESS = "Success";
	public static final String STATUS_FAILURE = "Failure";
	public static final String STATUS_SKIPPED = "Skipped";
	
	public static final String MSG_CHANNEL_LOAD_SUCCESS = "Channel loaded.  Item status below:";
	public static final String MSG_CHANNEL_LOAD_FAILURE = "Failed to load the RSS channel.  See log for details";
	public static final String MSG_CHANNEL_LOAD_EMPTY_FEED = "The incoming feed was empty or not found.  See log for details";
	
	public static final String MSG_ITEM_CREATED = "Item created";
	public static final String MSG_ITEM_UPDATED = "Item updated";
	public static final String MSG_ITEM_UNPUBLISHED = "Item unpublished";
	public static final String MSG_ITEM_SAME_VERSION = "Version is the same as the existing item in Rhythmyx";
	public static final String MSG_ITEM_NOT_FOUND = "Item marked for deletion, but not found in system";
	public static final String MSG_ITEM_NOT_PUBLIC = "Item marked for deletion, but not in public workflow state (not currently public)";

}