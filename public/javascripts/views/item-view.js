var app = app || {};

(function ($) {
	'use strict';

	app.ItemView = Backbone.View.extend({
		tagName:  'tr',

		template: _.template($('#item-template').html()),

		events: {
			'click .delete-btn': 'clear'
		},

		initialize: function () {
			this.listenTo(this.model, 'destroy', this.remove);
		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		},

		clear: function () {
			this.model.destroy();
		}
	});
})(jQuery);
