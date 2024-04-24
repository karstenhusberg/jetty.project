# DO NOT EDIT THIS FILE - See: https://eclipse.dev/jetty/documentation/

[description]
Adds support for servlet specification web applications to the server classpath.
Without this, only Jetty-specific handlers may be deployed.

[environment]
ee9

[depend]
ee-webapp
ee9-servlet
ee9-security

[xml]
etc/jetty-ee9-webapp.xml

[lib]
lib/jetty-ee9-webapp-${jetty.version}.jar

[ini-template]
## Add to the environment wide default jars and packages protected or hidden from webapps.
## System (aka Protected) classes cannot be overridden by a webapp.
## Server (aka Hidden) classes cannot be seen by a webapp
## Lists of patterns are comma separated and may be either:
##  + a qualified classname e.g. 'com.acme.Foo' 
##  + a package name e.g. 'net.example.'
##  + a jar file e.g. '${jetty.base.uri}/lib/dependency.jar' 
##  + a directory of jars,resource or classes e.g. '${jetty.base.uri}/resources' 
##  + A pattern preceded with a '-' is an exclusion, all other patterns are inclusions
##
## The +=, operator appends to a CSV list with a comma as needed.
##
#jetty.webapp.addProtectedClasses+=,org.example.
#jetty.webapp.addHiddenClasses+=,org.example.

[ini]
contextHandlerClass?=org.eclipse.jetty.ee9.webapp.WebAppContext

[jpms]
add-modules:java.instrument
