**Spring WS Grails Plugin**

Spring WS plugin without security.
With the upgrade of the plugin to Grails 2.4.2 came across the problem of clash between older version of spring security core with that of the latest got from Spring Security Core plugin 2.0-RC4.
This version is created by removing all security related classes and beans so that it won't give a compile error when used in an app in NetJets.

**Dependency:**  
`compile ':springws:2.1.0'`

**Feature for v2.1.0**
This is an update to the older version of plugin (v 2.0.0) with the below basic feature updates:

 - No Security features
 - No dependency to spring-security-core
 - Removed all security related classes to avoid compilation issues

**Feature for v2.0.0**
This is an update to the older version of plugin (v 1.0.0) with the below basic feature updates:

 - Updated to Grails 2.4.2
 - Updated `spring-ws-core` to version `2.2.0.RELEASE` from `2.1.2.RELEASE`.
 - Removed deprecated classes
 - Endpoints with security is a breaking change (TODO: Fix one test case)

**Feature for v1.0.0**  
This is an update to the older version of [springws](http://grails.org/plugin/springws) plugin (v 0.5.0) with the below basic feature updates:

 - Removed dependencies from `lib` directory to avoid clash with project dependencies.
 - Updated `spring-ws-core` to version `2.1.2` from `1.5.8`.
 - Added bean property `inline` to wsdl config in order to add imported schemas inline the `wsdl`.
 - Added a separate [sample app](https://github.com/dmahapatro/grails-springws-sample) showing the usage of latest version of `springws` plugin.

**To Run Locally**  
 - Clone the project.
 - Run `grails maven-install` to package the plugin and push it to local `.m2` repo if avaialble.
 - Or, use the plugin location directly using `grails.plugin.location.springws="your/plugin/location"`.
