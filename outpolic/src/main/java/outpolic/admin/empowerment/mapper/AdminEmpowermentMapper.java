package outpolic.admin.empowerment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.empowerment.dto.AdminEmpowermentDTO;

@Mapper
public interface AdminEmpowermentMapper {
	// 관리자 정보 호출
	List <AdminEmpowermentDTO> AdminEmpowermentSelect();
}
