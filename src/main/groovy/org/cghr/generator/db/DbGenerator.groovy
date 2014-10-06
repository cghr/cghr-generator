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

        List entityList = getEntityList(sql, entityDesignTable, dataDictTable)
        List transformedList = transform(entityList)
        generator.generate(templateLocation, [entities: transformedList])
    }

    List getEntityList(String sql, String entityDesignTable, String dataDictTable) {

        return gSql.rows(sql).collect {
            row ->
                List entityProperties = []

                entityProperties.addAll(propertiesFrom(entityDesignTable, row.entity))
                entityProperties.addAll(propertiesFrom(dataDictTable, row.entity))
                [name: row.entity, properties: entityProperties]
        }
    }

    List transform(List entityList) {
        entityList.collect { entityTransformer.transform(it) }
    }

    List propertiesFrom(String table, String entity) {
        String sql = (table == 'entityDesign') ? "SELECT name,type,key,strategy FROM entityDesign WHERE entity=?" :
                "SELECT distinct name,type FROM dataDict WHERE entity=?  and type!='heading'"

        gSql.rows(sql, [entity])
    }


    def generateToAFile(String entityDesignTable, String dataDictTable, File destinationFile) {

        destinationFile.write(generate(entityDesignTable, dataDictTable))

    }
}
