package outpolic.admin.empowerment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import outpolic.admin.empowerment.dto.AdminEmpowermentDTO;

@Service
public interface AdminEmpowermentService {
	// 관리자 정보 호출
	List <AdminEmpowermentDTO> AdminEmpowermentSelect();
		
	public void updateAdminPermissions(String admCd, List<String> newPermissions);	
}
