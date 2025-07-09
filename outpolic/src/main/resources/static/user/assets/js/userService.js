/**
 * 
 */

//마이페이지 내 정보 수정 시작
// 회원정보 불러오기
function fillUserInfoForm(data) {
  $('#memberName').val(data.memberName);
  $('#memberCode').val(data.memberCode);
  $('#memberNickname').val(data.memberNickname);
  $('#memberTelNo').val(data.memberTelNo);
  $('#sample4_roadAddress').val(data.memberAddress);
  $('#sample4_detailAddress').val(data.memberDAddress);
  $('#sample4_postcode').val(data.memberZip);
  $('#memberEmail').val(data.memberEmail);
  $('#memberBirth').val(data.memberBirth);
  $('#memberJoinDate').val(data.memberJoinDate);
}

//중복확인용
let isDupleNick = false;
let isVerified = true;
let isDupleEmail = false;
// 닉네임 입력안했을때 경고문 띄우기
function isNicknameValid(){
	const nickname = $('#memberNickname').val().trim();
	if(!nickname){
		alert("닉네임을 입력해주세요");
		return false;
	}
	return true;
	// return isDupleNick = true;
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
            document.getElementById("sample4_jibunAddress").value = data.jibunAddress;

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


// 수정요청 보내기 위해서 데이터 받아오기
function collectUserInfo() {
  return {
    memberId: $('#memberId').val(),
	memberCode: $('#memberCode').val(),
    memberName: $('#memberName').val(),
    memberNickname: $('#memberNickname').val(),
    memberTelNo: $('#memberTelNo').val(),
    memberAddress: $('#sample4_roadAddress').val(),
    memberDAddress: $('#sample4_detailAddress').val(),
    memberZip: $('#sample4_postcode').val(),
    memberEmail: $('#memberEmail').val(),
    memberBirth: $('#memberBirth').val(),
    memberJoinDate: $('#memberJoinDate').val()
  };
}

// 클릭 이벤트 시작
$(document).ready(function () {
	//사실상 필요없긴한데 혹시몰라서 넣어둔 메소드
	//getUserInfo(fillUserInfoForm);
	// 닉네임 중복확인 버튼 클릭 
	$('#nicknmDupleBtn').click(function () {
		console.log("닉네임 중복확인 버튼 눌림"); 
	   if (!isNicknameValid()) return;

	   const nickname = $('#memberNickname').val();
	   console.log('입력된 닉네임:', nickname);
	   const memberCode = $('#memberCode').val();
	   console.log('현재 memberCode:', memberCode);

	   dupleUserInfoCheck("memberNickname", nickname, memberCode, function(data){
			if (data === true || data === 'true') {
			    alert('닉네임이 중복입니다. 다른 닉네임을 입력하세요');
			    isDupleNick = false;
			} else {
			    alert('사용할 수 있는 닉네임입니다');
			    isDupleNick = true;
			}
		});
   });
   // 이메일 중복확인 버튼 클릭
   $('#emailDupleBtn').click(function () {
   	   if (!isEmailValid()) return;
   	   const email = $('#memberEmail').val();
   	   const memberCode = $('#memberCode').val();
	   // 이메일 유효성 검증
	   if (!isValidEmail(email)){ alert('이메일이 유효하지 않습니다'); return;}
   	   dupleUserInfoCheck("memberEmail", email, memberCode, function(data){
   			if (data === true || data === 'true') {
   			    alert('이메일이 중복입니다. 다른 이메일을 입력하세요');
   			    isDupleEmail = false;
   			} else {
   			    alert('사용할 수 있는 이메일입니다');
   			    isDupleEmail = true;
   			}
   		});
      });
   
/*	//인증번호 확인 버튼 클릭시
	// 상황상 인증번호를 보낼 수 없어서 isVerified를 true로 유지하도록 함
	$('#sendCodeBtn').click(function() {
		const phone = $('#memberTelNo').val();
		if(!phone){
			alert('휴대폰번호를 입력해주세요');
			return;
		}
		// ajax요청 가져오기
		sendAuthCode(phone, () => {alert('인증번호가 발송되었습니다');});
	});
	
	//인증번호 검토
	$('.btn-verify-code').click(function(){
		//사용자가 입력한 인증번호를 가져온다
		const code = $('#authCodeInput').val();
		if(!code){
			alert('인증번호를 입력해주세요.');
			isVerified = false;
			return;
		}
		checkAuthCode(code, ()=> {
			alert('인증완료 되었습니다');
			isVerified = true;
		});
	});*/
	
	
	// 우편번호 찾기 호출    
	$('#postcodeBtn').click(function () {
	    sample4_execDaumPostcode();  
	});
	
	
	
	// 회원정보 수정 요청
	$('#btnSave').click(function(){
		if(!isDupleNick || !isVerified || !isDupleEmail){
			alert('인증을 완료해주세요');
			return;
		}
		const email = $('#memberEmail').val();
			if (!isValidEmail(email)) {
				alert('이메일 형식이 유효하지 않습니다.');
				return;
			}
			//수정된 info 정보 가져오기
		const userInfo = collectUserInfo();
		
		saveUserInfo('/userEdit/update', userInfo, () => {
			alert('정보가 성공적으로 수정되었습니다.')
		window.location.href = '/mypage';
		});
	});
});

// 마이페이지 내 정보 수정 끝















