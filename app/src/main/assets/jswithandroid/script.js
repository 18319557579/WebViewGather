function calledBy_loadUrl(arguments) {
    console.log("JS得到Android的传值: " + arguments)
}

function calledBy_evaluateJavascript(arguments) {
    console.log("JS得到Android的传值: " + arguments)
    return 'Hello Android.'
}


function callAndroidFrom_addJavaScriptInterface() {
    var result = jscallandroid.executeHello('通过addJavaScriptInterface()对象映射')
    console.log("JS获得了Android的返回值: " + result)
}
function callAndroidFrom_documentlocation() {
    document.location = "daisyscheme://daisyhost?daisyarg1=123&daisyarg2=321"
}
function callAndroidFrom_prompt() {
    var result = prompt("daisyscheme://daisyhost?daisyarg1=123&daisyarg2=我是来自prompt")
    console.log("JS获得了Android的返回值: " + result)
}