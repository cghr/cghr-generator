package org.cghr.generator.dataStoreInfo

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 1/4/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class DataStoreInfoGeneratorSpec extends Specification {

    @Autowired
    Sql sqlMock

    DataStoreInfoGenerator dataStoreInfoGenerator

    String templateLocation = 'templates/dataStoreInfo'
    Map dataStoreInfoTemplateData = [
            entities: [
                    [name: 'user', keyField: 'id'],
                    [name: 'userlog', keyField: 'id'],
                    [name: 'country', keyField: 'id'],
                    [name: 'state', keyField: 'id']
            ]
    ]

    def setup() {

        Generator generator = Stub() {
            generate(templateLocation, dataStoreInfoTemplateData) >> new File('testResources/dataStoreInfo.expected').text

        }
        dataStoreInfoGenerator = new DataStoreInfoGenerator(sqlMock, generator)


    }

    def "should  generate DataStore info from a given dataset"() {

        expect:
        dataStoreInfoGenerator.generate('entityDesign', 'templates/dataStoreInfo') == new File('testResources/dataStoreInfo.expected').text


    }
}