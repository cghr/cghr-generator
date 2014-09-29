package org.cghr.generator.webservice

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.test.db.MockSql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 31/3/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class WebServiceGeneratorSpec extends Specification {

    WebServiceGenerator webServiceGenerator
    @Autowired
    MockSql mockSql
    String templateLocation = 'templates/webservice'
    String expectedStructure = 'testResources/webservice.expected'
    String webserviceTemplateData = [reports:
            [
                    [title: 'User', mapping: '/user', sql: 'select id,username from user', filters: '#text_filter,#text_filter', sortings: 'int,str', pathVariables: []],
                    [title: 'Country', mapping: '/continent/{continentId}/country', sql: 'select id,name from country where continentId=?', filters: '#text_filter,#text_filter', sortings: 'int,str', pathVariables: ['continentId']],
                    [title: 'State', mapping: '/continent/{continentId}/country/{countryId}/state', sql: 'select id,name from state where continentId=? and countryId=?', filters: '#text_filter,#text_filter', sortings: 'int,str', pathVariables: ['continentId', 'countryId']]
            ]
    ];


    def setupSpec() {

    }

    def setup() {


        Generator generator = Stub() {

            generate(templateLocation, _) >> new File(expectedStructure).text
        }
        Sql sql = Stub() {

            def sql = "select title,mapping,sql,filters,sortings from webserviceDesign"
            rows(sql) >> mockSql.rows(sql)
        }
        webServiceGenerator = new WebServiceGenerator(sql, generator)
    }


    def "should generate a web service from a given data set"() {

        given:
        String expectedWebService = new File(expectedStructure).text.replaceAll("\\s+", "")
        String designTable = "webserviceDesign"

        expect:
        webServiceGenerator.generate(designTable, templateLocation).replaceAll("\\s+", "") == expectedWebService

    }

}