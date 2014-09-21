package org.cghr.generator.cardinalSymptoms

import groovy.sql.Sql

/**
 * Created by ravitej on 11/9/14.
 */
class SymptomGenerator {

    Sql gSql

    SymptomGenerator() {

    }

    String generateCardinalSymptoms() {

        String sql = "select name from cardinalSymptoms group  by name"
        gSql.rows(sql).each {

        }
    }


}
