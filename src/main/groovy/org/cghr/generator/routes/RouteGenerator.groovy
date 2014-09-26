package org.cghr.generator.routes

import groovy.sql.Sql
import org.cghr.generator.Generator

/**
 * Created by ravitej on 25/9/14.
 */
class RouteGenerator {

    Sql gSql
    Generator generator
    String templateLocation

    RouteGenerator(Sql gSql, Generator generator, String templateLocation) {
        this.gSql = gSql
        this.generator = generator
        this.templateLocation = templateLocation
    }

    String generateRouteForModule(String moduleName) {


    }


}
