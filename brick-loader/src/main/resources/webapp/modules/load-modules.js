define([ "module", "message-bus" ], function(module, bus) {
	var moduleList = module.config();
	console.log(moduleList);
	require(moduleList, function() {
		console.log("Yay!");
		bus.send("modules-loaded");
	});
});
