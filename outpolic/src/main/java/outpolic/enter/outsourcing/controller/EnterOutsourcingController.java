/*
package outpolic.enter.outsourcing.controller;


import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;




@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {
	
	 @GetMapping("/list")
	 public String showPortfolioListView() { 
		 return "enter/outsourcing/outsourcingListView";
		 
	  }
	 /*
	 @GetMapping("/listData")
	 @ResponseBody
	 public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData(HttpSession session){
		 String currentEntCd = "EI_C00001";
		 return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(currentEntCd));
		 
	 }
	 *
	 */
/*
	 @GetMapping("/add")
	 public String showAddOutsourcingForm(Model model, HttpSession session) {
		 model.addAttribute("entCd","EI_C00001");
		 model.addAttribute("mbrCd", "MB_C0000036");
		 return "enter/outsourcing/addOutsourcingListView";
	 }
	 /*
	 @PostMapping("/add-ajax")
	 @ResponseBody
	 public ResponseEntity<Map<String, Object>> addPortfolioAjax(@ModelAttribute EnterPortfolio portfolio,
			 													 @RequestParam("portfolioFiles") List<MultipartFile> outsourcingFiles,
			 													 @RequestParam(value="categoryCodes",required=false) List<String> categoryCodes,
			 													 @RequestParam(value="tags", required=false) String tags) {
		
		 
 		*/											 
			 												



