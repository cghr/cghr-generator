package org.cghr.generator.jsonSchema
import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.test.db.MockData
import org.cghr.generator.transformer.EntityTransformer
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
            rows("select name,type,valdn,label,flow from dataDict where entity=?", ['country']) >> mockData.dataDictSchema.country.properties
            rows("select clabel from dataDict where name=?",['continent']) >> [[clabel:'continent']]
            rows("select text,value from  clabel where name=?",['continent']) >> mockData.transformedDataDictSchema.country.properties[3].items


        }
        EntityTransformer entityTransformer = Stub() {


            transform(mockData.dataDictSchema.country) >> mockData.transformedDataDictSchema.country


        }
        Generator generator = Stub() {
            generate(templateLocation, mockData.transformedDataDictSchema.country) >> new File(expectedJsonStruct).text

        }

        schemaGenerator = new SchemaGenerator(gSql, entityTransformer, generator, templateLocation)
    }



    def "should generate dbStructure from a given list of entities"() {

        given:
        String expectedSchemaStruct = new File(expectedJsonStruct).text.replaceAll("\\s", "")

        expect:
        schemaGenerator.generate(tablesWithEntities, tableWithPropertyItemInfo).replaceAll("\\s", "") == expectedSchemaStruct



    }

}