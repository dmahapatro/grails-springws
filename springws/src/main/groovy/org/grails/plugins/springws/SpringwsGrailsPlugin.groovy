package org.grails.plugins.springws

import grails.core.GrailsApplication
import grails.plugins.*
import grails.util.GrailsClassUtils
import groovy.util.logging.Slf4j
import org.apache.ws.commons.schema.resolver.DefaultURIResolver
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.core.io.ClassPathResource
import org.springframework.ws.transport.http.MessageDispatcherServlet
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection

@Slf4j
class SpringwsGrailsPlugin extends Plugin {
    def version = "3.0.0"
    def grailsVersion = "3.1.8 > *"
    def profiles = ['plugin']
    def pluginExcludes = [
        'grails-app/endpoints/*',
    ]
    def author = "Russ Miles"
    def authorEmail = "russ@russmiles.com"
    def title = "Spring WS Plugin"
    def description = 'Spring WS Plugin'
    def documentation = "http://grails.org/SpringWs+Plugin"
    def license = "APACHE"
    def issueManagement = [system: "JIRA", url: "http://jira.grails.org/browse/GPSPRINGWS"]
    def scm = [url: "https://github.com/gpc/grails-springws"]
    def developers = [[name: "Dhiraj Mahapatro", email: "dmahapatro@netjets.com"]]
    def artefacts = [EndpointArtefactHandler, InterceptorsConfigArtefactHandler]
    def watchedResources = [
        "file:./grails-app/endpoints/**/*"
    ]
    def loadAfter = ['hibernate', 'hibernate3', 'hibernate4', 'hibernate5']

    static final ENDPOINT_BEANS = { endpoint ->
        "${endpoint.fullName}"(endpoint.clazz) { bean ->
            bean.singleton = true
            bean.autowire = "byName"
        }
    }

    static final INTERCEPTOR_BEANS = { interceptor ->
        "${interceptor.fullName}Class"(MethodInvokingFactoryBean) {
            targetObject = ref("grailsApplication")
            targetMethod = "getArtefact"
            arguments = [InterceptorsConfigArtefactHandler.TYPE, interceptor.fullName]
        }
        "${interceptor.fullName}"(interceptor.clazz) { bean ->
            bean.singleton = true
            bean.autowire = "byName"
        }
    }

    Closure doWithSpring() {
        {->
            // Add each of the endpoints
            for (endpointClass in grailsApplication.getArtefacts(EndpointArtefactHandler.TYPE)) {
                def name = endpointClass.name
                def callable = ENDPOINT_BEANS.curry(endpointClass)
                callable.delegate = delegate
                callable.call()

                def wsdlConfig = application.config.springws?.wsdl?."$name"
                if (wsdlConfig) {
                    log.debug("exporting wsdl for $name")
                    "${wsdlConfig.wsdlName ?: name}"(DefaultWsdl11Definition) {
                        schemaCollection = new CommonsXsdSchemaCollection().with {
                            uriResolver = new DefaultURIResolver()
                            xsds = (wsdlConfig.xsds) ? wsdlConfig.xsds.split(',').collect { new ClassPathResource(it as String) } : "/WEB-INF/${name}.xsd"
                            inline = wsdlConfig.inline ?: false

                            it
                        }
                        portTypeName = wsdlConfig.portTypeName ?: "${name}Port"
                        serviceName = wsdlConfig.serviceName ?: "${name}Service"
                        locationUri = wsdlConfig.locationUri ?: "${application.config.grails.serverURL ?: 'http://localhost:8080/' + application.metadata.applicationName}/services/${name}Request"
                        targetNamespace = wsdlConfig.targetNamespace ?: "${endpointClass.getClazz().namespace}/definitions"
                    }
                }
            }

            // Add each of the interceptors
            for (interceptorsClass in application.getArtefacts(InterceptorsConfigArtefactHandler.TYPE)) {
                def callable = INTERCEPTOR_BEANS.curry(interceptorsClass)
                callable.delegate = delegate
                callable.call()
            }

            // Payload mapper
            payloadRootQNameEndpointMapping(ReloadablePayloadRootQNameEndpointMapping)

            // Add Message Dispatcher Servlet
            MessageDispatcherServlet servlet = new MessageDispatcherServlet(
                applicationContext: applicationContext,
                transformWsdlLocations: true
            )

            messageDispatcherServlet(ServletRegistrationBean, servlet, "/services/*") {
                loadOnStartup = 2
            }
        }
    }

    void doWithApplicationContext() {
        reload(grailsApplication, applicationContext)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.

        if (log.debugEnabled) log.debug("onChange: ${event}")

        if (event.source.toString().endsWith('Endpoint')) {
            def newEndpoint = event.application.addArtefact(EndpointArtefactHandler.TYPE, event.source)
            beans(ENDPOINT_BEANS.curry(newEndpoint))
        } else if (event.source.toString().endsWith('Interceptors')) {
            def newInterceptor = event.application.addArtefact(InterceptorsConfigArtefactHandler.TYPE, event.source)
            beans(INTERCEPTOR_BEANS.curry(newInterceptor))
        }

        reload(event.application as GrailsApplication, event.ctx)
    }

    private reload(GrailsApplication application, applicationContext) {
        log.info("reloadEndpoints")
        def defaultMappings = [:]
        for (endpointClass in application.getArtefacts(EndpointArtefactHandler.TYPE)) {
            def endpoint = applicationContext.getBean("${endpointClass.fullName}")
            def adapter = new DefaultEndpointAdapter(endpointImpl: endpoint, name: endpointClass.logicalPropertyName)
            def requestElement
            if (GrailsClassUtils.isStaticProperty(endpointClass.getClazz(), 'requestElement')) {
                requestElement = endpointClass.getClazz().requestElement
            } else {
                requestElement = "${endpointClass.name}Request"
            }
            defaultMappings["{${endpointClass.getClazz().namespace}}${requestElement}"] = adapter
        }

        if (log.debugEnabled) log.debug("resulting mappings: ${defaultMappings}")
        applicationContext.getBean('payloadRootQNameEndpointMapping').registerEndpoints(defaultMappings)

        log.info("reloadInterceptors")
        def interceptors = []
        for (ic in application.getArtefacts(InterceptorsConfigArtefactHandler.TYPE)) {
            def interceptorClass = applicationContext.getBean("${ic.fullName}Class")
            def bean = applicationContext.getBean(ic.fullName)
            for (interceptorConfig in interceptorClass.getConfigs(bean)) {
                interceptors << new EndpointInterceptorAdapter(interceptorConfig: interceptorConfig, configClass: bean)
            }
        }

        if (log.debugEnabled) log.debug("resulting interceptors: ${interceptors}")
        applicationContext.getBean('payloadRootQNameEndpointMapping').interceptors = interceptors
    }
}
