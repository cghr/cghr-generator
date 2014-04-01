package org.cghr.generator.webservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification


/**
 * Created by ravitej on 31/3/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class WebServiceGeneratorIntegrationSpec extends Specification {

    String templateLocation = 'templates/webservice'
    String expectedStructure = 'testResources/webservice.expected'

    @Autowired
    WebServiceGenerator webserviceGeneratorWithMock

    def setup() {

    }

    def "should generate a web service from a given data set"() {

        given:
        String expectedWebService = new File(expectedStructure).text.replaceAll("\\s+", "")
        String designTable = "webserviceDesign"

        expect:
        webserviceGeneratorWithMock.generate(designTable, templateLocation).replaceAll("\\s+", "") == expectedWebService

    }


}