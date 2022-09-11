// Page Index
let index = 1;

// Mobile Size
const MEDIUM = 768;

$(document).ready(function () {
    // Thymeleaf Variables
    const numOfPages = parseInt($('#numOfPages').val());
    const idPrefix = $('#idPrefix').val();

    // Auto Height Resizing
    const height = 100 * numOfPages;
    let minHeight = $('.p-container').css('min-height');
    minHeight = parseInt(minHeight.substring(0, minHeight.length - 2));

    $('.p-container').css('height', height + '%').css('min-height', (minHeight * numOfPages) + 'px');

    // First Page Resizing
    function updateHeaderResizing() {
        const firstPage = $('#' + idPrefix + '-1');
        const header = $('.p-header');
        const category = $('.pm-category');

        if (!(typeof header == 'undefined' || header == null) && !(typeof category == 'undefined' || category == null)) {
            firstPage
                .css('height', 'calc(' + (100 / numOfPages) + '% - ' + (header.outerHeight(true) + category.outerHeight(true)) + 'px)')
                .css('min-height', 'calc(' + minHeight + 'px - ' + (header.outerHeight(true) + category.outerHeight(true)) + 'px)');
        }
    }

    let previousWidth = $(window).width();
    let timer = null;
    updateHeaderResizing();

    // Category
    function selectCategory(id) {
        const btn = $('.pm-category .button-' + idPrefix + '-' + id);

        btn.addClass('selected');
        btn.removeClass('unselected');

        for (let j = 1; j <= numOfPages; j++) {
            if (j == id)
                continue;

            const btn_ = $('.pm-category .button-' + idPrefix + '-' + j);

            btn_.addClass('unselected');
            btn_.removeClass('selected');
        }

        btn.focus();

        if (id != 1)
            $('html').animate({scrollTop: $('#' + idPrefix + '-' + id).offset().top}, 200);
        else
            $('html').animate({scrollTop: 0}, 200);

        index = id;
    }

    for (let i = 1; i <= numOfPages; i++) {
        const btn = $('.pm-category .button-' + idPrefix + '-' + i);

        // first button selection
        btn.addClass(i == 1 ? 'selected' : 'unselected');

        // button names injection
        const name = $('#' + idPrefix + '-' + i + ' .pm-name');
        if (!(typeof name == 'undefined' || name == null)) {
            btn.text(name.text());
        }

        // button events
        btn.on('click', function () {
            selectCategory(i);
        });
    }

    // Prevent Wheel
    window.addEventListener('wheel', function (event) {
        event.preventDefault();
    }, {passive: false});

    // Init Scroll
    $('html').animate({scrollTop: 0}, 200);

    // Scroll Event
    $(window).on('wheel', function (event) {
        if ($('html').is(':animated'))
            return;
        let delta = event.originalEvent.deltaY;

        if (delta < 0 && index > 1)
            index--;
        else if (delta > 0 && index < numOfPages)
            index++;

        selectCategory(index);
    });

    // MD-size Resizing Event
    $(window).on('resize', function () {
        clearTimeout(timer);
        timer = setTimeout(function () {
            let curWidth = $(window).width();
            if ((curWidth < MEDIUM && previousWidth > MEDIUM)
                || (curWidth > MEDIUM && previousWidth < MEDIUM)) {
                updateHeaderResizing();
            }

            previousWidth = curWidth;
        }, 100);
    });
});