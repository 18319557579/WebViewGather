1.
webView.onResume()和webView.onPause()不是用来重写的，而是用来调用的
webView.onPause()：
调用后：对WebView的操作会变得没有反应，WebView中的轮播图也不动了

webView.onPause()：通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。
调用后，WebView可以接受操作了，并且在webView.onPause()期间的执行操作，也都展现了出来。轮播图也重新开始可以滚动了

结论：在webView.onPause()期间的无法执行操作，WebView像是失效了一样，调用webView.onResume()后，又重新活跃了起来

2.
//当应用程序(存在webview)被切换到后台时，这个方法不仅仅针对当前的webview而是全局的全应用程序的webview
//它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
webView.pauseTimers()
//恢复pauseTimers状态
webView.resumeTimers()；

webView.pauseTimers()调用后，轮播图不自己滚动了，手动还是可以动；点击网页里面的内容，也无法跳转了；滑动到京东页面的顶部，
横幅也不出来了；网页里面的倒计时也不动了
webView.resumeTimers()调用后 ，轮播图可以自己滚动了，点击网页可以跳转了，京东顶部的横幅出来了，倒计时又能动了

。