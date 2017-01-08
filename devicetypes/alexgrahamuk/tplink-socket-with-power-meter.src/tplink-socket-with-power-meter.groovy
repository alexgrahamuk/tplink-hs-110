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
}

def message(msg) {
    log.debug(msg)
}


def refresh() {
    message("Executing 'refresh'")
    sendEvent(name: "refresh", value: "refresh", isStateChange: true)
}

def on() {
    message("Executing 'on'")
    sendEvent(name: "switch", value: "on", isStateChange: true)
}

def off() {
    message("Executing 'off'")
    sendEvent(name: "switch", value: "off", isStateChange: true)
}

def power(val) {
    message("Executing 'power'")
    sendEvent(name: "power", value: val, isStateChange: true)
}

def poll() {
    message("Executing 'poll'")
}

