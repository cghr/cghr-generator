package org.cghr.generator.db

import groovy.sql.Sql
import groovy.transform.TupleConstructor
import org.cghr.generator.Generator
import org.cghr.generator.transformer.EntityTransformer

/**
 * Created by ravitej on 27/3/14.
 */

@TupleConstructor
class DbGenerator {


    Sql gSql
    EntityTransformer entityTransformer
    Generator generator
    String templateLocation

    String generate(String entityDesignTable, String dataDictTable) {

        def sql = "SELECT DISTINCT entity FROM $entityDesignTable  WHERE  entity!=''".toString()
        List entityList = gSql.rows(sql).collect {
            row ->

                List entityProperties = []
                entityProperties.addAll(propertiesFromEntityDesign(row.entity, entityDesignTable))
                entityProperties.addAll(propertiesFromDataDict(row.entity, dataDictTable))
                [name: row.entity, properties: keysToLowerCase(entityProperties)]
        }
        List transformedEntityList = entityList.collect { entityTransformer.transform(it) }

        return generator.generate(templateLocation, [entities: transformedEntityList])
    }


    List propertiesFromEntityDesign(String entity, String entityDesignTable) {

        def sql = "SELECT name,type,key,strategy FROM $entityDesignTable WHERE entity=?".toString()
        gSql.rows(sql, [entity])
    }

    List propertiesFromDataDict(String entity, String dataDictTable) {
        def sql = "SELECT distinct name,type FROM $dataDictTable WHERE entity=?  and type!='heading'".toString()
        gSql.rows(sql, [entity])

    }

    def generateToAFile(String entityDesignTable, String dataDictTable, File destinationFile) {

        destinationFile.write(generate(entityDesignTable, dataDictTable))

    }

    List keysToLowerCase(List mapList) {

        return mapList.collect {
            Map map ->
                map.collectEntries { k, v -> [k.toLowerCase(), v] }

        }
    }

}
