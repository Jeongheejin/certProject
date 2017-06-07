package com.kdn.controller;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kdn.model.biz.BoardService;
import com.kdn.model.domain.Board;
import com.kdn.model.domain.PageBean;

@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	@RequestMapping(value = "insertBoardForm.do", method = RequestMethod.GET)
	public String insertBoardForm(Model model){
		model.addAttribute("content", "board/insertBoard.jsp");
		return "index";
	}
	
	@RequestMapping(value = "insertBoard.do", method = RequestMethod.POST)
	public String insertBoard(Board board, HttpServletRequest request){
		String dir = request.getRealPath("upload/");
		boardService.add(board, dir); //게시판 목록? 과 경로를 넣어서 서비스 하면 된다.
		
		//listBoard 이 메소드 콜하면 안된다!!!!!!!!!!!!!!!!!! 무조건 redirect 해야함.
		//(insert, delete, update 하려면 바로 저 메서드 콜하면 안되고 무조건 redirect 해야함. => 요청 정보가 살아있어서
		return "redirect:listBoard.do";
	}
	
	@RequestMapping(value = "listBoard.do", method = RequestMethod.GET)
	public String listBoard(PageBean bean, Model model){
		
		List<Board> list = boardService.searchAll(bean);
		model.addAttribute("list", list);
		model.addAttribute("content", "board/listBoard.jsp");
		return "index";
	}
	
	@RequestMapping(value = "searchBoard.do", method = RequestMethod.GET)
	public String searchBoard(int no, Model model){
		model.addAttribute("board", boardService.search(no));
		model.addAttribute("content", "board/searchBoard.jsp");
		return "index";
	}
	

	
}




