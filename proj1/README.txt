simple HTTP Server in Java

Jason Zerbe
3830775

1) put assets to serve in "webdir" directory
2) compile HTTPServer.java class file: javac HTTPServer.java
3) compile ImageGen.java class file: javac ImageGen.java
4) run: java HTTPServer [port number > 1200]

ERRORS
when requesting /ImageGen.class, image output sends malformed bytes
resulting in incorrect colors
