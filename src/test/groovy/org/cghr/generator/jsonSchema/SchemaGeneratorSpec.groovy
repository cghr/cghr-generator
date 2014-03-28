package org.cghr.generator.jsonSchema
import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.test.db.MockData
import org.cghr.generator.transformer.EntityTransformer
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 27/3/14.
 */
class SchemaGeneratorSpec extends Specification {

    SchemaGenerator schemaGenerator
    @Shared
    List tablesWithEntities = ['dataDict']
    @Shared
    String tableWithPropertyItemInfo = 'clabel'
    @Shared
    String templateLocation = 'templates/jsonSchema.hbs'
    @Shared
    String expectedJsonStruct = 'testResources/jsonSchema.expected'


    def setupSpec() {

    }

    def setup() {

        MockData mockData = new MockData()


        Sql gSql = Stub() {

            rows('select distinct entity from dataDict') >> [[entity: 'country']]

            rows("select name,type,value,valdn,flow,clabel from dataDict where entity=?", ['country']) >> mockData.schemaSampleData.country.properties
            //rows("select name,type from dataDict where entity=?", ['state']) >> mockData.entitySampleData.state.properties
        }
        EntityTransformer entityTransformer = Stub() {

            transform(mockData.entitySampleData.country) >> mockData.transformedEntitySampleData.country
            //transform(mockData.entitySampleData.state) >> mockData.transformedEntitySampleData.state

        }
        Generator generator = Stub() {
            generate(templateLocation, [entities: [
                    mockData.transformedEntitySampleData.country
                    //mockData.transformedEntitySampleData.state
            ]]) >> new File(expectedJsonStruct).text

        }

        schemaGenerator = new SchemaGenerator(gSql, entityTransformer, generator, templateLocation)
    }



    @Ignore
    def "should generate dbStructure from a given list of entities"() {

        given:
        String expectedSchemaStruct = new File(expectedJsonStruct).text.replaceAll("\\s", "")

        expect:
        schemaGenerator.generate(tablesWithEntities, tableWithPropertyItemInfo).replaceAll("\\s", "") == expectedJsonStruct


    }

}