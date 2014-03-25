package org.cghr.generator.db

/**
 * Created by ravitej on 21/3/14.
 */
class DbTypeMapper {


    static final Map<String,String> map=[
            int:'int(11)',
            text:'varchar(255)',
            longtext:'clob',
            timestamp:'timestamp'
    ];

    static get(String type){

        return map.get(type);
    }



}
