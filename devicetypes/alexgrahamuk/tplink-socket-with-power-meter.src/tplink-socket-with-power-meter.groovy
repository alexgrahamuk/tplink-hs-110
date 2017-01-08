/**
 *  tplink-hs-100
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

metadata {
    definition(name: "Tplink Socket With Power Meter", namespace: "alexgrahamuk", author: "Alex Graham") {
        capability "Polling"
        capability "Switch"
        capability "Refresh"
        capability "Power Meter"

        command "on"
        command "off"
        command "refresh"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {

        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
                attributeState "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
                attributeState "turningOn", label: 'Turning On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
                attributeState "turningOff", label: 'Turning Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
            }
            tileAttribute ("power", key: "SECONDARY_CONTROL") {
                attributeState "power", label:'${currentValue} W'
            }
        }

        standardTile("refresh", "command.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main "switch"
        details(["switch","refresh"])
    }
}

preferences {
    input("outletIP", "text", title: "Outlet IP", required: true, displayDuringSetup: true)
    input("gatewayIP", "text", title: "Gateway IP", required: true, displayDuringSetup: true)
    input("gatewayPort", "text", title: "Gateway Port", required: true, displayDuringSetup: true)
}

def message(msg) {
    log.debug(msg)
}

// parse events into attributes
def parse(String description) {

    message("Parsing: $description")
    
    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data

    sendEvent(name: "power", value: "123", isStateChange: true)

    //def uuid = UUID.randomUUID().toString()
    //device.deviceNetworkId = "tp_link_${uuid}"
    
    def myEvt = createEvent(name:"hubInfo", value:"This is a test (hardcoded)", isStateChange: true)
    
    return myEvt
}

def refresh() {
    message("Executing 'refresh'")
    executeCommand("status")
    sendEvent(name: "refresh", value: "refresh", isStateChange: true)
}

// handle commands
def on() {
    message("Executing 'on'")
    executeCommand("on")
    sendEvent(name: "switch", value: "on", isStateChange: true)
    sendEvent(name: "power", value: "0", isStateChange: true)
}

def off() {
    message("Executing 'off'")
    executeCommand("off")
    sendEvent(name: "switch", value: "off", isStateChange: true)
    sendEvent(name: "power", value: "0", isStateChange: true)
}

def hubActionResponse(response) {

    message("Executing 'hubActionResponse': '${device.deviceNetworkId}'")

    def status = response.headers["x-hs100-status"] ?: ""
    message("switch status: '${status}'")
    if (status != "") {
        sendEvent(name: "switch", value: status, isStateChange: true)
    }

}

def poll() {
    message("Executing 'poll'")
}


private executeCommand(command) {

    def gatewayIPHex = convertIPtoHex(gatewayIP)
    def gatewayPortHex = convertPortToHex(gatewayPort)

    device.deviceNetworkId = "000C290D21CC"

    def headers = [:]
    headers.put("HOST", "$gatewayIP:$gatewayPort")
    headers.put("x-hs110-ip", outletIP)
    headers.put("x-hs110-command", command)
    headers.put("callback", getCallBackAddress())

    try {
        sendHubCommand(new physicalgraph.device.HubAction([
                method : "POST",
                path   : "/",
                headers: headers],
                device.deviceNetworkId,
                [callback: "hubActionResponse"]
        ))
    } catch (e) {
        message(e.message)
    }
}

//Callback Stuff
private getCallBackAddress() {
    device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

private Integer convertHexToInt(hex) {
    Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
    def parts = device.deviceNetworkId.split(":")
    def ip = convertHexToIP(parts[0])
    def port = convertHexToInt(parts[1])
    return ip + ":" + port
}

//Utils

private String convertIPtoHex(ipAddress) {
    String hex = ipAddress.tokenize('.').collect { String.format('%02x', it.toInteger()) }.join()
    return hex
}

private String convertPortToHex(port) {
    String hexport = port.toString().format('%04x', port.toInteger())
    return hexport
}