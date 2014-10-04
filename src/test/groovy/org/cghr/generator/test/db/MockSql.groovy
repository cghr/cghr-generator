package org.cghr.generator.test.db

import groovy.sql.Sql

/**
 * Created by ravitej on 31/3/14.
 */
class MockSql {


    Sql mockSql

    MockSql(Sql sql) {
        this.mockSql = sql
    }


    def lowerCaseKeys = {
        it.collectEntries {
            k, v ->
                [k.toLowerCase(), v]
        }
    }

    def rows(String sql, List args) {

        mockSql.rows(sql, args).collect lowerCaseKeys
    }

    def rows(String sql) {
        mockSql.rows(sql).collect lowerCaseKeys
    }

    def firstRow(String sql, List args) {


        Map row = mockSql.firstRow(sql, args)

        if (row != null) {
            Map map = row.collectEntries {
                key, value ->
                    [key.toLowerCase(), value]
            }

            return map;
        }
        return [:];
    }

    def firstRow(String sql) {
        mockSql.firstRow(sql).collectEntries {
            k, v ->
                [k.toLowerCase(), v]
        }
    }

}
