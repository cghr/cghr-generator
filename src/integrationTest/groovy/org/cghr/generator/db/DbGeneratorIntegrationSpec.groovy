package org.cghr.generator.db
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
/**
 * Created by ravitej on 28/3/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class DbGeneratorIntegrationSpec extends Specification {


    @Shared
    List tablesWithEntities = ['dbStructure', 'dataDict']

    @Autowired
    DbGenerator dbGenerator





    def "should generate dbStructure from a given list of entities"() {

        given:
        String expectedDbStruct = new File('testResources/db.expected').text.replaceAll("\\n","")


        expect:
        dbGenerator.generate(tablesWithEntities).replaceAll("\\n","")==expectedDbStruct



    }

}