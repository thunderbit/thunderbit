var app = app || {};

(function ($) {
	'use strict';

	app.AppView = Backbone.View.extend({
	    el: '.thunderbit-app',

		initialize: function () {
			this.$itemsContainer = $('.items-container');

			this.listenTo(app.items, 'reset', this.renderItems);

			app.items.fetch({reset: true});
		},

		renderItems: function () {
			this.$itemsContainer.html('');
			app.items.each(function (item) {
                    var view = new app.ItemView({ model: item });
                    this.$itemsContainer.append(view.render().el);
                }, this);
		}
	});
})(jQuery);
