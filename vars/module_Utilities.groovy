// vars/module_Utilities.groovy
import groovy.json.JsonSlurperClassic
 
Map parseJSONString(String json) {
    def jsonSlurper = new JsonSlurperClassic()
    return jsonSlurper.parseText(json) as Map
}