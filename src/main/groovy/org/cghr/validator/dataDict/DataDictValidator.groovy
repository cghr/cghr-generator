package org.cghr.validator.dataDict

import groovy.transform.TupleConstructor

/**
 * Created by ravitej on 10/10/14.
 */
@TupleConstructor
class DataDictValidator {

    List rules
    List dataList


    List validate() {
        List report = []
        rules.each {
            Map rule ->
                List errors = dataList.findAll(rule.condition)
                report.addAll(errors.collect { [qno: it.qno, entity: it.entity, msg: rule, msg: rule.msg] })

        }
        return report
    }

}
