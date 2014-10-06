package org.cghr.generator.routes

import org.cghr.generator.Generator
import org.cghr.generator.sqlUtil.SqlCustom
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 6/10/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class RouteGeneratorSpec extends Specification {

    RouteGenerator routeGenerator

    @Autowired
    SqlCustom sqlCustom
    @Autowired
    String routeTemplate
    @Autowired
    Generator generator

    def setupSpec() {

    }

    def setup() {

        routeGenerator = new RouteGenerator(sqlCustom, generator, routeTemplate)
    }

    def "should generate routing config for module:enum"() {

        given:
        File file = new File('testResources/routing.expected')

        expect:
        routeGenerator.generateRoutesFor('enumRoutes').replaceAll("\\s", "") == file.text.replaceAll("\\s", "")


    }
}