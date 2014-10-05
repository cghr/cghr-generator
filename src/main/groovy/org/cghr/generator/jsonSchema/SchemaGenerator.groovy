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

        String sql1 = "SELECT DISTINCT entity FROM $entitySchemaTable WHERE  entity!=''"
        List rows = gSql.rows(sql1)
        entityList = rows.collect {
            row ->
                String query = "select * from $entitySchemaTable where entity=?"
                Map schema = gSql.firstRow(query, [row.entity])

                List entityProperties = []
                List multipleItemTypes = ['select', 'multiselect', 'select-inline', 'dropdown', 'suggest', 'select_text', 'select_singletext', 'text_select', 'ffq']

                def sql = "select name,value,type from $entitySchemaMasterPropertiesTable where entity=?".toString()
                gSql.rows(sql, [row.entity]).each {
                    entityProperties.add(it)
                }
                sql = "select name,type,valdn,label,clabel,flow,image,crossflow,crosscheck,help,clabel from $dataDictTable where entity=?".toString()
                gSql.rows(sql, [row.entity]).each {


                    if (multipleItemTypes.contains(it.type))
                        it.items = getClabelItems(it.clabel)


                    if ('lookup' == it.type)
                        it.lookup = getLookupData(row.entity, it.name)


                    if ('dynamic_dropdown' == it.type)
                        it.metadata = getDynamicDropdownData(row.entity, it.name)

                    if (it.crosscheck != '')
                        it.crossCheck = getCrossCheckData(row.entity, it.name)


                    if (it.crossflow != '')
                        it.crossFlow = getCrossFlowData(it.crossflow)


                    it.remove('crossflow');
                    it.remove('crosscheck');
                    entityProperties.add(it)

                }

                [schemaName: schema.schemaName, condition: schema.condition, success: schema.success, fail: schema.fail, crossEntity: schema.crossEntity, onSave: schema.onSave, properties: entityProperties]
        }

        entityList.each { transformedEntityList.add(entityTransformer.transform(it)) }
        transformedEntityList.each { generatedList.add(generator.generate(templateLocation, it)) }

        generatedList
    }

    List getClabelItems(String clabel) {

        String sql = "SELECT  text,value,valdn   FROM clabel WHERE name=?"
        gSql.rows(sql, [clabel])

    }

    List getCrossFlowData(String crossFlowName) {

        String sql = "select entity,field,ref,condition,whereCondition from crossFlow  where name=?"
        gSql.rows(sql, [crossFlowName])

    }

    Map getLookupData(String entity, String name) {

        String sql = "SELECT lookup from dataDict where entity=? and name=?"
        def lookupName = gSql.firstRow(sql, [entity, name]).lookup


        sql = "SELECT entity,field,ref,condition from lookup where name=?"
        gSql.firstRow(sql, [lookupName])
    }

    Map getDynamicDropdownData(String entity, String name) {

        String sql = "SELECT dynamic_dropdown from dataDict where entity=? and name=?"
        def dynamicDropdownName = gSql.firstRow(sql, [entity, name]).dynamic_dropdown

        sql = "SELECT entity,field,ref,refValue from dynamicDropdown where name=?".toString()
        gSql.firstRow(sql, [dynamicDropdownName])

    }

    Map getCrossCheckData(String entity, String name) {

        String sql = "SELECT crossCheck from dataDict where entity=? and name=?"
        def crossCheckName = gSql.firstRow(sql, [entity, name]).crossCheck


        sql = "SELECT entity,field,ref,condition from crossCheck where name=?".toString()
        gSql.firstRow(sql, [crossCheckName])

    }


    void generateToAFolder(String entitySchemaTable, String entitySchemaMasterPropertiesTable, String dataDictTable, String tableWithPropertyItemInfo, String folderPath) {

        FileUtils.cleanDirectory(new File(folderPath))

        List generatedList = generate(entitySchemaTable, entitySchemaMasterPropertiesTable, dataDictTable, tableWithPropertyItemInfo)
        int i = 0
        entityList.each { new File(folderPath + '/' + it.schemaName + '.json').setText(generatedList[i++]) }

    }


}
