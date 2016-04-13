com.polarcape.plugins.keystore
==============================

Cordova plugin for storing sensitive information in app keystore/keychain

#Install
        window.keystore.getKey(successCallback, errorCallback, keyStorage);
#Usage
		
	var keyStorage = {"keyName": "key"};
	
        window.keystore.getKey(
            function (success) {
                if (success.modulus === '') {
                    success.modulus = devId;
                }
            }, function (error) {
                logDebug("error getting android private key modulus: " + JSON.stringify(error));
            }, keyStorage);

#Example
