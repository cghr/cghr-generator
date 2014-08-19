package org.cghr.generator

import com.github.jknack.handlebars.Handlebars
import org.cghr.generator.test.db.MockSql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 25/3/14.
 */

@ContextConfiguration(locations = "classpath:spring-context.xml")
class GeneratorSpec extends Specification {


    @Autowired
    MockSql mockSql

    Generator generator

    @Shared
    Map dbTemplateData, webserviceTemplateData, jsonSchemaTemplateData, dataStoreInfoTemplateData

    def lowerCaseKeys = {
        it.collectEntries {
            k, v ->
                [k.toLowerCase(), v]
        }
    }

    def getPropertiesDbTemplateData(String entity) {

        String sql = "select name,type,key,strategy from dbTemplateData where entity=?".toString()
        def rows = mockSql.rows(sql, [entity])
    }

    def getPropertiesJsonSchemaTemplateData(String entity) {

        List multipleItems = ['radio', 'checkbox', 'radio-inline', 'dropdown', 'suggest', 'duration']
        String sql = "select name,type,label,value,valdn,flow,image,crossFlow,crossCheck,help from jsonSchemaTemplateData where entity=?".toString()
        def rows = mockSql.rows(sql, [entity])
        rows = rows.collect {

            if (multipleItems.contains(it.type)) {
                sql = "select text,value from itemsTemplateData  where name=?".toString()
                it.items = mockSql.rows(sql, [it.name])
                // it.items=it.items.collect lowerCaseKeys

            }
            if ('lookup' == it.type) {

                sql = "SELECT lookup from dataDict where entity=? and name=?"
                def lookupName = mockSql.firstRow(sql, [entity, it.name]).lookup


                sql = "SELECT entity,field,ref from lookup where name=?".toString()
                it.lookup = mockSql.firstRow(sql, [lookupName])

            }
            if (it.crosscheck != '') {

                sql = "SELECT crosscheck from dataDict where entity=? and name=?".toString()
                def crossCheckName = mockSql.firstRow(sql, [entity, it.name]).crosscheck


                sql = "SELECT entity,field,ref,condition from crossCheck where name=?".toString()
                it.crossCheck = mockSql.firstRow(sql, [crossCheckName])


            }
            if (it.crossflow != '') {

                sql = "select entity,field,ref,condition from crossFlow  where name=?".toString()
                it.crossFlow = mockSql.rows(sql, [it.crossflow])

            }

            it

        }

    }

    def setupSpec() {


        webserviceTemplateData = [reports:
                [
                        [title: 'User', mapping: '/user', sql: 'select id,username from user', filters: '#text_filter,#text_filter', sortings: 'int,str', pathVariables: []],
                        [title: 'Country', mapping: '/continent/{continentId}/country', sql: 'select id,name from country where continentId=?', filters: '#text_filter,#text_filter', sortings: 'int,str', pathVariables: ['continentId']],
                        [title: 'State', mapping: '/continent/{continentId}/country/{countryId}/state', sql: 'select id,name from state where continentId=? and countryId=?', filters: '#text_filter,#text_filter', sortings: 'int,str', pathVariables: ['continentId', 'countryId']]
                ]
        ];

    }

    def setup() {

        dbTemplateData = [entities: [
                [name: 'user', properties: getPropertiesDbTemplateData('user')],
                [name: 'userlog', properties: getPropertiesDbTemplateData('userlog')],
                [name: 'country', properties: getPropertiesDbTemplateData('country')],
                [name: 'state', properties: getPropertiesDbTemplateData('state')]
        ]];

        jsonSchemaTemplateData = [
                schemaName: "country.basicInf",
                onSave: "country.next",
                properties: getPropertiesJsonSchemaTemplateData('country')
        ];
        dataStoreInfoTemplateData = [
                entities: [
                        [name: 'user', keyField: 'id'],
                        [name: 'userlog', keyField: 'id'],
                        [name: 'country', keyField: 'id'],
                        [name: 'state', keyField: 'id']
                ]
        ]

        Handlebars handlebars = new Handlebars();
        generator = new Generator(handlebars)

    }


    def "should generate db structure sql queries from a given dataset"() {

        given:
        File dbStruct = new File('testResources/db.expected')

        expect:
        generator.generate('templates/db', dbTemplateData).replaceAll("\\n", "") == dbStruct.text.replaceAll("\\n", "");

    }


    def "should generate json schema structure from a given dataset"() {

        given:
        File jsonStruct = new File('testResources/jsonSchema.expected')


        expect:
        generator.generate('templates/jsonSchema', jsonSchemaTemplateData).replaceAll("\\s+", "") == jsonStruct.text.replaceAll("\\s+", "")


    }

    def "should generate web service structure from a given dataset"() {
        given:
        File webServiceStruct = new File('testResources/webservice.expected')

        expect:
        //Ignoring all white Spaces
        generator.generate('templates/webservice', webserviceTemplateData).replaceAll("\\s+", "") == webServiceStruct.text.replaceAll("\\s+", "")

    }

    def "should generate dataStoreInfo from a given dataSet"() {

        given:
        File dataStoreInfo = new File('testResources/dataStoreInfo.expected')

        expect:
        generator.generate('templates/dataStoreInfo', dataStoreInfoTemplateData).replaceAll("\\s+", "") == dataStoreInfo.text.replaceAll("\\s+", "")

    }
}