package org.cghr.generator.dataStoreInfo

import org.cghr.generator.Generator
import org.cghr.generator.sqlUtil.SqlCustom
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 1/4/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class DataStoreInfoGeneratorSpec extends Specification {

    @Autowired
    SqlCustom sqlCustom

    DataStoreInfoGenerator dataStoreInfoGenerator
    String templateLocation = 'templates/dataStoreInfo'


    def setup() {

        String sql = "select  entity name,name keyfield from entityDesign where key='primary key'"
        Generator generator = Stub() {
            generate(templateLocation, [entities: sqlCustom.rows(sql)]) >>
                    new File('testResources/dataStoreInfo.expected').text
        }
        dataStoreInfoGenerator = new DataStoreInfoGenerator(sqlCustom, generator)
    }

    def "should  generate DataStore info from a given dataset"() {

        expect:
        dataStoreInfoGenerator.generate('entityDesign', 'templates/dataStoreInfo') == new File('testResources/dataStoreInfo.expected').text


    }
}