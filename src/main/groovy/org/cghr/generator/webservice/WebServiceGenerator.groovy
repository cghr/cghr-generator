package org.cghr.generator.webservice

import groovy.sql.Sql
import org.cghr.generator.Generator

/**
 * Created by ravitej on 31/3/14.
 */
class WebServiceGenerator {

    Generator generator
    Sql gSql

    WebServiceGenerator(Sql gSql, Generator generator) {

        this.generator = generator
        this.gSql = gSql
    }

    String generate(String webServiceDesignTable, String templateLocation) {

        def sql = "select title,mapping,sql,filters,sortings from $webServiceDesignTable".toString()
        List reports = gSql.rows(sql)
        reports = reports.collect {
            report ->
                report.pathVariables = []
                List mappingParams = report.mapping.split("/")
                mappingParams.each {
                    param ->
                        if (param.contains("{") && param.contains("}"))
                            report.pathVariables.add(param.replaceAll("[{}]", ""))

                }
                report
        }

        generator.generate(templateLocation, [reports: reports])
    }

    def generateToAFile(String webServiceDesignTable, String templateLocation, File file) {

        file.setText(generate(webServiceDesignTable, templateLocation))

    }

}
