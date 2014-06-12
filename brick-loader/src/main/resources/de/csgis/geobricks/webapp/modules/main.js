require.config({
	baseUrl : "modules",
	paths : {
		$nonRequireJSDependencies
	},
	shim : {},
	waitSeconds : 15
});

require.onError = function(err) {
	var msg;
	if (err.requireType == "timeout") {
		msg = "Timeout downloading web components, try reloading.";
	} else {
		msg = "Internal error.";
	}

	var body = document.body;

	var shade = document.createElement("div");
	shade.className = "fatal_error_shade";

	var text = document.createElement("div");
	text.appendChild(document.createTextNode(msg));
	text.className = "fatal_error";

	body.appendChild(shade);
	body.appendChild(text);

	console.error(err);
}

define([ "module", "message-bus" ], function(module, bus) {
	var moduleList = module.config();

	require(moduleList, function() {
		bus.send("modules-loaded");
	});
});
