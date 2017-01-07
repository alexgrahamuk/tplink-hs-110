definition(
        name: "TP Link HS-110 (Connect)",
        namespace: "alexgrahamuk",
        author: "Alex Graham",
        description: "Allows you to connect your HS-110 energy monitored sockets using the alexgrahamuk hs-110-server.",
        category: "My Apps",
        iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/hue.png",
        iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/hue@2x.png",
        singleInstance: true
)

preferences {
    section("Devices") {
        input(name: "devices", type: "device.tplinkSocketWithPowerMeter", title: "HS-110 Switches", required: true, displayDuringSetup: true, multiple: true)
    }
    section("Gateway") {
        input("gatewayIP", "text", title: "Gateway IP", required: true, displayDuringSetup: true)
        input("gatewayPort", "text", title: "Gateway Port", required: true, displayDuringSetup: true)
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    //   unsubscribe()
    initialize()
}

def initialize() {

    subscribe(devices, "switch.on", "switchOnHandler")
    subscribe(devices, "switch.off", "switchOffHandler")
    subscribe(devices, "refresh", "switchRefreshHandler")

    /*def headers = [:]
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
    }*/
}

def switchOnHandler(evt)
{
    log.debug("A switch turned on")
}

def switchOffHandler(evt)
{
    log.debug("A switch turned off")
}

def switchRefreshHandler(evt)
{
    log.debug("A switch was refreshed")
}