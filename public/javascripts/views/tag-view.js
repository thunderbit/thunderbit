var app = app || {};

(function ($) {
	'use strict';

	app.TagView = Backbone.View.extend({
		tagName:  'li',

		template: _.template($('#tag-template').html()),

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
		    this.model.trigger('destroy', this.model);
		}
	});
})(jQuery);
