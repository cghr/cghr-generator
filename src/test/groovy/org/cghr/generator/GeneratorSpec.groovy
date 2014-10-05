package org.cghr.generator

import com.github.jknack.handlebars.Handlebars
import org.cghr.generator.test.db.MockSql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 25/3/14.
 */

@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class GeneratorSpec extends Specification {


    @Autowired
    MockSql sqlCustom

    Generator generator

    @Shared
    Map dbTemplateData, jsonSchemaTemplateData, dataStoreInfoTemplateData


    def getPropertiesDbTemplateData(String entity) {

        String sql = "select name,type,key,strategy from dbTemplateData where entity=?"
        sqlCustom.rows(sql, [entity])
    }

    def getPropertiesJsonSchemaTemplateData(String entity) {

        List multipleItems = ['radio', 'checkbox', 'radio-inline', 'dropdown', 'suggest', 'duration']
        String sql = "select name,type,label,value,valdn,flow,image,crossFlow,crossCheck,help from jsonSchemaTemplateData where entity=?"
        def rows = sqlCustom.rows(sql, [entity])

        rows.collect {

            if (multipleItems.contains(it.type)) {
                sql = "select text,value,valdn from itemsTemplateData  where name=?".toString()
                it.items = sqlCustom.rows(sql, [it.name])

            }
            if ('lookup' == it.type) {

                sql = "SELECT lookup from dataDict where entity=? and name=?"
                def lookupName = sqlCustom.firstRow(sql, [entity, it.name]).lookup


                sql = "SELECT entity,field,ref from lookup where name=?".toString()
                it.lookup = sqlCustom.firstRow(sql, [lookupName])

            }
            if (it.crosscheck != '') {

                sql = "SELECT crosscheck from dataDict where entity=? and name=?".toString()
                def crossCheckName = sqlCustom.firstRow(sql, [entity, it.name]).crosscheck


                sql = "SELECT entity,field,ref,condition from crossCheck where name=?".toString()
                it.crossCheck = sqlCustom.firstRow(sql, [crossCheckName])


            }
            if (it.crossflow != '') {

                sql = "select entity,field,ref,condition from crossFlow  where name=?".toString()
                it.crossFlow = sqlCustom.rows(sql, [it.crossflow])

            }

            it

        }

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
                        [name: 'user', keyfield: 'id'],
                        [name: 'userlog', keyfield: 'id'],
                        [name: 'country', keyfield: 'id'],
                        [name: 'state', keyfield: 'id']
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


    def "should generate dataStoreInfo from a given dataSet"() {

        given:
        File dataStoreInfo = new File('testResources/dataStoreInfo.expected')

        expect:
        generator.generate('templates/dataStoreInfo', dataStoreInfoTemplateData).replaceAll("\\s+", "") == dataStoreInfo.text.replaceAll("\\s+", "")

    }
}