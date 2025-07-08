package outpolic.user.ranking.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import outpolic.user.ranking.domain.UserRankingContentsDTO;
import outpolic.user.ranking.service.UserRankingService;

@Controller
@RequiredArgsConstructor
public class UserRankingContoller {

	private final UserRankingService userRankingService;

}
