package org.cghr.generator.transformer

import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.Resource

/**
 * Created by ravitej on 26/3/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class EntityTransformerSpec extends Specification {

    EntityTransformer entityTransformer

    @Resource
    Map dbTypeMapping
    @Resource
    Map schemaTypeMapping


    def "should transform a given entity as per property Type Mapping(Generating Tables) "() {

        given:
        entityTransformer = new EntityTransformer(dbTypeMapping)


        Map givenEntity = [name: 'myentity', onSave: 'doSomething', properties: [
                [name: 'prop1', type: 'int'],
                [name: 'prop2', type: 'int'],
                [name: 'prop3', type: 'text'],
                [name: 'prop4', type: 'longtext'],
                [name: 'prop5', type: 'timestamp'],
                [name: 'prop6', type: 'select'],
                [name: 'prop7', type: 'lookup'],
                [name: 'prop8', type: 'gps'],
                [name: 'prop9', type: 'multiselect'],
                [name: 'prop10', type: 'duration'],
                [name: 'prop11', type: 'ffq']
        ]];

        Map expectedTransformedEntity = [name: 'myentity', onSave: 'doSomething', properties: [
                [name: 'prop1', type: 'bigint(10)'],
                [name: 'prop2', type: 'bigint(10)'],
                [name: 'prop3', type: 'varchar(100)'],
                [name: 'prop4', type: 'text'],
                [name: 'prop5', type: 'timestamp default current_timestamp'],
                [name: 'prop6', type: 'varchar(100)'],
                [name: 'prop7', type: 'varchar(100)'],
                [name: 'prop9', type: 'varchar(100)'],
                [name: 'prop8_latitude', type: 'varchar(100)'],
                [name: 'prop8_longitude', type: 'varchar(100)'],
                [name: 'prop10_value', type: 'varchar(100)'],
                [name: 'prop10_unit', type: 'varchar(100)'],
                [name: 'prop11_frequency', type: 'varchar(100)'],
                [name: 'prop11_measure', type: 'varchar(100)'],
                [name: 'prop11_unit', type: 'varchar(100)']

        ]];



        expect:
        entityTransformer.transform(givenEntity) == expectedTransformedEntity


    }

    def "should transform a given entity as per property Type Mapping(Generating Json Schema) "() {

        given:
        entityTransformer = new EntityTransformer(schemaTypeMapping)
        Map givenEntity = [name: 'myentity', onSuccess: '$stateParams.go("newState")', properties: [
                [name: 'prop1', type: 'text'],
                [name: 'prop2', type: 'select'],
                [name: 'prop3', type: 'multiselect'],
                [name: 'prop4', type: 'auto'],
                [name: 'prop5', type: 'textarea'],
                [name: 'prop6', type: 'heading'],
                [name: 'prop7', type: 'lookup'],
                [name: 'prop8', type: 'gps'],
                [name: 'prop9', type: 'ffq']

        ]];
        Map expectedTransformedEntity = [name: 'myentity', onSuccess: '$stateParams.go("newState")', properties: [
                [name: 'prop1', type: 'text'],
                [name: 'prop2', type: 'radio'],
                [name: 'prop3', type: 'checkbox'],
                [name: 'prop4', type: 'hidden'],
                [name: 'prop5', type: 'textarea'],
                [name: 'prop6', type: 'heading'],
                [name: 'prop7', type: 'lookup'],
                [name: 'prop8', type: 'gps'],
                [name: 'prop9', type: 'ffq']
        ]];



        expect:
        entityTransformer.transform(givenEntity) == expectedTransformedEntity


    }


}