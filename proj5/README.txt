Jason Zerbe
3830775

Java Servlet RSS feed filtering script
compatible with Java 1.5+

setup on CSELabs webserver
1) unpack proj5.war if not using Tomcat deployment interface or contents of war directory and upload contents. to build a new war --> "ant clean", "ant war"
2) chmod 755 all files and directories
3) access with web-browser --> /class/tomcat/zerbe006 translates
to http://tomcat.cselabs.umn.edu:8081/zerbe006/

Uses GNU Lesser GPL licensed library HORRORss [http://code.google.com/p/horrorss/]
for XML parsing. Covered under LGPL section 5:
"A program that contains no derivative of any portion of the Library, but is
designed to work with the Library by being compiled or linked with it, is called
a 'work that uses the Library'.  Such a work, in isolation, is not a derivative
work of the Library, and therefore falls outside the scope of this License."
