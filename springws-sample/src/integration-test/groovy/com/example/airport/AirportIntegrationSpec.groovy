package com.example.airport

import grails.test.mixin.integration.Integration
import grails.transaction.*
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.Diff
import spock.lang.*
import org.grails.plugins.springws.client.WebServiceTemplate

@Integration
@Rollback
class AirportIntegrationSpec extends Specification {
    def webServiceTemplate
    private final static String SERVICE_URL = "http://localhost:8080/airport-service-web/services"
    private static String NAMESPACE = "http://mycompany.com/airport/airportService"

    def setup() {
        webServiceTemplate = new WebServiceTemplate()
    }

    private def withEndpointRequest(Closure payload) {
        def writer = new StringWriter()
        def request = new MarkupBuilder(writer)
        payload.delegate = request
        payload.call()

        webServiceTemplate.sendToEndpoint(SERVICE_URL, writer.toString())
    }

    void "test get airports SOAP service is called"() {
        when:
        def result = withEndpointRequest {
            GetAirportsRequest(xmlns: NAMESPACE)
        }

        and:
        def expected = new StringWriter()
        new MarkupBuilder(expected).
            GetAirportsResponse(xmlns: "http://mycompany.com/airport/airportService") {
                Airports(xmlns: "http://mycompany.com/airport/airport") {
                    [
                        ['KCMH', 'Port Columbus', 'Columbus', 'Ohio'],
                        ['KDEN', 'Denver Intl', 'Denver', 'Colorado'],
                        ['KDFW', 'Dallas Fort Worth Intl', 'Dallas', 'Texas'],
                        ['KPHL', 'Philadelphia Intl', 'Philadelphia', 'Pennsylvania']
                    ].each { info ->
                        Airport {
                            ICAOCode(info[0])
                            Name(info[1])
                            City(info[2])
                            State(info[3])
                        }
                    }
                }
            }

        then:
        def xmlDiff = new Diff(result.toString(), expected.toString())
        xmlDiff.identical()
    }
}
