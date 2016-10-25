# plodder

See You Tube demo Video 

[You tube demo](https://youtu.be/5vy2UgRxWkQ)

This is a Play Framework application that stores markdown pages in a Sqlite database accessed using REST services.

The intention of the application is that it is deployed as a WAR file running alongside another web application. You can create notes about your application as you develop as well as keep those notes beside the application. I was inspired by poor documentation of webservices in a WSDL when I'm after are use cases for a webservice or something akin to a hello world that makes the usage of the service clear. This is also why I hooked in the SoapUI libraries so that I could import a WSDL and generated sample requests and responses. Also with a number of security caveats it is possible to also hit a "TryIt" button and call the webservice.

There is no accounting for multi user access to this application or security of any kind. What's needed and not done yet is a facility to export the markdown documents in the Sqlite database and generate static pages. 

I'm using the excellent javascript library https://github.com/chjj/marked to actually do the presentation work here.

## building

I've checked in a demo Sqlite database with the code btw

clone the code, then
```
./activator run
```
Visit from a browser (tested with firefox and chrome)

http://127.0.0.1:9000/plodder/

## deploying as a war
```
./activator war
```

then copy file ***plodder-1.0-SNAPSHOT.war*** to your application then make sure its context path is /plodder. There is an example application cxfdemo that uses plodder here.

## create a page

Once running, I am assuming using activator run rather than as a WAR for now go to

http://127.0.0.1:9000/plodder/

From the top menu select "edit" 

The next page shows the path that the page would be saved to, use /hello and the box on the left is where the markdown source goes. To create a new page hit the ***create*** button and to update an existing page use ***save***. These 2 buttons equate to POST and PUT a page of markdown while there is a ***get*** to.

### run time environment
Visit

http://127.0.0.1:9000/plodder/markdown/home

this accesses a ***home*** page for your application. If it doesn't exist it creates a list of pages. There is no ability to edit pages now.


