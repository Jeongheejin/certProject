package com.kdn.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kdn.model.biz.MemberDao;
import com.kdn.model.domain.Member;
import com.kdn.model.domain.PageBean;
import com.kdn.util.DBUtil;

@Repository("memberDao")
public class MemberDaoImpl implements MemberDao {
	/* 아이디에 해당하는 회원 정보를 찾음*/
	public Member search(Connection con,String id) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet   rs = null;
		try {
			String sql =" select * from member where id = ? ";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, id);
			rs = stmt.executeQuery();
			if(rs.next()){
				return new Member(rs.getString("id")
							, rs.getString("password")
							, rs.getString("name")
							, rs.getString("email")
							, rs.getString("address")
							, rs.getString("withdraw"));
			}
		}finally{
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		
		return null;
	}

	public List<Member> searchAll(Connection con,PageBean bean) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet   rs = null;
		try {
			StringBuilder sql = new StringBuilder(200);
			String key = bean.getKey();
			String word= bean.getWord();
			int idx = 1;
			sql.append("        select b.*                \n ");
			sql.append("        from(                     \n ");
			sql.append("        	select a.*, rownum ro \n ");
			sql.append("        	from                  \n ");
			sql.append("        	(                     \n ");
			sql.append("        		select *          \n ");
			sql.append("        		from MEMBER       \n ");
			sql.append("        		where 1 = 1       \n ");
			if(key != null && word != null && !key.equals("all") && !word.trim().equals("")){
				if(key.equals("id")){
					sql.append(" and id = ?  \n");
				}
				else if(key.equals("name")){
					sql.append(" and name like '%'||?||'%'  \n");
				}
				else if(key.equals("address")){
					sql.append(" and address like '%'||?||'%'  \n");
				}
			}
			sql.append("        		order by id       \n ");
			sql.append("        	) a                   \n ");
			sql.append("        ) b                       \n ");
			sql.append("        where ro between ? and ?  \n ");
			
			stmt = con.prepareStatement(sql.toString());
			if(key != null && word != null && !key.equals("all") && !word.trim().equals("")){
				stmt.setString(idx++, word);
			}
			
			stmt.setInt(idx++, bean.getStart());
			stmt.setInt(idx++, bean.getEnd());
			rs = stmt.executeQuery();
			ArrayList<Member> list =new ArrayList<Member>(bean.getInterval());
			while(rs.next()){
				list.add(new Member(  rs.getString("id")
									, rs.getString("password")
									, rs.getString("name")
									, rs.getString("email")
									, rs.getString("address")
									, rs.getString("withdraw")));
			}
			return list;
		}finally{
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
	}
	public int getCount(Connection con, PageBean bean) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet   rs = null;
		try {
			StringBuilder sql = new StringBuilder(200);
			String key = bean.getKey();
			String word= bean.getWord();
			sql.append(" select  count(*) cnt                \n");
			sql.append(" from   member                       \n");
			sql.append(" where  1=1                          \n");
			if(key!=null && !key.equals("all") 
			&& word !=null && !word.trim().equals("")){
				if(key.equals("name")){
					sql.append(" and    name like   '%'||?||'%'      \n");
				}else if(key.equals("email")){
					sql.append(" and    email like  '%'||?||'%'      \n");
				}else {
					sql.append(" and  address like  '%'||?||'%'      \n");
				}
			}
			
			stmt = con.prepareStatement(sql.toString());
			if(key!=null && !key.equals("all") 
					&& word !=null && !word.trim().equals("")){
				stmt.setString(1, word);
			}
			rs = stmt.executeQuery();
			if(rs.next()){
				return rs.getInt("cnt");
			}
		}finally{
			DBUtil.close(rs);
			DBUtil.close(stmt);
		}
		return 1;
	}
	public void add(Connection con,Member member) throws SQLException {
		PreparedStatement stmt = null;
		try {
			String sql =" insert into member(id, password,name, email,address) "
					+ " values(?,?,?,?,?)";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, member.getId());
			stmt.setString(2, member.getPassword());
			stmt.setString(3, member.getName());
			stmt.setString(4, member.getEmail());
			stmt.setString(5, member.getAddress());
			stmt.executeUpdate();
		}finally{
			DBUtil.close(stmt);
		}
	}

	public void update(Connection con, Member member) throws SQLException {
		PreparedStatement stmt = null;
		try {
			String sql =" update member set password=?, email=?, address=?"
					  + " where id= ? ";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, member.getPassword());
			stmt.setString(2, member.getEmail());
			stmt.setString(3, member.getAddress());
			stmt.setString(4, member.getId());
			stmt.executeUpdate();
		}finally{
			DBUtil.close(stmt);
		}
	}
	public void update(Connection con, String id) throws SQLException {
		PreparedStatement stmt = null;
		try {
			String sql =" update member set withdraw='Y' where id= ? ";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, id);
			stmt.executeUpdate();
		}finally{
			DBUtil.close(stmt);
		}
	}
}
