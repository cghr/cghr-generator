package org.cghr.generator.routes

import groovy.sql.Sql
import groovy.transform.TupleConstructor
import org.cghr.generator.Generator

/**
 * Created by ravitej on 25/9/14.
 */
@TupleConstructor
class RouteGenerator {

    Sql gSql
    Generator generator
    String templateLocation

    void generateRoutes(String folderPath) {

        List modules = gSql.rows("select distinct module from mainRoutes")
        List routing = modules.collect { generateRoutesFor(it.module) }
        int i = 0
        modules.each { new File(folderPath + it.module + '.json').setText(routing[i++]) }
    }

    String generateRoutesFor(String module) {

        Map routingInfo = buildRoutingInfo(module)
        generator.generate(templateLocation, routingInfo)

    }

    Map buildRoutingInfo(String module) {


        String sql = "select * from mainRoutes where module=?"
        Map routingInfo = gSql.firstRow(sql, [module])

        routingInfo.children = gSql.rows("select * from level2Routes where parent=?", [routingInfo.name]).collect {
            String parent = it.name
            it.children = gSql.rows("select * from level3Routes where parent=?", [parent])
            it
        }
        routingInfo
    }


}
