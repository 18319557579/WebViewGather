function calledBy_loadUrl(arguments) {
    console.log("JS得到Android的传值: " + arguments)
}

function calledBy_evaluateJavascript(arguments) {
    console.log("JS得到Android的传值: " + arguments)
    return 'Hello Android.'
}