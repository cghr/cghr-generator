package org.cghr.generator.dataStoreInfo

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.test.db.MockSql
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
    MockSql mockSql

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


    def setupSpec() {

    }

    def setup() {

        Sql gSql = Stub() {

            def query = "select distinct entity from entityDesign where entity!=''"
            rows(query) >> mockSql.rows(query)
            query = "select name from entityDesign where entity=? and key='primary key'"
            firstRow(query, ['user']) >> mockSql.firstRow(query, ['user'])
            firstRow(query, ['userlog']) >> mockSql.firstRow(query, ['userlog'])
            firstRow(query, ['country']) >> mockSql.firstRow(query, ['country'])
            firstRow(query, ['state']) >> mockSql.firstRow(query, ['state'])

        }
        Generator generator = Stub() {
            generate(templateLocation, dataStoreInfoTemplateData) >> new File('testResources/dataStoreInfo.expected').text

        }
        dataStoreInfoGenerator = new DataStoreInfoGenerator(gSql, generator)


    }

    def "should  generate DataStore info from a given dataset"() {

        given:
        File dataStoreInfo = new File('testResources/dataStoreInfo.expected')

        expect:
        dataStoreInfoGenerator.generate('entityDesign', 'templates/dataStoreInfo').replaceAll("\\s+", "") == dataStoreInfo.text.replaceAll("\\s+", "")


    }
}