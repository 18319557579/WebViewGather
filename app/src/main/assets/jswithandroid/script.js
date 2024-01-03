function calledBy_loadUrl(arguments) {
    console.log("JS得到Android的传值: " + arguments)
}

function calledBy_evaluateJavascript(arguments) {
    console.log("JS得到Android的传值: " + arguments)

    //下面是解析JSON数据
    console.log("判断是否为JSON数据类型" + isJSON(arguments))
    if (isJSON(arguments)) {
        const obj = JSON.parse(arguments)
        console.log(obj.page)
        console.log(obj.code)
        console.log(obj.data.weight)
        console.log(obj.data.person[0])
        console.log(obj.data.person[1].name)
        console.log(obj.data.person[1].nickname)
    }

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



//判断是否为JSON类型的数据
function isJSON(str) {
    if (typeof str != 'string') {
        return false
    }

    try {
        var obj=JSON.parse(str);
        if(typeof obj == 'object' && obj ){
            return true;
        }else{
            return false;
        }

    } catch(e) {
        return false;
    }
}