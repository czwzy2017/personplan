package cn.edu.zucc.personplan.model;

import java.util.Date;

public class BeanUser {
	public static BeanUser currentLoginUser=null;
	private String userId;
	private String userPwd;
	private Date registerTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}
}
