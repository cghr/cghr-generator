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

    String generate(List tablesWithEntitiesInfo) {

        tablesWithEntitiesInfo.each {
            tableWithEntityInfo ->
                collectEntities(tableWithEntityInfo, entityList)
        }


        entityList.each {
            entity ->
                transformedEntityList.add(entityTransformer.transform(entity))
        }

        return generator.generate(templateLocation, [entities: transformedEntityList])
    }

    List collectEntities(String tableWithEntityInfo, List listToCollect) {


        String allEntities = "select distinct entity from $tableWithEntityInfo".toString()
        List rows = gSql.rows(allEntities)

        rows.each {
            row ->

                String sql = "select name,type from $tableWithEntityInfo where entity=? and type!='heading'".toString()
                List entityProperties = gSql.rows(sql, [row.entity])
                entityProperties=entityProperties.collect{
                    sqlRow->
                        sqlRow.collectEntries{
                            k,v->
                                [k.toLowerCase(),v]
                        }
                }


                listToCollect.add([name: row.entity, properties: entityProperties])
        }


    }

    def generateToAFile(List tablesWithEntitiesInfo, File file) {

        file.setText(generate(tablesWithEntitiesInfo))
    }


}
