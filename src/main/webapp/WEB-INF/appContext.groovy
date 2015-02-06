import com.github.jknack.handlebars.Handlebars
import groovy.sql.Sql
import org.cghr.generator.Generator
import org.cghr.generator.dataStoreInfo.DataStoreInfoGenerator
import org.cghr.generator.db.DbGenerator
import org.cghr.generator.jsonSchema.SchemaGenerator
import org.cghr.generator.jsonSchema.SchemaValidator
import org.cghr.generator.routes.RouteGenerator
import org.cghr.generator.sqlUtil.SqlCustom
import org.cghr.generator.transformer.EntityTransformer
import org.cghr.validator.dataDict.DataDictValidator
import org.springframework.jdbc.datasource.DriverManagerDataSource

def multipleTypesList = ['select', 'multiselect', 'select-inline', 'dropdown', 'suggest', 'select_text', 'select_singletext', 'text_select', 'ffq', 'ffq_other']
def schemaMapping = [text: 'text', select: 'radio', 'select-inline': 'radio-inline', multiselect: 'checkbox', auto: 'hidden', textarea: 'textarea', heading: 'heading', lookup: 'lookup', gps: 'gps', text_select: 'text_select', select_text: 'select_text', select_singletext: 'select_singletext', dropdown: 'dropdown', dynamic_dropdown: 'dynamic_dropdown', suggest: 'suggest', hidden: 'hidden', ffq: 'ffq', ffq_other: 'ffq_other', alcoholFreq: 'alcoholFreq', fmhDisease: 'fmhDisease']
def allowedTypesList = schemaMapping.keySet() as List

