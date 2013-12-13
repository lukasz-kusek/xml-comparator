<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ The MIT License (MIT)
  ~
  ~ Copyright (c) 2013 Lukasz Kusek
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy of
  ~ this software and associated documentation files (the "Software"), to deal in
  ~ the Software without restriction, including without limitation the rights to
  ~ use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  ~ the Software, and to permit persons to whom the Software is furnished to do so,
  ~ subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in all
  ~ copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  ~ FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  ~ COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  ~ IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  ~ CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
  -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="*">
        <xsl:apply-templates select="ancestor-or-self::*" mode="path"/>

        <xsl:choose>
            <xsl:when test="not(*)">
                <xsl:value-of select="translate(concat('&#9;',.,'&#9;'), '&#xA;', ' ')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat('&#9;','&#9;')"/>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:text>&#xA;</xsl:text>

        <xsl:apply-templates select="@*|*"/>
    </xsl:template>

    <xsl:template match="*" mode="path">
        <xsl:value-of select="concat('/',name())"/>
        <xsl:variable name="vnumPrecSiblings" select="count(preceding-sibling::*[name()=name(current())])"/>
        <xsl:variable name="vnumFollowSiblings" select="count(following-sibling::*[name()=name(current())])"/>
        <xsl:if test="$vnumPrecSiblings or $vnumFollowSiblings">
            <xsl:value-of select="concat('[', $vnumPrecSiblings +1, ']')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:apply-templates select="../ancestor-or-self::*" mode="path"/>
        <xsl:value-of select="concat('&#9;&#9;', name(), '=', normalize-space(.))"/>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>

</xsl:stylesheet>