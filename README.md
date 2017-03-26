# Introduction

These are a selection of libraries for Java which has been original written by
"Matthew Johnson" (http://www.matthew.ath.cx/projects/java/)

As libmatthew-java does not seem to be maintained by the original author
anymore, I have taken the latest sources (libmatthew-java v0.8: 
http://www.matthew.ath.cx/projects/java/libmatthew-java-0.8.tar.gz).

The last single distribution of libmatthew-java v0.8 contains the following
versions of the included artifacts:
* debug: 1.1
* unix: 0.5
* cgi: 0.6
* io: 0.1
* hexdump: 0.2

The latest official version has been released under the expat licence.
This license is kept for further development.

# Unix Sockets Library

This is a collection of classes and native code to allow you to read and write
Unix sockets in Java. 

# Debug Library

This is a comprehensive logging and debugging solution. 

# CGI Library

This is a collection of classes and native code to allow you to write CGI
applications in Java. 

# I/O Library

This provides a few much needed extensions to the Java I/O subsystem. Firstly,
there is a class which will connect and InputStream with an OutputStream and
copy data between them. 

Secondly there are two classes for inserting into an Input or OutputStream pipe
a command line command, so that everything is piped through that command. 

Thirdly there are a pair of classes for splitting streams in two. This can
either be to two OuputStreams, or to an OutputStream and a file. Equivelent to
the UNIX tool tee in UNIX pipes. 

# Hexdump

This class formats byte-arrays in hex and ascii for display.
