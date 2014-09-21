package org.cghr.generator.transformer
/**
 * Created by ravitej on 26/3/14.
 */
class EntityTransformer {

    Map transformedEntity = [:]
    Map propertyTypeMapping

    EntityTransformer(Map propertyTypeMapping) {

        this.propertyTypeMapping = propertyTypeMapping
    }

    Map transform(Map givenEntity) {


        transformedEntity = givenEntity //Copy all properties initially

        List multipleProperties = []


        transformedEntity.properties = givenEntity.properties.collect {

            Map transformedProperty = it
            if (propertyTypeMapping.get(it.type) instanceof List) {

                List propertyList = propertyTypeMapping.get(it.type)
                propertyList.each {
                    prop ->
                        multipleProperties.add([name: prop.name.replace("{name}", it.name), type: prop.type])

                }
                return [:]

            }
            transformedProperty.type = propertyTypeMapping.get(it.type)
            return transformedProperty

        }
        multipleProperties.each {
            transformedEntity.properties.add(it)
        }

        transformedEntity.properties = transformedEntity.properties.findAll {
            !it.isEmpty()
        }

        return transformedEntity

    }


}
