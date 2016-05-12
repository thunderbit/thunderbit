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

function addTagToSearchBox () {
    var tagsInput = $('.search-box .tags-input');
    if (tagsInput.val() != null && tagsInput.val() != "" && app.searchTags.findWhere({name: tagsInput.val()}) == null) {
        app.searchTags.add({name: tagsInput.val()});
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

    $('.upload-file-modal .add-tag-btn').click(function(){
        var tagsInput = $('.upload-file-modal .tags-input');
        if (tagsInput.val() != null && tagsInput.val() != "" && app.uploadTags.findWhere({name: tagsInput.val()}) == null) {
            app.uploadTags.add({name: tagsInput.val()});
            tagsInput.val('');
        }
        tagsInput.focus();
    });

    $('.search-box .add-tag-btn').click(addTagToSearchBox);
    $('.search-box .tags-input').keyup(function(event) {
        if (event.which == 13) {
            addTagToSearchBox();
        }
    }).focus();

    $('.upload-file-modal').on('hidden.bs.modal', function (e) {
        resetUploadModal();
    });

    $('.upload-file-modal .upload-btn').click(function(){
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
        $('.upload-progress').show();
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