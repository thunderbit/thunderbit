// Resets upload modal
function resetUploadModal () {
    $('.upload-file-modal .upload-btn').button('reset');
    $('.upload-file-modal .cancel-btn').show();
    $('.upload-file-modal .tags-input').val('');
    $('.upload-progress').hide();
    updateUploadProgressBarValue(0);
    app.uploadTags.reset();
}

// Updates the progress value of the upload progress bar
function updateUploadProgressBarValue (percent) {
    $('.upload-progress .progress-bar').css('width', percent + '%');
    $('.upload-progress .progress-bar .sr-only').html(percent + "% " + messages['completed']);
}

// Gets current value of search box's tags input, checks it is not in the upload tags list,
// creates a tag from it and adds it to the search tags list
function addTagFromSearchBoxTagsInput () {
    var tagsInput = $('.search-box .tags-input.typeahead.tt-input');
    if (tagsInput.val() != null && tagsInput.val() != "" && app.searchTags.findWhere({name: tagsInput.val().toLowerCase()}) == null) {
        app.searchTags.add({name: tagsInput.val().toLowerCase()});
        tagsInput.val('');
    }
    tagsInput.focus();
}

// Gets current value of upload form's tags input, checks it is not in the upload tags list,
// creates a tag from it and adds it to the upload tags list
function addTagFromUploadBoxTagsInput () {
    var tagsInput = $('.upload-file-modal .tags-input.typeahead.tt-input');
    if (tagsInput.val() != null && tagsInput.val() != "" && app.uploadTags.findWhere({name: tagsInput.val().toLowerCase()}) == null) {
        app.uploadTags.add({name: tagsInput.val().toLowerCase()});
        tagsInput.val('');
    }
    tagsInput.focus();
}

// Applies a search filter by tags
function applySearchFilter () {
    var tags = [];
    app.searchTags.each(function(tag) {
        tags.push(tag.get("name"));
    });
    app.items.fetch({data: {tags: tags.join(",")}, reset: true});
}

$(document).ready(function(){
    resetUploadModal();

    // Source for typeaheads
    var tags = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: jsRoutes.controllers.Tags.findByName("TAG_NAME").url,
            wildcard: 'TAG_NAME'
        }
    });

    // Initialize typeaheads
    $('.typeahead').typeahead({
        highlight: true
    },
    {
        name: 'tags',
        display: 'name',
        source: tags
    });

    // When a tag is selected from the dropdown of the upload form's tags input, add it to the list
    $('.upload-file-modal .tags-input').bind('typeahead:select', addTagFromUploadBoxTagsInput);

    // When a tag is selected from the dropdown of the search box's tags input, add it to the list
    $('.search-box .tags-input').bind('typeahead:select', addTagFromSearchBoxTagsInput);

    // When the add-tag-button next to the upload form's tags input is clicked, add a new tag to the list
    $('.upload-file-modal .add-tag-btn').click(addTagFromUploadBoxTagsInput);

    // Reset the upload modal when it hides
    $('.upload-file-modal').on('hidden.bs.modal', resetUploadModal);

    $('.upload-form').submit(function( event ) {
        // Prevent the upload form from being sent without tags
        event.preventDefault();

        // Get the data from the upload form
        var formData = new FormData($('.upload-form')[0]);

        // Add tags
        app.uploadTags.each(function(tag) {
            formData.append('tags', tag.get("name"));
        });

        // Send upload form data trough Ajax (http://stackoverflow.com/questions/166221/how-can-i-upload-files-asynchronously)
        $.ajax({
            url: jsRoutes.controllers.Storage.upload().url,  //Server script to process data
            type: 'POST',

            // Custom XMLHttpRequest
            xhr: function() {
                var myXhr = $.ajaxSettings.xhr();
                // Check if upload property exists
                if(myXhr.upload){
                    // For handling the progress of the upload
                    myXhr.upload.addEventListener('progress',progressHandler, false);
                    $('.upload-progress').show();
                }
                return myXhr;
            },

            //Ajax events
            success: completeHandler,
            error: errorHandler,

            // Form data
            data: formData,

            //Options to tell jQuery not to process data or worry about content-type.
            cache: false,
            contentType: false,
            processData: false
        });

        // Hide the cancel button of the upload modal
        $('.upload-file-modal .cancel-btn').hide();

        // Change upload button's state to "uploading"
        $('.upload-file-modal .upload-btn').button('uploading');

        // Handle upload progress events
        function progressHandler(e){
            if(e.lengthComputable){
                updateUploadProgressBarValue (e.loaded / e.total * 100);
            }
        }

        // Handle upload complete event
        function completeHandler(){
            var searchTags = app.searchTags.pluck("name");
            var uploadTags = app.uploadTags.pluck("name");

            if (_.every(searchTags, function (value) { return _.contains(uploadTags, value) })) {
                applySearchFilter();
            }

            $('.upload-file-modal').modal('hide');
            resetUploadModal();
        }

        // Handle upload error event
        function errorHandler(data){
            $('.upload-file-modal .alert').remove();
            var template = _.template($('#alert-template').html());
            $('.upload-file-modal .modal-body').prepend(template({type: "danger", message: messages['uploadError']}));
            resetUploadModal();
        }
    });
});