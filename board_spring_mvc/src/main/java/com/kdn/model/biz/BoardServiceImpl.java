package com.kdn.model.biz;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kdn.model.domain.Board;
import com.kdn.model.domain.FileBean;
import com.kdn.model.domain.PageBean;
import com.kdn.model.domain.UpdateException;
import com.kdn.util.DBUtil;
import com.kdn.util.PageUtility;

@Service("boardService")
public class BoardServiceImpl implements BoardService {
	@Autowired
	@Qualifier("boardDao")
	private BoardDao dao;
	@Override
	public void add(Board board, String dir) {
		Connection con = null;
		File[] files = null;	//실제 저장할 파일정보
		int size = 0;
		
		try {
			con = DBUtil.getConnection();
			
			int bno = dao.getBoardNo(con);	//게시글 번호 추출
			board.setNo(bno);
			dao.add(con, board); //DB에 넣음
			//---- 여기까지 게시글 처리------			
			
			MultipartFile[] fileup = board.getFileup(); //추출된 fileup 정보
			
			if(fileup != null){	//사용자가 파일을 올린경우
				size = fileup.length;	//업로드한 파일 개수
				files = new File[size];					
				ArrayList<FileBean> fileInfos = new ArrayList<FileBean>(size);	//디비에 저장할 수 있는 객체?
				
				String rfilename = null;
				String sfilename = null;
				int index = 0;
				for (MultipartFile file : fileup) {	//사용자가 올린 파일들을 돌려서 이름 설정
					rfilename = file.getOriginalFilename();
					sfilename = String.format("%d%s", System.currentTimeMillis(), rfilename);
					
					fileInfos.add(new FileBean(rfilename, sfilename));	//디비에 저장하기 위한 마련.
					//---------- 여기까지는 파일 정보 만들어준 것---------------
					
					String fileName = String.format("%s/%s", dir, sfilename);	//실제 경로에 저장하기 위함
					files[index] = new File(fileName);
					file.transferTo(files[index++]);	// 실제 경로에 저장이 되도록 .... (transferTo : 원하는 위치에 저장해줌.) 
				}

				dao.addFiles(con, fileInfos, bno);	//파일 정보를 진짜로 디비에 저장 

			}
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			DBUtil.rollback(con);
			
			//롤백시 저장한 파일이 있다면 삭제
			if(files != null){
				for (File file : files) {
					if(file != null && file.exists()){ //exists() : 해당 파일이 지정한 경로에 존재하면
						file.delete(); 
					}
				}
			}
			
			throw new UpdateException("게시글 작성 중 오류 발생");
		} finally {
			DBUtil.close(con);
		}
	}

	@Override
	public void update(Board board) {
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			dao.update(con, board);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UpdateException("게시글 수정 중 오류 발생");
		} finally {
			DBUtil.close(con);
		}
	}

	@Override
	public void remove(int no) {
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			dao.removeFiles(con, no);
			dao.remove(con, no);
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			DBUtil.rollback(con);
			throw new UpdateException("게시글 삭제 중 오류 발생");
		} finally {
			DBUtil.close(con);
		}
	}
	@Override
	public Board search(int no) {
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			return dao.search(con, no);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UpdateException("게시글 검색 중 오류 발생");
		} finally {
			DBUtil.close(con);
		}
	}
	@Override
	public List<Board> searchAll(PageBean bean) {
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			int total = dao.getCount(con, bean);
			
			PageUtility bar = 
			  new PageUtility(bean.getInterval()
					  		, total
					  		, bean.getPageNo()
					  		, "images/");
			bean.setPagelink(bar.getPageBar());
			
			return dao.searchAll(con, bean);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UpdateException("게시글 검색 중 오류 발생");
		} finally {
			DBUtil.close(con);
		}
	}
}
