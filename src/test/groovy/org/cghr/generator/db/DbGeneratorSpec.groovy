package org.cghr.generator.db

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.test.db.MockData
import org.cghr.generator.transformer.EntityTransformer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 27/3/14.
 */
class DbGeneratorSpec extends Specification {

    DbGenerator dbGenerator
    @Shared
    String entityDesignTable = 'entityDesign'
    @Shared
    String dataDictTable = 'dataDict'


    def setupSpec() {

    }

    def setup() {

        MockData mockData = new MockData()

        String templateLocation = 'templates/db.hbs'
        Sql gSql = Stub() {

            rows("SELECT DISTINCT entity FROM entityDesign  WHERE  entity!=''") >> [[entity: 'user'], [entity: 'userlog'], [entity: 'country'], [entity: 'state']]

            rows("SELECT name,type,key,strategy FROM entityDesign WHERE entity=?", ['user']) >> mockData.entityDesign.user.properties
            rows("SELECT name,type,key,strategy FROM entityDesign WHERE entity=?", ['userlog']) >> mockData.entityDesign.userlog.properties
            rows("SELECT name,type,key,strategy FROM entityDesign WHERE entity=?", ['country']) >> mockData.entityDesign.country.properties
            rows("SELECT name,type,key,strategy FROM entityDesign WHERE entity=?", ['state']) >> mockData.entityDesign.state.properties

            rows("SELECT name,type FROM dataDict WHERE entity=?  and type!='heading'", ['country']) >> mockData.dataDict.country.properties
            rows("SELECT name,type FROM dataDict WHERE entity=?  and type!='heading'", ['state']) >> mockData.dataDict.state.properties
        }
        EntityTransformer entityTransformer = Stub() {

            transform(mockData.entityDesignRawData.user) >> mockData.transformedEntitySampleData.user
            transform(mockData.entityDesignRawData.userlog) >> mockData.transformedEntitySampleData.userlog
            transform(mockData.entityDesignRawData.country) >> mockData.transformedEntitySampleData.country
            transform(mockData.entityDesignRawData.state) >> mockData.transformedEntitySampleData.state

        }
        Generator generator = Stub() {
            generate(templateLocation, [entities: [
                    mockData.transformedEntitySampleData.user,
                    mockData.transformedEntitySampleData.userlog,
                    mockData.transformedEntitySampleData.country,
                    mockData.transformedEntitySampleData.state
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