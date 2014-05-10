package org.cghr.generator.dataStoreInfo

import groovy.sql.Sql
import org.cghr.generator.Generator

/**
 * Created by ravitej on 1/4/14.
 */
class DataStoreInfoGenerator {

    Generator generator
    Sql gSql


    DataStoreInfoGenerator(Sql gSql, Generator generator) {
        this.generator = generator
        this.gSql = gSql
    }

    String generate(String entityDesignTable, String templateLocation) {

        def sql = "select distinct entity from $entityDesignTable where entity!=''".toString()
        List entities = gSql.rows(sql).collect {
            row ->
                sql = "select name from $entityDesignTable where entity=? and key='primary key'".toString()
                Map info = gSql.firstRow(sql, [row.entity])
                if (info == null) return;
                [name: row.entity, keyField: info.name]
        }


        generator.generate(templateLocation, [entities: entities])
    }

    def generateToAFile(String entityDesignTable, String templateLocation, File destFile) {

        destFile.setText(generate(entityDesignTable, templateLocation))
    }


}
