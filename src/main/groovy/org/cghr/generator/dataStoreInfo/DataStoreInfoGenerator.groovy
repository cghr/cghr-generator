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

        String sql = "select  entity name,name keyfield from $entityDesignTable where key='primary key'";
        generator.generate(templateLocation, [entities: gSql.rows(sql)])
    }

    def generateToAFile(String entityDesignTable, String templateLocation, File destFile) {

        destFile.setText(generate(entityDesignTable, templateLocation))
    }

}
