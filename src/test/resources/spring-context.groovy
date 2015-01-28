import com.github.jknack.handlebars.Handlebars
import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.dataStoreInfo.DataStoreInfoGenerator
import org.cghr.generator.db.DbGenerator
import org.cghr.generator.jsonSchema.SchemaGenerator
import org.cghr.generator.jsonSchema.SchemaValidator
import org.cghr.generator.routes.RouteGenerator
import org.cghr.generator.sqlUtil.SqlCustom
import org.cghr.generator.test.db.MockSql
import org.cghr.generator.transformer.EntityTransformer
import org.cghr.validator.dataDict.DataDictValidator
import org.springframework.jdbc.datasource.DriverManagerDataSource

def multipleTypesList = ['select', 'multiselect', 'select-inline', 'dropdown', 'suggest', 'select_text', 'select_singletext', 'text_select', 'ffq', 'ffq_other']
def schemaMapping = [text: 'text', select: 'radio', 'select-inline': 'radio-inline', multiselect: 'checkbox', auto: 'hidden', textarea: 'textarea', heading: 'heading', lookup: 'lookup', gps: 'gps', text_select: 'text_select', select_text: 'select_text', select_singletext: 'select_singletext', dropdown: 'dropdown', dynamic_dropdown: 'dynamic_dropdown', suggest: 'suggest', hidden: 'hidden', ffq: 'ffq', ffq_other: 'ffq_other', alcoholFreq: 'alcoholFreq', fmhDisease: 'fmhDisease']
def allowedTypesList = schemaMapping.keySet() as List

