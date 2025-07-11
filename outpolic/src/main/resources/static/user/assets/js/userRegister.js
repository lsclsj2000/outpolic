/**
 * 
 */

let isTelUnique = false;
let isEmailUnique = false;
let isIdUnique = false;
let isNickNmUnique = false;
// AJAX
//회원가입 시작
// 아이디 중복확인
function getUserId(memberId){
	$.ajax({
		url: '/user/checkDuplicate',
		method: 'GET',
		data:{type: 'memberId',   
		      value: memberId},
		success:function(data){
			if(data.duplicate){
				$('#memberIdMsg').text('이미 사용중인 아이디입니다.').removeClass('text-success').addClass('text-danger');
				isIdUnique = false;
			}else{
				$('#memberIdMsg').text('사용 가능한 아이디입니다.').removeClass('text-danger').addClass('text-success');
				isIdUnique = true;
			}
		},
		error: function(){
			$('#memberTelNoMsg').text('서버 오류로 확인에 실패했습니다.').removeClass('text-success').addClass('text-danger');
			isIdUnique = false;
		}			  
	});
}

// 닉네임 중복확인
function getUserNickNm(memberNickname){
	$.ajax({
		url: '/user/checkDuplicate',
		method: 'GET',
		data:{type: 'memberNickname',
			  value: memberNickname},
		success:function(data){
			if(data.duplicate){
				$('#memberNickNmMsg').text('이미 사용중인 닉네임입니다.').removeClass('text-success').addClass('text-danger');
				isNickNmUnique = false;
			}else{
				$('#memberNickNmMsg').text('사용 가능한 닉네임입니다.').removeClass('text-danger').addClass('text-success');
				isNickNmUnique = true;
			}
		},
		error: function(){
			$('#memberNickNmMsg').text('서버 오류로 확인에 실패했습니다.').removeClass('text-success').addClass('text-danger');
			isNickNmUnique = false;
		}			  			  
	});
}
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
// 이메일 중복확인
function getUserEmail(memberEmail){
	$.ajax({
        url: '/user/checkDuplicate',
        method: 'GET',
        data: {
            type: 'memberEmail',
            value: memberEmail
        },
        success: function (data) {
            if (data.duplicate) {
                $('#memberEmailMsg').text('이미 사용 중인 이메일입니다.').removeClass('text-success').addClass('text-danger');
                isEmailUnique = false;
            } else {
                $('#memberEmailMsg').text('사용 가능한 이메일입니다.').removeClass('text-danger').addClass('text-success');
                isEmailUnique = true;
            }
        },
        error: function () {
            $('#memberEmailMsg').text('서버 오류로 확인에 실패했습니다.').removeClass('text-success').addClass('text-danger');
            isEmailUnique = false;
        }
    });
}



// 회원가입 시작

// 비밀번호 일치 여부
let pwEqual = false;

// 비밀번호와 비밀번호 확인 일치 여부 안내문
document.addEventListener('DOMContentLoaded', function () {
    const pwInput = document.getElementById("memberPw");
    const pwCheckInput = document.getElementById("userpwcheck");
    const pwMsg = document.getElementById("pwCheckMsg");

    function checkPasswordMatch() {
        const pw = pwInput.value;
        const pwCheck = pwCheckInput.value;

        if (pwCheck === "") {
            pwMsg.textContent = "";
			pwMsg.classList.remove("text-success", "text-danger");
            return;
        }

        if (pw !== pwCheck) {
            pwMsg.textContent = "비밀번호가 일치하지 않습니다.";
			pwMsg.classList.add("text-danger");
			pwEqual = false;
        } else {
            pwMsg.textContent = "비밀번호가 일치합니다";
			 pwMsg.classList.add("text-success");
			pwEqual = true;
        }
    }
    pwInput.addEventListener("input", checkPasswordMatch);
    pwCheckInput.addEventListener("input", checkPasswordMatch);
});

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
          //  document.getElementById("sample4_jibunAddress").value = data.jibunAddress;

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


