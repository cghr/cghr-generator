package org.cghr.generator.test.db

class MockData {

    def entityDesign = [
            user: [name: 'user', properties: [
                    [name: 'id', type: 'int', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'username', type: 'text'],
                    [name: 'password', type: 'text']
            ]
            ],
            userlog: [name: 'userlog', properties: [
                    [name: 'id', type: 'int', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'username', type: 'text'],
                    [name: 'status', type: 'text']
            ]
            ],
            country: [name: 'country', properties: [
                    [name: 'id', type: 'int', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'continentId', type: 'int']

            ]
            ],
            state: [name: 'state', properties: [
                    [name: 'id', type: 'int', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'countryId', type: 'int']
            ]
            ]
    ]
    def dataDict = [
            country: [name: 'country', properties: [
                    [name: 'name', type: 'text']

            ]
            ],
            state: [name: 'state', properties: [
                    [name: 'name', type: 'text']
            ]
            ]
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
                            [name: 'datastore', label: '', type: 'auto', value: 'country', valdn: '', flow: ''],
                            [name: 'id', label: '', type: 'auto', value: '$stateParams.countryId', valdn: '', flow: ''],
                            [name: 'name', label: 'Name', type: 'text', value: '', valdn: 'required', flow: "data.prop1 == 'yes' && data.prop2=='No'"],
                            [name: 'continent', label: 'Continent', type: 'select', value: '', valdn: 'required', flow: '', items: [
                                    [text: 'Asia', value: 'asia'],
                                    [text: 'Europe', value: 'europe'],
                                    [text: 'Africa', value: 'africa']
                            ]]
                    ]
            ]
    ]

    def transformedDataDictSchema = [
            country: [
                    name: 'country',
                    onSuccess: 'newState',
                    properties: [
                            [name: 'datastore', label: '', type: 'hidden', value: 'country', valdn: '', flow: ''],
                            [name: 'id', label: '', type: 'hidden', value: '$stateParams.countryId', valdn: '', flow: ''],
                            [name: 'name', label: 'Name', type: 'text', value: '', valdn: 'required', flow: "data.prop1 == 'yes' && data.prop2=='No'"],
                            [name: 'continent', label: 'Continent', type: 'radio', value: '', valdn: 'required', flow: '', items: [
                                    [text: 'Asia', value: 'asia'],
                                    [text: 'Europe', value: 'europe'],
                                    [text: 'Africa', value: 'africa']
                            ]]
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

    def entityDesignRawData=[
            user: [name: 'user', properties: [
                    [name: 'id', type: 'int', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'username', type: 'text'],
                    [name: 'password', type: 'text']
            ]
            ],
            userlog: [name: 'userlog', properties: [
                    [name: 'id', type: 'int', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'username', type: 'text'],
                    [name: 'status', type: 'text']
            ]
            ],
            country: [name: 'country', properties: [
                    [name: 'id', type: 'int', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'continentId', type: 'int'],
                    [name: 'name', type: 'text']

            ]
            ],
            state: [name: 'state', properties: [
                    [name: 'id', type: 'int', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'countryId', type: 'int'],
                    [name: 'name', type: 'text']

            ]
            ]
    ]

    def transformedEntitySampleData = [
            user: [name: 'user', properties: [
                    [name: 'id', type: 'int(11)', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'username', type: 'varchar(100)'],
                    [name: 'password', type: 'varchar(100)']
            ]
            ],
            userlog: [name: 'userlog', properties: [
                    [name: 'id', type: 'int(11)', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'username', type: 'varchar(100)'],
                    [name: 'status', type: 'varchar(100)']
            ]
            ],
            country: [name: 'country', properties: [
                    [name: 'id', type: 'int(11)', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'continentId', type: 'int(11)'],
                    [name: 'name', type: 'varchar(100)']

            ]
            ],
            state: [name: 'state', properties: [
                    [name: 'id', type: 'int(11)', key: 'primary key', strategy: 'auto_increment'],
                    [name: 'countryId', type: 'int(11)'],
                    [name: 'name', type: 'varchar(100)']

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
