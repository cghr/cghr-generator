import com.github.jknack.handlebars.Handlebars
import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.dataStoreInfo.DataStoreInfoGenerator
import org.cghr.generator.db.DbGenerator
import org.cghr.generator.jsonSchema.SchemaGenerator
import org.cghr.generator.sqlUtil.SqlCustom
import org.cghr.generator.test.db.MockSql
import org.cghr.generator.transformer.EntityTransformer
import org.springframework.jdbc.datasource.DriverManagerDataSource

beans {
    xmlns([context: 'http://www.springframework.org/schema/context'])

    String userHome = System.getProperty('user.home') + '/'

    //Todo Database Config
    dataSource(DriverManagerDataSource) {
        driverClassName = 'org.relique.jdbc.csv.CsvDriver'
        url = 'jdbc:relique:csv:csvDatabase'
        username = 'sa'
        password = ''
    }
    mockDataSource(DriverManagerDataSource) {
        driverClassName = 'org.relique.jdbc.csv.CsvDriver'
        url = 'jdbc:relique:csv:testResources/mockData'
        username = 'sa'
        password = ''
    }

    gSql(Sql, dataSource = dataSource)
    sqlMock(Sql, dataSource = mockDataSource)
    mockSql(MockSql, sqlMock)
    sqlCustom(SqlCustom, sqlMock)

    dbTypeMapping(HashMap, [int: 'bigint(10)', text: 'varchar(100)', longtext: 'text', timestamp: 'timestamp default current_timestamp',
            select: 'varchar(100)', 'select-inline': 'varchar(100)', multiselect: 'varchar(100)', lookup: 'varchar(100)',
            auto: 'bigint(10)', gps: [[name: '{name}_latitude', type: 'varchar(100)'], [name: '{name}_longitude', type: 'varchar(100)']],
            text_select: [[name: '{name}_value', type: 'varchar(100)'], [name: '{name}_unit', type: 'varchar(100)']],
            select_text: [[name: '{name}_value', type: 'varchar(100)'], [name: '{name}_unit', type: 'varchar(100)']],
            select_singletext: 'varchar(100)', dropdown: 'varchar(100)', dynamic_dropdown: 'varchar(100)', suggest: 'varchar(100)',
            ffq: [[name: '{name}_frequency', type: 'varchar(100)'], [name: '{name}_measure', type: 'varchar(100)'], [name: '{name}_unit', type: 'varchar(100)']],
            alcoholFreq: [[name: '{name}_frequency', type: 'varchar(100)'], [name: '{name}_typicalDay', type: 'varchar(100)'], [name: '{name}_lastTime', type: 'varchar(100)']],
            fmhDisease: [[name: '{name}_stroke', type: 'varchar(100)'], [name: '{name}_heartAttack', type: 'varchar(100)'], [name: '{name}_diabetes', type: 'varchar(100)'], [name: '{name}_mentalDisorder', type: 'varchar(100)'], [name: '{name}_cancer', type: 'varchar(100)']]
    ])
    schemaTypeMapping(HashMap, [
            text: 'text',
            select: 'radio',
            'select-inline': 'radio-inline',
            multiselect: 'checkbox',
            auto: 'hidden',
            textarea: 'textarea',
            heading: 'heading',
            lookup: 'lookup',
            gps: 'gps',
            text_select: 'text_select',
            select_text: 'select_text',
            select_singletext: 'select_singletext',
            dropdown: 'dropdown',
            dynamic_dropdown: 'dynamic_dropdown',
            suggest: 'suggest',
            hidden: 'hidden',
            ffq: 'ffq',
            alcoholFreq: 'alcoholFreq',
            fmhDisease: 'fmhDisease'

    ])
    multipleItemTypes(ArrayList, ['select', 'multiselect', 'select-inline', 'dropdown', 'suggest', 'select_text', 'select_singletext', 'text_select', 'ffq'])


    dbEntityTransformer(EntityTransformer, dbTypeMapping)
    schemaEntityTransformer(EntityTransformer, schemaTypeMapping)
    handlebars(Handlebars)
    generator(Generator, handlebars)
    dbTemplate(String, "templates/db")
    jsonTemplate(String, "templates/jsonSchema")
    routeTemplate(String, "templates/routing")

    dbGeneratorWithMock(DbGenerator, sqlCustom, dbEntityTransformer, generator, dbTemplate)
    schemaGeneratorWithMock(SchemaGenerator, sqlCustom, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes)
    dataStoreInfoGeneratorWithMock(DataStoreInfoGenerator, sqlCustom, generator)
    dbGenerator(DbGenerator, sqlCustom, dbEntityTransformer, generator, dbTemplate)
    schemaGenerator(SchemaGenerator, sqlCustom, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes)

    dataStoreInfoGenerator(DataStoreInfoGenerator, sqlCustom, generator)


}