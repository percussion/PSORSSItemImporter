<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
	<xsl:template match="/">
		<itemFieldMap>
			<field itemElement="sx:sync" itemAttribute="id" rxField="rss_syncid" dataType="text"/>
			<field itemElement="sx:sync" itemAttribute="version" rxField="rss_versionid" dataType="text"/>
			<field itemElement="title" rxField="sys_title" dataType="text"/>
			<field itemElement="guid" rxField="rss_guid" dataType="text"/>		
			<xsl:apply-templates select="//PSXContentEditorMapper/PSXUIDefinition/PSXDisplayMapper/PSXDisplayMapping"/>
		</itemFieldMap>	
	</xsl:template>
	<xsl:template match="PSXDisplayMapping[not(FieldRef='sys_title')]">
		<!-- PSORSSItemImporter does not support child fields -->
		<xsl:if test="not(PSXDisplayMapper)">
			<xsl:variable name="FieldRef" select="FieldRef"/>
			<field itemElement="{concat('psx:', $FieldRef)}" itemAttribute="" rxField="{$FieldRef}">
				<xsl:attribute name="dataType">
					<xsl:value-of select="//PSXContentEditorMapper/PSXFieldSet/PSXField[@name=$FieldRef]/DataType"/>
				</xsl:attribute>
				<xsl:attribute name="defaultValue">
					<xsl:value-of select="//PSXContentEditorMapper/PSXFieldSet/PSXField[@name=$FieldRef]/DefaultValue/DataLocator/PSXTextLiteral/text"/>
				</xsl:attribute>			
			</field>			
		</xsl:if>
	</xsl:template>
	<xsl:template match="PSXDisplayMapping"/>
</xsl:stylesheet>