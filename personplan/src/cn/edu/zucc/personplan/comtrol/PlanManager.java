package cn.edu.zucc.personplan.comtrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.DbException;

public class PlanManager implements IPlanManager {

    @Override
    public BeanPlan addPlan(String name) throws BaseException {
        if ("".equals(name)) {
            throw new BusinessException("计划名称不能为空");
        }
        Connection conn = null;

        try {
            conn = DBUtil.getConnection();
            int id = 0;
            String sql = "select COUNT(*) from tbl_plan where user_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, BeanUser.currentLoginUser.getUserId());
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) id = rs.getInt(1);
            rs.close();
            pst.close();
            sql = "insert into tbl_plan(user_id, plan_order, plan_name, create_time, step_count, start_step_count, finished_step_count) values(?,?,?,?,0,0,0)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, BeanUser.currentLoginUser.getUserId());
            pst.setInt(2, id + 1);
            pst.setString(3, name);
            pst.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            pst.execute();
            pst.close();
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
    public List<BeanPlan> loadAll() throws BaseException {
        List<BeanPlan> result = new ArrayList<BeanPlan>();
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "select * from tbl_plan where user_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, BeanUser.currentLoginUser.getUserId());
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                BeanPlan r = new BeanPlan();
                r.setPlanId(rs.getInt(1));
                r.setUserId(rs.getString(2));
                r.setPlanOrder(rs.getInt(3));
                r.setPlanName(rs.getString(4));
                r.setCreatTime(rs.getTime(5));
                r.setStepCount(rs.getInt(6));
                r.setStartStepCount(rs.getInt(7));
                r.setFinishedStepCount(rs.getInt(8));
                result.add(r);
            }
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
        return result;
    }

    @Override
    public void deletePlan(BeanPlan plan) throws BaseException {
        Connection conn = null;
        if (plan.getStepCount() > 0) throw new BusinessException("存在步骤不允许删除");
        try {
            conn = DBUtil.getConnection();
            String sql = "delete from tbl_plan where plan_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlanId());
            pst.execute();
            pst.close();
            sql = "update tbl_plan set plan_order=plan_order-1 where plan_order>?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlanOrder());
            pst.execute();
            pst.close();
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

}
