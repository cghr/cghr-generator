package org.cghr.generator.controller

import groovy.sql.Sql
import org.cghr.generator.dataStoreInfo.DataStoreInfoGenerator
import org.cghr.generator.db.DbGenerator
import org.cghr.generator.jsonSchema.SchemaGenerator
import org.cghr.generator.jsonSchema.SchemaValidator
import org.cghr.generator.routes.RouteGenerator
import org.cghr.validator.dataDict.DataDictValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

import javax.sql.DataSource

/**
 * Created by ravitej on 29/1/15.
 */

@RestController
@RequestMapping("/generate")
class GeneratorService {


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

//    @Qualifier("languageCodes")
//    @Autowired
//    ArrayList languageCodes

    @Autowired
    DataDictValidator dataDictValidator

    @Qualifier("dbStructureFilePath")
    @Autowired
    String dbStructureFilePath

    @Qualifier("schemaFolder")
    @Autowired
    String schemaFolder

    @Qualifier("validationReport")
    @Autowired
    String validationReport

    @Qualifier("webAppsPath")
    @Autowired
    String webAppsPath

    @Qualifier("onlineDocs")
    @Autowired
    HashMap onlineDocs

    @Autowired
    DataSource dataSource


    String basePath = System.getProperty("basePath")

    String entityDesignTable = 'entityDesign'
    String dataDictTable = 'dataDict'
    String entitySchemaTable = 'entitySchema'
    String entitySchemaMasterProperties = 'entitySchemaMasterProperties'
    String tableWithPropertyItemInfo = 'clabel'
    File dataStoreInfoFile = new File("$basePath/generated/dataStoreInfo/dataStoreInfo.groovy")


    @RequestMapping(value = "/{appName}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    Map generate(@PathVariable String appName, @RequestBody Map data) {

        List languageCodes = data.langs
        println "Requested for app $appName"


        ["sh", "-c", "rm -rf  $basePath/csvDatabase/*.csv"].execute()
        downloadGoogleDocs(appName)


        ["sh", "-c", "rm -rf  $basePath/generated/schemas/*.json"].execute()

        println gSql.rows("select * from entityDesign")

        //Generate Db structure
        dbGenerator.generateToAFile(entityDesignTable, dataDictTable, new File(dbStructureFilePath))

        //Generate json Schemas
        schemaGenerator.generateToAFolder(schemaFolder, languageCodes)
        schemaValidator.validateAllGeneratedSchemas(schemaFolder)

        //Generate data store Info
        dataStoreInfoGenerator.generateToAFile(entityDesignTable, "/templates/dataStoreInfo", dataStoreInfoFile)

        //Generate Routes config
        //routeGenerator.generateRoutes('generated/routing/')

        List dataList = gSql.rows("select * from dataDict")
        dataDictValidator.dataList = dataList

        dataDictValidator.generateReport('templates/validationReport', validationReport)


        runScripts(appName, languageCodes)


        [status: 'success']

    }

    boolean downloadGoogleDocs(String appName) {

        String encodedData = "DQAAAO0AAAAu1enVTHz5nYYb717Rcb66c7ygZ-SnU0NYxl2ZqZ_ePNp3n56mB3y85EnJ7vHQ7CEX0AuUts9wNxWWNUGUjPuFv4QnWkIW88JEsitOXtGa-y7Vw4m2Vt1GE3mi7tv_13aVvuDWsrRUcKuE5pA15R6AIuusLjZdZuLPGoR4CvYVsZgVJJEfkyuaOvro3ndRq1WFZKYC4fcB9UtWytAYKorWJC-S4hG4iQD1u7cNp5g38_JWkqiCb7MkzvFGWcJOsXHB8RO6edb03bJz0zGU_jJboBleSdLJpJlJkyuCVlR3KkWwEL_0sLOpi9LqB7j6dos"

        onlineDocs[appName].each {
            Map file ->
                ["sh", "-c", """curl --silent --header \"Authorization: GoogleLogin auth=$encodedData\" \"$file.url\"  >> "$basePath/csvDatabase/"$file.name".csv" """].execute()
        }
        println 'Download finished\n'

    }

    boolean runScripts(String targetApp, List languageCodes) {


        println "WebApps path " + webAppsPath
        println "Target App " + targetApp



        ["sh", "-c", "cp $basePath/generated/dbStructure/a.sql $webAppsPath/$targetApp/sqlImport/"].execute()
        languageCodes.each { String languageCode ->

            println "Language code " + languageCode
            String schemaPath = languageCode ?: ""
            println "generated/schemas/$languageCode/*.json"
            ["sh", "-c", "rm $webAppsPath/$targetApp/assets/jsonSchema/$schemaPath/*.json"]
            ["sh", "-c", "cp $basePath/generated/schemas/$languageCode/*.json $webAppsPath/$targetApp/assets/jsonSchema/" + schemaPath].execute()

        }

        return true
    }

    boolean triggerDbImport(String targetApp) {


        RestTemplate restTemplate = new RestTemplate()
        try {
            restTemplate.getForObject("http://localhost:8080/$targetApp/api/app/sqlImport", String.class)
        }
        catch (ex) {
            println ex
        }

    }


}
