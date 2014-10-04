package org.cghr.generator.db

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.test.db.MockSql
import org.cghr.generator.transformer.EntityTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 27/3/14.
 */

@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class DbGeneratorSpec extends Specification {


    @Autowired
    MockSql mockSql
    @Autowired
    Sql sqlMock

    DbGenerator dbGenerator
    @Shared
    String entityDesignTable = 'entityDesign'
    @Shared
    String dataDictTable = 'dataDict'


    def rawDataOf(String entity) {
        getData(entity, 'rawData')
    }

    def transformedData(String entity) {
        getData(entity, 'transformedData')
    }

    def getData(String entity, String type) {
        Map sqls = [
                rawData: ["select name,type,key,strategy from entityDesign where entity=?", "select name,type from dataDict where entity=?"],
                transformedData: ["select name,type,key,strategy from dbTemplateData where entity=?", "select name,type from jsonSchemaTemplateData where entity=?"]
        ]
        List list = (type == 'rawData') ? sqls.rawData : sqls.transformedData
        List result = []
        list.each {
            mockSql.rows(it, [entity]).each {
                row ->
                    result.add(row)
            }
        }
        return result
    }

    def setup() {


        String templateLocation = 'templates/db.hbs'

        EntityTransformer entityTransformer = Stub() {
            transform([name: 'user', properties: rawDataOf('user')]) >> [name: 'user', properties: transformedData('user')]
            transform([name: 'userlog', properties: rawDataOf('userlog')]) >> [name: 'userlog', properties: transformedData('userlog')]
            transform([name: 'country', properties: rawDataOf('country')]) >> [name: 'country', properties: transformedData('country')]
            transform([name: 'state', properties: rawDataOf('state')]) >> [name: 'state', properties: transformedData('state')]

        }
        Generator generator = Stub() {
            generate(templateLocation, [entities: [
                    [name: 'user', properties: transformedData('user')],
                    [name: 'userlog', properties: transformedData('userlog')],
                    [name: 'country', properties: transformedData('country')],
                    [name: 'state', properties: transformedData('state')]
            ]]) >> new File('testResources/db.expected').text


        }
        dbGenerator = new DbGenerator(sqlMock, entityTransformer, generator, templateLocation)
    }


    def "should generate dbStructure from a given list of entities"() {

        given:
        String expectedDbStruct = new File('testResources/db.expected').text.replaceAll("\\n", "")

        expect:
        dbGenerator.generate(entityDesignTable, dataDictTable).replaceAll("\\n", "") == expectedDbStruct


    }

}