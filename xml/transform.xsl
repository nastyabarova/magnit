<?xml version="1.0"?>
<xsl:transform version="2.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 
 
 <xsl:template match="/">
	 <xsl:apply-templates />
</xsl:template>
	 <xsl:template match="entries">
			 <entries>
				
				<xsl:apply-templates select="entry"/>
			 </entries>
	</xsl:template>
	
	<xsl:template match="field">
			<entry>
				<xsl:attribute name="field"><xsl:value-of select="."/></xsl:attribute>
			</entry>
		 
	</xsl:template>

</xsl:transform>