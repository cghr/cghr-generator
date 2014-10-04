package org.cghr.generator.sqlUtil

import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

/**
 * Created by ravitej on 4/10/14.
 */
class SqlCustom extends Sql {


    SqlCustom(Sql parent) {
        super(parent)
    }

    SqlCustom(DataSource dataSource) {
        super(dataSource)
    }

    SqlCustom(Connection connection) {
        super(connection)
    }

    def lowerCaseKeys = {
        it.collectEntries {
            k, v ->
                [k.toLowerCase(), v]
        }
    }

    @Override
    List<GroovyRowResult> rows(String sql, List<Object> params) throws SQLException {
        return super.rows(sql, params).collect(lowerCaseKeys)
    }

    @Override
    List<GroovyRowResult> rows(String sql) throws SQLException {
        return super.rows(sql).collect(lowerCaseKeys)
    }

    @Override
    List<GroovyRowResult> rows(GString sql) throws SQLException {
        return super.rows(sql).collect(lowerCaseKeys)
    }


    @Override
    GroovyRowResult firstRow(String sql, List<Object> params) throws SQLException {

        Map row = super.firstRow(sql, params)

        if (row != null) {
            Map map = row.collectEntries {
                key, value ->
                    [key.toLowerCase(), value]
            }

            return map;
        }
        return [:];
    }

    @Override
    GroovyRowResult firstRow(GString gstring) throws SQLException {
        return super.firstRow(gstring)
    }


}
