/**
 * 
 */
// 아이디 검사 (영문+숫자 5~20자)
function isValidId(id) {
    const pattern = /^[a-zA-Z0-9]{5,20}$/;
    return pattern.test(id);
}

// 비밀번호 검사 (영문+숫자+특수문자 8~20자)
function isValidPassword(pw) {
    const pattern = /^(?=.*[a-z])(?=.*\d)(?=.*[!@^*]).{8,20}$/;
    return pattern.test(pw);
}

// 이메일 검사
function isValidEmail(email) {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return pattern.test(email);
}

// 전화번호 검사 (숫자만, 010 포함 10~11자리)
function isValidTelNo(TelNo) {
    const pattern = /^01[016789]-[0-9]{3,4}-[0-9]{4}$/
    return pattern.test(TelNo);
}

//이름 검사(한글만)
function isValidName(name) {
    const pattern = /^[가-힣]{2,6}$/;
    return pattern.test(name);
}


function isValidNickname(nickname) {
    const pattern = /^[가-힣a-zA-Z]{2,15}$/;
    return pattern.test(nickname);
}