beans {

    xmlns([context: 'http://www.springframework.org/schema/context'])
    xmlns([mvc: 'http://www.springframework.org/schema/mvc'])

    //Todo project specific controller packages
    context.'component-scan'('base-package': 'org.cghr.generator.controller')

    mvc.'annotation-driven'()

    String basePath = System.getProperty("basePath")

    //Todo Database Config
    dataSource(DriverManagerDataSource) { bean ->
        bean.lazyInit = "true"
        driverClassName = 'org.relique.jdbc.csv.CsvDriver'
        url = "jdbc:relique:csv:$basePath" + "/csvDatabase"
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
    schemaGeneratorWithMock(SchemaGenerator, sqlCustom, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes)
    //schemaGeneratorWithMock(SchemaGenerator, sqlCustom, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes, "_mr")
    dataStoreInfoGeneratorWithMock(DataStoreInfoGenerator, sqlCustom, generator)

    dbGenerator(DbGenerator, sqlProd, dbEntityTransformer, generator, dbTemplate)
    //schemaGenerator(SchemaGenerator, sqlProd, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes, "_mr")
    schemaGenerator(SchemaGenerator, sqlProd, schemaEntityTransformer, generator, jsonTemplate, multipleItemTypes)
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

    languageCodes(ArrayList, ["", "mr"])

    dbStructureFilePath(String, basePath + "/generated/dbStructure/a.sql")
    schemaFolder(String, basePath + "/generated/schemas/")
    validationReport(String, basePath + "/generated/validation/report.txt")

    webAppsPath(String, "/opt/apache-tomcat-7.0.57/webapps/")

    List hc = [
            [name: 'dataDict', url: 'https://docs.google.com/spreadsheets/d/1vaNylr6RNHIuK9zgEzRMrssDsXKARY7s9XXTi1yL1JI/export?format=csv&id=1vaNylr6RNHIuK9zgEzRMrssDsXKARY7s9XXTi1yL1JI&gid=0'],
            [name: 'clabel', url: 'https://docs.google.com/spreadsheets/d/11P-ncoAHXMXag0NXRRjXpwazYNvlUUmK3a5OdL-7CFc/export?format=csv&id=11P-ncoAHXMXag0NXRRjXpwazYNvlUUmK3a5OdL-7CFc&gid=0'],
            [name: 'crossFlow', url: 'https://docs.google.com/spreadsheets/d/1FQOK-0DIlNVvrlMi83gjASfukvoGYDuIcHtZFdcFuik/export?format=csv&id=1FQOK-0DIlNVvrlMi83gjASfukvoGYDuIcHtZFdcFuik&gid=0'],
            [name: 'entityDesign', url: 'https://docs.google.com/spreadsheets/d/1r2pGFwM9Rg3FnkHHZJKihVJZNm6atmynqL9AXiTDJ4w/export?format=csv&id=1r2pGFwM9Rg3FnkHHZJKihVJZNm6atmynqL9AXiTDJ4w&gid=0'],
            [name: 'entitySchema', url: 'https://docs.google.com/spreadsheets/d/1pyK5sfFAIbP10896sBokZK93Hk7ZC-j4Uvdg8C94E8A/export?format=csv&id=1pyK5sfFAIbP10896sBokZK93Hk7ZC-j4Uvdg8C94E8A&gid=0'],
            [name: 'entitySchemaMasterProperties', url: 'https://docs.google.com/spreadsheets/d/1YdUoPqdeghqRtr1_LdHGSkoadJpWx5ZhIiZjZ_a6W6Q/export?format=csv&id=1YdUoPqdeghqRtr1_LdHGSkoadJpWx5ZhIiZjZ_a6W6Q&gid=0'],
            [name: 'lookup', url: 'https://docs.google.com/spreadsheets/d/1l3azCQh5BpA1WZOfT13p5u9OEj13rhTVYChpyklaUs4/export?format=csv&id=1l3azCQh5BpA1WZOfT13p5u9OEj13rhTVYChpyklaUs4&gid=0'],
            [name: 'crossCheck', url: 'https://docs.google.com/spreadsheets/d/1xLvpA6q2IHM72ChhyqDVJzxXHSG1CeRumSC2gadpnYY/export?format=csv&id=1xLvpA6q2IHM72ChhyqDVJzxXHSG1CeRumSC2gadpnYY&gid=0'],
            [name: 'dynamicDropdown', url: 'https://docs.google.com/spreadsheets/d/1cNYjz2sM6cQrLSTaTbKiYBvfputSq9v7Ba8SIm2zJ3Y/export?format=csv&id=1cNYjz2sM6cQrLSTaTbKiYBvfputSq9v7Ba8SIm2zJ3Y&gid=0'],
            [name: 'mainRoutes', url: 'https://docs.google.com/spreadsheets/d/14EfxoPOPKP3LSSRtMh9koT9M7SiihaIixSA9JWXmdcg/export?format=csv&id=14EfxoPOPKP3LSSRtMh9koT9M7SiihaIixSA9JWXmdcg&gid=0'],
            [name: 'level2Routes', url: 'https://docs.google.com/spreadsheets/d/1s4DgABBfD6pBxmDlXk6m8Z1f2k8lVTv3Psg862Z4s_s/export?format=csv&id=1s4DgABBfD6pBxmDlXk6m8Z1f2k8lVTv3Psg862Z4s_s&gid=0'],
            [name: 'level3Routes', url: 'https://docs.google.com/spreadsheets/d/1m-0i2C3WS8hl_KHRgubOIH0CaKWokupKkkZ9CGdqPzQ/export?format=csv&id=1m-0i2C3WS8hl_KHRgubOIH0CaKWokupKkkZ9CGdqPzQ&gid=0']

    ]
    List mvm = [
            [name: 'dataDict', url: 'https://docs.google.com/spreadsheets/d/1q-UUaZCejBt3snPANIm-UmR_qoc_qGni2jRhrLCO7-0/export?format=csv&id=1q-UUaZCejBt3snPANIm-UmR_qoc_qGni2jRhrLCO7-0&gid=0'],
            [name: 'clabel', url: 'https://docs.google.com/spreadsheets/d/1PT3TUS_HoZK8lJvu2Hx5qPfZrLwfPs-XVDmaQ_iHWg4/export?format=csv&id=1PT3TUS_HoZK8lJvu2Hx5qPfZrLwfPs-XVDmaQ_iHWg4&gid=800096487'],
            [name: 'crossFlow', url: 'https://docs.google.com/spreadsheets/d/1utyU-A1aq3yJutNiHuVtz6fPP9lYExNf-arWpqcc2_U/export?format=csv&id=1utyU-A1aq3yJutNiHuVtz6fPP9lYExNf-arWpqcc2_U&gid=1502380304'],
            [name: 'entityDesign', url: 'https://docs.google.com/spreadsheets/d/1DA0wMEYv1TW2-dJFuAZ1KO9iUiA63lMEK5Tq9zeQ7Tw/export?format=csv&id=1DA0wMEYv1TW2-dJFuAZ1KO9iUiA63lMEK5Tq9zeQ7Tw&gid=0'],
            [name: 'entitySchema', url: 'https://docs.google.com/spreadsheets/d/1fr5KvywbYI0kLpAmbnCfp_JGdDDsKJWTBsEQle8klpc/export?format=csv&id=1fr5KvywbYI0kLpAmbnCfp_JGdDDsKJWTBsEQle8klpc&gid=0'],
            [name: 'entitySchemaMasterProperties', url: 'https://docs.google.com/spreadsheets/d/1KzXBygP3TAwszN1B3uDF23akp7-LWVBGR4LgtpZNzYU/export?format=csv&id=1KzXBygP3TAwszN1B3uDF23akp7-LWVBGR4LgtpZNzYU&gid=0'],
            [name: 'crossCheck', url: 'https://docs.google.com/spreadsheets/d/17gDdGEFkkGVnzLT1wXYdswgD6R6aniS-oGBhuH7B6Kk/export?format=csv&id=17gDdGEFkkGVnzLT1wXYdswgD6R6aniS-oGBhuH7B6Kk&gid=0']
    ]

    onlineDocs(HashMap, [hc: hc, mvm: mvm])


}
