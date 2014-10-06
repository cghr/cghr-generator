package org.cghr.generator

import org.cghr.generator.dataStoreInfo.DataStoreInfoGenerator
import org.cghr.generator.db.DbGenerator
import org.cghr.generator.jsonSchema.SchemaGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 1/4/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class GenerateAll extends Specification {

    @Autowired
    DbGenerator dbGenerator

    @Autowired
    SchemaGenerator schemaGenerator


    @Autowired
    DataStoreInfoGenerator dataStoreInfoGenerator


    String entityDesignTable = 'entityDesign'
    String dataDictTable = 'dataDict'
    String entitySchemaTable = 'entitySchema'
    String entitySchemaMasterProperties = 'entitySchemaMasterProperties'
    String tableWithPropertyItemInfo = 'clabel'
    File dataStoreInfoFile = new File('generated/dataStoreInfo/dataStoreInfo.groovy')

    File dbFile = new File('generated/dbStructure/db.sql')
    String schemaFolder = 'generated/schemas/'


    def "should generate dbStructure,json schemas and webservice "() {

        given:
        //Generate Db structure
        dbGenerator.generateToAFile(entityDesignTable, dataDictTable, dbFile)

        //Generate json Schemas
        schemaGenerator.generateToAFolder(schemaFolder)

        //Generate data store Info
        dataStoreInfoGenerator.generateToAFile(entityDesignTable, "/templates/dataStoreInfo", dataStoreInfoFile)

    }


}



