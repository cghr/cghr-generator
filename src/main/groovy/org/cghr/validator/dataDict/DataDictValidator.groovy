package org.cghr.validator.dataDict

import groovy.transform.TupleConstructor
import org.cghr.generator.Generator

/**
 * Created by ravitej on 10/10/14.
 */
@TupleConstructor
class DataDictValidator {

    List rules
    List dataList
    Generator generator

    List validate() {
        List report = []
        rules.each {
            Map rule ->
                List errors = dataList.findAll(rule.condition)
                report.addAll(errors.collect { [qno: it.qno, entity: it.entity, msg: rule.msg] })

        }
        return report
    }


    void generateReport(String templateLocation, String outputFile) {
        String report = generator.generate(templateLocation, [errors: validate()])
        new File(outputFile).setText(report)
    }


}