beans {
    xmlns([context: 'http://www.springframework.org/schema/context'])

    //Todo Database Config
    dataSource(DriverManagerDataSource) {
        driverClassName = 'org.relique.jdbc.csv.CsvDriver'
        url = 'jdbc:relique:csv:csvDatabase'
        username = 'sa'
        password = ''
    }
    vaDataSource(DriverManagerDataSource) {
        driverClassName = 'org.relique.jdbc.csv.CsvDriver'
        url = 'jdbc:relique:csv:cardinalSymptoms'
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
    cardinalSymptoms(Sql, dataSource = vaDataSource)
    sqlMock(Sql, dataSource = mockDataSource)
    mockSql(MockSql, sqlMock)
    sqlCustom(SqlCustom, sqlMock)
    sqlProd(SqlCustom, gSql)
    vaSql(SqlCustom, cardinalSymptoms)



    dbTypeMapping(HashMap, [int              : 'int', text: 'varchar(100)', longtext: 'text', timestamp: 'timestamp default current_timestamp',
                            select           : 'varchar(100)', 'select-inline': 'varchar(100)', multiselect: 'varchar(100)', lookup: 'varchar(100)',
                            auto             : 'int', gps: [[name: '{name}_latitude', type: 'varchar(100)'], [name: '{name}_longitude', type: 'varchar(100)']],
                            text_select      : [[name: '{name}_value', type: 'varchar(100)'], [name: '{name}_unit', type: 'varchar(100)']],
                            select_text      : [[name: '{name}_value', type: 'varchar(100)'], [name: '{name}_unit', type: 'varchar(100)']],
                            select_singletext: [[name: '{name}_value', type: 'varchar(100)'], [name: '{name}_unit', type: 'varchar(100)']], dropdown: 'varchar(100)', dynamic_dropdown: 'varchar(100)', suggest: 'varchar(100)',
                            ffq              : [[name: '{name}_frequency', type: 'varchar(100)'], [name: '{name}_measure', type: 'varchar(100)'], [name: '{name}_unit', type: 'varchar(100)']],
                            ffq_other        : [[name: '{name}', type: 'varchar(100)'], [name: '{name}_frequency', type: 'varchar(100)'], [name: '{name}_measure', type: 'varchar(100)'], [name: '{name}_unit', type: 'varchar(100)']],
                            alcoholFreq      : [[name: '{name}_frequency', type: 'varchar(100)'], [name: '{name}_typicalDay', type: 'varchar(100)']],
                            fmhDisease       : [[name: '{name}_stroke', type: 'varchar(100)'], [name: '{name}_heartAttack', type: 'varchar(100)'], [name: '{name}_diabetes', type: 'varchar(100)'], [name: '{name}_cancer', type: 'varchar(100)'],
                                                [name: '{name}_stroke_count', type: 'varchar(100)'], [name: '{name}_heartAttack_count', type: 'varchar(100)'], [name: '{name}_diabetes_count', type: 'varchar(100)'], [name: '{name}_cancer_count', type: 'varchar(100)']]
    ])
    schemaTypeMapping(HashMap, schemaMapping)
    allowedTypes(ArrayList, allowedTypesList)

    multipleItemTypes(ArrayList, multipleTypesList)


    dbEntityTransformer(EntityTransformer, dbTypeMapping)
    schemaEntityTransformer(EntityTransformer, schemaTypeMapping)
    handlebars(Handlebars)
    generator(Generator, handlebars)
    dbTemplate(String, "templates/db")
    jsonTemplate(String, "templates/jsonSchema")
    routeTemplate(String, "templates/routing")

    dbGeneratorWithMock(DbGenerator, sqlCustom, dbEntityTransformer, generator, dbTemplate)
    schemaGeneratorWithMock(SchemaGenerator, sqlCustom, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes, "")
    //schemaGeneratorWithMock(SchemaGenerator, sqlCustom, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes, "_mr")
    dataStoreInfoGeneratorWithMock(DataStoreInfoGenerator, sqlCustom, generator)

    dbGenerator(DbGenerator, sqlProd, dbEntityTransformer, generator, dbTemplate)
    //schemaGenerator(SchemaGenerator, sqlProd, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes, "_mr")
    schemaGenerator(SchemaGenerator, sqlProd, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes, "")
    routeGenerator(RouteGenerator, sqlProd, generator, routeTemplate)

    schemaValidator(SchemaValidator)
    dataStoreInfoGenerator(DataStoreInfoGenerator, sqlProd, generator)


    rules(ArrayList, [
            [condition: {
                it.name == '' && it.type != 'heading'
            }, msg    : "name can't be blank for types not equal to heading"],
            [condition: { it.name.contains(' ') }, msg: "name can't contain spaces"],
            [condition: {
                it.name.contains('"') || it.name.contains('\\')
            }, msg    : "label can't contain double quotes or backslash"],
            [condition: {
                it.name.contains('\n') || it.name.contains('\r')
            }, msg    : "label can't contain new line characters"],
            [condition: {
                multipleTypesList.contains(it.type) && it.clabel == ''
            }, msg    : "clabel can't be blank for type which has multiple options"],
            [condition: {
                !allowedTypesList.contains(it.type)
            }, msg    : "type not allowed.check for available types"],
            [condition: {
                (it.flow.size()) > 0 ? (it.flow.contains('=<')) || (it.flow.contains('=>')) : false
            }, msg    : "Syntax error in condition definition"],
            [condition: { it.type == '' }, msg: "type can't be blank"],
            [condition: {
                (it.clabel.size() > 0) ? it.clabel.contains(' ') : false
            }, msg    : "clabel can't contain spaces"],
            [condition: {
                (it.clabel.size() > 0) && (it.type == 'text')
            }, msg    : 'clabel defined but type is text'],
            [condition: {
                it.flow.contains('=') && !it.flow.contains('<') && !it.flow.contains('>') && !it.flow.contains('!') && !it.flow.contains('==')

            }, msg    : 'Use Double Equals for comparision(==). Single Equals not allowed(=)'],
            [condition: {
                it.label.contains("\n")
            }, msg    : 'Should not contain new  line characters']


    ])
    dataList(ArrayList, [
            [entity: 'country', qno: '1', label: 'country id', name: 'id', type: 'text', flow: '', clabel: ''],
            [entity: 'country', qno: '2', label: 'Name', name: 'name', type: '', flow: '', clabel: ''],
            [entity: 'country', qno: '3', label: '', name: '', type: 'text', flow: '', clabel: ''],
            [entity: 'country', qno: '4', label: '', name: 'captial city', type: '', flow: '', clabel: ''],

    ])
    expectedReport(ArrayList, [
            [qno: 3, entity: 'country'],
            [qno: 4, entity: 'country'],
            [qno: 2, entity: 'country'],
            [qno: 4, entity: 'country'],
            [qno: 2, entity: 'country'],
            [qno: 4, entity: 'country']

    ])


    dataDictList(ArrayList, [])
    dataDictValidator(DataDictValidator, rules, dataDictList, generator)


}
