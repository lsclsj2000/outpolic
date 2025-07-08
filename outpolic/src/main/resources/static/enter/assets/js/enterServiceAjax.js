/**
 * 
 */

// 마이페이지 내 정보 수정 시작
// ajax
// 회원정보 불러오기
function getEnterInfo(callback){
	$.ajax({
		url: '/enterEdit/info',
        method: 'GET',
		success: callback,
		error: () => alert('회원정보 조회 실패')
	});
}

// 닉네임 중복체크 
function dupleNicknameCheck(nickname, callback){
	$.ajax({
		url : '/check-nickname',
		type : 'POST',
		data : { memberNickname : nickname
				},
		success: callback,
		error: () => alert("닉네임이 중복입니다. 다른 닉네임을 입력하세요")
	});
}
//중복체크 확장
function dupleEnterInfoCheck(type, value, memberId, callback){
	$.ajax({
		url: `/check/${type}`,
		type:'POST',
		data:{
			[type] : value,
			memberId: memberId
		},
		success:callback,
		error:() => alert("중복 확인 중 에러가 발생했습니다.")
	})
	
}


// 회원 휴대폰 인증번호 발송
function sendAuthCode(phone, callback){
	$.ajax({
		url: '/send-code',
		type: 'POST',
		data: { phone: phone },
		success: callback,
		error: () => alert('인증번호 발송에 실패했습니다. 휴대폰 번호를 확인해주세요')
		
	})
}

//휴대폰 인증번호 검증
function checkAuthCode(code, callback){
	$.ajax({
		url: '/verify-code',
		type: 'POST',
		data: { code: code },
		success: callback,
		error: ()=> alert('인증번호가 틀렸습니다.')
	})
}

//수정한 데이터 서버에 전달 <- 회원가입시에도 사용가능.
function saveEnterInfo(url, enterInfo, callback) {
  $.ajax({
    url: url,
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(enterInfo),
    success: callback,
    error: () => alert('서버와 통신 중 오류가 발생했습니다.')
  });
}


// 마이페이지 내 정보 수정 끝


// 마이페이지 내 기업정보 수정 시작
unction getEnterpriseInfo(callback){
	$.ajax({
		url: '/enterpriseEdit/info',
		method: 'GET',
		success: callback,
		error: () => alert('기업정보 조회 실패')
	});
}

function saveEnterpriseInfo(url, enterpriseInfo, callback) {
  $.ajax({
    url: url,
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(enterpriseInfo),
    success: callback,
    error: () => alert('기업정보 저장 실패')
  });
}














