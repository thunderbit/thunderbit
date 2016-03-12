var app = app || {};

(function ($) {
	'use strict';

	app.AppView = Backbone.View.extend({
	    el: '.thunderbit-app',

		initialize: function () {
			this.$itemsContainer = $('.items-container');
			this.$uploadTagsContainer = $('.upload-file-modal .tags-container');
			this.$searchTagsContainer = $('.search-box .tags-container');

			this.listenTo(app.items, 'reset', this.renderItems);

			this.listenTo(app.uploadTags, 'add', this.addUploadTagView);
			this.listenTo(app.uploadTags, 'reset', this.resetUploadTagsView);

			this.listenTo(app.searchTags, 'add', this.addSearchTagView);
			this.listenTo(app.searchTags, 'update', this.fetchItems);

			app.items.fetch({reset: true});
		},

		renderItems: function () {
			this.$itemsContainer.html('');
			app.items.each(function (item) {
                    var view = new app.ItemView({ model: item });
                    this.$itemsContainer.append(view.render().el);
                }, this);
		},

		addUploadTagView: function (tag) {
            var view = new app.TagView({ model: tag });
            this.$uploadTagsContainer.append(view.render().el);
        },

        resetUploadTagsView: function () {
            this.$uploadTagsContainer.html('');
            app.uploadTags.each(this.addUploadTagView, this);
        },

        addSearchTagView: function (tag) {
            var view = new app.TagView({ model: tag });
            this.$searchTagsContainer.append(view.render().el);
        },

        fetchItems: function () {
            var tags = [];
            app.searchTags.each(function(tag) {
                tags.push(tag.get("name"));
            });
            app.items.fetch({data: {tags: tags.join(",")}, reset: true});
        }
	});
})(jQuery);
