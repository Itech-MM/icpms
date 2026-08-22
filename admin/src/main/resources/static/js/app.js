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
        $.alert({
            title: 'Success',
            type: 'green',
            icon: 'bi bi-check-circle-fill',
            content: flashSuccessMsg,
            buttons: {
                ok: { text: 'OK', btnClass: 'btn-success' }
            }
        });
    }

    if (typeof flashErrorMsg !== 'undefined' && flashErrorMsg) {
        $.alert({
            title: 'Error',
            type: 'red',
            icon: 'bi bi-exclamation-triangle-fill',
            content: flashErrorMsg,
            buttons: {
                ok: { text: 'OK', btnClass: 'btn-danger' }
            }
        });
    }
});