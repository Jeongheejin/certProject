package com.kdn.model.biz;

import java.util.List;

import com.kdn.model.domain.Board;
import com.kdn.model.domain.PageBean;

public interface BoardService {
	public void update(Board board);
	public void remove(int no);
	public Board search(int no);
	public void add(Board board, String dir);
	public List<Board> searchAll(PageBean bean);

}