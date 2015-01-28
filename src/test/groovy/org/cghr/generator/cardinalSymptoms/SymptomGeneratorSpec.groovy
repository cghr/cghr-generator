package org.cghr.generator.cardinalSymptoms

import groovy.sql.Sql
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 11/10/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class SymptomGeneratorSpec extends Specification {

    @Autowired
    Sql vaSql
    SymptomGenerator symptomGenerator


    def setup() {
        symptomGenerator = new SymptomGenerator(vaSql)
    }

    @Ignore
    def "should generate Symptom info"() {
        given:
        symptomGenerator.generateSymptoms()
        symptomGenerator.generateProbingInfo()

        expect:
        true == true

    }

}