import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor

// Place your Spring DSL code here
beans = {
    getAirportsValidator(PayloadValidatingInterceptor) {
        schema = 'classpath:/schemas/services/airport/GetAirports.xsd'
        validateRequest = true
        validateResponse = true
    }
}
