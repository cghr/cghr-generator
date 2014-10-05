package org.cghr.generator.transformer

import groovy.transform.TupleConstructor

/**
 * Created by ravitej on 26/3/14.
 */

@TupleConstructor
class EntityTransformer {


    Map propertyTypeMapping

    Map transform(Map entity) {

        Map transformedEntity = entity.clone() //Copy all properties initially

        List stdItems = entity.properties.findAll { !(propertyTypeMapping.get(it.type) instanceof List) }
        List multiplePropertyItems = entity.properties.findAll { (propertyTypeMapping.get(it.type) instanceof List) }

        transformedEntity.properties = stdItems.collect {
            it.type = propertyTypeMapping.get(it.type)
            it
        }
        multiplePropertyItems.each {
            transformedEntity.properties.addAll(getItemWithMultipleProperties(it))
        }

        return transformedEntity

    }

    //Like text_select,select_text
    List getItemWithMultipleProperties(Map item) {

        List propertyList = propertyTypeMapping.get(item.type)
        return propertyList.collect {
            prop ->
                [name: prop.name.replace("{name}", item.name), type: prop.type]

        }
    }


}
