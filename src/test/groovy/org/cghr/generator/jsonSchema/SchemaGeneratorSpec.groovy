package org.cghr.generator.jsonSchema

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.test.db.MockSql
import org.cghr.generator.transformer.EntityTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 27/3/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class SchemaGeneratorSpec extends Specification {

    @Autowired
    MockSql mockSql

    SchemaGenerator schemaGenerator
    @Shared
    String entitySchemaTable = 'entitySchema'
    @Shared
    String entitySchemaMasterPropertiesTable = 'entitySchemaMasterProperties'
    @Shared
    String dataDictTable = 'dataDict'
    @Shared
    String tableWithPropertyItemInfo = 'clabel'
    @Shared
    String templateLocation = 'templates/jsonSchema.hbs'
    @Shared
    String expectedJsonStruct = 'testResources/jsonSchema.expected'


    def rawDataOf(String entity) {

        List list = []
        List multipleItems = ['select', 'select-inline', 'multiselect']
        def sql = "select name,value,type from entitySchemaMasterProperties where entity=?".toString()
        mockSql.rows(sql, [entity]).each {
            list.add(it)
        }
        sql = "select name,type,valdn,label,flow from dataDict where entity=?".toString()
        mockSql.rows(sql, [entity]).each {


            if (multipleItems.contains(it.type)) {
                it.items = []
                sql = "SELECT  clabel FROM  dataDict WHERE entity=? and name=?"

                String clabel = mockSql.rows(sql, [entity, it.name])[0].clabel

                sql = "select text,value from clabel where name=?".toString()
                it.items = mockSql.rows(sql, [clabel])

            }


            list.add(it)


        }

        list

    }

    def transformedData(String entity) {

        List list = []

        List multipleItems = ['radio', 'radio-inline', 'checkbox']
        def sql = "select name,type,valdn,label,flow from jsonSchemaTemplateData where entity=?".toString()

        mockSql.rows(sql, [entity]).each {
            if (multipleItems.contains(it.type)) {
                it.items = []
                sql = "SELECT  clabel FROM  dataDict WHERE entity=? and name=?"

                String clabel = mockSql.rows(sql, [entity, it.name])[0].clabel

                sql = "select text,value from clabel where name=?".toString()
                it.items = mockSql.rows(sql, [clabel])

            }
            list.add(it)
        }


        list
    }


    def setupSpec() {

    }

    def setup() {


        Sql gSql = Stub() {


            def sql = "SELECT DISTINCT entity FROM entitySchema WHERE  entity!=''".toString()
            rows(sql) >> mockSql.rows(sql)

            sql = "select schemaName,onSave from entitySchema where entity=?".toString()

            firstRow(sql, ['user']) >> mockSql.firstRow(sql, ['user'])
            firstRow(sql, ['userlog']) >> mockSql.firstRow(sql, ['userlog'])
            firstRow(sql, ['country']) >> mockSql.firstRow(sql, ['country'])
            firstRow(sql, ['state']) >> mockSql.firstRow(sql, ['state'])

            sql = "select name,value,type from entitySchemaMasterProperties where entity=?".toString()
            rows(sql, ['user']) >> mockSql.rows(sql, ['user'])
            rows(sql, ['userlog']) >> mockSql.rows(sql, ['userlog'])
            rows(sql, ['country']) >> mockSql.rows(sql, ['country'])
            rows(sql, ['state']) >> mockSql.rows(sql, ['state'])

            sql = "SELECT name,type,valdn,label,flow FROM dataDict WHERE entity=?".toString()
            rows(sql, ['country']) >> mockSql.rows(sql, ['country'])
            rows(sql, ['state']) >> mockSql.rows(sql, ['state'])

            sql = "SELECT  clabel FROM  dataDict WHERE entity=? and name=?".toString()
            rows(sql, ['country', 'language']) >> mockSql.rows(sql, ['country', 'language'])

            sql = "SELECT  text,value   FROM clabel WHERE name=?"
            rows(sql, ['language']) >> mockSql.rows(sql, ['language'])


        }
        EntityTransformer entityTransformer = Stub() {

            transform([schemaName: 'country.basicInf', onSave: 'country.next', properties: rawDataOf('country')]) >> [schemaName: 'country.basicInf', onSave: 'country.next', properties: transformedData('country')]

            transform([schemaName: 'state.basicInf', onSave: 'state.next', properties: rawDataOf('state')]) >> [schemaName: 'state.basicInf', onSave: 'state.next', properties: transformedData('state')]


        }
        Generator generator = Stub() {
            generate(templateLocation, [schemaName: 'country.basicInf', onSave: 'country.next', properties: transformedData('country')]) >> new File(expectedJsonStruct).text
        }

        schemaGenerator = new SchemaGenerator(gSql, entityTransformer, generator, templateLocation)
    }


    def "should generate dbStructure from a given list of entities"() {

        given:
        String expectedSchemaStruct = new File(expectedJsonStruct).text.replaceAll("\\s", "");
        String generated = schemaGenerator.generate(entitySchemaTable, entitySchemaMasterPropertiesTable, dataDictTable, tableWithPropertyItemInfo)[0]


        expect:
        generated.replaceAll("\\s", "") == expectedSchemaStruct


    }

}