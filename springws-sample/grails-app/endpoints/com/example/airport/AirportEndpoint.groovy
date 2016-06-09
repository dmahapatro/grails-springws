package com.example.airport

/**
 * This endpoint is required by default in order to to get the WSDL
 * DO NOT REMOVE THIS ENDPOINT although it has no usage
 * http://localhost:8080/airport-service-web/services/Airport/AirportService.wsdl
 */
class AirportEndpoint {
    def static namespace = "http://netjets.com/airport/airportService"

    def invoke(request, response) {
        null
    }
}