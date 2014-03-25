package org.cghr.generator
import com.github.jknack.handlebars.Handlebars
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

        dbTemplateData = [tables: [
                [
                        name: 'country',
                        cols: [
                                [label: 'id', type: 'int'],
                                [label: 'name', type: 'varchar(100)'],
                                [label: 'continent', type: 'varchar(100)']
                        ]
                ],
                [
                        name: 'user',
                        cols: [
                                [label: 'id', type: 'int'],
                                [label: 'username', type: 'varchar(100)'],
                                [label: 'password', type: 'varchar(100)']
                        ]
                ]
        ]];
        jsonSchemaTemplateData = [
                onSuccess: "newState",
                elements: [
                        [name: 'datastore', type: 'hidden', value: 'member'],
                        [name: 'member_id', type: 'hidden', value: '$stateParams.memberId'],
                        [name: 'firstname', type: 'text', label: 'First Name', valdn: 'required'],
                        [name: 'gender', type: 'radio', label: 'Gender', valdn: 'required', items: [
                                [text: 'Male', value: 'male'],
                                [text: 'Female', value: 'female'],
                                [text: 'Others', value: 'others']
                        ]]

                ]
        ];
        webserviceTemplateData = [package:'org.cghr.hc.service',reports:
                [
                        [title:'Areas',mapping:'/area',sql:'select * from area',filters:'#text_filter,#text_filter',sortings:'int,str',pathVariables:[]],
                        [title:'Houses',mapping:'/area/{areaId}/house',sql:'select * from house where areaId=?',filters:'#text_filter,#text_filter',sortings:'int,str',pathVariables: ['areaId']]

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
        generator.generate(dbTemplateLocation, dbTemplateData).replaceAll("\\n","") == dbStruct.text.replaceAll("\\n","");


    }


    def "should generate json schema structure from a given dataset"() {
        given:
        File jsonStruct = new File(expectedJsonSchemaStructure)


        expect:
        generator.generate(jsonSchemaTemplateLocation, jsonSchemaTemplateData).replaceAll("\\s+","")== jsonStruct.text.replaceAll("\\s+","")


    }

    def "should generate web service structure from a given dataset"(){
        given:
        File webServiceStruct=new File(expectedWebserviceStructure)

        expect:
        //Ignoring all white Spaces
        generator.generate(webserviceTemplateLocation,webserviceTemplateData).replaceAll("\\s+","")==webServiceStruct.text.replaceAll("\\s+","")


    }


}