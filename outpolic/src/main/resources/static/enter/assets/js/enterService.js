/**
 * 
 */

//마이페이지 기업 정보 수정 시작
// 기업정보 불러오기
function fillEnterInfoForm(data) {
	$('#corpName').val(data.corpName);
	$('#corpRprsv').val(data.corpRprsv);
	$('#corpTelNo').val(data.corpTelNo);
	$('#corpFoundationYmdt').val(data.corpFoundationYmdt);
	$('#sample4_postcode').val(data.corpZip);
	$('#sample4_roadAddress').val(data.corpAddress);
	$('#sample4_detailAddress').val(data.corpDAddress);
	$('#corpScale').val(data.corpScale);
	$('#corpExplain').val(data.corpExplain);
}



// 기업명 입력안했을때 경고문 띄우기
function isCorpNameValid(){
	const CorpName = $('#corpName').val().trim();
	if(! CorpName){
		alert("기업명을 입력해주세요");
		return false;
	}
	return true;

}

// 이메일 입력 안했을때 경고문 띄우기
function isEmailValid(){
	const email = $('#memberEmail').val().trim();
	if(!email){
		alert("이메일을 입력해주세요");
		return false;
	}
	return true;
	// return isDupleEmail = true;
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

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('sample4_postcode').value = data.zonecode;
            document.getElementById("sample4_roadAddress").value = roadAddr;
            //document.getElementById("sample4_jibunAddress").value = data.jibunAddress;

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
            }
        }
    }).open();
}


// 수정요청 보내기 위해서 데이터 받아오기
function collectEnterInfo() {
	return{
		corpName: $('#corpName').val(),
		corpRprsv: $('#corpRprsv').val(),
		corpTelNo: $('#corpTelNo').val(),
		corpFoundationYmdt: $('#corpFoundationYmdt').val(),
		corpZip: $('#sample4_postcode').val(),
		corpAddress: $('#sample4_roadAddress').val(),
		corpDAddress: $('#sample4_detailAddress').val(),
		corpScale: $('#corpScale').val(),
		corpExplain: $('#corpExplain').val()		
	};
}
// 클릭 이벤트 시작
$(document).ready(function () {

	// 우편번호 찾기 호출    
	$('#postcodeBtn').click(function () {
	    sample4_execDaumPostcode();  
	});
	
	
	
	// 회원정보 수정 요청
	$('#btnSave').click(function(e){
		e.preventDefault();
		//수정된 info 정보 가져오기
		const enterInfo = collectEnterInfo();
			
		if (!isValidTelNo(enterInfo.corpTelNo)) {
			alert("전화번호는 형식에 맞게 입력해주세요. (예: 010-1234-5678)");
			return;
		}
		if (!isValidCorpScale(enterInfo.corpScale)) {
			alert("기업 규모는 숫자만 입력 가능합니다.");
			return;
		}

		
		saveEnterInfo('/enterpriseEdit', enterInfo, () => {
			alert('정보가 성공적으로 수정되었습니다.')
		window.location.href = '/enter/mypage';
		});
	});
});

// 마이페이지 기업 정보 수정 끝

















