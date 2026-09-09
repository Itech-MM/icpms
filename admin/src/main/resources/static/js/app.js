$(function () {
    $('#sidebarToggle').on('click', function () {
        $('#sidebar').addClass('show');
        $('#sidebarBackdrop').addClass('show');
    });

    $('#sidebarClose, #sidebarBackdrop').on('click', function () {
        $('#sidebar').removeClass('show');
        $('#sidebarBackdrop').removeClass('show');
    });

    $(document).on('click', '[data-confirm]', function (e) {
        e.preventDefault();
        var $el = $(this);
        var message = $el.data('confirm') || 'Are you sure?';

        $.confirm({
            title: 'Confirm',
            content: message,
            type: 'red',
            theme: 'material',
            buttons: {
                confirm: {
                    text: 'Yes',
                    btnClass: 'btn-danger',
                    action: function () {
                        if ($el.is('form')) {
                            $el.off('submit').trigger('submit');
                        } else if ($el.is(':submit') || $el.is('button[type="submit"]')) {
                            $el.closest('form').off('submit').trigger('submit');
                        } else if ($el.is('a')) {
                            window.location.href = $el.attr('href');
                        } else {
                            var $form = $el.closest('form');
                            if ($form.length) {
                                $form.off('submit').trigger('submit');
                            }
                        }
                    }
                },
                cancel: {
                    text: 'No'
                }
            }
        });
    });

    if (typeof flashSuccessMsg !== 'undefined' && flashSuccessMsg) {
        showSuccess(flashSuccessMsg);
    }

    if (typeof flashErrorMsg !== 'undefined' && flashErrorMsg) {
        showError(flashErrorMsg);
    }
});

var TOAST_ICONS = {
    green: 'bi bi-check-circle-fill',
    red: 'bi bi-exclamation-triangle-fill',
    orange: 'bi bi-exclamation-circle-fill',
    blue: 'bi bi-info-circle-fill'
};

var openToasts = [];
var TOAST_GAP = 12;

function positionToasts() {
    var offset = 20;
    openToasts.forEach(function (inst) {
        var $box = inst.$el.find('.jconfirm-box-container');
        $box.css('margin-top', offset + 'px');
        offset += $box.outerHeight() + TOAST_GAP;
    });
}

function showToast(message, type, options) {
    type = type || 'blue';
    var icon = TOAST_ICONS[type] || TOAST_ICONS.blue;
    var content = '<div class="app-toast-body">' +
                        '<i class="' + icon + ' app-toast-icon"></i>' +
                        '<span class="app-toast-message">' + message + '</span>' +
                  '</div>';

    return $.alert($.extend({
        title: false,
        content: content,
        type: type,
        typeAnimated: false,
        theme: 'toast',
        useBootstrap: false,
        draggable: false,
        animation: 'right',
        closeAnimation: 'right',
        animationSpeed: 300,
        bgOpacity: 0,
        backgroundDismiss: false,
        container: 'body',
        containerFluid: false,
        boxWidth: '340px',
        buttons: false,
        closeIcon: true,
        autoClose: 'close|3000',
        onOpenBefore: function () {
            openToasts.push(this);
            positionToasts();
        },
        onClose: function () {
            var idx = openToasts.indexOf(this);
            if (idx > -1) openToasts.splice(idx, 1);
            setTimeout(positionToasts, 0);
        }
    }, options || {}));
}

function showSuccess(message, options) {
    return showToast(message, 'green', options);
}

function showError(message, options) {
    return showToast(message, 'red', options);
}

function showWarning(message, options) {
    return showToast(message, 'orange', options);
}

function showInfo(message, options) {
    return showToast(message, 'blue', options);
}

window.showToast = showToast;
window.showSuccess = showSuccess;
window.showError = showError;
window.showWarning = showWarning;
window.showInfo = showInfo;