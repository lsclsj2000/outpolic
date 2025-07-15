package outpolic.admin.statistics.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.statistics.domain.AdminPortfolioRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminRankingContentsDTO;

@Mapper
public interface AdminRankingMapper {

	
	List<AdminPortfolioRankingContentsDTO> getAdminRankingPoContents();

	List<AdminRankingContentsDTO> getAdminRankingOsContents();
}
