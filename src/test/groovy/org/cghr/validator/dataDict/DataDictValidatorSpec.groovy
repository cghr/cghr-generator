package org.cghr.validator.dataDict

import org.cghr.generator.Generator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 10/10/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class DataDictValidatorSpec extends Specification {

    @Autowired
    ArrayList rules
    @Autowired
    ArrayList dataList
    @Autowired
    ArrayList expectedReport
    @Autowired
    Generator generator

    DataDictValidator dataDictValidator

    def setup() {

        dataDictValidator = new DataDictValidator(rules, dataList, generator)
    }

    def "should generate an expected report from given rules and dataList"() {
        given:
        List result = dataDictValidator.validate()
        def actualReport = result.collect {
            it.subMap(['qno', 'entity'])
        }


        expect:
        actualReport.toString() == expectedReport.toString()


    }

}