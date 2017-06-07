package com.kdn.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kdn.model.biz.BoardDao;
import com.kdn.model.domain.Board;
import com.kdn.model.domain.FileBean;
import com.kdn.model.domain.PageBean;
import com.kdn.util.DBUtil;

@Repository("boardDao")
public class BoardDaoImpl implements BoardDao {

	@Override
	public void add(Connection con, Board board) throws SQLException {
		PreparedStatement  stmt = null;
		try {
			String sql = " insert into board(NO,ID,TITLE,REGDATE,CONTENTS) "
					   + " values(?,?,?,sysdate,?) ";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, board.getNo());
			stmt.setString(2, board.getId());
			stmt.setString(3, board.getTitle());
			stmt.setString(4, board.getContents());
			
			stmt.executeUpdate();
		} finally {
			DBUtil.close(stmt);
		}
	}
	@Override
	public void update(Connection con, Board board) throws SQLException {
		PreparedStatement  stmt = null;
		try {
			String sql = " update board set ID=?,TITLE=?,REGDATE=sysdate,CONTENTS=? "
					   + " where no =?  ";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, board.getId());
			stmt.setString(2, board.getTitle());
			stmt.setString(3, board.getContents());
			stmt.setInt(4, board.getNo());
			
			stmt.executeUpdate();
		} finally {
			DBUtil.close(stmt);
		}
	}

	@Override
	public void remove(Connection con, int no) throws SQLException {
		PreparedStatement  stmt = null;
		try {
			String sql = " delete from board  where no =?  ";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, no);
			stmt.executeUpdate();
		} finally {
			DBUtil.close(stmt);
		}
	}

	@Override
	public Board search(Connection con, int no) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder(100);
			sql.append(" select b.no no, id, title, contents ,regdate        \n");
		    sql.append("    , bf.no fileno, rfilename, sfilename             \n");
			sql.append(" from   boardfile bf,                                \n");
			sql.append("       (select  no, id, title, contents              \n");
			sql.append("               , to_char(regdate,'yy-mm-dd') regdate \n");
			sql.append("        from    board                                \n");
			sql.append("        where   no = ? ) b                           \n");
			sql.append(" where b.no = bf.bno(+)                              \n");
			
			stmt = con.prepareStatement(sql.toString());
			stmt.setInt(1, no);
			rs = stmt.executeQuery();
			boolean isFirst = true;
			Board  board = null;
			LinkedList<FileBean> list = new LinkedList<FileBean>(); 
			while(rs.next()){
				if(isFirst){
					board = new Board(rs.getInt("no")
									, rs.getString("id")
									, rs.getString("title")
									, rs.getString("regdate")
									, rs.getString("contents"));
					isFirst = false;
				}
				String rfilename = rs.getString("rfilename");
				String sfilename = rs.getString("sfilename");
				if(rfilename!=null){
					list.add(new FileBean(rfilename, sfilename));
				}
			}
			if(board!=null){
				board.setFiles(list);
			}
			return board;
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
	}

	@Override
	public List<Board> searchAll(Connection con, PageBean bean) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String key = bean.getKey();
			String word= bean.getWord();
			//검색 조건 :  id, title, contents
			StringBuilder sql = new StringBuilder(200);
			sql.append(" select  a.*                             \n");
			sql.append(" from   ( select rownum ro, b.*          \n");
			sql.append(" 		 from   ( select  no, id, title  \n");
			sql.append(" 		          from board             \n");
			sql.append(" 		          where 1=1              \n");
			if(key!=null && !key.equals("all") && word != null && !word.trim().equals("")){
				if(key.equals("id")){
					sql.append(" and  id = ? ");
				}else if(key.equals("title")){
					sql.append(" and  title like '%'||?||'%' ");
				}else if(key.equals("contents")){
					sql.append(" and  contents like '%'||?||'%' ");
				}
			}
			sql.append(" 		          order by  no desc      \n");
			sql.append(" 		          ) b                    \n");
			sql.append("        ) a                              \n");
			sql.append(" where  ro between ? and ?               \n");
			stmt = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(key!=null && !key.equals("all") && word != null && !word.trim().equals("")){
				stmt.setString(idx++, word);
			}
			stmt.setInt(idx++, bean.getStart());
			stmt.setInt(idx++, bean.getEnd());
			rs = stmt.executeQuery();
			
			ArrayList<Board> list =  new ArrayList<Board>(bean.getInterval());
			while(rs.next()){
				list.add(new Board(   rs.getInt("no")
									, rs.getString("id")
									, rs.getString("title")
									));
			}
			return list;
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
	}

	@Override  
	public int getCount(Connection con, PageBean bean) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String key = bean.getKey();
			String word= bean.getWord();
			//검색 조건 :  id, title, contents
			StringBuilder sql = new StringBuilder(100);
			sql.append(" select count(*) cnt from board  where 1=1 ");
			if(key!=null && !key.equals("all") && word != null && !word.trim().equals("")){
				if(key.equals("id")){
					sql.append(" and  id = ? ");
				}else if(key.equals("title")){
					sql.append(" and  title like '%'||?||'%' ");
				}else if(key.equals("contents")){
					sql.append(" and  contents like '%'||?||'%' ");
				}
			}
			stmt = con.prepareStatement(sql.toString());
			if(key!=null && !key.equals("all") && word != null && !word.trim().equals("")){
				stmt.setString(1, word);
			}
			rs = stmt.executeQuery();
			if(rs.next()){
				return rs.getInt("cnt");
			}
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return 0;
	}

	@Override
	public int getBoardNo(Connection con) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql =" select board_no.nextval from dual ";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
		} finally {
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return 0;
	}

	@Override
	public void addFiles(Connection con, List<FileBean> files, int bno) throws SQLException {
		PreparedStatement  stmt = null;
		try {
			String sql = " insert into boardfile(NO,RFILENAME,SFILENAME,BNO) "
					   + " values(boardfile_no.nextval,?,?,?) ";
			stmt = con.prepareStatement(sql);
			for (FileBean fileBean : files) {
				stmt.setString(1, fileBean.getFilename());
				stmt.setString(2, fileBean.getSfilename());
				stmt.setInt(3, bno);
				stmt.addBatch();
			}
			stmt.executeBatch();
		} finally {
			DBUtil.close(stmt);
		}
	}

	@Override
	public void removeFiles(Connection con, int bno) throws SQLException {
		PreparedStatement  stmt = null;
		try {
			String sql = " delete from boardfile where bno = ? ";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, bno);
			stmt.executeUpdate();
		} finally {
			DBUtil.close(stmt);
		}
	}
}



