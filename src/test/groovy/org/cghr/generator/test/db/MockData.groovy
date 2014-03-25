package org.cghr.generator.test.db

class MockData {


    def structure = [
            country: [id: 'int', name: 'varchar(20)', continent: 'varchar(20)'],

    ]

    def sampleData = [
            country: [
                    [id: 1, name: 'india', continent: 'asia'],
                    [id: 2, name: 'pakistan', continent: 'asia'],
                    [id: 3, name: 'srilanka', continent: 'asia']
            ]
    ]

    List<Map> getFilteredSampleData(String datastore,List columns){


        List actualData=  this.sampleData.get(datastore)
        actualData.collect {
            it.subMap(columns)
        }

    }
}
