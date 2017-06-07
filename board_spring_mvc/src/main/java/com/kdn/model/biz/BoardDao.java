package com.kdn.model.biz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.kdn.model.domain.Board;
import com.kdn.model.domain.FileBean;
import com.kdn.model.domain.PageBean;

public interface BoardDao {
	public void 	add(Connection con, Board board) 	throws SQLException;
	public void 	update(Connection con, Board board) throws SQLException;
	public void 	remove(Connection con, int no) 		throws SQLException;
	public Board 	search(Connection con, int no) 		throws SQLException;
	public List<Board> searchAll(Connection con, PageBean bean) throws SQLException;
	public int 		getCount(Connection con, PageBean bean) throws SQLException;
	public int 		getBoardNo(Connection con) 			throws SQLException;
	public void 	addFiles(Connection con, List<FileBean> files, int bno) throws SQLException;
	public void 	removeFiles(Connection con, int bno) throws SQLException;
}





