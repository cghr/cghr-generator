package org.cghr.generator.test.db

class MockData {


    def structure = [


    ]

    def entitySampleData = [
            user: [name: 'user', properties: [
                    [name: 'id', type: 'int'],
                    [name: 'username', type: 'text)'],
                    [name: 'password', type: 'text']
            ]],
            userlog: [name: 'userlog', properties: [
                    [name: 'id', type: 'int'],
                    [name: 'username', type: 'text'],
                    [name: 'status', type: 'text']
            ]],
            country: [name: 'country', properties: [
                    [name: 'id', type: 'int'],
                    [name: 'name', type: 'text'],
                    [name: 'continent', type: 'text']
            ]],
            state: [name: 'state', properties: [
                    [name: 'id', type: 'int'],
                    [name: 'name', type: 'text'],
                    [name: 'country', type: 'text']
            ]]
    ]
    def schemaSampleData = [
            country: [
                    name: 'country',
                    onSuccess: "newState",
                    properties: [
                            [name: 'datastore', type: 'auto', value: 'country'],
                            [name: 'country_id', type: 'auto', value: '$stateParams.countryId'],
                            [name: 'name', type: 'text', label: 'Name', valdn: 'required', flow: "data.prop1 == 'yes' && data.prop2=='No'"],
                            [name: 'continent', type: 'select', label: 'Continent', valdn: 'required', items: [
                                    [text: 'Asia', value: 'asia'],
                                    [text: 'Europe', value: 'europe'],
                                    [text: 'Africa', value: 'africa']
                            ]]]

            ]
    ]
    def dataDictSchema = [
            country: [
                    name: 'country',
                    onSuccess: 'newState',
                    properties: [

                    ]
            ]
    ]
    def transformedSchemaSampleData = [
            country: [
                    name: 'country',
                    onSuccess: "newState",
                    properties: [
                            [name: 'datastore', type: 'hidden', value: 'country'],
                            [name: 'country_id', type: 'hidden', value: '$stateParams.countryId'],
                            [name: 'name', type: 'text', label: 'Name', valdn: 'required', flow: "data.prop1 == 'yes' && data.prop2=='No'"],
                            [name: 'continent', type: 'radio', label: 'Continent', valdn: 'required', items: [
                                    [text: 'Asia', value: 'asia'],
                                    [text: 'Europe', value: 'europe'],
                                    [text: 'Africa', value: 'africa']
                            ]]]

            ]
    ]

    def transformedEntitySampleData = [
            user: [name: 'user', properties: [
                    [name: 'id', type: 'int(11)'],
                    [name: 'username', type: 'varchar(100)'],
                    [name: 'password', type: 'varchar(100)']
            ]
            ],
            userlog: [name: 'userlog', properties: [
                    [name: 'id', type: 'int(11)'],
                    [name: 'username', type: 'varchar(100)'],
                    [name: 'status', type: 'varchar(100)']
            ]
            ],
            country: [name: 'country', properties: [
                    [name: 'id', type: 'int(11)'],
                    [name: 'name', type: 'varchar(100)'],
                    [name: 'continent', type: 'varchar(100)']
            ]
            ],
            state: [name: 'state', properties: [
                    [name: 'id', type: 'int(11)'],
                    [name: 'name', type: 'varchar(100)'],
                    [name: 'country', type: 'varchar(100)']
            ]
            ]
    ]

    List<Map> getFilteredSampleData(String datastore, Listcolumns) {


        List actualData = this.sampleData.get(datastore)
        actualData.collect {
            it.subMap(columns)
        }

    }
}
