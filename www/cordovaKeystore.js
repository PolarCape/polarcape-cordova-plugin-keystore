var cordovaKeystore = {
	getKey: function(successCallback,errorCallback,args) {
    	cordova.exec(
            	function(data){successCallback(data);},
            	function(err){errorCallback(err);},
            	"CordovaKeystore",
            	'getKey',
            	[args]
    	);
	}
};

module.exports = cordovaKeystore;