package org.cghr.generator.cardinalSymptoms

import com.google.gson.Gson
import groovy.sql.Sql
import groovy.transform.TupleConstructor

/**
 * Created by ravitej on 11/9/14.
 */

@TupleConstructor
class SymptomGenerator {

    Sql gSql

    List getDomains() {
        gSql.rows("select distinct domain from cardinalSymptoms").collect {
            it.domain
        }
    }

    List getSymptomInfo(String domain) {
        gSql.rows("select name,label,img from cardinalSymptoms where domain=?", [domain])
    }

    List getProbingInfo(String symptom, String domain) {
        gSql.rows("select symptom,name,type,clabel,label from probingInfo where symptom=? and domain=?", [symptom, domain])
    }

    List getOptionsForProbing(String clabel) {
        gSql.rows("select text,value from probingOptions where name=?", [clabel])
    }

    List generateSymptoms() {
        Gson gson = new Gson()
        getDomains().each {
            new File("generated/cardinalSymptoms/$it").setText(gson.toJson(getSymptomInfo(it)))
        }
    }

    void generateProbingInfo() {

        List probingInfo = []
        getDomains().each {
            String domain ->
                getSymptomInfo(domain).each {
                    getProbingInfo(it.name, domain).each {
                        Map symptom ->
                            symptom.items = getOptionsForProbing(symptom.clabel)
                            probingInfo.add(symptom)
                    }
                }
        }
        new File('generated/cardinalSymptoms/probingInfo').setText(new Gson().toJson(probingInfo))
    }


}
