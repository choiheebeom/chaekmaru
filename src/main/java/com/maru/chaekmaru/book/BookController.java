package com.maru.chaekmaru.book;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/book")
public class BookController {

	@Autowired
	BookService bookService;

	String nextPage = "";

	@GetMapping("/list")
	public String list(Model model, @RequestParam(required = false, value = "sort", defaultValue = "new") String sort,
			@RequestParam(required = false, value = "page", defaultValue = "1") String page,
			@RequestParam(required = false, value = "search", defaultValue = "") String search) {

		int nowPage = Integer.parseInt(page);
		int pageItem = 20;
		
		log.info(nowPage + "/" + search + "/" + sort);

		ArrayList<BookDto> items = new ArrayList<>();
		items = bookService.setList(sort, pageItem, nowPage, search);
		
		ArrayList<ListPageDto> listPageDtos = bookService.setPaging(pageItem, nowPage, search);

		model.addAttribute("sort", sort);
		model.addAttribute("nowPage", nowPage);
		model.addAttribute("allPage", nowPage);
		model.addAttribute("listPageDtos", listPageDtos);
		model.addAttribute("search", search);
		model.addAttribute("items", items);

		nextPage = "/book/list";

		return nextPage;

	}

}