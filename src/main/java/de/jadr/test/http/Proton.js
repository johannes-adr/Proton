function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function setCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/";
}
const PORT = getCookie("port");

function fetchRest(strUrl, strMethod, objPayload, funcCallback) {
    let strPayload = JSON.stringify(objPayload);
    fetch(`http://localhost:${PORT}${strUrl}`, {
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


function setCookie(name, value, days) {
    var expires = '';
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = '; expires=' + date.toUTCString();
    }
    document.cookie = name + '=' + (value || '') + expires + '; path=/';
}