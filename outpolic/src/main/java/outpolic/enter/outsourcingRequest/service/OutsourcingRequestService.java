package outpolic.enter.outsourcingRequest.service;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;

public interface OutsourcingRequestService {

    /**
     * 외주 신청 또는 문의 생성
     */
    OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request);

    /**
     * 보낸 '신청' 목록 조회
     */
    List<RequestViewDTO> getSentRequests(String requesterId);
    
    /**
     * [추가] 보낸 '문의' 목록 조회
     */
    List<RequestViewDTO> getSentInquiries(String requesterId);

    /**
     * 받은 '신청' 목록 조회
     */
    List<RequestViewDTO> getReceivedRequests(String supplierEntCd);
    
    /**
     * 받은 '문의' 목록 조회
     */
	List<RequestViewDTO> getReceivedInquiries(String supplierEntCd);

    /**
     * 신청/문의 상세 내역 조회
     */
    RequestViewDTO getRequestDetails(String requestId);

    /**
     * 회원 코드로 기업 코드 조회
     */
    String findEntCdByMbrCd(String mbrCd);
    
    /**
     * [추가] 기업 코드로 회원 코드 조회
     */
    String findMbrCdByEntCd(String entCd);

	/**
     * 신청 상태 변경 (승인/거절)
     */
	void updateRequestStatus(String requestId, String status);
}