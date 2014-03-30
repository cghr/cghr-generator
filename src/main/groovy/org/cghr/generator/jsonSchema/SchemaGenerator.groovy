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

    String generate(List tablesWithEntitiesInfo, String tableWithPropertyItemInfo) {

        tablesWithEntitiesInfo.each {
            tableWithEntityInfo ->
                collectEntities(tableWithEntityInfo, entityList, tableWithPropertyItemInfo)
        }



        entityList.each {
            entity ->
                transformedEntityList.add(entityTransformer.transform(entity))
        }

        return generator.generate(templateLocation, transformedEntityList[0])
    }

    List collectEntities(String tableWithEntityInfo, List listToCollect, String tableWithPropertyItemInfo) {


        String sql = "select distinct entity from $tableWithEntityInfo".toString()
        List rows = gSql.rows(sql)

        List multipleItemTypes = ['select', 'multiselect', 'lookup', 'radio-inline']

        rows.each {
            row ->

                String entityPropertiesQuery = "select name,type,valdn,label,flow from $tableWithEntityInfo where entity=?".toString()
                List entityProperties = gSql.rows(entityPropertiesQuery, [row.entity])


                entityProperties = entityProperties.collect {
                    property ->
                        List propertyItems = []
                        if (multipleItemTypes.contains(property.type)) {

                            String clabelSql = "select clabel from $tableWithEntityInfo where name=?".toString()
                            String clabel = gSql.rows(clabelSql, [property.name])[0].clabel

                            String propertyItemSql = "select text,value from  $tableWithPropertyItemInfo where name=?"
                            propertyItems = gSql.rows(propertyItemSql, [clabel])
                            property.items = propertyItems


                        }
                        return property

                }

                listToCollect.add([name: row.entity, onSuccess:'newState', properties: entityProperties])
        }


    }

    def generateToAFile(List tablesWithEntitiesInfo, File file) {

        file.setText(generate(tablesWithEntitiesInfo))
    }


}
