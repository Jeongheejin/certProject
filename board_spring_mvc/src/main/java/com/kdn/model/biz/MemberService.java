package com.kdn.model.biz;

import java.util.List;

import com.kdn.model.domain.Member;
import com.kdn.model.domain.PageBean;

public interface MemberService {

	public Member search(String id);
	public List<Member> searchAll(PageBean bean);
	public boolean login(String id, String passwrod);
	public void withdraw(String id);
	public void update(Member member);
	public void add(Member member);

}