package org.cghr.generator.dataStoreInfo

import groovy.sql.Sql
import groovy.transform.TupleConstructor
import org.cghr.generator.Generator

/**
 * Created by ravitej on 1/4/14.
 */
@TupleConstructor
class DataStoreInfoGenerator {


    Sql gSql
    Generator generator

    String generate(String entityDesignTable, String templateLocation) {

        def sql = "select  entity,name from $entityDesignTable where key='primary key'".toString()
        List entities = gSql.rows(sql).collect { [name: it.entity, keyField: it.name] }

        generator.generate(templateLocation, [entities: entities])
    }

    def generateToAFile(String entityDesignTable, String templateLocation, File destFile) {

        destFile.setText(generate(entityDesignTable, templateLocation))
    }


}
