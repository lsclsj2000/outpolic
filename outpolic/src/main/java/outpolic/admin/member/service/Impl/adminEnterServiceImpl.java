package outpolic.admin.member.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.member.dto.AdminMemberDTO;
import outpolic.admin.member.mapper.AdminEnterMapper;
import outpolic.admin.member.service.AdminEnterService;
import outpolic.common.domain.Member;
@Service
@RequiredArgsConstructor
public class adminEnterServiceImpl implements AdminEnterService {
	
	private final AdminEnterMapper adminEnterMapper;
	
	@Override
	public List<AdminMemberDTO> getEnterList() {
		List<AdminMemberDTO> enterList = adminEnterMapper.getEnterList();			
		return enterList;
	}


	@Override
	public List<AdminMemberDTO> getEnterListByStatus(String statusCode) {
		if (statusCode == null || statusCode.isBlank()) {
			return adminEnterMapper.getEnterList(); // 전체 목록
		}
		return adminEnterMapper.getEnterListByStatus(statusCode);
	}
	

	@Override
	public AdminMemberDTO selectEnterByCode(String memberCode) {
		return adminEnterMapper.selectEnterByCode(memberCode);
	}

	@Override
	public int updateAdminEnterEditInfo(AdminMemberDTO adminMemberDTO) {
		int result = adminEnterMapper.updateAdminEnterEditInfo(adminMemberDTO);
		return result;
	}

	@Override
	public List<AdminMemberDTO> selectFilteredEnterpriseMembers(String statusCode, String orderBy) {
		return adminEnterMapper.selectFilteredEnterpriseMembers(statusCode, orderBy);
	}


	@Override
	public List<AdminMemberDTO> searchEnterpriseMembers(String keyword) {
		return adminEnterMapper.searchEnterpriseMembers(keyword);
	}


	@Override
	public int getEnterCount() {
		// TODO Auto-generated method stub
		return adminEnterMapper.getEnterCount();
	}


	@Override
	public List<AdminMemberDTO> getEnterListForPg(int startRow, int rowPerPage) {
		// TODO Auto-generated method stub
		return adminEnterMapper.getEnterListForPg(startRow, rowPerPage);
	}



}
