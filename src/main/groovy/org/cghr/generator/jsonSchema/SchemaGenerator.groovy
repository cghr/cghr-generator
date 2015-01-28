package org.cghr.generator.jsonSchema

import groovy.sql.Sql
import groovy.transform.TupleConstructor
import org.apache.commons.io.FileUtils
import org.cghr.generator.Generator
import org.cghr.generator.transformer.EntityTransformer

/**
 * Created by ravitej on 26/3/14.
 */
@TupleConstructor(excludes = ['entityList', 'transformedEntityList', 'generatedList'])
class SchemaGenerator {


    Sql gSql
    EntityTransformer entityTransformer
    Generator generator
    String templateLocation
    List multipleItemTypes
    String languageCode


    List entityList = []
    List transformedEntityList = []
    List generatedList = []


    List generate() {

        String sql1 = "SELECT DISTINCT entity FROM entitySchema WHERE  entity!=''"
        entityList = gSql.rows(sql1).collect {

            Map schema = gSql.firstRow("select * from entitySchema where entity=?", [it.entity])

            List entityProperties = []

            entityProperties.addAll(propertiesFromSchemaMasterProperties(it.entity))
            entityProperties.addAll(propertiesFromDataDict(it.entity))

            [schemaName: schema.schemaName, condition: schema.condition, success: schema.success, fail: schema.fail, crossEntity: schema.crossEntity, onSave: schema.onSave, properties: entityProperties]
        }

        transformedEntityList = entityList.collect { entityTransformer.transform(it) }
        generatedList = transformedEntityList.collect { generator.generate(templateLocation, it) }
    }

    List propertiesFromSchemaMasterProperties(String entity) {
        gSql.rows("select * from entitySchemaMasterProperties where entity=?", [entity])
    }

    List propertiesFromDataDict(String entity) {

        String sql = "select entity,label$languageCode label,name,clabel,type,valdn,flow,crossFlow,crossCheck,lookup,image,help from dataDict where entity=?"
        gSql.rows(sql, [entity]).collect {


            if (multipleItemTypes.contains(it.type))
                it.items = (multipleItemTypes.contains(it.type)) ? getClabelItems(it.clabel) : []


            if ('lookup' == it.type)
                it.lookup = getLookupData(entity, it.name)


            if ('dynamic_dropdown' == it.type)
                it.metadata = getDynamicDropdownData(entity, it.name)

            if (it.crosscheck != '')
                it.crossCheck = getCrossCheckData(entity, it.name)


            if (it.crossflow != '')
                it.crossFlow = getCrossFlowData(it.crossflow)


            it.remove('crossflow');
            it.remove('crosscheck');
            it

        }
    }

    List dataList(String table, String name) {
        gSql.rows("select * from $table where name=?".toString(), [name])
    }

    List getClabelItems(String clabel) {
        String sql = "select name,text$languageCode text,value,valdn from clabel where name=?"
        gSql.rows(sql, [clabel])
    }

    List getCrossFlowData(String crossFlowName) {
        dataList('crossFlow', crossFlowName)
    }

    Map dataMap(String type, String table, String entity, String name) {

        String sql = "SELECT $type ref from dataDict where entity=? and name=?"
        def ref = gSql.firstRow(sql, [entity, name]).ref


        sql = "SELECT * from $table where name=?"
        gSql.firstRow(sql, [ref])
    }

    Map getLookupData(String entity, String name) {
        dataMap('lookup', 'lookup', entity, name)
    }

    Map getDynamicDropdownData(String entity, String name) {
        dataMap('dynamic_dropdown', 'dynamicDropdown', entity, name)
    }

    Map getCrossCheckData(String entity, String name) {
        dataMap('crossCheck', 'crossCheck', entity, name)
    }


    void generateToAFolder(String folderPath) {

        FileUtils.cleanDirectory(new File(folderPath))

        List generatedList = generate()
        int i = 0
        entityList.each { new File(folderPath + '/' + it.schemaName + '.json').setText(generatedList[i++]) }

    }


}
