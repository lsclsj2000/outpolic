package outpolic.admin.empowerment.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.admin.empowerment.dto.AdminEmpowermentDTO;
import outpolic.admin.empowerment.mapper.AdminEmpowermentMapper;
import outpolic.admin.empowerment.service.AdminEmpowermentService;

@Service
@RequiredArgsConstructor
public class adminEmpowermentServiceImpl implements AdminEmpowermentService {
	
	private final AdminEmpowermentMapper adminEmpowermentMapper;

	@Override
	public List<AdminEmpowermentDTO> AdminEmpowermentSelect() {
		return adminEmpowermentMapper.AdminEmpowermentSelect();
	}

	@Override
	@Transactional
	public void updateAdminPermissions(String admCd, List<String> newPermissions) {
		List<String> currentPermissions = adminEmpowermentMapper.selectPermissionCodesByAdmCd(admCd);

	    List<String> toInsert = newPermissions != null
	        ? newPermissions.stream().filter(p -> !currentPermissions.contains(p)).toList()
	        : List.of();

	    List<String> toDelete = currentPermissions.stream()
	        .filter(p -> newPermissions == null || !newPermissions.contains(p))
	        .toList();

	    for (String code : toInsert) {
	    	adminEmpowermentMapper.insertPermission(admCd, code);
	    }

	    for (String code : toDelete) {
	    	adminEmpowermentMapper.deletePermission(admCd, code);
	    }
		
	}


}
