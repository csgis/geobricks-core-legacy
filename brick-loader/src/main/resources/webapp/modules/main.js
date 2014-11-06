require.config({
	baseUrl : "modules",
	$paths,
	$shim,
	waitSeconds : 15
});

var defaultOnError = require.onError;

require.onError = function(err) {
	var body = document.body;

	var shade = document.createElement("div");
	shade.className = "fatal_error_shade";

	var text = document.createElement("div");
	text.className = "fatal_error";

	body.appendChild(shade);
	body.appendChild(text);

	if (err.requireType == "timeout") {
		text.appendChild(document.createTextNode("Timeout downloading web components, try reloading."));
		console.error(err);
	} else {
		text.appendChild(document.createTextNode("Internal error."));
		defaultOnError(err);
	}
}

define([ "module", "message-bus" ], function(module, bus) {
	var moduleList = module.config();

	require(moduleList, function() {
		bus.send("modules-loaded");
	});
});
