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
    List tablesWithEntities = ['primaryDbStruct', 'dataDict']


    def setupSpec() {

    }

    def setup() {

        MockData mockData = new MockData()

        String templateLocation = 'templates/db.hbs'
        Sql gSql = Stub() {
            rows('select distinct entity from primaryDbStruct') >> [[entity: 'user'], [entity: 'userlog']]
            rows('select distinct entity from dataDict') >> [[entity: 'country'], [entity: 'state']]

            rows("select name,type from primaryDbStruct where entity=? and type!='heading'", ['user']) >> mockData.entitySampleData.user.properties
            rows("select name,type from primaryDbStruct where entity=? and type!='heading'", ['userlog']) >> mockData.entitySampleData.userlog.properties

            rows("select name,type from dataDict where entity=? and type!='heading'", ['country']) >> mockData.entitySampleData.country.properties
            rows("select name,type from dataDict where entity=? and type!='heading'", ['state']) >> mockData.entitySampleData.state.properties
        }
        EntityTransformer entityTransformer = Stub() {
            transform(mockData.entitySampleData.user) >> mockData.transformedEntitySampleData.user
            transform(mockData.entitySampleData.userlog) >> mockData.transformedEntitySampleData.userlog
            transform(mockData.entitySampleData.country) >> mockData.transformedEntitySampleData.country
            transform(mockData.entitySampleData.state) >> mockData.transformedEntitySampleData.state

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
        dbGenerator.generate(tablesWithEntities).replaceAll("\\n", "") == expectedDbStruct




    }

}