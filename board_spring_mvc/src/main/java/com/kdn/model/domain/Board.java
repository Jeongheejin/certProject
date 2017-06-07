package com.kdn.model.domain;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class Board implements Serializable{
	private int no;
	private String id;
	private String title;
	private String regdate;
	private String contents;
	private MultipartFile[] fileup;		//업로드 된 파일 정보(MultipartFile resolver 가 분리해줄 것이다)
	private List<FileBean>  files;    //DB에 저장하는 file 정보
	public Board(){}
	public Board(int no, String id, String title) {
		this.no = no;
		this.id = id;
		this.title = title;
	}
	public Board(int no, String id, String title, String regdate, String contents) {
		this.no = no;
		this.id = id;
		this.title = title;
		this.regdate = regdate;
		this.contents = contents;
	}
	public Board(int no, String id, String title, String regdate, String contents, List<FileBean> files) {
		this.no = no;
		this.id = id;
		this.title = title;
		this.regdate = regdate;
		this.contents = contents;
		this.files = files;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Board [no=").append(no).append(", id=").append(id)
				.append(", title=").append(title)
				.append(", regdate=").append(regdate).append(", contents=")
				.append(contents).append(", files=")
				.append(files).append("]");
		return builder.toString();
	}
	
	
	public MultipartFile[] getFileup() {
		return fileup;
	}
	public void setFileup(MultipartFile[] fileup) {
		this.fileup = fileup;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public List<FileBean> getFiles() {
		return files;
	}
	public void setFiles(List<FileBean> files) {
		this.files = files;
	}
	
	
}
