package outpolic.enter.company.domain;

import lombok.Data;

@Data
public class EnterCompanyReviewDTO {

	private String osTtl; // 어떤 외주에 대한 리뷰인지
    private String mbrNicknm; // 작성자 닉네임
    private String rvwCn; // 리뷰 내용
    private double rvwEvl; // 평점
    private String rvwRegYmdt; // 작성일
}
