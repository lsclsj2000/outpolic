$(document).ready(function(){
		    // HTML에서 만든 슬라이더 컨테이너의 클래스명('.popular-outsourcing-slider')을 선택합니다.
		    $('.popular-outsourcing-slider').slick({
		        dots: false,              // 아래쪽 점 네비게이션 비활성화
		        infinite: false,          // 무한 반복 비활성화 (끝까지 가면 멈춤)
		        speed: 500,               // 슬라이드 전환 속도 (0.5초)
		        slidesToShow: 5,          // ★ 한 번에 보여줄 아이템 개수
		        slidesToScroll: 5,        // ★ 한 번에 넘길 아이템 개수
		        
		        // 화살표 버튼 커스터마이징 (템플릿의 아이콘 클래스 사용)
		        prevArrow: '<button type="button" class="slick-prev"><i class="fi-rs-arrow-small-left"></i></button>',
		        nextArrow: '<button type="button" class="slick-next"><i class="fi-rs-arrow-small-right"></i></button>',
		        
		        // 반응형 웹 설정 (화면 크기에 따라 보여줄 아이템 개수 조절)
		        responsive: [
		            {
		                breakpoint: 1300, 
		                settings: {
		                    slidesToShow: 4,
		                    slidesToScroll: 4
		                }
		            },
		            {
		                breakpoint: 992,  
		                settings: {
		                    slidesToShow: 3,
		                    slidesToScroll: 3
		                }
		            },
		            {
		                breakpoint: 768,  
		                settings: {
		                    slidesToShow: 2,
		                    slidesToScroll: 2
		                }
		            },
		            {
		                breakpoint: 576,  
		                settings: {
		                    slidesToShow: 1,
		                    slidesToScroll: 1
		                }
		            }
		        ]
		    });
		});