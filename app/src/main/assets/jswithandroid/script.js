function calledBy_loadUrl(arguments) {
    console.log("JS得到Android的传值: " + arguments)
}

function calledBy_evaluateJavascript(arguments) {
    console.log("JS得到Android的传值: " + arguments)
    return 'Hello Android.'
}

function callAndroidFrom_documentlocation() {
    document.location = "daisyscheme://daisyhost?daisyarg1=123&daisyarg2=321"
}