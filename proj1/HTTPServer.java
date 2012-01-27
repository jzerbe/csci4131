/*   Author:  Anand Tripathi, Univeristy of Minnesota,  Minneapolis  2009   */
/*   This is a highly simplified web server, with fairly limited functions. */

/*   This is a highly simplified web server, wiht failry limited functions. */
/*   Create a directory called "webdir"  in your home directory and put     */
/*   put web contents to be served in this directory.                       */
/*   Run this on some machine say sun.cs.umn.edu on some port  as follows   */
/*       java HTTPServer 3333                                               */
/*   Access from your browser with the followin URL:                        */
/*       http://sun.cs.umn.edu:3333/someData.html                           */


/*   Create a directory called "webdir"  in your home directory and put     */
/*   put web contents to be served in this directory.                       */


 
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;

/* HTTPServer object is a subclass of thread.  For every request, an HTTPServer object  */
/* is created to handle that request.                                                   */
public class HTTPServer extends Thread {
  protected Socket s;
  String rootPath;

  HTTPServer (Socket s) {   
    System.out.println ("New client.");
    this.s = s;
    rootPath =  new  String ( System.getProperty("user.home") + "/webdir"); 
    System.out.println("Web Server Rootpath = " + rootPath );
 }

  /* getMimeType method returns the MIME type/subtype for a given file */
  private String getMimeType  (String rpath )  {

   if ( rpath.endsWith(".html") || rpath.endsWith(".html") ) {
      return  "text/html";
   }
   if ( rpath.endsWith(".txt") || rpath.endsWith(".c") || rpath.endsWith(".pl") || rpath.endsWith(".cc")
        || rpath.endsWith(".h") ) {
      return  "text/plain";
   }
   if ( rpath.endsWith(".jpg") || rpath.endsWith(".jpeg") || rpath.endsWith(".jpe") ) {
      return  "image/jpeg";
   }
   if ( rpath.endsWith(".gif")  ) {
      return  "image/gif";
   }
   if ( rpath.endsWith(".pdf")  ) {
      return  "application/pdf";
   }
   if ( rpath.endsWith(".ps") || rpath.endsWith(".eps") ) {
      return  "application/postscript";
   }
   if ( rpath.endsWith(".ppt")  ) {
      return  "application/vnd.ms-powerpoint";
   }
   if ( rpath.endsWith(".rtf")  ) {
      return  "application/rtf";
   }
   if ( rpath.endsWith(".doc")  ) {
      return  "application/msword";
   }
   if ( rpath.endsWith(".xls") || rpath.endsWith(".xla") || rpath.endsWith(".xlm") || rpath.endsWith(".xlc")  ) {
      return  "application/vnd.ims-excel";
   }
   if ( rpath.endsWith(".tgz")  ) {
      return  "application/x-compressed";
   }

   return  "text/plain";
  } 

