Inkstand
=======

Inkstand, a lightweight integration of RestEasy, Undertow, and Weld, based on Hammock by John Ament.

In comparison to the original hammock project, Inkstand provides the following features:
- default configuration, overridable with properties or JVM parameters
- separation of webserver and application configuration
- automatic Resource and Provider discovery per default
- exchangeable / injectable HTTP container 
- exchangeable Resteasy deployment (CDI extension)
- injectable JCR support (Jackrabbit 2)
- global alternative support (CDI extension)
- NO support for management resources

Configuration
-------------
Beyond Hammock, Inkstand provides a default configuration for the HTTP server (localhost:80) which can 
easily be overriden - either by providing JVM parameters
-Dinkstand.http.port=
-Dinkstand.http.listenaddress=
or by providing a properties file by implementing org.apache.deltaspike.core.api.config.PropertyFileConfig containing
the very same parameters. Alternatively, the parameters can be set by implementing the WebServerConfiguration interface.

Resources and Providers
-----------------------
Per default, inkstand scans the classpath for Path resources an Providers which are automatically published as REST
services. The default contextRoot can be overridden by defining the property inkstand.rest.contextRoot (see above)
or by implementing and injecting an implementation of ApplicationConfiguration. 

HTTP Container and Rest Implementation
--------------------------------------
Inkstand allows to replace the http container (default: undertow) by providing an Alternative an the Rest implementation
(default: Resteasy without security).

JCR support
-----------
For application integration, Inkstand provides injectable access to a Jackrabbit based JCR repository (OAK is planned).

Global Alternatives
-------------------
One of the more important feature is the CDI Extention GlobalAlternativeSelector. The selector allows to define 
an alternative class or stereotype in an application jar's beans.xml, which are injectable as alternative in other
bean deployment archive as long as they are annotation with @Priority. The default CDI 1.1 spec only supports the 
@Priority annotation, but does not allow to select a global alternative in a beans.xml. The feature was required for
JCR Repository providers to be injected in REST service JARs, that are defined in another jar than the actual application.
Example:
- jar1: inkstand-jcr-jackrabbit (@Produces Repository)
- jar2: my-rest-services (@Inject Repository)  
- jar3: standalone (depends: inkstand, inkstand-jcr-jackrabbit, my-rest-services, defines global alternative in beans.xml)

