package org.cghr.generator.db

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 28/3/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class DbGeneratorIntegrationSpec extends Specification {


    @Shared
    String entityDesignTable = 'entityDesign'
    @Shared
    String dataDictTable = 'dataDict'


    @Autowired
    DbGenerator dbGeneratorWithMock

    def "should generate dbStructure from a given list of entities"() {

        given:
        String expectedDbStruct = new File('testResources/db.expected').text.replaceAll("\\n", "")


        expect:
        dbGeneratorWithMock.generate(entityDesignTable, dataDictTable).replaceAll("\\n", "") == expectedDbStruct


    }

}