// userRanking.js
$(document).ready(function(){
    var $slider = $('.popular-outsourcing-slider');

    if ($slider.length > 0 && $slider.find('.slider-item').length > 0) {
        
        $slider.slick({
            // ... (slidesToShow, slidesToScroll 등 다른 옵션은 그대로) ...
            
            // ★★★ 화살표 옵션을 아래와 같이 변경합니다 ★★★
            arrows: true, // 화살표를 표시
            
            // prevArrow, nextArrow 옵션: 생성될 화살표의 HTML 코드를 직접 정의합니다.
            // 버튼에 'slick-btn' 이라는 커스텀 클래스를 추가해서 CSS로 제어하기 쉽게 만듭니다.
            prevArrow: '<button type="button" class="slick-prev slick-btn"><i class="fi-rs-arrow-small-left"></i></button>',
            nextArrow: '<button type="button" class="slick-next slick-btn"><i class="fi-rs-arrow-small-right"></i></button>',

            infinite: false,
            slidesToShow: 5,
            slidesToScroll: 5,
            draggable: false,
            swipe: false,
            // (responsive 옵션은 그대로 둡니다)
        });
    }
});