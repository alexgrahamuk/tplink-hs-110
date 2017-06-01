/**
 *  TPLink HS-110 (Connect)
 *
 *  Copyright 2016 `
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

definition(
        name: "TPLink HS-110 (Connect)",
        namespace: "alexgrahamuk",
        author: "Alex Graham",
        description: "Allows you to connect your HS-110 energy monitored sockets using the alexgrahamuk hs-110-server.",
        category: "My Apps",
    	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
		iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
        singleInstance: true
)

preferences {
    section("Devices") {
        input(name: "devices", type: "device.tplinkSocketWithPowerMeter", title: "HS-110 Switches", required: true, displayDuringSetup: true, multiple: true)
    }
    section("Gateway") {
        input(name: "gateway", type: "device.tplinkSocketGateway", title: "HS-110 Gateway", required: true, displayDuringSetup: true, multiple: false)
    }
}


def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {

    subscribe(devices, "switch.on", switchActionHandler)
    subscribe(devices, "switch.off", switchActionHandler)
    subscribe(devices, "refresh.refresh", switchActionHandler)
    subscribe(gateway, "ping", switchStatusHandler)
}

def switchActionHandler(evt) {

	def jsonSlurper = new groovy.json.JsonSlurper()
	def eventData = jsonSlurper.parseText(evt.data)
    
	log.debug(evt)
    log.debug(evt.data)
   
	def device = devices.find { eventData.deviceNetworkId == it.id }  
    log.debug(device)
    
    return
   
    sendEvent(device, [name: "power", value: 777, isStateChange: true])
    
    gateway.executeCommand("refresh", eventData)
}

def switchStatusHandler(evt) {

    log.debug("A switch was queried for status")

    def description = evt.value
    message("Parsing: $description")

    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data

    //def uuid = UUID.randomUUID().toString()
    //device.deviceNetworkId = "tp_link_${uuid}"

    //sendEvent(name: "power", value: "123", isStateChange: true)
}
