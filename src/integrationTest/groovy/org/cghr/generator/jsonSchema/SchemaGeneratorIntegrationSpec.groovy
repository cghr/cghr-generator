package groovy.org.cghr.generator.jsonSchema

import org.cghr.generator.jsonSchema.SchemaGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 31/3/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
//@ContextConfiguration(locations = "classpath:spring-context.xml")
class SchemaGeneratorIntegrationSpec extends Specification {

    @Autowired
    SchemaGenerator schemaGeneratorWithMock


    def "should generate json schema for a given data set"() {
        given:
        String expectedJsonSchemaStruct = new File('testResources/jsonSchema.expected').text.replaceAll("\\s", "")
        String generated = schemaGeneratorWithMock.generate("")[0]

        expect:
        generated.replaceAll("\\s", "") == expectedJsonSchemaStruct

    }

}