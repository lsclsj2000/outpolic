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
function getUserNickNm(memberNickName){
	$.ajax({
		url: '/user/checkDuplicate',
		method: 'GET',
		data:{type: 'memberNickName',
			  value: memberNickName},
		success:function(data){
			if(data.duplicate){
				$('#memberNickNmMsg').text('이미 사용중인 닉네임입니다.').removeClass=('text-success').addClass('text-danger');
								isIdUnique = false;
			}else{
				$('#memberNickNmMsg').text('사용 가능한 닉네임입니다.').removeClass('text-danger').addClass('text-success');
				isIdUnique = true;
			}
		},
		error: function(){
			$('#memberNickNmMsg').text('서버 오류로 확인에 실패했습니다.').removeClass('text-success').addClass('text-danger');
			isIdUnique = false;
		}			  
			}
		}			  
	})
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

// 아이디, 이메일, 전화번호 중복 blur처리
$(document).ready(function(){
	
	$('#memberId').on('blur', function(){
		const Id = $(this).val().trim();
		if(Id===''){
			$('#memberIdMsg').text('').removeClass('text-success text-danger');
			return
		}
		getUserId(Id);
	})
	
	$('#memberTelNo').on('blur', function(){
		const tel = $(this).val().trim();
		if(tel===''){
			$('#memberTelNoMsg').text('').removeClass('text-success text-danger');
		       return;
		}
		getUserTelNo(tel);
	});
	
	$('#memberEmail').on('blur', function(){
		const email = $(this).val();
		if(email!==''){
			$('#memberEmailMsg').text('').removeClass('text-success text-danger');
		}
		getUserEmail(email);
	});
	
})
