package com.example.airport

class GetAirportsEndpoint {
    def static namespace = "http://mycompany.com/airport/airportService"

    def invoke(request, response) {
        try {
            Airport.withTransaction {
                def airportList = Airport.list()
                setGetAirportsResponseValues(response, airportList)
            }
        } catch (Exception ex) {
            println "There was an error processing the GetAirports request." + ex
        }
    }

    private setGetAirportsResponseValues(response, airportList) {
        response.GetAirportsResponse(xmlns: namespace) {
            Airports(xmlns: "http://mycompany.com/airport/airport") {
                airportList.each { airport ->
                    Airport {
                        ICAOCode(airport.icao)
                        Name(airport.name)
                        City(airport.city)
                        State(airport.state)
                    }
                }
            }
        }
    }
}