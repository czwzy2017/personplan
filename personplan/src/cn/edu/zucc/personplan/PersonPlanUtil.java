package cn.edu.zucc.personplan;

import cn.edu.zucc.personplan.comtrol.PlanManager;
import cn.edu.zucc.personplan.comtrol.StepManager;
import cn.edu.zucc.personplan.comtrol.UserManager;
import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.itf.IStepManager;
import cn.edu.zucc.personplan.itf.IUserManager;

public class PersonPlanUtil {
	public static IPlanManager planManager=new PlanManager();//��Ҫ����������Ƶ�ʵ����
	public static IStepManager stepManager=new StepManager();//��Ҫ����������Ƶ�ʵ����
	public static IUserManager userManager=new UserManager();//��Ҫ����������Ƶ�ʵ����
	
}
