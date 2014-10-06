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

    Map generateRoutes() {

        List modules = []
        List routing = modules.collect { generateRoutesFor(it) }
        Map routes = [:]
        int i = 0
        modules.each { routes.put(it, routing[i++]) }
    }

    String generateRoutesFor(String module) {

        Map routingInfo = buildRoutingInfo(module)
        generator.generate(templateLocation, routingInfo)

    }

    Map buildRoutingInfo(String module) {

        Map routingInfo = [:]
        String sql = "select * from mainRoutes where module=?"
        routingInfo = gSql.firstRow(sql, [module])

        routingInfo.children = gSql.rows("select * from level2Routes where parent=?", [routingInfo.name]).collect {
            println it
            String parent = it.name
            it.children = gSql.rows("select * from level3Routes where parent=?", [parent])
            println it.children
            it
        }
        routingInfo
    }


}
