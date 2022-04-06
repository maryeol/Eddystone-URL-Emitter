# Eddystone-URL-Emitter
This repository contains an example of using the Android BLE APIs (21+) to emit an Eddystone-URL beacon. You can find more information about Eddystone at https://github.com/google/eddystone
# Eddystone format
Eddystone is an open beacon format developed by Google and designed with transparency and robustness in mind. Eddystone can be detected by both Android and iOS devices. The Eddystone format builds on lessons learned from working with industry partners in existing deployments, as well as the wider beacon community. Several different types of payload can be included in the frame format(URL/UID/TLM/EID).
In this project, You will learn how to emit Eddystone URL from Android app.
# Eddystone Specification
The Eddystone-URL frame broadcasts a URL using a compressed encoding format in order to fit more within the limited advertisement packet.
You can set up your own link. However, the Eddystone protocol provides 17 bytes for the URL packet so you might need a URL shortener.
All URLs must resolve to an HTTPS URL. Google requires all Physical Web pages be served over HTTPS due to security considerations. 

for more Details : https://developers.google.com/beacons/eddystone
