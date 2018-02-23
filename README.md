# Malicious AdServer #

'metawear-ads' provide ads in WebView of MetaWear-SampleApp-Android. WebView uses the JavaScript code inside it to invoke Android apps' Java code using addJavaScriptInterface API. Android applications register Java objects from WebView through this API, and all the public methods in these objects are invoked by JavaScript code provided here. 

## Instruction ##
- clone the repo
- cut folder 'metawear-ads' (it is not part of MetaWear-SampleApp-Android)
- paste the folder 'metawear-ads' inside htdocs folder of XAMPP or www folder of WAMP server.


