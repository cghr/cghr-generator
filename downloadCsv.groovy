#!/usr/bin/env groovy

//Configuration For language Code
String languageCode = ""
//languageCode="mr"

println """ Project List
1.HC
2.Hcamp
3.HCServer
4.Man vs Machine """

BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
print "Select:"
def projectId = br.readLine().toInteger()

def projects = ["", "hc", "hcamp", "hcServer", "mvm", "logistics"]
def projectName = projects.get(projectId)

//Todo Download
["sh", "-c", "rm csvDatabase/*.csv"].execute()
println "Cleaned up  all old csv files\n"

String encodedData = "DQAAAO0AAAAu1enVTHz5nYYb717Rcb66c7ygZ-SnU0NYxl2ZqZ_ePNp3n56mB3y85EnJ7vHQ7CEX0AuUts9wNxWWNUGUjPuFv4QnWkIW88JEsitOXtGa-y7Vw4m2Vt1GE3mi7tv_13aVvuDWsrRUcKuE5pA15R6AIuusLjZdZuLPGoR4CvYVsZgVJJEfkyuaOvro3ndRq1WFZKYC4fcB9UtWytAYKorWJC-S4hG4iQD1u7cNp5g38_JWkqiCb7MkzvFGWcJOsXHB8RO6edb03bJz0zGU_jJboBleSdLJpJlJkyuCVlR3KkWwEL_0sLOpi9LqB7j6dos"

println "Starting to download csv files from google docs...."
List repository = []
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
List hcamp = [
        [name: 'dataDict', url: 'https://docs.google.com/spreadsheets/d/177L8QuXCFqzAop1jQcCQNuuYB_IVhhn9JAdtXyi5gdI/export?format=csv&id=177L8QuXCFqzAop1jQcCQNuuYB_IVhhn9JAdtXyi5gdI&gid=0'],
        [name: 'clabel', url: 'https://docs.google.com/spreadsheet/fm?id=tX7ewp4fK-5qc_pFazDC0VQ.11408791945967866523.779868074028532667&fmcmd=5&gid=0'],
        [name: 'crossFlow', url: 'https://docs.google.com/spreadsheets/d/1H0mqOoHORlLzl183_nnwauLiUTLVaBujZe5W4Giqy88/export?format=csv&id=1H0mqOoHORlLzl183_nnwauLiUTLVaBujZe5W4Giqy88&gid=0'],
        [name: 'entityDesign', url: 'https://docs.google.com/spreadsheets/d/12GqdyDTDkmTCv79WOGU5pB3XVXxB6dJcxtPNrUkfsEU/export?format=csv&id=12GqdyDTDkmTCv79WOGU5pB3XVXxB6dJcxtPNrUkfsEU&gid=0'],
        [name: 'entitySchema', url: 'https://docs.google.com/spreadsheets/d/1O3kFgT6LC0UYh18KZ6lB5fRk8aVTig2bfoJWc9n8od8/export?format=csv&id=1O3kFgT6LC0UYh18KZ6lB5fRk8aVTig2bfoJWc9n8od8&gid=0'],
        [name: 'entitySchemaMasterProperties', url: 'https://docs.google.com/spreadsheets/d/1YlmPgNwzMpO7tNQeCy79VdDBWZ_EQ7999Zdsmllp1G4/export?format=csv&id=1YlmPgNwzMpO7tNQeCy79VdDBWZ_EQ7999Zdsmllp1G4&gid=0'],
        [name: 'lookup', url: 'https://docs.google.com/spreadsheets/d/1sP1z1VWVxb3zYFVjncR-saccln2IBusPkcHHbP1aSdg/export?format=csv&id=1sP1z1VWVxb3zYFVjncR-saccln2IBusPkcHHbP1aSdg&gid=0'],
        [name: 'crossCheck', url: 'https://docs.google.com/spreadsheets/d/1nfw3xLDf4pGyobFfXSHegepZO2aLUYfvck6BUuL8xuY/export?format=csv&id=1nfw3xLDf4pGyobFfXSHegepZO2aLUYfvck6BUuL8xuY&gid=0']
]
List hcServer = [

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
List logistics = [

]

repository.add([])
repository.add(hc)
repository.add(hcamp)
repository.add(hcServer)
repository.add(mvm)
repository.add(logistics)


repository[projectId].each {
    Map file ->
        ["sh", "-c", """curl --silent --header \"Authorization: GoogleLogin auth=$encodedData\" \"$file.url\"  >> "csvDatabase/"$file.name".csv" """].execute()
}
println 'Download finished\n'

//Todo Generate
println 'Generating schemas...Processing...'
["sh", "-c", "rm -rf build/"].execute()
["sh", "-c", "rm -rf generated/schemas/*.json"].execute()
println "Cleaned up directories"
def output = ["sh", "-c", "gradle -Dtest=org.cghr.generator.GenerateAll test"].execute().text
println output
println 'Generated schemas and db structure\n'

def sqlImportPath = "~/apps/$projectName/services/src/main/webapp/sqlImport/".toString()

println 'sql Import path'
println sqlImportPath

//Todo Copy
String schemaPath = languageCode ?: ""
println schemaPath
["sh", "-c", "cp generated/schemas/* ~/apps/$projectName/ui/src/assets/jsonSchema/" + schemaPath].execute()
["sh", "-c", "cp generated/dbStructure/a.sql ~/apps/$projectName/services/src/main/webapp/sqlImport/"].execute()


