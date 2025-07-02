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
    const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@^*]).{8,20}$/;
    return pattern.test(pw);
}

// 이메일 검사
function isValidEmail(email) {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return pattern.test(email);
}

// 전화번호 검사 (숫자만, 010 포함 10~11자리)
function isValidPhone(phone) {
    const pattern = /^01[016789][0-9]{7,8}$/;
    return pattern.test(phone);
}