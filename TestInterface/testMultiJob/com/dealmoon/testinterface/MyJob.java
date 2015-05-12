package com.dealmoon.testinterface;

import java.io.File;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import com.dealmoon.testinterface.app.*;

/**
 * @author: WY
 * @data:2015年5月4日 上午11:24:17
 * @description: 定义作业
 */
public class MyJob implements Job {

	@Override
	public void execute(JobExecutionContext context) {
		System.out.println("--------------------------job begin--------------------- " + new Date());
		exeBuildFile("build.xml", Project.MSG_INFO);
		System.out.println(Thread.currentThread().getName());
		/*System.out.println("hhhhhh");
		try {
			Thread.sleep(100000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		//runTestCase();
		System.out.println("--------------------------job end--------------------- " + new Date());
	}

	/**
	 * 执行build.xml文件
	 * 
	 * @param build build.xml文件
	 * @param level 日志输出级别(Project.MSG_INFO)
	 * */
	private void exeBuildFile(String build, int level) {
		File buildFile = new File(build);
		Project p = new Project();
		//添加日志输出          
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		//输出信息级别  
		consoleLogger.setMessageOutputLevel(level);
		p.addBuildListener(consoleLogger);

		try {
			p.fireBuildStarted();
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			helper.parse(p, buildFile);
			p.executeTarget(p.getDefaultTarget());
			p.fireBuildFinished(null);
		} catch (BuildException e) {
			p.fireBuildFinished(e);
		}
	}

	/*private void runTestCase() {
		Result result = JUnitCore.runClasses(TestUserProfile.class,TestCategoryList.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println("运行结果："+result.wasSuccessful());
	}*/

}
