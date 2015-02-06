package org.cghr.generator

import groovy.sql.Sql
import org.cghr.generator.dataStoreInfo.DataStoreInfoGenerator
import org.cghr.generator.db.DbGenerator
import org.cghr.generator.jsonSchema.SchemaGenerator
import org.cghr.generator.jsonSchema.SchemaValidator
import org.cghr.generator.routes.RouteGenerator
import org.cghr.validator.dataDict.DataDictValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 1/4/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class GenerateAll extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    DbGenerator dbGenerator
    @Autowired
    SchemaGenerator schemaGenerator
    @Autowired
    SchemaValidator schemaValidator

    @Autowired
    DataStoreInfoGenerator dataStoreInfoGenerator
    @Autowired
    RouteGenerator routeGenerator

    @Qualifier("languageCodes")
    @Autowired
    ArrayList languageCodes

    @Autowired
    DataDictValidator dataDictValidator


    String entityDesignTable = 'entityDesign'
    String dataDictTable = 'dataDict'
    String entitySchemaTable = 'entitySchema'
    String entitySchemaMasterProperties = 'entitySchemaMasterProperties'
    String tableWithPropertyItemInfo = 'clabel'
    File dataStoreInfoFile = new File('generated/dataStoreInfo/dataStoreInfo.groovy')

    File dbFile = new File('generated/dbStructure/a.sql')
    String schemaFolder = 'generated/schemas/'

    //@spock.lang.Ignore
    def "should generate dbStructure,json schemas and webservice "() {

        given:
        //Generate Db structure
        dbGenerator.generateToAFile(entityDesignTable, dataDictTable, dbFile)

        //Generate json Schemas
        schemaGenerator.generateToAFolder(schemaFolder, languageCodes)
        schemaValidator.validateAllGeneratedSchemas(schemaFolder)

        //Generate data store Info
        dataStoreInfoGenerator.generateToAFile(entityDesignTable, "/templates/dataStoreInfo", dataStoreInfoFile)

        //Generate Routes config
        //routeGenerator.generateRoutes('generated/routing/')

        List dataList = gSql.rows("select * from dataDict")
        dataDictValidator.dataList = dataList

        dataDictValidator.generateReport('templates/validationReport', 'generated/validation/report.txt')


    }


}



