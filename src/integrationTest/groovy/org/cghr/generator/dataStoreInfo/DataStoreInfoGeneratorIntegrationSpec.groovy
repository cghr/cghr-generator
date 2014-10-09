package groovy.org.cghr.generator.dataStoreInfo

import org.cghr.generator.dataStoreInfo.DataStoreInfoGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification


/**
 * Created by ravitej on 1/4/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class DataStoreInfoGeneratorIntegrationSpec extends Specification {

    @Autowired
    DataStoreInfoGenerator dataStoreInfoGeneratorWithMock


    def "should  generate DataStore info from a given dataset"() {

        given:
        File dataStoreInfo = new File('testResources/dataStoreInfo.expected')

        expect:
        dataStoreInfoGeneratorWithMock.generate('entityDesign', 'templates/dataStoreInfo').replaceAll("\\s+", "") == dataStoreInfo.text.replaceAll("\\s+", "")


    }

}