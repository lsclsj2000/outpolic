package outpolic.admin.search.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import outpolic.admin.search.domain.AdminContentItemDTO;
import outpolic.admin.search.domain.AdminContents;
import outpolic.admin.search.domain.AdminContentsDetailDTO;
import outpolic.admin.search.mapper.AdminSearchMapper;
import outpolic.admin.search.service.AdminSearchService;

@Service
public class AdminSearchServiceImpl implements AdminSearchService {

    // final 키워드를 사용하여 Mapper가 변경되지 않도록 함
    private final AdminSearchMapper adminSearchMapper;

    // @Autowired 대신 생성자를 통한 의존성 주입을 권장합니다.
    public AdminSearchServiceImpl(AdminSearchMapper adminSearchMapper) {
        this.adminSearchMapper = adminSearchMapper;
    }

    @Override
    public List<AdminContents> getContentsList(String keyword) {
        return adminSearchMapper.getContentsList(keyword);
    }

    @Override
    public AdminContentsDetailDTO getContentsDetailById(String contentsId) {
        return adminSearchMapper.getContentsDetailById(contentsId);
    }

    @Override
    public List<AdminContentItemDTO> findContentsByCategoryId(String categoryId) {
        return adminSearchMapper.findContentsByCategoryId(categoryId);
    }

    // ▼▼▼ [추가/수정] 삭제 기능을 위한 메소드 구현부 ▼▼▼
    @Override
    @Transactional // 여러 DB 작업을 하나의 트랜잭션으로 묶어 데이터 일관성을 보장합니다.
    public void deleteContent(String contentsId) {
       // 1. contentsId(prtf_cd 또는 os_cd)를 사용해 cl_cd를 조회합니다.
    	String clCd = adminSearchMapper.findClCdByCntdCd(contentsId);
    	
    	// cl_cd가 존재하지 않으면 더 이상 진행하지 않음
    	if(clCd == null) {
    		// 또는 예외를 발생시킬 수 있습니다.
    		System.out.println("삭제할 콘텐츠의 cl_cd를 찾을 수 없습니다:"+contentsId);
    		return;
    	}
    	
    	// 2. cl_cd를 사용하는 모든 매핑 테이블의 데이터를 먼저 삭제합니다.
    	adminSearchMapper.deleteCategoryMappingByClCd(clCd);
    	adminSearchMapper.deleteTagMappingByClCd(clCd);
    	adminSearchMapper.deleteBookmarkByClCd(clCd);
    	
    	// 3. 콘텐츠 타입에 따라 다른 테이블의 데이터를 삭제합니다.
    	if(contentsId != null && contentsId.startsWith("PO_")) {
    		// 포트폴리오일 경우
    		adminSearchMapper.deleteOutsourcingPortfolioByPrtfCd(contentsId);
    		adminSearchMapper.deletePortfolioByPrtfCd(contentsId);
    		
    	}else if(contentsId != null && contentsId.startsWith("OS_")) {
    		// 외주일 경우(관련 테이블이 더 있다면 여기에 추가)
    		adminSearchMapper.deleteOutsourcingByOsCd(contentsId);
    	}

    	// 4. 마지막으로 content_list 테이블의 데이터를 삭제합니다.
    	adminSearchMapper.deleteContentListByClCd(clCd);
    }
}   

