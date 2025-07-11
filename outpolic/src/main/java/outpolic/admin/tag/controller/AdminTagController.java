package outpolic.admin.tag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminTagController {

	@GetMapping("/addTag")
	public String addTagView() {
		return "admin/tag/adminTagAddView";
	}
	
	@GetMapping("/deleteTag")
	public String deleteTagView() {
		return "admin/tag/adminTagDeleteView";
	}
	
	@GetMapping("/selectTag")
	public String selectTagView() {
		return "admin/tag/adminTagSelectView";
	}
	
	@GetMapping("/editTag")
	public String editTagView() {
		return "admin/tag/adminTagEditView";
	}
}
