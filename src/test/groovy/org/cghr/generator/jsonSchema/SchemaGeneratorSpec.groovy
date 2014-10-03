package org.cghr.generator.jsonSchema

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.test.db.MockSql
import org.cghr.generator.transformer.EntityTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Shared

/**
 * Created by ravitej on 27/3/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class SchemaGeneratorSpec {

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
        List multipleItems = ['select', 'multiselect', 'select-inline', 'dropdown', 'suggest', 'duration', 'ffq']
        def sql = "select name,value,type from entitySchemaMasterProperties where entity=?".toString()
        mockSql.rows(sql, [entity]).each {
            list.add(it)
        }
        sql = "select name,type,valdn,label,flow,image,crossflow,crosscheck,help,clabel from dataDict where entity=?".toString()
        mockSql.rows(sql, [entity]).each {

            def property = it

            if (multipleItems.contains(it.type)) {
                it.items = []
                sql = "SELECT  clabel FROM  dataDict WHERE entity=? and name=?"

                String clabel = mockSql.rows(sql, [entity, it.name])[0].clabel

                sql = "select text,value,valdn from clabel where name=?".toString()
                it.items = mockSql.rows(sql, [clabel])
            }
            if ("lookup" == it.type) {

                sql = "SELECT lookup from dataDict where entity=? and name=?"
                def lookupName = mockSql.firstRow(sql, [entity, it.name]).lookup


                sql = "SELECT entity,field,ref,condition from lookup where name=?".toString()
                it.lookup = mockSql.firstRow(sql, [lookupName])

            }
            if (it.crosscheck != '') {

                sql = "SELECT crossCheck from dataDict where entity=? and name=?".toString()
                def crossCheckName = mockSql.firstRow(sql, [entity, it.name]).crosscheck


                sql = "SELECT entity,field,ref,condition from crossCheck where name=?".toString()
                it.crossCheck = mockSql.firstRow(sql, [crossCheckName])
            }
            if (it.crossflow != '') {

                def crossFlowName = it.crossflow
                sql = "select entity,field,ref,condition,whereCondition from crossFlow  where name=?".toString()
                it.crossFlow = mockSql.rows(sql, [crossFlowName])

            }
            it.remove('crossflow');
            it.remove('crosscheck');
            list.add(it)


        }
        //println 'in spec '+list
        list

    }

    def transformedData(String entity) {

        List list = []

        List multipleItems = ['select', 'multiselect', 'select-inline', 'dropdown', 'suggest', 'duration', 'ffq']
        def sql = "select name,type,valdn,label,flow from jsonSchemaTemplateData where entity=?".toString()

        mockSql.rows(sql, [entity]).each {
            if (multipleItems.contains(it.type)) {
                it.items = []
                sql = "SELECT  clabel FROM  dataDict WHERE entity=? and name=?"

                String clabel = mockSql.rows(sql, [entity, it.name])[0].clabel

                sql = "select text,value,valdn from clabel where name=?".toString()
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

            sql = "select schemaName,onSave,condition,success,fail,crossEntity from entitySchema where entity=?".toString()

            firstRow(sql, ['user']) >> mockSql.firstRow(sql, ['user'])
            firstRow(sql, ['userlog']) >> mockSql.firstRow(sql, ['userlog'])
            firstRow(sql, ['country']) >> mockSql.firstRow(sql, ['country'])
            firstRow(sql, ['state']) >> mockSql.firstRow(sql, ['state'])

            sql = "select name,value,type from entitySchemaMasterProperties where entity=?".toString()
            rows(sql, ['user']) >> mockSql.rows(sql, ['user'])
            rows(sql, ['userlog']) >> mockSql.rows(sql, ['userlog'])
            rows(sql, ['country']) >> mockSql.rows(sql, ['country'])
            rows(sql, ['state']) >> mockSql.rows(sql, ['state'])

            sql = "select name,type,valdn,label,flow,image,crossflow,crosscheck,help,clabel from dataDict where entity=?".toString()
            rows(sql, ['country']) >> mockSql.rows(sql, ['country'])
            rows(sql, ['state']) >> mockSql.rows(sql, ['state'])

            sql = "SELECT  clabel FROM  dataDict WHERE entity=? and name=?".toString()
            rows(sql, ['country', 'language']) >> mockSql.rows(sql, ['country', 'language'])

            sql = "SELECT  text,value,valdn   FROM clabel WHERE name=?"
            rows(sql, ['language']) >> mockSql.rows(sql, ['language'])

            sql = "SELECT lookup from dataDict where entity=? and name=?"
            firstRow(sql, ['country', 'capital']) >> mockSql.firstRow(sql, ['country', 'capital'])


            sql = "SELECT entity,field,ref,condition from lookup where name=?".toString()
            firstRow(sql, ['capital']) >> mockSql.firstRow(sql, ['capital'])

            sql = "SELECT crossCheck from dataDict where entity=? and name=?"
            firstRow(sql, ['country', 'capital']) >> mockSql.firstRow(sql, ['country', 'capital'])

            sql = "SELECT entity,field,ref,condition from crossCheck where name=?"
            firstRow(sql, ['capitalName']) >> mockSql.firstRow(sql, ['capitalName'])

            sql = "select entity,field,ref,condition,whereCondition from crossFlow  where name=?".toString()
            rows(sql, ['countryLanguage']) >> mockSql.rows(sql, ['countryLanguage'])


        }
        EntityTransformer entityTransformer = Stub() {

            transform([schemaName: 'country.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'country.next', properties: rawDataOf('country')]) >> [schemaName: 'country.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'country.next', properties: transformedData('country')];
            transform([schemaName: 'state.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'state.next', properties: rawDataOf('state')]) >> [schemaName: 'state.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'state.next', properties: transformedData('state')]


        }
        Generator generator = Stub() {
            generate(templateLocation, [schemaName: 'country.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'country.next', properties: transformedData('country')]) >> new File(expectedJsonStruct).text

        }

        schemaGenerator = new SchemaGenerator(gSql, entityTransformer, generator, templateLocation)
    }


    def "should generate schema structure from a given list of entities"() {

        given:
        String expectedSchemaStruct = new File(expectedJsonStruct).text.replaceAll("\\s", "");
        String generated = schemaGenerator.generate(entitySchemaTable, entitySchemaMasterPropertiesTable, dataDictTable, tableWithPropertyItemInfo)[0]


        expect:
        generated.replaceAll("\\s", "") == expectedSchemaStruct


    }

}