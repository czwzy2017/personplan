package cn.edu.zucc.personplan.comtrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IStepManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanStep;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.DbException;

import javax.swing.*;

public class StepManager implements IStepManager {

    @Override
    public void add(BeanPlan plan, String name, String planstartdate,
                    String planfinishdate) throws BaseException {
        if ("".equals(name)) {
            throw new BusinessException("步骤名称不能为空");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Timestamp startTime = null;
        Timestamp finishTime = null;
        try {
            startTime = new Timestamp(df.parse(planstartdate).getTime());
            finishTime = new Timestamp(df.parse(planfinishdate).getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "请使用yyyy-MM-dd HH:mm格式", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        if (finishTime.before(startTime)) throw new BusinessException("开始时间不能晚于完成时间");
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "insert into tbl_step(plan_id, step_order, step_name, plan_begin_time, plan_end_time) values (?,?,?,?,?)";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlanId());
            pst.setInt(2, plan.getStepCount() + 1);
            pst.setString(3, name);
            pst.setTimestamp(4, startTime);
            pst.setTimestamp(5, finishTime);
            pst.execute();
            pst.close();
            sql = "update tbl_plan set step_count=step_count+1 where plan_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlanId());
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

    @Override
    public List<BeanStep> loadSteps(BeanPlan plan) throws BaseException {
        List<BeanStep> result = new ArrayList<BeanStep>();
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "select * from tbl_step where plan_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlanId());
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                BeanStep r = new BeanStep();
                r.setStepId(rs.getInt(1));
                r.setPlanId(rs.getInt(2));
                r.setStepOrder(rs.getInt(3));
                r.setStepName(rs.getString(4));
                r.setPlanBeginTime(rs.getTimestamp(5));
                r.setPlanEndTime(rs.getTimestamp(6));
                r.setRealBeginTime(rs.getObject(7) == null ? null : rs.getTimestamp(7));
                r.setRealEndTime(rs.getObject(8) == null ? null : rs.getTimestamp(8));
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
    public void deleteStep(BeanStep step) throws BaseException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "delete from tbl_step where step_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStepId());
            pst.execute();
            pst.close();
            sql = "update tbl_step set step_order=step_order-1 where step_order>?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStepOrder());
            pst.execute();
            pst.close();
            sql = "update tbl_plan set step_count=step_count-1 where plan_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getPlanId());
            pst.execute();
            pst.close();
            if (step.getRealBeginTime() != null) {
                sql = "update tbl_plan set start_step_count=start_step_count-1 where plan_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, step.getPlanId());
                pst.execute();
                pst.close();
            }
            if (step.getRealEndTime() != null) {
                sql = "update tbl_plan set finished_step_count=finished_step_count-1 where plan_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, step.getPlanId());
                pst.execute();
                pst.close();
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
    }

    @Override
    public void startStep(BeanStep step) throws BaseException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "update tbl_step set real_begin_time=?,real_end_time=null where step_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            pst.setInt(2, step.getStepId());
            pst.execute();
            pst.close();
            if (step.getRealBeginTime() == null) {
                sql = "update tbl_plan set start_step_count=start_step_count+1 where plan_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, step.getPlanId());
                pst.execute();
                pst.close();
            }
            if (step.getRealEndTime() == null) {
                sql = "update tbl_plan set finished_step_count=finished_step_count-1 where plan_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, step.getPlanId());
                pst.execute();
                pst.close();
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
    }

    @Override
    public void finishStep(BeanStep step) throws BaseException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            if (step.getRealBeginTime() == null) throw new BusinessException("步骤还未开始");
            String sql = "update tbl_step set real_end_time=? where step_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            pst.setInt(2, step.getStepId());
            pst.execute();
            pst.close();
            if (step.getRealEndTime() == null) {
                sql = "update tbl_plan set finished_step_count=finished_step_count+1 where plan_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, step.getPlanId());
                pst.execute();
                pst.close();
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
    }

    @Override
    public void moveUp(BeanStep step) throws BaseException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            if (step.getStepOrder() == 1) throw new BusinessException("已经是第一步");
            String sql = "update tbl_step set step_order=step_order+1 where step_order=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStepOrder() - 1);
            pst.execute();
            pst.close();
            sql = "update tbl_step set step_order=step_order-1 where step_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStepId());
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

    @Override
    public void moveDown(BeanStep step) throws BaseException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "select count(*) from tbl_step";
            java.sql.Statement st = conn.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sql);
            int orderSum = 0;
            int order = 0;
            if (rs.next()) {
                orderSum = rs.getInt(1);
            }
            sql = "select step_order from tbl_step where step_id=?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStepId());
            rs = pst.executeQuery();
            if (rs.next()) {
                order = rs.getInt(1);
                if (orderSum == order) throw new BusinessException("已经是最后一步");
            }
            pst.close();
            sql = "update tbl_step set step_order=step_order-1 where step_order=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, order + 1);
            pst.execute();
            pst.close();
            sql = "update tbl_step set step_order=step_order+1 where step_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStepId());
            pst.execute();
            pst.close();
            rs.close();

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
