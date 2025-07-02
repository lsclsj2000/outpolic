(function ($) {
    ("use strict");
    // Page loading
    $(window).on("load", function () {
        $("#preloader-active").delay(450).fadeOut("slow");
        $("body").delay(450).css({
            overflow: "visible"
        });
        $("#onloadModal").modal("show");
    });
    /*-----------------
        Menu Stick
    -----------------*/
    var header = $(".sticky-bar");
    var win = $(window);
    win.on("scroll", function () {
        var scroll = win.scrollTop();
        if (scroll < 200) {
            header.removeClass("stick");
            $(".header-style-2 .categories-dropdown-active-large").removeClass("open");
            $(".header-style-2 .categories-button-active").removeClass("open");
        } else {
            header.addClass("stick");
        }
    });

    /*------ ScrollUp -------- */
    $.scrollUp({
        scrollText: '<i class="fi-rs-arrow-small-up"></i>',
        easingType: "linear",
        scrollSpeed: 900,
        animation: "fade"
    });
	
    /*------ Wow Active ----*/
    new WOW().init();

    //sidebar sticky
    if ($(".sticky-sidebar").length) {
        $(".sticky-sidebar").theiaStickySidebar();
    }

    // Slider Range JS
    if ($("#slider-range").length) {
        $(".noUi-handle").on("click", function () {
            $(this).width(50);
        });
        var rangeSlider = document.getElementById("slider-range");
        var moneyFormat = wNumb({
            decimals: 0,
            thousand: ",",
            prefix: "$"
        });
        noUiSlider.create(rangeSlider, {
            start: [500, 1000],
            step: 1,
            range: {
                min: [0],
                max: [2000]
            },
            format: moneyFormat,
            connect: true
        });

        // Set visual min and max values and also update value hidden form inputs
        rangeSlider.noUiSlider.on("update", function (values, handle) {
            document.getElementById("slider-range-value1").innerHTML = values[0];
            document.getElementById("slider-range-value2").innerHTML = values[1];
            document.getElementsByName("min-value").value = moneyFormat.from(values[0]);
            document.getElementsByName("max-value").value = moneyFormat.from(values[1]);
        });
    }

    /*------ Hero slider 1 ----*/
    $(".hero-slider-1").slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        fade: true,
        loop: true,
        dots: true,
        arrows: true,
        prevArrow: '<span class="slider-btn slider-prev"><i class="fi-rs-angle-left"></i></span>',
        nextArrow: '<span class="slider-btn slider-next"><i class="fi-rs-angle-right"></i></span>',
        appendArrows: ".hero-slider-1-arrow",
        autoplay: true
    });

    /*Carausel 8 columns*/
    $(".carausel-8-columns").each(function (key, item) {
        var id = $(this).attr("id");
        var sliderID = "#" + id;
        var appendArrowsClassName = "#" + id + "-arrows";

        $(sliderID).slick({
            dots: false,
            infinite: true,
            speed: 1000,
            arrows: true,
            autoplay: true,
            slidesToShow: 8,
            slidesToScroll: 1,
            loop: true,
            adaptiveHeight: true,
            responsive: [
                {
                    breakpoint: 1025,
                    settings: {
                        slidesToShow: 4,
                        slidesToScroll: 1
                    }
                },
                {
                    breakpoint: 768,
                    settings: {
                        slidesToShow: 3,
                        slidesToScroll: 1
                    }
                },
                {
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 2,
                        slidesToScroll: 1
                    }
                }
            ],
            prevArrow: '<span class="slider-btn slider-prev"><i class="fi-rs-arrow-small-left"></i></span>',
            nextArrow: '<span class="slider-btn slider-next"><i class="fi-rs-arrow-small-right"></i></span>',
            appendArrows: appendArrowsClassName
        });
    });

    /*Carausel 10 columns*/
    $(".carausel-10-columns").each(function (key, item) {
        var id = $(this).attr("id");
        var sliderID = "#" + id;
        var appendArrowsClassName = "#" + id + "-arrows";

        $(sliderID).slick({
            dots: false,
            infinite: true,
            speed: 1000,
            arrows: true,
            autoplay: false,
            slidesToShow: 10,
            slidesToScroll: 1,
            loop: true,
            adaptiveHeight: true,
            responsive: [
                {
                    breakpoint: 1025,
                    settings: {
                        slidesToShow: 4,
                        slidesToScroll: 1
                    }
                },
                {
                    breakpoint: 768,
                    settings: {
                        slidesToShow: 3,
                        slidesToScroll: 1
                    }
                },
                {
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 2,
                        slidesToScroll: 1
                    }
                }
            ],
            prevArrow: '<span class="slider-btn slider-prev"><i class="fi-rs-arrow-small-left"></i></span>',
            nextArrow: '<span class="slider-btn slider-next"><i class="fi-rs-arrow-small-right"></i></span>',
            appendArrows: appendArrowsClassName
        });
    });

    /*Carausel 4 columns*/
    $(".carausel-4-columns").each(function (key, item) {
        var id = $(this).attr("id");
        var sliderID = "#" + id;
        var appendArrowsClassName = "#" + id + "-arrows";

        $(sliderID).slick({
            dots: false,
            infinite: true,
            speed: 1000,
            arrows: true,
            autoplay: true,
            slidesToShow: 4,
            slidesToScroll: 1,
            loop: true,
            adaptiveHeight: true,
            responsive: [
                {
                    breakpoint: 1025,
                    settings: {
                        slidesToShow: 3,
                        slidesToScroll: 3
                    }
                },
                {
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 1,
                        slidesToScroll: 1
                    }
                }
            ],
            prevArrow: '<span class="slider-btn slider-prev"><i class="fi-rs-arrow-small-left"></i></span>',
            nextArrow: '<span class="slider-btn slider-next"><i class="fi-rs-arrow-small-right"></i></span>',
            appendArrows: appendArrowsClassName
        });
    });
    /*Carausel 4 columns*/
    $(".carausel-3-columns").each(function (key, item) {
        var id = $(this).attr("id");
        var sliderID = "#" + id;
        var appendArrowsClassName = "#" + id + "-arrows";

        $(sliderID).slick({
            dots: false,
            infinite: true,
            speed: 1000,
            arrows: true,
            autoplay: true,
            slidesToShow: 3,
            slidesToScroll: 1,
            loop: true,
            adaptiveHeight: true,
            responsive: [
                {
                    breakpoint: 1025,
                    settings: {
                        slidesToShow: 3,
                        slidesToScroll: 3
                    }
                },
                {
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 1,
                        slidesToScroll: 1
                    }
                }
            ],
            prevArrow: '<span class="slider-btn slider-prev"><i class="fi-rs-arrow-small-left"></i></span>',
            nextArrow: '<span class="slider-btn slider-next"><i class="fi-rs-arrow-small-right"></i></span>',
            appendArrows: appendArrowsClassName
        });
    });

    /*Fix Bootstrap 5 tab & slick slider*/

    $('button[data-bs-toggle="tab"]').on("shown.bs.tab", function (e) {
        $(".carausel-4-columns").slick("setPosition");
    });

    /*------ Timer Countdown ----*/

    $("[data-countdown]").each(function () {
        var $this = $(this),
            finalDate = $(this).data("countdown");
        $this.countdown(finalDate, function (event) {
            $(this).html(event.strftime("" + '<span class="countdown-section"><span class="countdown-amount hover-up">%D</span><span class="countdown-period"> days </span></span>' + '<span class="countdown-section"><span class="countdown-amount hover-up">%H</span><span class="countdown-period"> hours </span></span>' + '<span class="countdown-section"><span class="countdown-amount hover-up">%M</span><span class="countdown-period"> mins </span></span>' + '<span class="countdown-section"><span class="countdown-amount hover-up">%S</span><span class="countdown-period"> sec </span></span>'));
        });
    });

    /*------ Product slider active 1 ----*/
    $(".product-slider-active-1").slick({
        slidesToShow: 5,
        slidesToScroll: 1,
        autoplay: true,
        fade: false,
        loop: true,
        dots: false,
        arrows: true,
        prevArrow: '<span class="pro-icon-1-prev"><i class="fi-rs-angle-small-left"></i></span>',
        nextArrow: '<span class="pro-icon-1-next"><i class="fi-rs-angle-small-right"></i></span>',
        responsive: [
            {
                breakpoint: 1199,
                settings: {
                    slidesToShow: 3
                }
            },
            {
                breakpoint: 991,
                settings: {
                    slidesToShow: 2
                }
            },
            {
                breakpoint: 767,
                settings: {
                    slidesToShow: 2
                }
            },
            {
                breakpoint: 575,
                settings: {
                    slidesToShow: 1
                }
            }
        ]
    });

    /*------ Testimonial active 1 ----*/
    $(".testimonial-active-1").slick({
        slidesToShow: 3,
        slidesToScroll: 1,
        fade: false,
        loop: true,
        dots: false,
        arrows: true,
        prevArrow: '<span class="pro-icon-1-prev"><i class="fi-rs-angle-small-left"></i></span>',
        nextArrow: '<span class="pro-icon-1-next"><i class="fi-rs-angle-small-right"></i></span>',
        responsive: [
            {
                breakpoint: 1199,
                settings: {
                    slidesToShow: 3
                }
            },
            {
                breakpoint: 991,
                settings: {
                    slidesToShow: 2
                }
            },
            {
                breakpoint: 767,
                settings: {
                    slidesToShow: 1
                }
            },
            {
                breakpoint: 575,
                settings: {
                    slidesToShow: 1
                }
            }
        ]
    });

    /*------ Testimonial active 3 ----*/
    $(".testimonial-active-3").slick({
        slidesToShow: 3,
        slidesToScroll: 1,
        fade: false,
        loop: true,
        dots: true,
        arrows: false,
        responsive: [
            {
                breakpoint: 1199,
                settings: {
                    slidesToShow: 3
                }
            },
            {
                breakpoint: 991,
                settings: {
                    slidesToShow: 2
                }
            },
            {
                breakpoint: 767,
                settings: {
                    slidesToShow: 1
                }
            },
            {
                breakpoint: 575,
                settings: {
                    slidesToShow: 1
                }
            }
        ]
    });

    /*------ Categories slider 1 ----*/
    $(".categories-slider-1").slick({
        slidesToShow: 6,
        slidesToScroll: 1,
        fade: false,
        loop: true,
        dots: false,
        arrows: false,
        responsive: [
            {
                breakpoint: 1199,
                settings: {
                    slidesToShow: 4
                }
            },
            {
                breakpoint: 991,
                settings: {
                    slidesToShow: 3
                }
            },
            {
                breakpoint: 767,
                settings: {
                    slidesToShow: 2
                }
            },
            {
                breakpoint: 575,
                settings: {
                    slidesToShow: 1
                }
            }
        ]
    });

    /*----------------------------
        Category toggle function
    ------------------------------*/
    var searchToggle = $(".categories-button-active");
    searchToggle.on("click", function (e) {
        e.preventDefault();
        if ($(this).hasClass("open")) {
            $(this).removeClass("open");
            $(this).siblings(".categories-dropdown-active-large").removeClass("open");
        } else {
            $(this).addClass("open");
            $(this).siblings(".categories-dropdown-active-large").addClass("open");
        }
    });

    /*-------------------------------
        Sort by active
    -----------------------------------*/
    if ($(".sort-by-product-area").length) {
        var $body = $("body"),
            $cartWrap = $(".sort-by-product-area"),
            $cartContent = $cartWrap.find(".sort-by-dropdown");
        $cartWrap.on("click", ".sort-by-product-wrap", function (e) {
            e.preventDefault();
            var $this = $(this);
            if (!$this.parent().hasClass("show")) {
                $this.siblings(".sort-by-dropdown").addClass("show").parent().addClass("show");
            } else {
                $this.siblings(".sort-by-dropdown").removeClass("show").parent().removeClass("show");
            }
        });
        /*Close When Click Outside*/
        $body.on("click", function (e) {
            var $target = e.target;
            if (!$($target).is(".sort-by-product-area") && !$($target).parents().is(".sort-by-product-area")) {
                $cartWrap.removeClass("show");
                $cartContent.removeClass("show");
            }
        });
    }

    /*-----------------------
        Shop filter active
    ------------------------- */
    $(".shop-filter-toogle").on("click", function (e) {
        e.preventDefault();
        $(".shop-product-fillter-header").slideToggle();
    });
    var shopFiltericon = $(".shop-filter-toogle");
    shopFiltericon.on("click", function () {
        $(".shop-filter-toogle").toggleClass("active");
    });

    /*-------------------------------------
        Product details big image slider
    ---------------------------------------*/
    $(".pro-dec-big-img-slider").slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        arrows: false,
        draggable: false,
        fade: false,
        asNavFor: ".product-dec-slider-small , .product-dec-slider-small-2"
    });

    /*---------------------------------------
        Product details small image slider
    -----------------------------------------*/
    $(".product-dec-slider-small").slick({
        slidesToShow: 4,
        slidesToScroll: 1,
        asNavFor: ".pro-dec-big-img-slider",
        dots: false,
        focusOnSelect: true,
        fade: false,
        arrows: false,
        responsive: [
            {
                breakpoint: 991,
                settings: {
                    slidesToShow: 3
                }
            },
            {
                breakpoint: 767,
                settings: {
                    slidesToShow: 4
                }
            },
            {
                breakpoint: 575,
                settings: {
                    slidesToShow: 2
                }
            }
        ]
    });

    /*-----------------------
        Magnific Popup
    ------------------------*/
    $(".img-popup").magnificPopup({
        type: "image",
        gallery: {
            enabled: true
        }
    });

    $('.btn-close').on('click', function(e) {
        $('.zoomContainer').remove();
    });

    $('#quickViewModal').on('show.bs.modal', function (e) {
        $(document).click(function (e) {
            var modalDialog = $('.modal-dialog');
            if (!modalDialog.is(e.target) && modalDialog.has(e.target).length === 0) {
                $('.zoomContainer').remove();
            }
        });
    });



    /*---------------------
        Select active
    --------------------- */
    $(".select-active").select2();

    /*--- Checkout toggle function ----*/
    $(".checkout-click1").on("click", function (e) {
        e.preventDefault();
        $(".checkout-login-info").slideToggle(900);
    });

    /*--- Checkout toggle function ----*/
    $(".checkout-click3").on("click", function (e) {
        e.preventDefault();
        $(".checkout-login-info3").slideToggle(1000);
    });

    /*-------------------------
        Create an account toggle
    --------------------------*/
    $(".checkout-toggle2").on("click", function () {
        $(".open-toggle2").slideToggle(1000);
    });

    $(".checkout-toggle").on("click", function () {
        $(".open-toggle").slideToggle(1000);
    });

    /*-------------------------------------
        Checkout paymentMethod function
    ---------------------------------------*/
    paymentMethodChanged();
    function paymentMethodChanged() {
        var $order_review = $(".payment-method");

        $order_review.on("click", 'input[name="payment_method"]', function () {
            var selectedClass = "payment-selected";
            var parent = $(this).parents(".sin-payment").first();
            parent.addClass(selectedClass).siblings().removeClass(selectedClass);
        });
    }

    /*---- CounterUp ----*/
    $(".count").counterUp({
        delay: 10,
        time: 2000
    });

    // Isotope active
    $(".grid").imagesLoaded(function () {
        // init Isotope
        var $grid = $(".grid").isotope({
            itemSelector: ".grid-item",
            percentPosition: true,
            layoutMode: "masonry",
            masonry: {
                // use outer width of grid-sizer for columnWidth
                columnWidth: ".grid-item"
            }
        });
    });

    /*====== SidebarSearch ======*/
    function sidebarSearch() {
        var searchTrigger = $(".search-active"),
            endTriggersearch = $(".search-close"),
            container = $(".main-search-active");

        searchTrigger.on("click", function (e) {
            e.preventDefault();
            container.addClass("search-visible");
        });

        endTriggersearch.on("click", function () {
            container.removeClass("search-visible");
        });
    }
    sidebarSearch();

    /*====== Sidebar menu Active ======*/
    function mobileHeaderActive() {
        var navbarTrigger = $(".burger-icon"),
            endTrigger = $(".mobile-menu-close"),
            container = $(".mobile-header-active"),
            wrapper4 = $("body");

        wrapper4.prepend('<div class="body-overlay-1"></div>');

        navbarTrigger.on("click", function (e) {
            e.preventDefault();
            container.addClass("sidebar-visible");
            wrapper4.addClass("mobile-menu-active");
        });

        endTrigger.on("click", function () {
            container.removeClass("sidebar-visible");
            wrapper4.removeClass("mobile-menu-active");
        });

        $(".body-overlay-1").on("click", function () {
            container.removeClass("sidebar-visible");
            wrapper4.removeClass("mobile-menu-active");
        });
    }
    mobileHeaderActive();

    /*---------------------
        Mobile menu active
    ------------------------ */
    var $offCanvasNav = $(".mobile-menu"),
        $offCanvasNavSubMenu = $offCanvasNav.find(".dropdown");

    /*Add Toggle Button With Off Canvas Sub Menu*/
    $offCanvasNavSubMenu.parent().prepend('<span class="menu-expand"><i class="fi-rs-angle-small-down"></i></span>');

    /*Close Off Canvas Sub Menu*/
    $offCanvasNavSubMenu.slideUp();

    /*Category Sub Menu Toggle*/
    $offCanvasNav.on("click", "li a, li .menu-expand", function (e) {
        var $this = $(this);
        if (
            $this
                .parent()
                .attr("class")
                .match(/\b(menu-item-has-children|has-children|has-sub-menu)\b/) &&
            ($this.attr("href") === "#" || $this.hasClass("menu-expand"))
        ) {
            e.preventDefault();
            if ($this.siblings("ul:visible").length) {
                $this.parent("li").removeClass("active");
                $this.siblings("ul").slideUp();
            } else {
                $this.parent("li").addClass("active");
                $this.closest("li").siblings("li").removeClass("active").find("li").removeClass("active");
                $this.closest("li").siblings("li").find("ul:visible").slideUp();
                $this.siblings("ul").slideDown();
            }
        }
    });

    /*--- language currency active ----*/
    $(".mobile-language-active").on("click", function (e) {
        e.preventDefault();
        $(".lang-dropdown-active").slideToggle(900);
    });

    /*--- categories-button-active-2 ----*/
    $(".categories-button-active-2").on("click", function (e) {
        e.preventDefault();
        $(".categori-dropdown-active-small").slideToggle(900);
    });

    /*--- Mobile demo active ----*/
    var demo = $(".tm-demo-options-wrapper");
    $(".view-demo-btn-active").on("click", function (e) {
        e.preventDefault();
        demo.toggleClass("demo-open");
    });

    /*-----More Menu Open----*/
    $(".more_slide_open").slideUp();
    $(".more_categories").on("click", function () {
        $(this).toggleClass("show");
        $(".more_slide_open").slideToggle();
    });

    /*-----Modal----*/

    $(".modal").on("shown.bs.modal", function (e) {
        $(".product-image-slider").slick("setPosition");
        $(".slider-nav-thumbnails").slick("setPosition");
        if ($(window).width() > 768) {
            $(".product-image-slider .slick-active img").elevateZoom({
                zoomType: "inner",
                cursor: "crosshair",
                zoomWindowFadeIn: 500,
                zoomWindowFadeOut: 750
            });
        }
    });

    /*--- VSticker ----*/
    $("#news-flash").vTicker({
        speed: 500,
        pause: 3000,
        animation: "fade",
        mousePause: false,
        showItems: 1
    });
})(jQuery);

	/*-- 외주 등록(outsourcing) --*/
	document.addEventListener('DOMContentLoaded', function () {
	               const form = document.getElementById('outsourcingRegistrationForm');
	               const referenceFilesInput = document.getElementById('reference_files');
	               const filePreviewContainer = document.getElementById('file-preview-container');
	               const MAX_FILE_COUNT = 5;

	               const categorySelectionContainer = document.getElementById('categorySelectionContainer');
	               const addCategoryBtn = document.getElementById('addCategoryBtn');
	               const MAX_CATEGORIES = 3; // Maximum 3 categories can be selected

	               const availableCategoryOptions = [
	                   { value: 'design', text: '디자인' },
	                   { value: 'it', text: 'IT·프로그래밍' },
	                   { value: 'video-audio', text: '영상·사진·음향' },
	                   { value: 'marketing', text: '마케팅' },
	                   { value: 'translation', text: '번역·통역' },
	                   { value: 'writing', text: '기획·글쓰기' },
	                   { value: 'etc', text: '기타' }
	               ];
	               
	               let selectedCategories = new Set(); // Tracks currently selected category values

	               // Function to render all category dropdowns
	               function renderCategoryDropdowns() {
	                   categorySelectionContainer.innerHTML = ''; // Clear existing dropdowns

	                   // Add existing selected categories first
	                   selectedCategories.forEach(categoryValue => {
	                       addCategoryDropdown(categoryValue);
	                   });

	                   // If fewer than max categories and no empty dropdowns, add one more empty
	                   if (selectedCategories.size < MAX_CATEGORIES && !categorySelectionContainer.querySelector('select[value=""]')) {
	                        addCategoryDropdown(''); // Add an empty one for new selection
	                   }
	                   
	                   // Update add button state
	                   addCategoryBtn.disabled = selectedCategories.size >= MAX_CATEGORIES && !categorySelectionContainer.querySelector('select[value=""]');
	                   addCategoryBtn.style.opacity = addCategoryBtn.disabled ? '0.6' : '1';
	               }

	               // Function to add a single category dropdown
	               function addCategoryDropdown(selectedValue = '') {
	                   const categoryItemDiv = document.createElement('div');
	                   categoryItemDiv.classList.add('category-item');

	                   const selectElement = document.createElement('select');
	                   selectElement.classList.add('form-control', 'out_category_select'); // Add a class for easier selection later
	                   selectElement.name = 'out_category'; // Use array-like name if needed for backend, or handle in JS
	                   selectElement.required = true;

	                   const defaultOption = document.createElement('option');
	                   defaultOption.value = '';
	                   defaultOption.textContent = '카테고리를 선택하세요';
	                   defaultOption.disabled = true;
	                   defaultOption.selected = true;
	                   selectElement.appendChild(defaultOption);

	                   availableCategoryOptions.forEach(option => {
	                       const opt = document.createElement('option');
	                       opt.value = option.value;
	                       opt.textContent = option.text;
	                       // Disable if already selected in another dropdown, unless it's the current selected value
	                       if (selectedCategories.has(option.value) && option.value !== selectedValue) {
	                           opt.disabled = true;
	                       }
	                       selectElement.appendChild(opt);
	                   });

	                   if (selectedValue) {
	                       selectElement.value = selectedValue;
	                       defaultOption.selected = false; // Deselect default if a value is set
	                   }

	                   selectElement.addEventListener('change', function() {
	                       // Remove old value from set if it existed
	                       if (this.dataset.oldValue && selectedCategories.has(this.dataset.oldValue)) {
	                           selectedCategories.delete(this.dataset.oldValue);
	                       }
	                       // Add new value to set
	                       if (this.value) {
	                           selectedCategories.add(this.value);
	                           this.dataset.oldValue = this.value; // Store current value as old value
	                       }
	                       renderCategoryDropdowns(); // Re-render all to update disabled options
	                   });
	                   
	                   categoryItemDiv.appendChild(selectElement);

	                   // Add remove button if not the initial empty one or if there are multiple
	                   const removeBtn = document.createElement('button');
	                   removeBtn.type = 'button';
	                   removeBtn.classList.add('btn-remove-category');
	                   removeBtn.innerHTML = '<i class="fas fa-minus"></i>'; // Changed icon to minus
	                   removeBtn.addEventListener('click', function() {
	                       if (selectElement.value) { // Remove the value from the set if it was selected
	                           selectedCategories.delete(selectElement.value);
	                       }
	                       categoryItemDiv.remove(); // Remove the div
	                       renderCategoryDropdowns(); // Re-render to update options
	                   });
	                   categoryItemDiv.appendChild(removeBtn);
	                   

	                   categorySelectionContainer.appendChild(categoryItemDiv);
	               }

	               // Initial render of categories when page loads
	               renderCategoryDropdowns();


	               // Add Category Button Event Listener
	               addCategoryBtn.addEventListener('click', function() {
	                   if (selectedCategories.size < MAX_CATEGORIES) {
	                       addCategoryDropdown();
	                   } else {
	                       alert(`카테고리는 최대 ${MAX_CATEGORIES}개까지 선택할 수 있습니다.`);
	                   }
	               });


	               // Handle form submission
	               if (form) {
	                   form.addEventListener('submit', function(e) {
	                       e.preventDefault(); // Prevent default form submission

	                       const outTitle = document.getElementById('out_ttl').value.trim();
	                       // Get all selected categories from the DOM
	                       const finalSelectedCategories = Array.from(categorySelectionContainer.querySelectorAll('.out_category_select'))
	                                                               .map(select => select.value)
	                                                               .filter(value => value !== ''); // Filter out empty selections

	                       const outContent = document.getElementById('out_cn').value.trim();
	                       const outTags = document.getElementById('out_tags').value.trim();
	                       const workStartDate = document.getElementById('work-start-date').value;
	                       const workEndDate = document.getElementById('work-end-date').value;
	                       const outHopeAmount = document.getElementById('out_hope_amnt').value;
	                       const requiredPersonnel = document.getElementById('required_personnel').value;
	                       const files = referenceFilesInput.files;

	                       // Basic validation
	                       if (!outTitle) {
	                           alert('외주 프로젝트 제목을 입력해주세요.');
	                           document.getElementById('out_ttl').focus();
	                           return;
	                       }
	                       if (finalSelectedCategories.length === 0) {
	                           alert('카테고리를 최소 하나 이상 선택해주세요.');
	                           const firstEmptyCategorySelect = categorySelectionContainer.querySelector('select[value=""]');
	                           if(firstEmptyCategorySelect) firstEmptyCategorySelect.focus();
	                           return;
	                       }
	                       if (!outContent) {
	                           alert('외주 프로젝트 내용을 입력해주세요.');
	                           document.getElementById('out_cn').focus();
	                           return;
	                       }
	                       if (!outHopeAmount || parseInt(outHopeAmount) < 100000) {
	                           alert('희망 금액을 10만원 이상으로 입력해주세요.');
	                           document.getElementById('out_hope_amnt').focus();
	                           return;
	                       }
	                       if (!requiredPersonnel || parseInt(requiredPersonnel) < 1) {
	                           alert('필요 수행 인원을 1명 이상으로 입력해주세요.');
	                           document.getElementById('required_personnel').focus();
	                           return;
	                       }


	                       // Parse tags (comma-separated)
	                       const parsedTags = outTags.split(',').map(tag => tag.trim()).filter(tag => tag !== '');

	                       // Collect form data (for demonstration)
	                       const formData = {
	                           out_ttl: outTitle,
	                           out_category: finalSelectedCategories, // Array of categories
	                           out_cn: outContent,
	                           out_tags: parsedTags, // Array of tags
	                           work_start_date: workStartDate,
	                           work_end_date: workEndDate,
	                           out_hope_amnt: outHopeAmount,
	                           required_personnel: requiredPersonnel,
	                           // In a real application, you would handle file uploads to a backend API
	                           reference_files_count: files.length
	                       };

	                       console.log('외주 등록 데이터:', formData);
	                       alert('외주 프로젝트가 성공적으로 등록되었습니다! (실제 저장 로직은 백엔드 구현 필요)');
	                       form.reset(); // Clear form after submission
	                       filePreviewContainer.innerHTML = ''; // Clear file previews
	                       selectedCategories.clear(); // Clear selected categories set
	                       renderCategoryDropdowns(); // Re-render category dropdowns to initial state
	                       // Optionally redirect to outsourcing list page
	                       // window.location.href = '/my-outsourcing-list'; 
	                   });
	               }

	               // File preview logic
	               if (referenceFilesInput && filePreviewContainer) {
	                   referenceFilesInput.addEventListener('change', function(event) {
	                       filePreviewContainer.innerHTML = ''; // Clear previous previews
	                       const files = event.target.files;
	                       
	                       if (files.length > MAX_FILE_COUNT) {
	                           alert(`파일은 최대 ${MAX_FILE_COUNT}개까지 첨부할 수 있습니다.`);
	                           this.value = ''; // Clear selected files
	                           return;
	                       }

	                       for (let i = 0; i < files.length; i++) {
	                           const file = files[i];
	                           const fileType = file.type;
	                           const fileName = file.name;

	                           const fileDiv = document.createElement('div');
	                           fileDiv.classList.add('relative', 'w-24', 'h-24', 'overflow-hidden', 'rounded-lg', 'shadow-sm', 'group', 'flex', 'items-center', 'justify-center', 'text-gray-500', 'bg-gray-100', 'text-center', 'break-all', 'p-1'); // Added classes for non-image files

	                           let fileContent = '';
	                           if (fileType.startsWith('image/')) {
	                               const reader = new FileReader();
	                               reader.onload = (e) => {
	                                   fileDiv.innerHTML = `
	                                       <img src="${e.target.result}" alt="Reference File" class="w-full h-full object-cover">
	                                       <button type="button" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 text-white opacity-0 group-hover:opacity-100 transition-opacity remove-image-btn" data-file-index="${i}">
	                                           <i class="fas fa-times-circle"></i>
	                                       </button>
	                                   `;
	                               };
	                               reader.readAsDataURL(file);
	                           } else {
	                               // For non-image files, display an icon and filename
	                               let iconClass = 'fas fa-file'; // Default icon
	                               if (fileType.includes('pdf')) iconClass = 'fas fa-file-pdf';
	                               else if (fileType.includes('word')) iconClass = 'fas fa-file-word';
	                               else if (fileType.includes('powerpoint')) iconClass = 'fas fa-file-powerpoint';
	                               else if (fileType.includes('text')) iconClass = 'fas fa-file-alt';

	                               fileDiv.innerHTML = `
	                                   <div class="flex flex-col items-center justify-center p-1">
	                                       <i class="${iconClass} text-3xl mb-1 text-gray-400"></i>
	                                       <span class="text-xs font-medium">${fileName}</span>
	                                   </div>
	                                   <button type="button" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 text-white opacity-0 group-hover:opacity-100 transition-opacity remove-image-btn" data-file-index="${i}">
	                                       <i class="fas fa-times-circle"></i>
	                                   </button>
	                               `;
	                           }
	                           filePreviewContainer.appendChild(fileDiv);

	                           fileDiv.querySelector('.remove-image-btn').addEventListener('click', function() {
	                               fileDiv.remove();
	                               // In a real application, you'd manage the file list in the input element or a separate array.
	                           });
	                       }
	                   });
	               }
	           });
			   
			/*-- 외주 신청(outsourcingContract) --*/			   
			document.addEventListener('DOMContentLoaded', function () {
			               const form = document.getElementById('outsourcingApplicationForm');
			               const referenceFilesInput = document.getElementById('application_reference_files');
			               const filePreviewContainer = document.getElementById('application-file-preview-container');
			               const MAX_FILE_COUNT = 5;

			               // Handle form submission
			               if (form) {
			                   form.addEventListener('submit', function(e) {
			                       e.preventDefault(); // Prevent default form submission

			                       const outsourcingId = document.getElementById('outsourcing_id').value;
			                       const proposalDescription = document.getElementById('proposal_description').value.trim();
			                       const desiredWorkStartDate = document.getElementById('desired_work_start_date').value;
			                       const desiredWorkEndDate = document.getElementById('desired_work_end_date').value;
			                       const proposedAmount = document.getElementById('proposed_amount').value;
			                       const files = referenceFilesInput.files;

			                       // Basic validation
			                       if (!proposalDescription) {
			                           alert('제안 상세 설명을 입력해주세요.');
			                           document.getElementById('proposal_description').focus();
			                           return;
			                       }
			                       if (!proposedAmount || parseFloat(proposedAmount) <= 0) {
			                           alert('제안 금액을 올바르게 입력해주세요.');
			                           document.getElementById('proposed_amount').focus();
			                           return;
			                       }

			                       // Collect form data (for demonstration)
			                       const formData = {
			                           outsourcing_id: outsourcingId,
			                           proposal_description: proposalDescription,
			                           desired_work_start_date: desiredWorkStartDate,
			                           desired_work_end_date: desiredWorkEndDate,
			                           proposed_amount: parseFloat(proposedAmount),
			                           reference_files_count: files.length
			                       };

			                       console.log('외주 프로젝트 신청 데이터:', formData);
			                       alert('외주 프로젝트 신청이 성공적으로 접수되었습니다! (실제 저장 로직은 백엔드 구현 필요)');
			                       form.reset(); // Clear form after submission
			                       filePreviewContainer.innerHTML = ''; // Clear file previews
			                       // Optionally redirect or show success message
			                   });
			               }

			               // File preview logic
			               if (referenceFilesInput && filePreviewContainer) {
			                   referenceFilesInput.addEventListener('change', function(event) {
			                       filePreviewContainer.innerHTML = ''; // Clear previous previews
			                       const files = event.target.files;
			                       
			                       if (files.length > MAX_FILE_COUNT) {
			                           alert(`파일은 최대 ${MAX_FILE_COUNT}개까지 첨부할 수 있습니다.`);
			                           this.value = ''; // Clear selected files
			                           return;
			                       }

			                       for (let i = 0; i < files.length; i++) {
			                           const file = files[i];
			                           const fileType = file.type;
			                           const fileName = file.name;

			                           const fileDiv = document.createElement('div');
			                           fileDiv.classList.add('relative', 'w-24', 'h-24', 'overflow-hidden', 'rounded-lg', 'shadow-sm', 'group', 'flex', 'items-center', 'justify-center', 'text-gray-500', 'bg-gray-100', 'text-center', 'break-all', 'p-1');

			                           if (fileType.startsWith('image/')) {
			                               const reader = new FileReader();
			                               reader.onload = (e) => {
			                                   fileDiv.innerHTML = `
			                                       <img src="${e.target.result}" alt="Reference File" class="w-full h-full object-cover">
			                                       <button type="button" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 text-white opacity-0 group-hover:opacity-100 transition-opacity remove-image-btn" data-file-index="${i}">
			                                           <i class="fas fa-times-circle"></i>
			                                       </button>
			                                   `;
			                               };
			                               reader.readAsDataURL(file);
			                           } else {
			                               let iconClass = 'fas fa-file';
			                               if (fileType.includes('pdf')) iconClass = 'fas fa-file-pdf';
			                               else if (fileType.includes('word')) iconClass = 'fas fa-file-word';
			                               else if (fileType.includes('powerpoint')) iconClass = 'fas fa-file-powerpoint';
			                               else if (fileType.includes('text')) iconClass = 'fas fa-file-alt';

			                               fileDiv.innerHTML = `
			                                   <div class="flex flex-col items-center justify-center p-1">
			                                       <i class="${iconClass} text-3xl mb-1 text-gray-400"></i>
			                                       <span class="text-xs font-medium">${fileName}</span>
			                                   </div>
			                                   <button type="button" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 text-white opacity-0 group-hover:opacity-100 transition-opacity remove-image-btn" data-file-index="${i}">
			                                       <i class="fas fa-times-circle"></i>
			                                   </button>
			                               `;
			                           }
			                           filePreviewContainer.appendChild(fileDiv);

			                           fileDiv.querySelector('.remove-image-btn').addEventListener('click', function() {
			                               fileDiv.remove();
			                           });
			                       }
			                   });
			               }
			           });
					   
					   			   
		/*-- 포트폴리오 문의(portfolioContract) --*/	
		document.addEventListener('DOMContentLoaded', function () {
		               const form = document.getElementById('portfolioInquiryForm');
		               const referenceFilesInput = document.getElementById('inquiry_reference_files');
		               const filePreviewContainer = document.getElementById('inquiry-file-preview-container');
		               const MAX_FILE_COUNT = 5;

		               // Handle form submission
		               if (form) {
		                   form.addEventListener('submit', function(e) {
		                       e.preventDefault(); // Prevent default form submission

		                       const portfolioId = document.getElementById('portfolio_id').value;
		                       const inquiryDescription = document.getElementById('inquiry_description').value.trim();
		                       const desiredProjectStartDate = document.getElementById('desired_project_start_date').value;
		                       const desiredProjectEndDate = document.getElementById('desired_project_end_date').value;
		                       const desiredBudget = document.getElementById('desired_budget').value;
		                       const files = referenceFilesInput.files;

		                       // Basic validation
		                       if (!inquiryDescription) {
		                           alert('의뢰 상세 내용을 입력해주세요.');
		                           document.getElementById('inquiry_description').focus();
		                           return;
		                       }
		                       if (desiredBudget && parseFloat(desiredBudget) < 0) {
		                           alert('희망 예산을 올바르게 입력해주세요.');
		                           document.getElementById('desired_budget').focus();
		                           return;
		                       }

		                       // Collect form data (for demonstration)
		                       const formData = {
		                           portfolio_id: portfolioId,
		                           inquiry_description: inquiryDescription,
		                           desired_project_start_date: desiredProjectStartDate,
		                           desired_project_end_date: desiredProjectEndDate,
		                           desired_budget: desiredBudget ? parseFloat(desiredBudget) : null,
		                           reference_files_count: files.length
		                       };

		                       console.log('포트폴리오 문의 데이터:', formData);
		                       alert('포트폴리오 문의 (새 외주 의뢰)가 성공적으로 접수되었습니다! (실제 저장 로직은 백엔드 구현 필요)');
		                       form.reset(); // Clear form after submission
		                       filePreviewContainer.innerHTML = ''; // Clear file previews
		                       // Optionally redirect or show success message
		                   });
		               }

		               // File preview logic (reusable from outsourcing form)
		               if (referenceFilesInput && filePreviewContainer) {
		                   referenceFilesInput.addEventListener('change', function(event) {
		                       filePreviewContainer.innerHTML = ''; // Clear previous previews
		                       const files = event.target.files;
		                       
		                       if (files.length > MAX_FILE_COUNT) {
		                           alert(`파일은 최대 ${MAX_FILE_COUNT}개까지 첨부할 수 있습니다.`);
		                           this.value = ''; // Clear selected files
		                           return;
		                       }

		                       for (let i = 0; i < files.length; i++) {
		                           const file = files[i];
		                           const fileType = file.type;
		                           const fileName = file.name;

		                           const fileDiv = document.createElement('div');
		                           fileDiv.classList.add('relative', 'w-24', 'h-24', 'overflow-hidden', 'rounded-lg', 'shadow-sm', 'group', 'flex', 'items-center', 'justify-center', 'text-gray-500', 'bg-gray-100', 'text-center', 'break-all', 'p-1');

		                           if (fileType.startsWith('image/')) {
		                               const reader = new FileReader();
		                               reader.onload = (e) => {
		                                   fileDiv.innerHTML = `
		                                       <img src="${e.target.result}" alt="Reference File" class="w-full h-full object-cover">
		                                       <button type="button" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 text-white opacity-0 group-hover:opacity-100 transition-opacity remove-image-btn" data-file-index="${i}">
		                                           <i class="fas fa-times-circle"></i>
		                                       </button>
		                                   `;
		                               };
		                               reader.readAsDataURL(file);
		                           } else {
		                               let iconClass = 'fas fa-file';
		                               if (fileType.includes('pdf')) iconClass = 'fas fa-file-pdf';
		                               else if (fileType.includes('word')) iconClass = 'fas fa-file-word';
		                               else if (fileType.includes('powerpoint')) iconClass = 'fas fa-file-powerpoint';
		                               else if (fileType.includes('text')) iconClass = 'fas fa-file-alt';

		                               fileDiv.innerHTML = `
		                                   <div class="flex flex-col items-center justify-center p-1">
		                                       <i class="${iconClass} text-3xl mb-1 text-gray-400"></i>
		                                       <span class="text-xs font-medium">${fileName}</span>
		                                   </div>
		                                   <button type="button" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 text-white opacity:0 group-hover:opacity-100 transition-opacity remove-image-btn" data-file-index="${i}">
		                                       <i class="fas fa-times-circle"></i>
		                                   </button>
		                               `;
		                           }
		                           filePreviewContainer.appendChild(fileDiv);

		                           fileDiv.querySelector('.remove-image-btn').addEventListener('click', function() {
		                               fileDiv.remove();
		                           });
		                       }
		                   });
		               }
		           });
				   
				   /*-- 프로젝트 진행 현황 페이지(outsourcingStatus) --*/
				   document.addEventListener('DOMContentLoaded', function () {
				       // --- Modal Logic (Reusable) ---
				       function openModal(modalId) {
				           document.getElementById(modalId).classList.add('active');
				       }

				       function closeModal(modalId) {
				           document.getElementById(modalId).classList.remove('active');
				       }

				       // Attach event listeners to all modal close buttons
				       document.querySelectorAll('.modal-close-btn').forEach(button => {
				           button.addEventListener('click', function() {
				               const modalId = this.dataset.modalId;
				               closeModal(modalId);
				           });
				       });

				       // --- Report Write Modal ---
				       const writeReportBtn = document.getElementById('writeReportBtn');
				       const reportWriteModal = document.getElementById('reportWriteModal');
				       const submitReportBtn = document.getElementById('submitReportBtn');
				       const reportTitleInput = document.getElementById('report_title');
				       const reportContentInput = document.getElementById('report_content');
				       const reportListContainer = document.getElementById('reportList');

				       writeReportBtn.addEventListener('click', () => {
				           openModal('reportWriteModal');
				           reportTitleInput.value = ''; // Clear previous input
				           reportContentInput.value = '';
				       });

				       submitReportBtn.addEventListener('click', function() {
				           const title = reportTitleInput.value.trim();
				           const content = reportContentInput.value.trim();
				           
				           if (!title || !content) {
				               alert('제목과 내용을 모두 입력해주세요.');
				               return;
				           }

				           // For demonstration: Add to list (in real app, save to DB)
				           const now = new Date();
				           const dateString = `${now.getFullYear()}.${(now.getMonth() + 1).toString().padStart(2, '0')}.${now.getDate().toString().padStart(2, '0')}`;
				           const newReportId = `report-${Date.now()}`; // Unique ID

				           const newReportItem = document.createElement('div');
				           newReportItem.classList.add('list-item');
				           newReportItem.innerHTML = `
				               <span>${title} (${dateString})</span>
				               <button type="button" class="btn-detail view-report-btn" data-report-id="${newReportId}">자세히 보기</button>
				           `;
				           reportListContainer.prepend(newReportItem); // Add to top of list

				           // Store report data for viewing (in real app, retrieve from DB)
				           // This is a simple in-memory storage for demo purposes
				           if (!window.mockReports) window.mockReports = {};
				           window.mockReports[newReportId] = { title: title, content: content, date: dateString };

				           // Attach event listener to the new "자세히 보기" button
				           newReportItem.querySelector('.view-report-btn').addEventListener('click', function() {
				               viewReportDetails(this.dataset.reportId);
				           });

				           // Hide "no items" message if present
				           reportListContainer.querySelector('.no-items-message')?.classList.add('hidden');


				           alert('보고서가 성공적으로 제출되었습니다!');
				           closeModal('reportWriteModal');
				       });


				       // --- Feedback Write Modal ---
				       const writeFeedbackBtn = document.getElementById('writeFeedbackBtn');
				       const feedbackWriteModal = document.getElementById('feedbackWriteModal');
				       const submitFeedbackBtn = document.getElementById('submitFeedbackBtn');
				       const feedbackTitleInput = document.getElementById('feedback_title');
				       const feedbackContentInput = document.getElementById('feedback_content');
				       const feedbackListContainer = document.getElementById('feedbackList');

				       writeFeedbackBtn.addEventListener('click', () => {
				           openModal('feedbackWriteModal');
				           feedbackTitleInput.value = ''; // Clear previous input
				           feedbackContentInput.value = '';
				       });

				       submitFeedbackBtn.addEventListener('click', function() {
				           const title = feedbackTitleInput.value.trim();
				           const content = feedbackContentInput.value.trim();
				           
				           if (!title || !content) {
				               alert('제목과 내용을 모두 입력해주세요.');
				               return;
				           }

				           // For demonstration: Add to list (in real app, save to DB)
				           const now = new Date();
				           const dateString = `${now.getFullYear()}.${(now.getMonth() + 1).toString().padStart(2, '0')}.${now.getDate().toString().padStart(2, '0')}`;
				           const newFeedbackId = `feedback-${Date.now()}`; // Unique ID

				           const newFeedbackItem = document.createElement('div');
				           newFeedbackItem.classList.add('list-item');
				           newFeedbackItem.innerHTML = `
				               <span>${title} (${dateString})</span>
				               <button type="button" class="btn-detail view-feedback-btn" data-feedback-id="${newFeedbackId}">자세히 보기</button>
				           `;
				           feedbackListContainer.prepend(newFeedbackItem); // Add to top of list

				           // Store feedback data for viewing (in real app, retrieve from DB)
				           // This is a simple in-memory storage for demo purposes
				           if (!window.mockFeedback) window.mockFeedback = {};
				           window.mockFeedback[newFeedbackId] = { title: title, content: content, date: dateString };

				           // Attach event listener to the new "자세히 보기" button
				           newFeedbackItem.querySelector('.view-feedback-btn').addEventListener('click', function() {
				               viewFeedbackDetails(this.dataset.feedbackId);
				           });

				           // Hide "no items" message if present
				           feedbackListContainer.querySelector('.no-items-message')?.classList.add('hidden');

				           alert('피드백이 성공적으로 제출되었습니다!');
				           closeModal('feedbackWriteModal');
				       });


				       // --- Report View Modal ---
				       const reportViewModal = document.getElementById('reportViewModal');
				       const viewReportTitle = document.getElementById('viewReportTitle');
				       const viewReportDate = document.getElementById('viewReportDate');
				       const viewReportContent = document.getElementById('viewReportContent');

				       function viewReportDetails(reportId) {
				           // In a real application, fetch report data from DB using reportId
				           const report = window.mockReports[reportId]; // Using mock data for demo
				           if (report) {
				               viewReportTitle.textContent = report.title;
				               viewReportDate.textContent = `작성일: ${report.date}`;
				               viewReportContent.textContent = report.content;
				               openModal('reportViewModal');
				           } else {
				               alert('보고서 내용을 불러올 수 없습니다.');
				           }
				       }

				       // Attach click listeners to existing report "자세히 보기" buttons
				       document.querySelectorAll('.view-report-btn').forEach(button => {
				           button.addEventListener('click', function() {
				               viewReportDetails(this.dataset.reportId);
				           });
				       });

				       // Mock data for initial reports (in a real app, this comes from backend)
				       window.mockReports = {
				           'report1': { title: '1차 보고서 - 초기 기획 진행 상황', content: '프로젝트 초기 기획 및 요구사항 정의 단계가 80% 완료되었습니다. 주요 기능 목록 확정 및 사용자 플로우 정의를 마쳤습니다. 다음 주까지 세부 기능 명세를 마무리할 예정입니다.', date: '2025.06.20' },
				           'report2': { title: '2차 보고서 - UI/UX 설계 진행 상황', content: 'UI/UX 디자인 초안이 완료되었으며, 주요 화면의 와이어프레임과 프로토타입을 제작했습니다. 사용자 테스트를 진행하여 피드백을 수렴할 예정입니다.', date: '2025.06.25' }
				       };
				       if (Object.keys(window.mockReports).length === 0) {
				            reportListContainer.querySelector('.no-items-message')?.classList.remove('hidden');
				       } else {
				            reportListContainer.querySelector('.no-items-message')?.classList.add('hidden');
				       }

				       // --- Feedback View Modal ---
				       const feedbackViewModal = document.getElementById('feedbackViewModal');
				       const viewFeedbackTitle = document.getElementById('viewFeedbackTitle');
				       const viewFeedbackDate = document.getElementById('viewFeedbackDate');
				       const viewFeedbackContent = document.getElementById('viewFeedbackContent');

				       function viewFeedbackDetails(feedbackId) {
				           // In a real application, fetch feedback data from DB using feedbackId
				           const feedback = window.mockFeedback[feedbackId]; // Using mock data for demo
				           if (feedback) {
				               viewFeedbackTitle.textContent = feedback.title;
				               viewFeedbackDate.textContent = `작성일: ${feedback.date}`;
				               viewFeedbackContent.textContent = feedback.content;
				               openModal('feedbackViewModal');
				           } else {
				               alert('피드백 내용을 불러올 수 없습니다.');
				           }
				       }

				       // Attach click listeners to existing feedback "자세히 보기" buttons
				       document.querySelectorAll('.view-feedback-btn').forEach(button => {
				           button.addEventListener('click', function() {
				               viewFeedbackDetails(this.dataset.feedbackId);
				           });
				       });

				       // Mock data for initial feedback (in a real app, this comes from backend)
				       window.mockFeedback = {
				           'feedback1': { title: '피드백 #1 - 초기 기획 방향', content: '초기 기획안 잘 받아보았습니다. 전반적인 방향은 좋습니다만, 로그인 프로세스에서 사용성 개선이 필요해 보입니다. 예를 들어, 소셜 로그인 버튼의 시인성을 높이는 방안을 고려해주시면 좋겠습니다.', date: '2025.06.21' }
				       };
				       if (Object.keys(window.mockFeedback).length === 0) {
				           feedbackListContainer.querySelector('.no-items-message')?.classList.remove('hidden');
				       } else {
				           feedbackListContainer.querySelector('.no-items-message')?.classList.add('hidden');
				       }
				   });
		
		
		
		
		/* 포폴 등록 */

		document.addEventListener('DOMContentLoaded', function() {
		    console.log('main.js: DOMContentLoaded 이벤트 발생. 모든 HTML 요소가 로드되었습니다.');

		    const portfolioItemsGrid = document.getElementById('portfolioItemsGrid');
		    const noPortfolioMessage = document.getElementById('noPortfolioMessage');
		    const addPortfolioTopBtn = document.getElementById('addPortfolioTopBtn');
		    const addPortfolioBottomBtn = document.getElementById('addPortfolioBottomBtn');

		    /**
		     * 포트폴리오 목록을 서버로부터 동적으로 불러와 화면에 렌더링하는 함수
		     */
		    function loadAndRenderPortfolioList() {
		        console.log('main.js: 포트폴리오 목록 로드 시작...');
		        
		        // ★★★ 이 부분이 가장 중요합니다! (캐시 방지) ★★★
		        // URL 뒤에 현재 시간을 붙여 매번 새로운 주소로 요청하여 캐시된 데이터를 사용하지 않도록 합니다.
		        const urlWithCacheBuster = '/enter/portfolio/listData?_=' + new Date().getTime();

		        fetch(urlWithCacheBuster) // 수정된 URL 사용
		            .then(response => {
		                if (!response.ok) {
		                    throw new Error(`HTTP error! status: ${response.status}`);
		                }
		                return response.json();
		            })
		            .then(portfolios => {
		                console.log('main.js: 포트폴리오 목록 데이터 수신:', portfolios);
		                
		                portfolioItemsGrid.innerHTML = ''; 

		                if (portfolios && portfolios.length > 0) {
		                    noPortfolioMessage.classList.add('hidden');
		                    portfolioItemsGrid.classList.remove('hidden');

		                    portfolios.forEach(portfolio => {
		                        // DB에 저장된 데이터를 기반으로 HTML 카드 생성
		                        const cardHtml = `
		                            <div class="portfolio-item-card">
		                                <div class="portfolio-thumbnail-wrapper">
		                                    <img src="${portfolio.prtfThumbnailUrl || 'https://via.placeholder.com/300x200?text=No+Image'}" 
		                                         alt="포트폴리오 썸네일" class="portfolio-thumbnail">
		                                </div>
		                                <div class="portfolio-content">
		                                    <h3 class="portfolio-title">${portfolio.prtfTtl || '제목 없음'}</h3>
		                                    <p class="portfolio-description">${portfolio.prtfCn || '내용 없음'}</p>
		                                    <div class="portfolio-actions">
		                                        <button class="btn-edit" data-prtf-cd="${portfolio.prtfCd || ''}">수정</button>
		                                        <button class="btn-delete" data-prtf-cd="${portfolio.prtfCd || ''}">삭제</button>
		                                    </div>
		                                </div>
		                            </div>
		                        `;
		                        portfolioItemsGrid.insertAdjacentHTML('beforeend', cardHtml);
		                    });
		                } else {
		                    noPortfolioMessage.classList.remove('hidden');
		                    portfolioItemsGrid.classList.add('hidden');
		                }
		            })
		            .catch(error => {
		                console.error('main.js: 포트폴리오 목록 로드 실패:', error);
		                portfolioItemsGrid.innerHTML = '<p style="color: red;">포트폴리오 목록을 불러오는 데 실패했습니다.</p>';
		            });
		    }

		    /**
		     * "새 포트폴리오 등록하기" 버튼 클릭 시 등록 페이지로 이동하는 함수
		     */
		    function goToAddPage() {
		        window.location.href = '/enter/portfolio/add';
		    }

		    if (addPortfolioTopBtn) {
		        addPortfolioTopBtn.addEventListener('click', goToAddPage);
		    }
		    if (addPortfolioBottomBtn) {
		        addPortfolioBottomBtn.addEventListener('click', goToAddPage);
		    }

		    // 페이지가 처음 로드될 때 포트폴리오 목록을 불러옵니다.
		    loadAndRenderPortfolioList();
		});
		// ddㅓ
		
		
		