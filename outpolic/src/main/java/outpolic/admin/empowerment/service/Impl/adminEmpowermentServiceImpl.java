package outpolic.admin.empowerment.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

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


}
