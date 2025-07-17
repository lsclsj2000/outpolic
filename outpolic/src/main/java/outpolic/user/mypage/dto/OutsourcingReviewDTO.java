package outpolic.user.mypage.dto;

import lombok.Data;

@Data
public class OutsourcingReviewDTO {
	  // outsourcing_contract
    private String ospCd;
    private String mbrCd;

    // outsourcing_prograss
    private String ocdCd;

    // outsourcing_contract_details
    private String clCd;

    // review
    private String rvwCd;
    private String rvwCn;
    private int rvwEvl;

    // content_list
    private String cntdCd;
    private String ocdTtl;

    // outsourcing
    private String osCd;
    private String osTtl;
    
    //portfolio
    private String prtfCd;
    private String prtfTtl;
    
    private String contentType;
}
