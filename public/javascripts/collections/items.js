var app = app || {};

(function () {
	'use strict';

	var Items = Backbone.Collection.extend({
		model: app.Item,
		url: 'items'
	});

	app.items = new Items();
})();