  private void insertProxyCacheControlHeaders( PrintStream ost, String fileName)  {
/*  The following  code shows how control headers can be generated by this server */
/*  to be included in response headers.                                          */
/*  This is currently disnabled by commenting out the code below.                */
/*
     if ( fileName.equals( "no-store.html" ) ) {
        System.out.println( "Cache-Control: no-store" );
        ost.println( "Cache-Control: no-store" );
     }       

     if ( fileName.equals( "no-cache.html" ) ) {
        System.out.println( "Cache-Control: no-cache" );
        ost.println( "Cache-Control: no-cache" );
     }       

     if ( fileName.equals( "private.html" ) ) {
        System.out.println( "Cache-Control: private" );
        ost.println( "Cache-Control: private" );
     }       

     if ( fileName.equals( "public.html" ) ) {
        System.out.println( "Cache-Control: public" );
        ost.println( "Cache-Control: public" );
     }       

     if ( fileName.equals( "max-age-100.html" ) ) {
        System.out.println( "Cache-Control: max-age=100" );
        ost.println( "Cache-Control: max-age=100" );
     }       

     if ( fileName.equals( "max-age-0.html" ) ) {
        System.out.println( "Cache-Control: max-age=0" );
        ost.println( "Cache-Control: max-age=0" );
     }       

     if ( fileName.equals("set-cookie.html" ) ) {
        System.out.println( "Set-Cookie: x=y" );
        ost.println( "Set-Cookie: x=y" );
     }       
*/

  }

  
  public void run () {
    StringTokenizer st;
    URL  resourceURL;

    try {
      InputStream istream = s.getInputStream ();
      BufferedReader inLine = new BufferedReader(  new  InputStreamReader(istream) );
      OutputStream ostream = s.getOutputStream ();
      PrintStream pout = new PrintStream (ostream);
      
     /********************************************************************************/
     /* Read Input Request Lines 										            */
     /* Parse the request line using StringTokenizer                                */
     /********************************************************************************/

      String resourcePath;
      String filePath;
      String requestLine =  inLine.readLine();
      System.out.println( requestLine );

      st  = new StringTokenizer(requestLine);

      String request = st.nextToken();   /*  GET, POST, HEAD */
      String uri = st.nextToken();  /*  /  URI          */
      String protocol = st.nextToken();  /* HTTP1.1 or HTTP1.0 */


      if ( uri.startsWith("http") ) {
          /*  It is a full URL.  So get the file path    */
          resourceURL =  new URL ( uri );
          filePath  = resourceURL.getPath();
          System.out.println("Absolute URL with file-path= " + filePath );
      }
      else {
          filePath =  uri;
      }

      System.out.println("Request= " + request + " Resource= " +  filePath + " Protocol= " + protocol); 

      if ( filePath.equals("/") || filePath.equals("") )  {
         resourcePath =  new  String (rootPath + "/index.html" );
      } 
      else {
        resourcePath =  new  String (rootPath + filePath );
      }

      System.out.println( "ResourcePath = " + resourcePath );  
   
      requestLine =  inLine.readLine();
      while ( requestLine.length() != 0 ) {
         System.out.println( requestLine );
         requestLine = new String (); 
         requestLine =  inLine.readLine();
     } 
         
 

    /********************************************************************************/
    /*   Generate Output for the client                                             */
    /********************************************************************************/

      File resourceFile = new File(resourcePath);

      int   lastSlash =  resourcePath.lastIndexOf( "/" );
      String fileName =  resourcePath.substring( lastSlash+1 );;
      System.out.println( "File name: " + fileName );
      

      /* Create a new instance of GMT  time formatter   */
      DateFormat df =  DateFormat.getDateInstance(  DateFormat.FULL );
	  TimeZone tz  =  TimeZone.getTimeZone( "GMT" );
      df.setTimeZone( tz );
    

      if (  ! resourceFile.isFile() ) {
          System.out.println ("***-*-*-*-*- Sending HTTP Response Message -*-*-*-***");
          System.out.println ("HTTP/1.1  404  Not found");
          pout.println ("HTTP/1.1 404 Not found");

          System.out.println("Date: "+ df.format(new Date()) );
          pout.println("Date: "+ df.format(new Date()) );

          System.out.println( "Content-Type: text/html; charset=ISO-8859-1");
          pout.println( "Content-Type: text/html; charset=ISO-8859-1");

          System.out.println( "Accept-Ranges: bytes" );
          pout.println( "Accept-Ranges: bytes" );

          System.out.println( "Connection: close" );
          pout.println( "Connection: close" );

          pout.println(); 
          s.close();
      } 
      else {
         String mimeType  = getMimeType (resourcePath );

          System.out.println ("");
          System.out.println ("***-*-*-*-*- Sending HTTP Response Message -*-*-*-***");
          System.out.println ("HTTP/1.1 200 OK");
          pout.println ("HTTP/1.1 200 OK");

          System.out.println("Date: "+ df.format(new Date()) );
          pout.println("Date: "+ df.format(new Date()) );

          System.out.println( "Last-Modified: "+  df.format(  new Date ( resourceFile.lastModified() ) ));
          pout.println( "Last-Modeified: "+  df.format(  new Date(resourceFile.lastModified() ) ) );

          System.out.println( "Content-Length: "+ resourceFile.length() );
          pout.println( "Content-Length: "+ resourceFile.length() );

          System.out.println( "Content-Type:" + mimeType  + "; charset=ISO-8859-1");
          pout.println( "Content-Type:" + mimeType  + "; charset=ISO-8859-1");

          System.out.println( "Accept-Ranges: bytes" );
          pout.println( "Accept-Ranges: bytes" );

          System.out.println( "Connection: close" );
          pout.println( "Connection: close" );
        
          insertProxyCacheControlHeaders( pout, fileName );    
              /* This method  generates and adds Cache-Control headers */
              /* Currently the generation of the cache control headers is disabled   */
          

          pout.println();
          /*  End of Response Headers    */

          FileInputStream fstream = new FileInputStream( resourcePath );    
          byte buffer[] = new byte[4096];
          int read;
          while ((read = fstream.read (buffer)) >= 0) {
             ostream.write (buffer, 0, read);
          }
      } 
      System.out.println ("Client exit.");
      System.out.println ("---------------------------------------------------");
      s.close ();  
    }  catch (IOException ex) {
        ex.printStackTrace ();
    }
     
  }
/* End of HTTPServer class                                                    */


/***********************************************************************************/
/*                    Main method                                                  */
/* Main thread waits for a new TCP  connection request  to arrive.                 */
/* For each connection, it creates a new HTTPServer objecct to handle that request */   
/***********************************************************************************/
  public static void main (String args[]) throws IOException {
    String  webroot;
    if (args.length != 1)
         throw new RuntimeException ("Syntax: HTTPServer port-number");

    System.out.println ("Starting on port " + args[0]);
    webroot =  new  String ( System.getProperty("user.home") + "/webdir"); 
    System.out.println("Web Server Rootpath = " + webroot );
    ServerSocket server = new ServerSocket (Integer.parseInt (args[0]));

    while (true) {
      System.out.println ("Waiting for a client request");
      Socket client = server.accept ();
      System.out.println("*********************************");
      System.out.println ("Received request from " + client.getInetAddress ());
      
      /* Create a new instance of HTTPServer to handle the connection on "client" socket */
      HTTPServer c = new HTTPServer(client);
      c.start ();
    }
  }
}