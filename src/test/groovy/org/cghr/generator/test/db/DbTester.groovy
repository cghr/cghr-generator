package org.cghr.generator.test.db

import groovy.sql.Sql

import javax.sql.DataSource


class DbTester {

    Sql gSql
    MockData dataSet = new MockData()

    def dataStore

    DbTester(DataSource ds) {
        this.gSql = new Sql(ds.connection)
    }

    DbTester(Sql sql) {
        this.gSql = sql
    }


    def cleanInsert(String tablesCommaSeparated) {
        def tables = tablesCommaSeparated.split(",")


        for (table in tables) {
            clean(table)
            insert(table)
        }
    }

    def clean(String tablesCommaSeparated) {

        def tables = tablesCommaSeparated.split(",")
        for (table in tables) {
            gSql.execute("drop table $table if exists".toString())
            createTable(table)

        }
    }

    def insert(String table) {

        insertSampleData(table, dataSet.sampleData.get(table))
    }

    def createTable(String table) {

        def map = dataSet.structure.get(table)
        def cols = map.collect() { key, value ->
            key + ' ' + value
        }
        def sql = "create table if not exists $table(${cols.join(',')})".toString()
        gSql.execute(sql)
    }

    def insertSampleData(String table, List sampleData) {

        Set columnsSet = (dataSet.structure.get(table)).keySet()
        def columns = (columnsSet as String[]).join(',')
        StringBuffer placeHolders = new StringBuffer()
        for (i in 1..columnsSet.size())
            placeHolders.append('?,')

        placeHolders.deleteCharAt(placeHolders.length() - 1)

        for (Map row : sampleData) {
            def sql = "insert into $table($columns) values($placeHolders)".toString()
            gSql.execute(sql, row.values().toList())
        }
    }
}
