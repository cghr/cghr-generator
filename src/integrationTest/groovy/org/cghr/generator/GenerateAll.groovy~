package org.cghr.generator

import org.cghr.generator.dataStoreInfo.DataStoreInfoGenerator
import org.cghr.generator.db.DbGenerator
import org.cghr.generator.jsonSchema.SchemaGenerator
import org.cghr.generator.webservice.WebServiceGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by ravitej on 1/4/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class GenerateAll extends Specification {

    @Autowired
    DbGenerator dbGenerator

    @Autowired
    SchemaGenerator schemaGenerator

    @Autowired
    WebServiceGenerator webserviceGenerator

    @Autowired
    DataStoreInfoGenerator dataStoreInfoGenerator


    String entityDesignTable = 'entityDesign'
    String dataDictTable = 'dataDict'
    String entitySchemaTable = 'entitySchema'
    String entitySchemaMasterProperties = 'entitySchemaMasterProperties'
    String tableWithPropertyItemInfo = 'clabel'
    String webserviceDesignTable = 'webserviceDesign'
    String webserviceTemplateLocation = 'templates/webservice'
    File webserviceFile = new File('generated/webservice/WebService.groovy')
    File dataStoreInfoFile = new File('generated/dataStoreInfo/dataStoreInfo.groovy')

    File dbFile = new File('generated/dbStructure/db.sql')
    String schemaFolder = 'generated/schemas/'


    def setupSpec() {

    }

    def setup() {

    }

    @Ignore
    def "should generate dbStructure,json schemas and webservice "() {

        given:
        //Generate Db structure
        dbGenerator.generateToAFile(entityDesignTable, dataDictTable, dbFile)

        //Generate json Schemas
        schemaGenerator.generateToAFolder(entitySchemaTable, entitySchemaMasterProperties, dataDictTable, tableWithPropertyItemInfo, schemaFolder)

        //Generate Web service
        //webserviceGenerator.generateToAFile(webserviceDesignTable, webserviceTemplateLocation, webserviceFile)

        //Generate data store Info
        dataStoreInfoGenerator.generateToAFile(entityDesignTable, "/templates/dataStoreInfo", dataStoreInfoFile)

        File baseDir = new File('generated/schemas/')
        baseDir.listFiles().each {
            println "'" + it.name + "',"
        }


    }


}