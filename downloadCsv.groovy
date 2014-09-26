#!/usr/bin/groovy
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

String googleAuth = "DQAAAO0AAABCMwkMOhvmcuD1HYraevsdfKJgd-w9nWGDch4vhiSVndca5CMz7j3kvGWgQv9ose2zqxzVkteyOOQFeycyQdkIAAn5oplZsubxf23f_yvP8FhaPxdcwa0r2dc-ogqA7cccVhRRmKr3mXVB64moxAf6azF_fCpoopVO7-bBx5iwlrNQ2Pcz4XXSGvD3SAks2b2s4kpX7YAgyXYZ2NyervEPiKGg68dTHfJUOvTAnfTiEvY9lNWGOg7dgP0olxrEWZkuti-b7XwjjyEHkFKlPQajhYQE4PRbhx1AlPoCX5PBZSjzJyXysbL4AIzUQUxhKbQ"

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
        [name: 'dataDict', url: 'https://docs.google.com/spreadsheets/d/1PAkd1YLbBKH5VdTmxlm-f3tiTJocEGK0tCg5eyodtOs/export?format=csv&id=1PAkd1YLbBKH5VdTmxlm-f3tiTJocEGK0tCg5eyodtOs&gid=1430303457'],
        [name: 'clabel', url: 'https://docs.google.com/spreadsheets/d/1qESqJ6Oje4raN2QDphYiu21-ReMCJYR8k2k-9D5IyZA/export?format=csv&id=1qESqJ6Oje4raN2QDphYiu21-ReMCJYR8k2k-9D5IyZA&gid=800096487'],
        [name: 'crossFlow', url: 'https://docs.google.com/spreadsheets/d/1utyU-A1aq3yJutNiHuVtz6fPP9lYExNf-arWpqcc2_U/export?format=csv&id=1utyU-A1aq3yJutNiHuVtz6fPP9lYExNf-arWpqcc2_U&gid=1502380304'],
        [name: 'entityDesign', url: 'https://docs.google.com/spreadsheets/d/1DA0wMEYv1TW2-dJFuAZ1KO9iUiA63lMEK5Tq9zeQ7Tw/export?format=csv&id=1DA0wMEYv1TW2-dJFuAZ1KO9iUiA63lMEK5Tq9zeQ7Tw&gid=0'],
        [name: 'entitySchema', url: 'https://docs.google.com/spreadsheets/d/1fr5KvywbYI0kLpAmbnCfp_JGdDDsKJWTBsEQle8klpc/export?format=csv&id=1fr5KvywbYI0kLpAmbnCfp_JGdDDsKJWTBsEQle8klpc&gid=0'],
        [name: 'entitySchemaMasterProperties', url: 'https://docs.google.com/spreadsheets/d/1KzXBygP3TAwszN1B3uDF23akp7-LWVBGR4LgtpZNzYU/export?format=csv&id=1KzXBygP3TAwszN1B3uDF23akp7-LWVBGR4LgtpZNzYU&gid=0']
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
        ["sh", "-c", """curl --silent --header \"Authorization: GoogleLogin auth=$googleAuth\" \"$file.url\"  >> "csvDatabase/"$file.name".csv" """].execute()
}
println 'Download finished\n'

//Todo Generate
println 'Generating schemas...Processing...'
["sh", "-c", "rm -rf build/"].execute()
["sh", "-c", "rm -rf generated/schemas/*.json"].execute()
def output = ["sh", "-c", "gradle check"].execute().text
println output
println 'Generated schemas and db structure\n'

//Todo Copy
["sh", "-c", "cp generated/schemas/* ~/apps/$projectName/ui/src/assets/jsonSchema"].execute()
["sh", "-c", "cp generated/dbStructure/db.sql ~/apps/$projectName/services/src/main/webapp/sqlImport/"].execute()
["sh", "-c", "cd ~/apps/$projectName/services/src/main/webapp/sqlImport"].execute()
["sh", "-c", "mv db.sql a.sql"].execute()
println 'copied to respective directories'


