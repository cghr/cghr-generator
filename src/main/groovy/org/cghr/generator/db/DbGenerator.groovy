package org.cghr.generator.db

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.transformer.EntityTransformer

/**
 * Created by ravitej on 27/3/14.
 */
class DbGenerator {


    Sql gSql
    EntityTransformer entityTransformer
    Generator generator
    String templateLocation


    List entityList = []
    List transformedEntityList = []


    DbGenerator(Sql gSql, EntityTransformer entityTransformer, Generator generator, String templateLocation) {
        this.gSql = gSql
        this.entityTransformer = entityTransformer
        this.generator = generator
        this.templateLocation = templateLocation
    }

    String generate(String entityDesignTable, String dataDictTable) {

        def sql = "SELECT DISTINCT entity FROM $entityDesignTable  WHERE  entity!=''".toString()

        List rows = gSql.rows(sql)

        entityList = rows.collect {

            row ->


                List entityProperties = []
                sql = "SELECT name,type,key,strategy FROM $entityDesignTable WHERE entity=?".toString()
                gSql.rows(sql, [row.entity]).each {
                    entityProperties.add(it)
                }
                sql = "SELECT distinct name,type FROM $dataDictTable WHERE entity=?  and type!='heading'".toString()
                gSql.rows(sql, [row.entity]).each {
                    entityProperties.add(it)
                }

                entityProperties = entityProperties.collect {
                    sqlRow ->
                        sqlRow.collectEntries {
                            k, v ->
                                [k.toLowerCase(), v]
                        }
                }

                [name: row.entity, properties: entityProperties]
        }


        entityList.each {
            entity ->
                transformedEntityList.add(entityTransformer.transform(entity))
        }

        return generator.generate(templateLocation, [entities: transformedEntityList])
    }


    def generateToAFile(String entityDesignTable, String dataDictTable, File destinationFile) {

        destinationFile.write(generate(entityDesignTable, dataDictTable))

    }

}
