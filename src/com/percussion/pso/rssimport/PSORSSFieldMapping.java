package com.percussion.pso.rssimport;

import java.io.Serializable;
import java.text.DateFormat;

public class PSORSSFieldMapping implements Serializable {

	private static final long serialVersionUID = 7532202453871020482L;
	private String rxFieldName;
	private String itemElementName;
	private String itemAttributeName;
	private String dataType;
	private DateFormat dateFormat;
	private String defaultValue;

	public PSORSSFieldMapping() {
		super();
	}

	public void setRxFieldName(String name)
	{
		rxFieldName = name;
	}
	
	public String getRxFieldName()
	{
		return rxFieldName;
	}
	
	public void setItemElementName(String name)
	{
		itemElementName = name;
	}
	
	public String getItemElementName()
	{
		return itemElementName;
	}
	
	public void setItemAttributeName(String name)
	{
		itemAttributeName = name;
	}
	
	public String getItemAttributeName()
	{
		return itemAttributeName;
	}
	
	public void setDataType(String type)
	{
		dataType = type;
	}
	
	public String getDataType()
	{
		return dataType;
	}
	
	public void setDateFormat(DateFormat format)
	{
		dateFormat = format;
	}
	
	public DateFormat getDateFormat()
	{
		return dateFormat;
	}
	
	public void setDefaultValue(String defValue)
	{
		defaultValue = defValue;
	}
	
	public String getDefaultValue()
	{
		return defaultValue;
	}
	
	public int hashCode() {
		return this.rxFieldName.hashCode();
	}
}