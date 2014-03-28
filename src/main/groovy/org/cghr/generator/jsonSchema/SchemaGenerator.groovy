package org.cghr.generator.jsonSchema

import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.transformer.EntityTransformer

/**
 * Created by ravitej on 26/3/14.
 */
class SchemaGenerator {


    Sql gSql
    EntityTransformer entityTransformer
    Generator generator
    String templateLocation


    List entityList = []
    List transformedEntityList = []


    SchemaGenerator(Sql gSql, EntityTransformer entityTransformer, Generator generator, String templateLocation) {
        this.gSql = gSql
        this.entityTransformer = entityTransformer
        this.generator = generator
        this.templateLocation = templateLocation
    }

    String generate(List tablesWithEntitiesInfo,String tableWithPropertyItemInfo) {

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

    List collectEntities(String tableWithEntityInfo, List listToCollect,String tableWithPropertyItemInfo) {


        List rows = gSql.rows("select distinct entity from $tableWithEntityInfo")
        List multipleItemTypes=['select','multiselect','lookup']

        rows.each {
            row ->

                List entityProperties = gSql.rows("select name,type,valdn,label,flow from $tableWithEntityInfo where entity=?", [row.entity])
                entityProperties.each {
                    property ->
                        List propertyItems=[]
                        if (multipleItemTypes.contains(property.type)){

                            String clabel=gSql.rows("select clabel from $tableWithEntityInfo where name=?",[property.name])
                            propertyItems=gSql.rows("select text,value from  $tableWithPropertyItemInfo where name=?",[clabel])
                            property.items=propertyItems

                        }

                }
                listToCollect.add([name: row.entity, properties: entityProperties])
        }


    }

    def generateToAFile(List tablesWithEntitiesInfo, File file) {

        file.setText(generate(tablesWithEntitiesInfo))
    }


}
