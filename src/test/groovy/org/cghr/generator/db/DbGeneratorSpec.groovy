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

    DbGenerator dbGenerator
    @Shared
    String entityDesignTable = 'entityDesign'
    @Shared
    String dataDictTable = 'dataDict'


    def rawDataOf(String entity) {

        List list = []
        def sql = "select name,type,key,strategy from entityDesign where entity=?".toString()
        mockSql.rows(sql, [entity]).each {
            list.add(it)
        }
        sql = "select name,type from dataDict where entity=?".toString()
        mockSql.rows(sql, [entity]).each {
            list.add(it)
        }


        list

    }

    def transformedData(String entity) {

        List list = []
        def sql = "select name,type,key,strategy from dbTemplateData where entity=?".toString()
        mockSql.rows(sql, [entity]).each {
            list.add(it)
        }
        sql = "select name,type from jsonSchemaTemplateData where entity=?".toString()
        mockSql.rows(sql, [entity]).each {
            list.add(it)
        }

        list
    }


    def setupSpec() {

    }

    def setup() {


        String templateLocation = 'templates/db.hbs'
        Sql gSql = Stub() {

            String sql1 = "SELECT DISTINCT entity FROM entityDesign  WHERE  entity!=''"
            rows(sql1) >> mockSql.rows(sql1)

            String sql2 = "SELECT name,type,key,strategy FROM entityDesign WHERE entity=?"
            rows(sql2, ['user']) >> mockSql.rows(sql2, ['user'])

            def sql = "SELECT distinct entityAlias from entitySchema where entity=?"
            rows(sql, ['country']) >> mockSql.rows(sql, ['country'])

            String sql3 = "SELECT name,type,key,strategy FROM entityDesign WHERE entity=?"
            rows(sql2, ['userlog']) >> mockSql.rows(sql2, ['userlog'])

            String sql4 = "SELECT name,type,key,strategy FROM entityDesign WHERE entity=?"
            rows(sql2, ['country']) >> mockSql.rows(sql2, ['country'])

            String sql5 = "SELECT name,type,key,strategy FROM entityDesign WHERE entity=?"
            rows(sql2, ['state']) >> mockSql.rows(sql2, ['state'])

            def sql6 = "SELECT distinct name,type FROM dataDict WHERE entity=?  and type!='heading'"
            rows(sql6, ['country']) >> mockSql.rows(sql6, ['country'])

            def sql7 = "SELECT distinct name,type FROM dataDict WHERE entity=?  and type!='heading'"
            rows(sql7, ['state']) >> mockSql.rows(sql6, ['state'])

        }
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

        dbGenerator = new DbGenerator(gSql, entityTransformer, generator, templateLocation)
    }


    def "should generate dbStructure from a given list of entities"() {

        given:
        String expectedDbStruct = new File('testResources/db.expected').text.replaceAll("\\n", "")


        expect:
        dbGenerator.generate(entityDesignTable, dataDictTable).replaceAll("\\n", "") == expectedDbStruct


    }

}