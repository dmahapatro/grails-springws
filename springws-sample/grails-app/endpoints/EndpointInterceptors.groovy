class EndpointInterceptors {
    def getAirportsValidator

    def interceptors = {
        getAirportsValidate(endpoint: 'getAirports', interceptors: [getAirportsValidator], validation)
    }

    def validation = {
        handleRequest = { messageContext, endpoint ->
            return true
        }
        handleResponse = { messageContext, endpoint ->
            return true
        }
        handleFault = { messageContext, endpoint ->
            return true
        }
    }
}