// 아이디, 이메일, 닉네임, 전화번호 중복 blur처리
$(document).ready(function(){
	// 이름 유효성검증
	$('#memberName').on('blur', function(){
			const name = $(this).val().trim();
			if(name===''){
				$('#memberNameMsg').text('').removeClass('text-success text-danger');
				return;
			}
			if(!isValidName(name)){
				$('#memberNameMsg').text('이름에는 한글만 사용 가능합니다.').removeClass().addClass('text-danger');
			}else {
			    $('#memberNameMsg').text('').removeClass('text-danger');
			}
		});
	
	$('#memberId').on('blur', function(){
		const Id = $(this).val().trim();
		//빈값일 경우 중복확인 메세지 출력 안함
		if(Id===''){
			$('#memberIdMsg').text('아이디를 입력해주세요.').removeClass('text-success').addClass('text-danger');
			isIdUnique = false;
			return;
		}
		//유효성 검증
		if(!isValidId(Id)){
			$('#memberIdMsg').text('아이디는 영문과 숫자를 섞어서 5~20자리로 적어주십시오.').removeClass().addClass('text-danger');
			isIdUnique = false;
			return;
		}
		getUserId(Id);
	})
	
	$('#memberTelNo').on('blur', function(){
		const tel = $(this).val().trim();
		//빈값일경우 중복확인 메세지 출력 안함
		if(tel===''){
			$('#memberTelNoMsg').text('').removeClass('text-success text-danger');
			isTelUnique = false;
		    return;
		}
		// 유효성검증
		if(!isValidTelNo(tel)){
			$('#memberTelNoMsg').text('유효한 전화번호 형식이 아닙니다.').removeClass().addClass('text-danger');
			isTelUnique = false;
			return;
		}
		getUserTelNo(tel);
	});
	
	$('#memberEmail').on('blur', function(){
		const email = $(this).val();
		//빈값일경우 중복확인 메세지 출력 안함
		if(email===''){
			$('#memberEmailMsg').text('').removeClass('text-success text-danger');
			isEmailUnique = false;
			return;
		}
		//유효성검증
		if (!isValidEmail(email)) {
			$('#memberEmailMsg').text('유효한 이메일 형식이 아닙니다.').removeClass().addClass('text-danger');
			isEmailUnique = false;
			return;
		}
		getUserEmail(email);
	});
	
	$('#memberNickname').on('blur', function(){
		const nickNm = $(this).val();
		if(nickNm === ''){
			// 빈 닉네임은 유효함 → 랜덤 닉네임이 자동 생성되므로
			$('#memberNickNmMsg').text('입력하지 않으면 랜덤 닉네임이 부여됩니다.')
			                     .removeClass('text-danger')
			                     .addClass('text-info'); 
			isNickNmUnique = true; 
			return;
		}
		getUserNickNm(nickNm);
	});
	
	//비밀번호 유효성검증
	$('#memberPw').on('blur', function(){
		const pw = $(this).val();
		//빈값검사는 위에서 할것이므로 유효성검증만 진행
		if(!isValidPassword(pw)){
			$('#memberPwMsg').text('영문 숫자 특수문자를 사용해주십시오.').removeClass().addClass('text-danger');
			$('#userpwcheck').prop('disabled', true);
			pwEqual = false;
			return;
		}else{
			$('#memberPwMsg').text('사용할 수 있는 비밀번호입니다').removeClass('text-danger').addClass('text-success');
			$('#userpwcheck').prop('disabled', false);
		}
	});
	
	// 우편번호 찾기 호출    
		$('#postcodeBtn').click(function () {
		    sample4_execDaumPostcode();  
		});
	
	
	$('#btnSave').on('click', function(e){
		
		e.preventDefault();
		
		const Id = $('#memberId').val().trim();
		if (!isIdUnique&&Id!=='') {
		    $('#memberIdMsg').text('이미 사용 중인 아이디입니다.').removeClass('text-success').addClass('text-danger');
		    $('#memberId').focus();
		    return;
		}else if(Id===''){
			$('#memberIdMsg').text('아이디를 입력해주세요.').removeClass('text-success').addClass('text-danger');
			$('#memberId').focus();
			return;
		}else{
			
		}
		
		const pw = $('#memberPw').val().trim();
		if(pw===''){
			$('#memberPwMsg').text('사용하실 비밀번호를 입력해주세요.').removeClass().addClass('text-danger');
			$('#memberPw').focus();
			return;
		}
		if (!pwEqual) {
		    $('#pwCheckMsg').text('비밀번호가 일치하지 않습니다.').addClass('text-danger');
		    $('#userpwcheck').focus();
		    return;
		}

		const name = $('#memberName').val().trim();
		if(name === ''){
			$('#memberNameMsg').text('이름을 입력해주세요.').removeClass().addClass('text-danger');
			$('#memberName').focus();
			return;
		}
		const nickNm = $('#memberNickname').val().trim();
			if (!isNickNmUnique&&nickNm!=='') {
			    $('#memberNickNmMsg').text('이미 사용 중인 닉네임입니다.').removeClass('text-success').addClass('text-danger');
				$('#memberNickname').focus();
			    return;
			}else if(nickNm === ''){
				// 빈 닉네임은 유효함 → 랜덤 닉네임이 자동 생성되므로
				$('#memberNickNmMsg').text('입력하지 않으면 랜덤 닉네임이 부여됩니다.')
				                     .removeClass('text-danger')
				                     .addClass('text-info'); 
									 
			}else{
				
			}
		const telNo = $('#memberTelNo').val().trim();
		if (!isTelUnique&&telNo!=='') {
		    $('#memberTelNoMsg').text('이미 사용 중인 전화번호입니다.').removeClass('text-success').addClass('text-danger');
		    $('#memberTelNo').focus();
		    return;
		}else if(telNo===''){
			$('#memberTelNoMsg').text('전화번호를 입력해주세요.').removeClass('text-success').addClass('text-danger');
			$('#memberTelNo').focus();
			return;
		}else{
			
		}
		
		const email = $('#memberEmail').val().trim();
		if (!isEmailUnique&&email!=='') {
		    $('#memberEmailMsg').text('이미 사용 중인 이메일입니다.').removeClass('text-success').addClass('text-danger');
		    $('#memberEmail').focus();
		    return;
		}else if(email===''){
			$('#memberEmailMsg').text('이메일을 입력해주세요.').removeClass('text-success').addClass('text-danger');
			$('#memberEmail').focus();
			return;
		}else{
			
		}
		const birth = $('#memberBirth').val().trim();
		if(birth===''){
			$('#memberBirthMsg').text('생일을 입력해주세요').removeClass('text-success').addClass('text-danger');
			return; 
		}else{
			$('#memberBirthMsg').text('');
		}

		const zip = $('#sample4_postcode').val().trim();
		if(zip===''){
			$('#memberAddressMsg').text('주소를 입력해주세요').removeClass('text-success').addClass('text-danger');
			return;
		}else{
			$('#memberAddressMsg').text('');
		}
		const gender = document.querySelector('input[name="memberGender"]:checked')?.value;
		if(!gender){
			alert("성별을 선택해주세요.");
			return;
		}
		const isAgreeChecked = document.getElementById('agree')?.checked;
		if (!$('#agree').is(':checked')) {
		    alert("이용약관 및 개인정보처리방침에 동의하셔야 가입이 가능합니다.");
		    return;
		}
		
	
	   if (!$('#agree').is(':checked')) {
	       alert("이용약관 및 개인정보처리방침에 동의하셔야 가입이 가능합니다.");
	       return;
	   }
	   
	   const memberAgreeYN = $('#memberAgreeYN').is(':checked') ? 1 : 0;
	       $('#hiddenMemberAgreeYN').val(memberAgreeYN);
	   
	   $('form.register').submit();
	
	});
});