package cn.edu.zucc.personplan.comtrol;

import cn.edu.zucc.personplan.itf.IUserManager;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.DbException;

import java.sql.Connection;
import java.sql.SQLException;

public class UserManager implements IUserManager {

    @Override
    public BeanUser reg(String userid, String pwd, String pwd2) throws BaseException {
		if ("".equals(userid)) throw new BusinessException("�û�������Ϊ��");
		if ("".equals(pwd)) throw new BusinessException("���벻��Ϊ��");
		if (!pwd.equals(pwd2)) throw new BusinessException("�������벻һ��");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = "select * from tbl_user where user_id=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1,userid);
			java.sql.ResultSet rs=pst.executeQuery();
			if (rs.next()) throw new BusinessException("�û����Ѵ���");
			rs.close();
			pst.close();
			System.out.println(userid+" "+pwd);
			sql = "insert into tbl_user(user_id, user_pwd, register_time) values(?,?,?)";
			pst = conn.prepareStatement(sql);
			pst.setString(1,userid);
			pst.setString(2,pwd);
			pst.setTimestamp(3,new java.sql.Timestamp(System.currentTimeMillis()+8*60*60*1000));
			System.out.println("insert number="+pst.executeUpdate());
			pst.close();
			System.out.println("ע��ɹ�");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
        return null;
    }


    @Override
    public BeanUser login(String userid, String pwd) throws BaseException {
        BeanUser r = new BeanUser();
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            String sql = "select user_pwd,register_time from tbl_user where user_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, userid);
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                if (!rs.getString(1).equals(pwd)) throw new BusinessException("�������");
            } else throw new BusinessException("�û�������");
            r.setUserId(userid);
            r.setUserPwd(pwd);
            r.setRegisterTime(rs.getTime(2));

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException(e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return r;
    }


    @Override
    public void changePwd(BeanUser user, String oldPwd, String newPwd,
                          String newPwd2) throws BaseException {
		if ("".equals(newPwd)) throw new BusinessException("���벻��Ϊ��");
		if (!newPwd.equals(newPwd2)) throw new BusinessException("�������벻һ��");
		if (!oldPwd.equals(user.getUserPwd())) throw new BusinessException("ԭ�������");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = "update tbl_user set user_pwd = ? where user_id = ?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1,newPwd);
			pst.setString(2,user.getUserId());
			pst.execute();
			pst.close();
			System.out.println("�޸ĳɹ�");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

    }

	public static void main(String[] args) {

	}
}
