!function(e) {
    "use strict";
    if (e(".menu-item.has-submenu .menu-link").on("click", function(s) {
        s.preventDefault(),
        e(this).next(".submenu").is(":hidden") && e(this).parent(".has-submenu").siblings().find(".submenu").slideUp(200),
        e(this).next(".submenu").slideToggle(200)
    }),
    e("[data-trigger]").on("click", function(s) {
        s.preventDefault(),
        s.stopPropagation();
        var n = e(this).attr("data-trigger");
        e(n).toggleClass("show"),
        e("body").toggleClass("offcanvas-active"),
        e(".screen-overlay").toggleClass("show")
    }),
    e(".screen-overlay, .btn-close").click(function(s) {
        e(".screen-overlay").removeClass("show"),
        e(".mobile-offcanvas, .show").removeClass("show"),
        e("body").removeClass("offcanvas-active")
    }),
    e(".btn-aside-minimize").on("click", function() {
        window.innerWidth < 768 ? (e("body").removeClass("aside-mini"),
        e(".screen-overlay").removeClass("show"),
        e(".navbar-aside").removeClass("show"),
        e("body").removeClass("offcanvas-active")) : e("body").toggleClass("aside-mini")
    }),
    e(".select-nice").length && e(".select-nice").select2(),
    e("#offcanvas_aside").length) {
        const e = document.querySelector("#offcanvas_aside");
        new PerfectScrollbar(e)
    }
    e(".darkmode").on("click", function() {
        e("body").toggleClass("dark")
    })
}(jQuery);
/**
 * menu  활성화
 */
const url = new URL(location.href);
$('#offcanvas_aside nav a').each((idx, element)=>{
	const menuHref = $(element).attr('href');
	if(url.pathname === menuHref){
		$(element).closest('li.menu-item').addClass('active');
		if($(element).closest('li.menu-item').hasClass('has-submenu')){
			$(element).closest('li.menu-item')
					  .find(`a[href="${menuHref}"]`)
					  .addClass('active');
		}
		return false;
	}
});
















