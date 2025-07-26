package outpolic.admin.empowerment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.admin.empowerment.dto.AdminEmpowermentDTO;

@Mapper
public interface AdminEmpowermentMapper {
	// 관리자 정보 호출
	List <AdminEmpowermentDTO> AdminEmpowermentSelect();
	
	List<String> selectPermissionCodesByAdmCd(@Param("admCd") String admCd);
	
	// 권한 INSERT
    int insertPermission(@Param("admCd") String admCd, @Param("arCd") String authCd);

    // 권한 DELETE
    int deletePermission(@Param("admCd") String admCd, @Param("arCd") String authCd);
	
}
