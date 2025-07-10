/**
 * 
 */


let isTelUnique = false;
let isEmailUnique = false;
let isIdUnique = false;
let isNickNmUnique = false;
// AJAX
//회원가입 시작


// 전화번호 중복확인
function getUserTelNo(memberTelNo){
	$.ajax({
		url: '/user/checkDuplicate',
		method: 'GET',
		data:{type: 'memberTelNo',   
		      value: memberTelNo},
		  success: function (data) {
              if (data.duplicate) {
                  $('#memberTelNoMsg').text('이미 사용 중인 전화번호입니다.').removeClass('text-success').addClass('text-danger');
                  isTelUnique = false;
              } else {
                  $('#memberTelNoMsg').text('사용 가능한 전화번호입니다.').removeClass('text-danger').addClass('text-success');
                  isTelUnique = true;
              }
          },
          error: function () {
              $('#memberTelNoMsg').text('서버 오류로 확인에 실패했습니다.').removeClass('text-success').addClass('text-danger');
              isTelUnique = false;
          }	  
	});
	
}



// 기업 회원가입 시작
let isBrnoUnique = false;
function verifyBiz() {
    const corpBrno = document.getElementById("corpBrno").value.trim();

    if (!corpBrno || corpBrno.length !== 10) {
        alert("- 를 뺀 10자리의 유효한 사업자등록번호를 입력해주세요.");
        return;
    }
    const requestData = {
        b_no: [corpBrno]
    };

    $.ajax({
        url: "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=Ss%2FcvhJpKRk4dplp4cgMb0nTueeuNsPgQjcHEdPf08R7qidouA2AqUvvMatuMmUPrj5dPlVXjUH%2BkaEyGR2k8A%3D%3D",
        type: "POST",
        data: JSON.stringify(requestData),
        dataType: "JSON",
        contentType: "application/json",
        accept: "application/json",
        success: function(result) {
            console.log(result); 
			if (!result || !result.data || result.data.length === 0) {
                alert("조회 결과가 없습니다. 사업자번호를 다시 확인해주세요.");
                return;
            }
            const data = result.data[0];
            if (data.b_stt === "계속사업자") {
				getCorpBrno(corpBrno);
            } else {
                alert("유효하지 않은 사업자등록번호입니다.");
            }
        },
        error: function(error) {
            console.log(error.responseText);
            alert("기업 인증 중 오류가 발생했습니다.");
        }
    });
}
function getCorpBrno(corpBrno){
	$.ajax({
		url: '/enter/checkDuplicate',
		method: 'GET',
		data:{type: 'corpBrno',
		      value: $('#corpBrno').val()},
		success:function(result){
			if(result>0){
				alert('이미 등록된 사용자입니다.')
			}else{
				alert('유효한 사업자등록번호입니다. 나머지 폼을 작성해주세요')
				document.getElementById("corpName").readOnly = false;
				document.getElementById("corpRprsv").readOnly = false;
				document.getElementById("corpTelNo").readOnly = false;
				document.getElementById("corpHomepageUrl").readOnly = false;
				document.getElementById("corpFoundationYmdt").readOnly = false;
				document.getElementById("corpScale").readOnly = false;
				document.getElementById("sample4_postcode").readOnly = false;
				document.getElementById("sample4_roadAddress").readOnly = false;
				document.getElementById("sample4_detailAddress").readOnly = false;
				document.getElementById("corpBrno").readOnly = true;
			}
		},
		error: function(){
			$('#memberTelNoMsg').text('서버 오류로 확인에 실패했습니다.').removeClass('text-success').addClass('text-danger');
			isBrnoUnique = false;
		}			  
	});
}


// 다음 주소 api 사용
//본 예제에서는 도로명 주소 표기 방식에 대한 법령에 따라, 내려오는 데이터를 조합하여 올바른 주소를 구성하는 방법을 설명합니다.
function sample4_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
			const guideTextBox = document.getElementById('guide');
            // 도로명 주소의 노출 규칙에 따라 주소를 표시한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var roadAddr = data.roadAddress; // 도로명 주소 변수
            var extraRoadAddr = ''; // 참고 항목 변수
			
            // 법정동명이 있을 경우 추가한다. (법정리는 제외)
            // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
            if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                extraRoadAddr += data.bname;
            }
            // 건물명이 있고, 공동주택일 경우 추가한다.
            if(data.buildingName !== '' && data.apartment === 'Y'){
               extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
            }
            // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
            if(extraRoadAddr !== ''){
                extraRoadAddr = ' (' + extraRoadAddr + ')';
            }
			//var guideTextBox = document.getElementById("guide");
            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('sample4_postcode').value = data.zonecode;
            document.getElementById("sample4_roadAddress").value = roadAddr;
            // 사용자가 '선택 안함'을 클릭한 경우, 예상 주소라는 표시를 해준다.
            if(data.autoRoadAddress) {
                var expRoadAddr = data.autoRoadAddress + extraRoadAddr;
                guideTextBox.innerHTML = '(예상 도로명 주소 : ' + expRoadAddr + ')';
                guideTextBox.style.display = 'block';

            } else if(data.autoJibunAddress) {
                var expJibunAddr = data.autoJibunAddress;
                guideTextBox.innerHTML = '(예상 지번 주소 : ' + expJibunAddr + ')';
                guideTextBox.style.display = 'block';
            } else {
                guideTextBox.innerHTML = '';
                guideTextBox.style.display = 'none';
            }
        }
    }).open();
}

document.addEventListener('DOMContentLoaded', function () {
	
	$('#verifiedbtn').click(function(){
		verifyBiz();
	})
// 우편번호 찾기 호출    
	$('#postcodeBtn').click(function () {
	    sample4_execDaumPostcode();  
	});

	
	$('#btnSave').on('click', function(e){
		
		e.preventDefault();

	   $('form.register').submit();
	
	});
});
