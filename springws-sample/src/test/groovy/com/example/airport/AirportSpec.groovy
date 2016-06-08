package com.example.airport

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(Airport)
class AirportSpec extends Specification {

    def setup() {
        [
            ['KCMH', 'Port Columbus', 'Columbus', 'Ohio'],
            ['KDEN', 'Denver Intl', 'Denver', 'Colorado'],
            ['KDFW', 'Dallas Fort Worth Intl', 'Dallas', 'Texas'],
            ['KPHL', 'Philadelphia Intl', 'Philadelphia', 'Pennsylvania']
        ].each {
            new Airport(icao: it[0], name: it[1], city: it[2], state: it[3]).save(flush: true)
        }
    }

    def cleanup() {
    }

    void "test something"() {
        expect:
        Airport.count() == 4
    }
}
