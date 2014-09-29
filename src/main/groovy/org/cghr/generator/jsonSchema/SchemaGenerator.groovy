package org.cghr.generator.jsonSchema

import groovy.sql.Sql
import org.apache.commons.io.FileUtils
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

                def query = "select schemaName,onSave,condition,success,fail,crossEntity from $entitySchemaTable where entity=?".toString()
                String onSave = gSql.firstRow(query, [row.entity]).onSave
                String schemaName = gSql.firstRow(query, [row.entity]).schemaName

                Map entitySchemaRow = gSql.firstRow(query, [row.entity])
                String condition = entitySchemaRow.condition
                String success = entitySchemaRow.success
                String fail = entitySchemaRow.fail
                String crossEntity = entitySchemaRow.crossEntity



                List entityProperties = []
                List multipleItemTypes = ['select', 'multiselect', 'select-inline', 'dropdown', 'suggest', 'select_text', 'select_singletext', 'text_select', 'ffq']

                def sql = "select name,value,type from $entitySchemaMasterPropertiesTable where entity=?".toString()
                gSql.rows(sql, [row.entity]).each {
                    entityProperties.add(it)
                }
                sql = "select name,type,valdn,label,flow,image,crossflow,crosscheck,help from $dataDictTable where entity=?".toString()
                gSql.rows(sql, [row.entity]).each {


                    if (multipleItemTypes.contains(it.type)) {

                        it.items = []

                        sql = "SELECT  clabel FROM  $dataDictTable WHERE entity=? and name=?".toString()

                        String clabel = gSql.rows(sql, [row.entity, it.name])[0].clabel


                        sql = "SELECT  text,value,valdn   FROM $tableWithPropertyItemInfo WHERE name=?".toString()
                        it.items = gSql.rows(sql, [clabel])
                        it.items = it.items.collect {
                            sqlRow ->
                                sqlRow.collectEntries {
                                    k, v ->
                                        [k.toLowerCase(), v]
                                }
                        }
                    }
                    if ('lookup' == it.type) {

                        sql = "SELECT lookup from $dataDictTable where entity=? and name=?".toString()
                        def lookupName = gSql.firstRow(sql, [row.entity, it.name]).lookup


                        sql = "SELECT entity,field,ref,condition from lookup where name=?".toString()
                        it.lookup = gSql.firstRow(sql, [lookupName])

                    }
                    if ('dynamic_dropdown' == it.type) {

                        sql = "SELECT dynamic_dropdown from $dataDictTable where entity=? and name=?".toString()
                        def dynamicDropdownName = gSql.firstRow(sql, [row.entity, it.name]).dynamic_dropdown

                        sql = "SELECT entity,field,ref,refValue from dynamicDropdown where name=?".toString()
                        it.metadata = gSql.firstRow(sql, [dynamicDropdownName])
                    }
                    if (it.crosscheck != '') {

                        sql = "SELECT crossCheck from $dataDictTable where entity=? and name=?".toString()
                        def crossCheckName = gSql.firstRow(sql, [row.entity, it.name]).crossCheck


                        sql = "SELECT entity,field,ref,condition from crossCheck where name=?".toString()
                        it.crossCheck = gSql.firstRow(sql, [crossCheckName])


                    }
                    if (it.crossflow != '') {

                        def crossFlowName = it.crossflow
                        sql = "select entity,field,ref,condition from crossFlow  where name=?".toString()

                        //println gSql.rows("select * from crossFlow")
                        it.crossFlow = gSql.rows(sql, [crossFlowName])

                        it.crossFlow = it.crossFlow.collect {
                            sqlRow ->
                                sqlRow.collectEntries {
                                    k, v ->
                                        [k.toLowerCase(), v]
                                }
                        }

                    }
                    it.remove('crossflow');
                    it.remove('crosscheck');
                    entityProperties.add(it)

                }


                entityProperties = entityProperties.collect {
                    sqlRow ->
                        sqlRow.collectEntries {
                            k, v ->
                                if (k == 'crossFlow' || k == 'crossCheck')
                                    return [k, v];
                                else
                                    [k.toLowerCase(), v]
                        }
                }

                [schemaName: schemaName, condition: condition, success: success, fail: fail, crossEntity: crossEntity, onSave: onSave, properties: entityProperties]
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

        FileUtils.cleanDirectory(new File(folderPath))

        List generatedList = generate(entitySchemaTable, entitySchemaMasterPropertiesTable, dataDictTable, tableWithPropertyItemInfo)
        int i = 0
        entityList.each {
            entity ->
                new File(folderPath + '/' + entity.schemaName + '.json').setText(generatedList[i++])

        }

    }


}
