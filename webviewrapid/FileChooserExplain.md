1.默认不开启开启图片上传的功能（因为默认值为0），那啥都不用做。
2.使用自带图片上传功能，intent用WebView给的，设置值为1，在Activity的onActivityResult()时调用rapidWebView.handleFileChooser()并传入回调的结果即可
3.使用自带的图片上传功能，intent用相册 + 拍照，设置值为2，在Activity的onActivityResult()时调用rapidWebView.handleFileChooser()并传入回调的结果即可
4.使用图片上传功能，调用setmShowFileChooserCallback()传入图片上传时的回调，并在onActivityResult()中对选择的图片自己进行处理

如果1，强行在在Activity的onActivityResult()中调用rapidWebView.handleFileChooser()，那也是没有效果的