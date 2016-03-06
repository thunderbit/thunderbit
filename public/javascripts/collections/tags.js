var app = app || {};

(function () {
	'use strict';

	var Tags = Backbone.Collection.extend({
		model: app.Tag
	});

	app.searchTags = new Tags();
	app.uploadTags = new Tags();
})();
