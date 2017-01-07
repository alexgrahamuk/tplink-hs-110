definition(
        name: "TP Link HS-110 (Connect)",
        namespace: "alexgrahamuk",
        author: "Alex Graham",
        description: "Allows you to connect your HS-110 energy monitored sockets using the alexgrahamuk/hs-110-server.",
        category: "Bridges", //check me
        iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/hue.png", //change me
        iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/hue@2x.png", //change me
        singleInstance: true
)


preferences {
    input(name: "devices", type: "device.tplink-hs-110", title: "HS-110 Switches", required: true, displayDuringSetup: true, multiple: true)
    input("gatewayIP", "text", title: "Gateway IP", required: true, displayDuringSetup: true)
    input("gatewayPort", "text", title: "Gateway Port", required: true, displayDuringSetup: true)
}