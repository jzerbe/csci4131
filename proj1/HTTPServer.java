
/**
 * HTTPServer object is a subclass of thread
 * For every request, an HTTPServer object is created to handle that request
 *
 * serves content from "webdir" directory relative to running instance
 *
 * run: java HTTPServer 1201
 * access: http://[host]:1201/
 */
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Anand Tripathi [Univeristy of Minnesota] (original example)
 * @author Jason Zerbe (assignment contributor)
 */
public class HTTPServer extends Thread {

    //read-only header constants
    public static final String kIfModifiedSinceStr = "If-Modified-Since";
    public static final String kIfUnmodifiedSinceStr = "If-Unmodified-Since";
    //internal objects
    protected Socket myServerSocket = null;
    protected static String myRootPath = "webdir"; //relative to HTTPServer

    HTTPServer(Socket theSocket) {
        System.out.println("DEBUG: spawn process for new client");
        this.myServerSocket = theSocket;
        //myRootPath = System.getProperty("user.home") + "/webdir";
        System.out.println("DEBUG: server rootpath = " + myRootPath);
    }

    /**
     * Main thread waits for a new TCP connection request to arrive and
     * for each connection, it creates a new HTTPServer objecct to
     * handle that request
     *
     * @param args String[]
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            throw new RuntimeException("Syntax: HTTPServer [port number > 1200]");
        }
        int aServerPortNum = Integer.parseInt(args[0]);
        if (aServerPortNum <= 1200) {
            throw new RuntimeException("Syntax: HTTPServer [port number > 1200]");
        }

        System.out.println("Starting server on port " + args[0]);
        System.out.println("Server rootpath = " + myRootPath);
        ServerSocket server = new ServerSocket(aServerPortNum);

        while (true) {
            System.out.println("Waiting for client request");
            Socket client = server.accept();
            System.out.println("*********************************");
            System.out.println("Received request from " + client.getInetAddress());

            /* Create a new instance of HTTPServer to handle the connection on "client" socket */
            HTTPServer c = new HTTPServer(client);
            c.start();
        }
    }

    /* the thread that is started when a new instance is invoked */
    @Override
    public void run() {
        /* setup HTTP server input and output */
        InputStream aInputStream = null;
        try {
            aInputStream = myServerSocket.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
            if (aInputStream == null) {
                return;
            }
        }
        BufferedReader inLine = new BufferedReader(new InputStreamReader(aInputStream));
        OutputStream myResponseOutputStream = null;
        try {
            myResponseOutputStream = myServerSocket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
            if (myResponseOutputStream == null) {
                return;
            }
        }
        PrintStream myResponsePrintStream = new PrintStream(myResponseOutputStream);

        /* the tokenizer we will use throughout */
        StringTokenizer myStringTokenizer = null;


        /* process first HTTP line - [method] [myRequestUri] [myRequestProtocol] */
        String requestLine = null;
        try {
            requestLine = inLine.readLine();
        } catch (IOException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.WARNING, null, ex);
            if (requestLine == null) {
                return;
            }
        }
        System.out.println(requestLine);

        myStringTokenizer = new StringTokenizer(requestLine);

        String myRequestMethod = myStringTokenizer.nextToken();   // [GET|HEAD]
        String myRequestUri = myStringTokenizer.nextToken();  // [URI]
        String myRequestProtocol = myStringTokenizer.nextToken();  // [1.1|1.0]

        URL myResourceURL = null;
        String myFilePath;
        if (myRequestUri.startsWith("http")) {
            try {
                myResourceURL = new URL(myRequestUri); //this is full URL
            } catch (MalformedURLException ex) {
                Logger.getLogger(HTTPServer.class.getName()).log(Level.WARNING, null, ex);
                if (myResourceURL == null) {
                    return;
                }
            }
            myFilePath = myResourceURL.getPath();
            System.out.println("DEBUG: absolute URL has filepath = " + myFilePath);
        } else {
            myFilePath = myRequestUri;
        }

        System.out.println("Request=" + myRequestMethod + "; Resource=" + myFilePath + "; Protocol=" + myRequestProtocol);

        String myFullResourcePath;
        if (myFilePath.equals("/") || myFilePath.equals("")) {
            myFullResourcePath = myRootPath + "/index.html";
        } else {
            myFullResourcePath = myRootPath + myFilePath;
        }

        System.out.println("DEBUG: the full resource path = " + myFullResourcePath);

        /* handle remaining headers no matter what order they are in */
        HashMap<String, String> myRequestHeaders = new HashMap<String, String>();
        try {
            requestLine = inLine.readLine();
        } catch (IOException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.WARNING, null, ex);
        }
        while (requestLine.length() != 0) {
            myStringTokenizer = new StringTokenizer(requestLine); //parse header line
            String aRequestHeaderKey = myStringTokenizer.nextToken(":").trim();
            String aRequestHeaderValue = requestLine.replaceFirst(aRequestHeaderKey, "").replaceFirst(":", "").trim();

            myRequestHeaders.put(aRequestHeaderKey, aRequestHeaderValue); //add to hashmap for use later

            System.out.println(aRequestHeaderKey + ": " + aRequestHeaderValue); //finally print out the parsed line

            //pull down the next header line
            try {
                requestLine = inLine.readLine();
            } catch (IOException ex) {
                Logger.getLogger(HTTPServer.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        /* Create a new instance of GMT  time formatter   */
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        TimeZone tz = TimeZone.getTimeZone("GMT");
        df.setTimeZone(tz);

        System.out.println("***-*-*-*-*- Sending HTTP Response Message -*-*-*-***");

        /* check if the request method is supported */
        if (myRequestMethod.equals("GET") || myRequestMethod.equals("HEAD")) {

            /* setup file that is being requested */
            File aResourceFile = new File(myFullResourcePath);

            if (myFullResourcePath.endsWith(".class")) { //dynamic class execution
                System.out.println("HTTP/1.1 200 OK");
                myResponsePrintStream.println("HTTP/1.1 200 OK");

                System.out.println("Date: " + df.format(new Date()));
                myResponsePrintStream.println("Date: " + df.format(new Date()));

                System.out.println("Content-Type: image/jpeg; charset=ISO-8859-1");
                myResponsePrintStream.println("Content-Type: image/jpeg; charset=ISO-8859-1");

                System.out.println("Accept-Ranges: bytes");
                myResponsePrintStream.println("Accept-Ranges: bytes");

                System.out.println("Connection: close");
                myResponsePrintStream.println("Connection: close");

                System.out.println("Transfer-Encoding: chunked");
                myResponsePrintStream.println("Transfer-Encoding: chunked");

                myResponsePrintStream.println();

                //setup and start process
                int aLastSlash = myFullResourcePath.lastIndexOf("/");
                String aFilename = myFullResourcePath.substring(aLastSlash + 1);
                Process aProcess = null;
                try {
                    aProcess = Runtime.getRuntime().exec("java " + aFilename.replace(".class", ""));
                } catch (IOException ex) {
                    Logger.getLogger(HTTPServer.class.getName()).log(Level.WARNING, null, ex);

                    System.out.println("0");
                    myResponsePrintStream.println("0");

                    System.out.println("DEBUG - error with exec");
                }

                //get and output process STDOUT buffer
                DataInputStream aProcessStdBinOutput = new DataInputStream(aProcess.getInputStream()); //read raw binary data
                BufferedReader aProcessStdErrorOutput = new BufferedReader(new InputStreamReader(aProcess.getErrorStream()));

                byte inputBuffer[] = new byte[1024];
                int inputBufferLen = 0;
                try {
                    while ((inputBufferLen = aProcessStdBinOutput.read(inputBuffer)) > -1) {
                        System.out.println(Integer.toHexString(inputBufferLen));
                        myResponsePrintStream.println(Integer.toHexString(inputBufferLen));

                        System.out.write(inputBuffer, 0, inputBufferLen);
                        System.out.println();
                        myResponsePrintStream.write(inputBuffer, 0, inputBufferLen);
                        myResponsePrintStream.println();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);

                    System.out.println("0");
                    myResponsePrintStream.println("0");

                    System.out.println("DEBUG - error reading from STDOUT of subprocess");
                }

                System.out.println("0");
                myResponsePrintStream.println("0");

                //output any errors to debug terminal
                String aErrorStr = null;
                try {
                    while ((aErrorStr = aProcessStdErrorOutput.readLine()) != null) {
                        System.out.println("DEBUG - " + aErrorStr);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (myFullResourcePath.contains("csci4131")) {
                System.out.println("HTTP/1.1 301 Moved Permanently");
                myResponsePrintStream.println("HTTP/1.1 301 Moved Permanently");

                System.out.println("Date: " + df.format(new Date()));
                myResponsePrintStream.println("Date: " + df.format(new Date()));

                System.out.println("Content-Type: text/plain; charset=ISO-8859-1");
                myResponsePrintStream.println("Content-Type: text/plain; charset=ISO-8859-1");

                System.out.println("Accept-Ranges: bytes");
                myResponsePrintStream.println("Accept-Ranges: bytes");

                System.out.println("Location: http://www-users.cselabs.umn.edu/classes/Spring-2012/csci4131/");
                myResponsePrintStream.println("Location: http://www-users.cselabs.umn.edu/classes/Spring-2012/csci4131/");

                System.out.println("Connection: close");
                myResponsePrintStream.println("Connection: close");

                myResponsePrintStream.println();

                myResponsePrintStream.println("resource has moved permanently");
            } else if (!aResourceFile.exists()) {
                System.out.println("HTTP/1.1 404 Not found");
                myResponsePrintStream.println("HTTP/1.1 404 Not found");

                System.out.println("Date: " + df.format(new Date()));
                myResponsePrintStream.println("Date: " + df.format(new Date()));

                System.out.println("Content-Type: text/plain; charset=ISO-8859-1");
                myResponsePrintStream.println("Content-Type: text/plain; charset=ISO-8859-1");

                System.out.println("Accept-Ranges: bytes");
                myResponsePrintStream.println("Accept-Ranges: bytes");

                System.out.println("Connection: close");
                myResponsePrintStream.println("Connection: close");

                myResponsePrintStream.println();

                myResponsePrintStream.println("resource does not exist");
            } else if (!aResourceFile.canRead()) {
                System.out.println("HTTP/1.1 403 Forbidden");
                myResponsePrintStream.println("HTTP/1.1 403 Forbidden");

                System.out.println("Date: " + df.format(new Date()));
                myResponsePrintStream.println("Date: " + df.format(new Date()));

                System.out.println("Content-Type: text/plain; charset=ISO-8859-1");
                myResponsePrintStream.println("Content-Type: text/plain; charset=ISO-8859-1");

                System.out.println("Accept-Ranges: bytes");
                myResponsePrintStream.println("Accept-Ranges: bytes");

                System.out.println("Connection: close");
                myResponsePrintStream.println("Connection: close");

                myResponsePrintStream.println();

                myResponsePrintStream.println("resource is unable to be read by the server");
            } else if (aResourceFile.isFile()) {
                boolean myEntityEmptyBoolean = false;

                if (myRequestMethod.equals("HEAD")) {
                    myEntityEmptyBoolean = true;
                }

                String mimeType = getMimeType(myFullResourcePath);
                String mimeTypeArray[] = mimeType.split("/");

                if (myRequestHeaders.containsKey("Accept")
                        && (!myRequestHeaders.get("Accept").contains(mimeType)
                        && !myRequestHeaders.get("Accept").contains(mimeTypeArray[0] + "/*")
                        && !myRequestHeaders.get("Accept").contains("*/*"))) {
                    System.out.println("HTTP/1.1 406 Not Acceptable");
                    myResponsePrintStream.println("HTTP/1.1 406 Not Acceptable");
                    myEntityEmptyBoolean = false; //RFC: HTTP/1.1 servers allowed return not acceptable responses
                } else if (myRequestHeaders.containsKey(kIfModifiedSinceStr)
                        && (myRequestHeaders.get(kIfModifiedSinceStr) == null
                        ? df.format(new Date(aResourceFile.lastModified())) == null
                        : myRequestHeaders.get(kIfModifiedSinceStr).equals(
                        df.format(new Date(aResourceFile.lastModified()))))) {
                    System.out.println("HTTP/1.1 304 Not Modified");
                    myResponsePrintStream.println("HTTP/1.1 304 Not Modified");
                    myEntityEmptyBoolean = true;
                } else if (myRequestHeaders.containsKey(kIfUnmodifiedSinceStr)
                        && (myRequestHeaders.get(kIfUnmodifiedSinceStr) == null
                        ? df.format(new Date(aResourceFile.lastModified())) != null
                        : !myRequestHeaders.get(kIfUnmodifiedSinceStr).equals(
                        df.format(new Date(aResourceFile.lastModified()))))) {
                    System.out.println("HTTP/1.1 412 Precondition Failed");
                    myResponsePrintStream.println("HTTP/1.1 412 Precondition Failed");
                    myEntityEmptyBoolean = true;
                } else {
                    System.out.println("HTTP/1.1 200 OK");
                    myResponsePrintStream.println("HTTP/1.1 200 OK");
                }

                System.out.println("Date: " + df.format(new Date()));
                myResponsePrintStream.println("Date: " + df.format(new Date()));

                System.out.println("Last-Modified: " + df.format(new Date(aResourceFile.lastModified())));
                myResponsePrintStream.println("Last-Modified: " + df.format(new Date(aResourceFile.lastModified())));

                System.out.println("Content-Length: " + aResourceFile.length());
                myResponsePrintStream.println("Content-Length: " + aResourceFile.length());

                System.out.println("Content-Type: " + mimeType + "; charset=ISO-8859-1");
                myResponsePrintStream.println("Content-Type: " + mimeType + "; charset=ISO-8859-1");

                System.out.println("Accept-Ranges: bytes");
                myResponsePrintStream.println("Accept-Ranges: bytes");

                System.out.println("Connection: close");
                myResponsePrintStream.println("Connection: close");

                myResponsePrintStream.println();

                /* output the file data - if necessary */
                if (!myEntityEmptyBoolean) {
                    FileInputStream aFileInputStream = null;
                    try {
                        aFileInputStream = new FileInputStream(myFullResourcePath);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
                        if (aFileInputStream == null) {
                            return;
                        }
                    }
                    byte buffer[] = new byte[4096];
                    int read;
                    try {
                        while ((read = aFileInputStream.read(buffer)) >= 0) {
                            myResponseOutputStream.write(buffer, 0, read);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(HTTPServer.class.getName()).log(Level.WARNING, null, ex);
                    }
                }
            } else { //nothing to do?
                System.out.println("HTTP/1.1 400 Bad Request");
                myResponsePrintStream.println("HTTP/1.1 400 Bad Request");

                System.out.println("Date: " + df.format(new Date()));
                myResponsePrintStream.println("Date: " + df.format(new Date()));

                System.out.println("Content-Type: text/plain; charset=ISO-8859-1");
                myResponsePrintStream.println("Content-Type: text/plain; charset=ISO-8859-1");

                System.out.println("Accept-Ranges: bytes");
                myResponsePrintStream.println("Accept-Ranges: bytes");

                System.out.println("Connection: close");
                myResponsePrintStream.println("Connection: close");

                myResponsePrintStream.println();

                myResponsePrintStream.println("server does not understand");
            }
        } else { //unsupported method
            System.out.println("HTTP/1.1 405 Method Not Allowed");
            myResponsePrintStream.println("HTTP/1.1 405 Method Not Allowed");

            System.out.println("Date: " + df.format(new Date()));
            myResponsePrintStream.println("Date: " + df.format(new Date()));

            System.out.println("Content-Type: text/plain; charset=ISO-8859-1");
            myResponsePrintStream.println("Content-Type: text/plain; charset=ISO-8859-1");

            System.out.println("Accept-Ranges: bytes");
            myResponsePrintStream.println("Accept-Ranges: bytes");

            System.out.println("Allow: GET, HEAD");
            myResponsePrintStream.println("Allow: GET, HEAD");

            System.out.println("Connection: close");
            myResponsePrintStream.println("Connection: close");

            myResponsePrintStream.println();

            myResponsePrintStream.println("method is not allowed in this context");
        }

        System.out.println("---------------------------------------------------");

        try {
            myServerSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.WARNING, null, ex);
            return;
        }
    }

    /**
     * getMimeType method returns the MIME type/subtype for a given file
     */
    private String getMimeType(String theRealPathStr) {
        if (theRealPathStr.endsWith(".html") || theRealPathStr.endsWith(".html")) {
            return "text/html";
        } else if (theRealPathStr.endsWith(".txt") || theRealPathStr.endsWith(".c")
                || theRealPathStr.endsWith(".pl") || theRealPathStr.endsWith(".cc")
                || theRealPathStr.endsWith(".h")) {
            return "text/plain";
        } else if (theRealPathStr.endsWith(".jpg") || theRealPathStr.endsWith(".jpeg")
                || theRealPathStr.endsWith(".jpe")) {
            return "image/jpeg";
        } else if (theRealPathStr.endsWith(".gif")) {
            return "image/gif";
        } else if (theRealPathStr.endsWith(".pdf")) {
            return "application/pdf";
        } else if (theRealPathStr.endsWith(".ps") || theRealPathStr.endsWith(".eps")) {
            return "application/postscript";
        } else if (theRealPathStr.endsWith(".ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (theRealPathStr.endsWith(".rtf")) {
            return "application/rtf";
        } else if (theRealPathStr.endsWith(".doc")) {
            return "application/msword";
        } else if (theRealPathStr.endsWith(".xls") || theRealPathStr.endsWith(".xla")
                || theRealPathStr.endsWith(".xlm") || theRealPathStr.endsWith(".xlc")) {
            return "application/vnd.ims-excel";
        } else if (theRealPathStr.endsWith(".tgz")) {
            return "application/x-compressed";
        } else if (theRealPathStr.endsWith(".class")) {
            return "";
        } else {
            return "text/plain";
        }
    }
}
