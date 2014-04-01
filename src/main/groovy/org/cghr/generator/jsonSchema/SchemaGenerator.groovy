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
    List generatedList = []


    SchemaGenerator(Sql gSql, EntityTransformer entityTransformer, Generator generator, String templateLocation) {
        this.gSql = gSql
        this.entityTransformer = entityTransformer
        this.generator = generator
        this.templateLocation = templateLocation
    }

    List generate(String entitySchemaTable, String entitySchemaMasterPropertiesTable, String dataDictTable, String tableWithPropertyItemInfo) {

        String sql1 = "SELECT DISTINCT entity FROM $entitySchemaTable WHERE  entity!=''".toString()
        List rows = gSql.rows(sql1)
        entityList = rows.collect {
            row ->

                def query = "select schemaName,onSave from $entitySchemaTable where entity=?".toString()
                String onSave = gSql.firstRow(query, [row.entity]).onSave
                String schemaName = gSql.firstRow(query, [row.entity]).schemaName



                List entityProperties = []
                List multipleItemTypes = ['select', 'multiselect', 'lookup', 'select-inline']

                String sql2 = "select name,value,type from $entitySchemaMasterPropertiesTable where entity=?".toString()
                gSql.rows(sql2, [row.entity]).each {
                    entityProperties.add(it)
                }
                String sql3 = "SELECT name,type,valdn,label,flow FROM $dataDictTable WHERE entity=?".toString()
                gSql.rows(sql3, [row.entity]).each {


                    if (multipleItemTypes.contains(it.type)) {

                        it.items = []

                        String sql4 = "SELECT  clabel FROM  $dataDictTable WHERE entity=? and name=?".toString()

                        String clabel = gSql.rows(sql4, [row.entity, it.name])[0].clabel


                        String sql5 = "SELECT  text,value   FROM $tableWithPropertyItemInfo WHERE name=?".toString()
                        it.items = gSql.rows(sql5, [clabel])
                        it.items = it.items.collect {
                            sqlRow ->
                                sqlRow.collectEntries {
                                    k, v ->
                                        [k.toLowerCase(), v]
                                }
                        }
                        it.items

                    }
                    entityProperties.add(it)

                }


                entityProperties = entityProperties.collect {
                    sqlRow ->
                        sqlRow.collectEntries {
                            k, v ->
                                [k.toLowerCase(), v]
                        }
                }

                [schemaName: schemaName, onSave: onSave, properties: entityProperties]
        }



        entityList.each {
            entity ->
                transformedEntityList.add(entityTransformer.transform(entity))
        }



        transformedEntityList.each {
            generatedList.add(generator.generate(templateLocation, it))
        }


        generatedList
    }

    void generateToAFolder(String entitySchemaTable, String entitySchemaMasterPropertiesTable, String dataDictTable, String tableWithPropertyItemInfo, String folderPath) {

        List generatedList = generate(entitySchemaTable, entitySchemaMasterPropertiesTable, dataDictTable, tableWithPropertyItemInfo)
        int i = 0
        entityList.each {
            entity ->
                new File(folderPath + '/' + entity.schemaName + '.json').setText(generatedList[i++])

        }

    }


}
