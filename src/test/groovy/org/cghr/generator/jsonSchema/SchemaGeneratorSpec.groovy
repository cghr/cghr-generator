package org.cghr.generator.jsonSchema

import org.cghr.generator.Generator
import org.cghr.generator.sqlUtil.SqlCustom
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
class SchemaGeneratorSpec extends Specification {

    @Autowired
    MockSql mockSql
    @Autowired
    SqlCustom sqlCustom

    @Autowired
    ArrayList multipleItemTypes

    SchemaGenerator schemaGenerator

    @Shared
    String templateLocation = 'templates/jsonSchema.hbs'
    @Shared
    String expectedJsonStruct = 'testResources/jsonSchema.expected'


    def rawDataOf(String entity) {

        List list = []
        def sql = "select * from entitySchemaMasterProperties where entity=?".toString()
        mockSql.rows(sql, [entity]).each {
            list.add(it)
        }
        sql = "select * from dataDict where entity=?".toString()
        mockSql.rows(sql, [entity]).each {

            def property = it

            if (multipleItemTypes.contains(it.type)) {
                it.items = []
                sql = "SELECT  clabel FROM  dataDict WHERE entity=? and name=?"

                String clabel = mockSql.rows(sql, [entity, it.name])[0].clabel

                sql = "select * from clabel where name=?".toString()
                it.items = mockSql.rows(sql, [clabel])
            }
            if ("lookup" == it.type) {

                sql = "SELECT lookup from dataDict where entity=? and name=?"
                def lookupName = mockSql.firstRow(sql, [entity, it.name]).lookup


                sql = "SELECT * from lookup where name=?".toString()
                it.lookup = mockSql.firstRow(sql, [lookupName])

            }
            if (it.crosscheck != '') {

                sql = "SELECT crossCheck from dataDict where entity=? and name=?".toString()
                def crossCheckName = mockSql.firstRow(sql, [entity, it.name]).crosscheck


                sql = "SELECT * from crossCheck where name=?".toString()
                it.crossCheck = mockSql.firstRow(sql, [crossCheckName])
            }
            if (it.crossflow != '') {

                def crossFlowName = it.crossflow
                sql = "select * from crossFlow  where name=?".toString()
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

                sql = "select * from clabel where name=?".toString()
                it.items = mockSql.rows(sql, [clabel])

            }
            list.add(it)
        }


        list
    }

    def setup() {


        EntityTransformer entityTransformer = Stub() {

            transform([schemaName: 'country.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'country.next', properties: rawDataOf('country')]) >>
                    [schemaName: 'country.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'country.next', properties: transformedData('country')];

            transform([schemaName: 'state.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'state.next', properties: rawDataOf('state')]) >>
                    [schemaName: 'state.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'state.next', properties: transformedData('state')]

        }
        Generator generator = Stub() {
            generate(templateLocation, [schemaName: 'country.basicInf', condition: '', success: '', fail: '', crossEntity: '', onSave: 'country.next', properties: transformedData('country')]) >>
                    new File(expectedJsonStruct).text

        }

        schemaGenerator = new SchemaGenerator(sqlCustom, entityTransformer, generator, templateLocation, multipleItemTypes, "")
    }


    def "should generate schema structure from a given list of entities"() {

        given:
        String expectedSchemaStruct = new File(expectedJsonStruct).text.replaceAll("\\s", "");
        String generated = schemaGenerator.generate()[0]


        expect:
        generated.replaceAll("\\s", "") == expectedSchemaStruct


    }

}