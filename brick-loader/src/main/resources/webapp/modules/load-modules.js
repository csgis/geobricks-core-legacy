define([ "module", "message-bus" ], function(module, bus) {
	var moduleList = module.config();
	require(moduleList, function() {
		bus.send("modules-loaded");
	});
});
