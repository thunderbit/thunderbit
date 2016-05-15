// Prepares upload modal
function resetUploadModal () {
    $('.upload-file-modal .upload-btn').button('reset');
    $('.upload-file-modal .cancel-btn').show();
    $('.upload-progress').hide();
    updateUploadProgressBarValue(0);
    app.uploadTags.reset();
}

// Updates the progress value of the upload progress bar
function updateUploadProgressBarValue (percent) {
    $('.upload-progress .progress-bar').css('width', percent + '%');
    $('.upload-progress .progress-bar .sr-only').html(percent + "% " + messages['completed']);
}

function addTagFromSearchBoxTagsInput () {
    var tagsInput = $('.search-box .tags-input.typeahead.tt-input');
    if (tagsInput.val() != null && tagsInput.val() != "" && app.searchTags.findWhere({name: tagsInput.val()}) == null) {
        app.searchTags.add({name: tagsInput.val()});
        tagsInput.val('');
    }
    tagsInput.focus();
}

function addTagFromUploadBoxTagsInput () {
    var tagsInput = $('.upload-file-modal .tags-input.typeahead.tt-input');
    if (tagsInput.val() != null && tagsInput.val() != "" && app.uploadTags.findWhere({name: tagsInput.val()}) == null) {
        app.uploadTags.add({name: tagsInput.val()});
        tagsInput.val('');
    }
    tagsInput.focus();
}

function applySearchFilter () {
    var tags = [];
    app.searchTags.each(function(tag) {
        tags.push(tag.get("name"));
    });
    app.items.fetch({data: {tags: tags.join(",")}, reset: true});
}

$(document).ready(function(){
    resetUploadModal();

    var tags = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: jsRoutes.controllers.Tags.findByName("TAG_NAME").url,
            wildcard: 'TAG_NAME'
        }
    });

    $('.typeahead').typeahead({
        highlight: true
    },
    {
        name: 'tags',
        display: 'name',
        source: tags
    });

    $('.upload-file-modal .tags-input').bind('typeahead:select', function(ev, suggestion) {
        addTagFromUploadBoxTagsInput ();
    });

    $('.search-box .tags-input').bind('typeahead:select', addTagFromSearchBoxTagsInput);

    $('.upload-file-modal .add-tag-btn').click(function(){
        addTagFromUploadBoxTagsInput ();
    });

    $('.upload-file-modal').on('hidden.bs.modal', function (e) {
        resetUploadModal();
    });

    $('.upload-form').submit(function( event ) {
        event.preventDefault();

        // Get the data from the upload form
        var formData = new FormData($('.upload-form')[0]);
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

        $('.upload-file-modal .cancel-btn').hide();
        $('.upload-file-modal .upload-btn').button('uploading');

        function progressHandler(e){
            if(e.lengthComputable){
                updateUploadProgressBarValue (e.loaded / e.total * 100);
            }
        }

        function completeHandler(){
            var searchTags = app.searchTags.pluck("name");
            var uploadTags = app.uploadTags.pluck("name");

            if (_.every(searchTags, function (value) { return _.contains(uploadTags, value) })) {
                applySearchFilter();
            }

            $('.upload-file-modal').modal('hide');
            resetUploadModal();
        }

        function errorHandler(data){
            $('.upload-file-modal .alert').remove();
            var template = _.template($('#alert-template').html());
            $('.upload-file-modal .modal-body').prepend(template({type: "danger", message: messages['uploadError']}));
            resetUploadModal();
        }
    });
});