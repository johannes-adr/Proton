class Cookies {
    static getCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    static setCookie(name, value, days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "") + expires + "; path=/";
    }
}

class JUtils {
    static generateID() {
        return Math.random().toString(36).substring(2) + Date.now().toString();
    }
}


class Proton {

    static getPort() {
        return $port;
    }
    static getWebSocketPort() {
        return $wsport;
    }
    static getToken() {
        return $token;
    }
    static getArgs() {
        return JSON.parse('$jsonargs');
    }

    static fetch(adress, jsonObj, callback) {
        let id = JUtils.generateID();
        Proton.packetHandlers[id] = callback;
        Proton.websocket.send(JSON.stringify({
            "id": id,
            "adress": adress,
            "body": jsonObj
        }));
    }

    static registerEvent(event, callback) {
        let eventarr = Proton.eventlisteners[event];
        if (typeof eventarr == 'undefined') {
            Proton.eventlisteners[event] = [];
            eventarr = Proton.eventlisteners[event];
        }
        eventarr.push(callback);
        console.log("Registered event! " + event);
    }

    static objectToString(obj) {
        let string = "[";
        if (obj === null) {
            return "null";
        }
        Object.keys(obj).forEach((key) => {
            let val = obj[key];
            val = typeof val == 'object' ? this.objectToString(val) : val;
            string += "(" + key + "=>" + val + ")";
        });
        return string + "]";
    }

    static log(obj) {
        console.log(Proton.objectToString(obj));
    }

    static fetchRest(strUrl, strMethod, objPayload, funcCallback) {
        let strPayload = JSON.stringify(objPayload);
        fetch(`http://localhost:${Proton.getPort()}${strUrl}`, {
                method: strMethod.toUpperCase(),
                headers: {
                    "Content-Type": "application/json",
                    "Content-Lenght": strPayload.length
                },
                body: strPayload,
            })
            .then(response => response.text())
            .then((body) => {
                let json = JSON.parse(body);
                funcCallback(json);
            });
    }
}
//Create idhandler
Proton.packetHandlers = {};
Proton.eventlisteners = {};


Proton.websocket = new WebSocket("ws://localhost:" + Proton.getWebSocketPort());
Proton.websocket.onopen = e => {
    //Connection established
    Proton.websocket.send(JSON.stringify({
        "token": Proton.getToken()
    }));
}

Proton.websocket.onclose = () => {
    alert("ERROR [17]");
}
JUtils.generateID();
Proton.websocket.onmessage = e => {
    let json = JSON.parse(e.data);


    //this packet is an answer for a fetch
    if ("id" in json) {
        if ("error" in json) {
            Proton.packetHandlers[json.id]({
                "error": json.error
            });
        } else {
            Proton.packetHandlers[json.id](json.body);
        }
        delete Proton.packetHandlers[json.id];
    } else
    //This packet is a eventcall
    if ("event" in json) {
        let name = json.event;
        let listeners = Proton.eventlisteners[name];
        if (typeof listeners != 'undefined') {
            listeners.forEach(e => {
                e(json.body);
            });
        }
    }
}