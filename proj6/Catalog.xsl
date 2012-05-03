<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:cat="urn:jzerbe:proj6">
    <xsl:variable name="varTotalEnrollment"
    select="sum(/cat:courseCatalog/cat:course/cat:enrollment)" />

    <xsl:template match="/">
        <html>
            <head>
                <title>Course Catalog</title>
            </head>
            <body>
                <table>
                    <thead>
                        <tr>
                            <th>Course Number</th>
                            <th>Course Title</th>
                            <th>Enrollment Count</th>
                        </tr>
                    </thead>
                    <xsl:for-each select="cat:courseCatalog/cat:course">
                        <xsl:sort select="cat:title" data-type="text" order="ascending" />
                        <tr>
                            <td><xsl:value-of select="cat:number" /></td>
                            <td><xsl:value-of select="cat:title" /></td>
                            <td><xsl:value-of select="cat:enrollment" /></td>
                        </tr>
                    </xsl:for-each>
                </table>
                Total Enrollment = <xsl:value-of select="$varTotalEnrollment" />
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
