require.config({
	baseUrl : "modules",
	paths : {
		"jquery" : "http://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min"
	},
	shim : {}
});

define([ "module", "message-bus" ], function(module, bus) {
	var moduleList = module.config();

	require(moduleList, function() {
		bus.send("modules-loaded");
	});
});
