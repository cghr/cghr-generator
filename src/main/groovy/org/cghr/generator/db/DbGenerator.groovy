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

        String sql1 = "SELECT DISTINCT entity FROM $entityDesignTable  WHERE  entity!=''".toString()

        List rows = gSql.rows(sql1)

        entityList = rows.collect {

            row ->

                List entityProperties = []
                String sql2 = "SELECT name,type,key,strategy FROM $entityDesignTable WHERE entity=?".toString()
                gSql.rows(sql2, [row.entity]).each {
                    entityProperties.add(it)
                }
                String sql3 = "SELECT name,type FROM $dataDictTable WHERE entity=?  and type!='heading'".toString()
                gSql.rows(sql3, [row.entity]).each {
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

       // println entityList
        entityList.each {
            entity ->
                transformedEntityList.add(entityTransformer.transform(entity))
        }


        return generator.generate(templateLocation, [entities: transformedEntityList])
    }


    def generateToAFile(List tablesWithEntitiesInfo, File file) {

        file.setText(generate(tablesWithEntitiesInfo))
    }


}
