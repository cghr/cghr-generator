package org.cghr.generator

import com.github.jknack.handlebars.Handlebars
import org.cghr.generator.test.db.MockData
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 25/3/14.
 */

class GeneratorSpec extends Specification {


    Generator generator
    @Shared
    String templateBasePath = 'templates/'

    @Shared
    String expectedBasePath = 'testResources/'

    @Shared
    Map dbTemplateData, webserviceTemplateData, jsonSchemaTemplateData
    @Shared
    String dbTemplateLocation, webserviceTemplateLocation, jsonSchemaTemplateLocation
    @Shared
    String expectedDbStructure, expectedWebserviceStructure, expectedJsonSchemaStructure

    def setupSpec() {


        MockData mockData = new MockData()
        Map transformedEntitySampleData = mockData.transformedEntitySampleData
        dbTemplateData = [entities: [
                transformedEntitySampleData.user,
                transformedEntitySampleData.userlog,
                transformedEntitySampleData.country,
                transformedEntitySampleData.state
        ]];
        jsonSchemaTemplateData = [
                onSuccess: "newState",
                properties: [
                        [name: 'datastore', type: 'hidden', value: 'country'],
                        [name: 'country_id', type: 'hidden', value: '$stateParams.countryId'],
                        [name: 'name', type: 'text', label: 'Name', valdn: 'required',flow:"data.prop1 == 'yes' && data.prop2=='No'"],
                        [name: 'continent', type: 'radio', label: 'Continent', valdn: 'required', items: [
                                [text: 'Asia', value: 'asia'],
                                [text: 'Europe', value: 'europe'],
                                [text: 'Africa', value: 'africa']
                        ]]

                ]
        ];

        webserviceTemplateData = [package: 'org.cghr.hc.service', reports:
                [
                        [title: 'Areas', mapping: '/area', sql: 'select * from area', filters: '#text_filter,#text_filter', sortings: 'int,str', pathVariables: []],
                        [title: 'Houses', mapping: '/area/{areaId}/house', sql: 'select * from house where areaId=?', filters: '#text_filter,#text_filter', sortings: 'int,str', pathVariables: ['areaId']]

                ]
        ];

        dbTemplateLocation = templateBasePath + 'db'
        jsonSchemaTemplateLocation = templateBasePath + 'jsonSchema'
        webserviceTemplateLocation = templateBasePath + 'webservice'

        expectedDbStructure = expectedBasePath + 'db.expected'
        expectedJsonSchemaStructure = expectedBasePath + 'jsonSchema.expected'
        expectedWebserviceStructure = expectedBasePath + 'webservice.expected'

    }

    def setup() {

        Handlebars handlebars = new Handlebars();
        generator = new Generator(handlebars)

    }

    def "should generate db structure sql queries from a given dataset"() {

        given:
        File dbStruct = new File(expectedDbStructure)


        expect:
        generator.generate(dbTemplateLocation, dbTemplateData).replaceAll("\\n", "") == dbStruct.text.replaceAll("\\n", "");

    }



    def "should generate json schema structure from a given dataset"() {

        given:
        File jsonStruct = new File(expectedJsonSchemaStructure)


        expect:
        generator.generate(jsonSchemaTemplateLocation, jsonSchemaTemplateData).replaceAll("\\s+", "") == jsonStruct.text.replaceAll("\\s+", "")


    }

    def "should generate web service structure from a given dataset"() {
        given:
        File webServiceStruct = new File(expectedWebserviceStructure)

        expect:
        //Ignoring all white Spaces
        generator.generate(webserviceTemplateLocation, webserviceTemplateData).replaceAll("\\s+", "") == webServiceStruct.text.replaceAll("\\s+", "")

    }
}