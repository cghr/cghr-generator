package org.cghr.generator.jsonSchema

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import groovy.transform.TupleConstructor

/**
 * Created by ravitej on 9/10/14.
 */
@TupleConstructor
class SchemaValidator {


    boolean validate(File file) {

        print 'validating  ' + file.name

        //Can't load a malformed json,throws JsonParseException
        JsonNode schema = JsonLoader.fromFile(file)
        print ' --success\n'
        return true


    }

    void validateAllGeneratedSchemas(String schemaPath) {

        File dir = new File(schemaPath)
        dir.listFiles().each {

            if (!validate(it))
                println 'validation failed : ' + it.name

        }
    }


}
