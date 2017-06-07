package com.kdn.model.biz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kdn.model.dao.MemberDaoImpl;
import com.kdn.model.domain.Member;
import com.kdn.model.domain.UpdateException;
import com.kdn.model.domain.PageBean;
import com.kdn.util.DBUtil;

@Service("memberService")
public class MemberServiceImpl implements MemberService {
	@Autowired
	@Qualifier("memberDao")
	private MemberDao dao;
	public Member search(String id) {
		Connection con = null;
		Member member = null;
		try {
			con = DBUtil.getConnection();
			member = dao.search(con, id);
		} catch(SQLException  s){
			throw new UpdateException("DB 서버 오류");
		} finally {
			DBUtil.close(con);
		}
		if(member == null){
			throw new UpdateException("아이디에 해당하는 회원을 찾을 수 없습니다.");
		}else{
			return member;
		}
		
	}

	public List<Member> searchAll(PageBean bean) {
		Connection con = null;
		List<Member> members= null;
		try {
			con = DBUtil.getConnection();
			int count = dao.getCount(con, bean);
			return dao.searchAll(con,bean);
		} catch(SQLException  s){
			throw new UpdateException("DB 서버 오류");
		} finally {
			DBUtil.close(con);
		}
	}

	public boolean login(String id, String passwrod) {
		Connection con = null;
		Member member = null;
		try {
			con = DBUtil.getConnection();
			member = dao.search(con, id);
		} catch(SQLException  s){
			throw new UpdateException("DB 서버 오류");
		} finally {
			DBUtil.close(con);
		}
		if(member == null){
			throw new UpdateException("해당하는 아이디는 존재하지 않습니다.");
		}
		if(passwrod ==null || !passwrod.equals(member.getPassword())){
			throw new UpdateException("비밀번호가 맞지 않습니다.");
		}
		if(member.getWithdraw().equals("Y")){
			throw new UpdateException("탈퇴한 회원 아이디입니다.");
		}
		return true;
	}

	public void withdraw(String id) {
		Connection con = null;
		Member member = null;
		try {
			con = DBUtil.getConnection();
			member = dao.search(con, id);
			if(member == null){
				throw new UpdateException("아이디에 해당하는 회원이 없어 탈퇴 처리할 수 없습니다.");
			}else{
				dao.update(con, id);
			}
		} catch(SQLException  s){
			throw new UpdateException("DB 서버 오류");
		} finally {
			DBUtil.close(con);
		}
	}


	public void update(Member member) {
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			Member find= dao.search(con, member.getId());
			if(find == null){
				throw new UpdateException("아이디에 해당하는 회원이 없어 수정할 수 없습니다.");
			}else{
				dao.update(con, member);
			}
		} catch(SQLException  s){
			throw new UpdateException("DB 서버 오류");
		} finally {
			DBUtil.close(con);
		}
	}
	
	public void add(Member member) {
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			Member find= dao.search(con, member.getId());
			if(find != null){
				throw new UpdateException("이미 등록된 아이디 입니다.");
			}else{
				dao.add(con, member);
			}
		} catch(SQLException  s){
			throw new UpdateException("DB 서버 오류");
		} finally {
			DBUtil.close(con);
		}
	}
	
}
