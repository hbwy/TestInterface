package com.dealmoon.testinterface;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class Start {
	public void run() throws Exception {
		System.out.println("------------- 初始化 获得 Scheduler 对象 -------------");

		// 获得 Scheduler 对象   
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler scheduler = sf.getScheduler();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		// 在当前时间0秒后运行  
		Date startTime = DateBuilder.nextGivenSecondDate(null, 1);
		//当前时间的加上10分钟  
		Date endTime = DateBuilder.nextGivenMinuteDate(null, 10);

		// 定义一个 job 对象并绑定我们写的  MyJob 类   
		// 真正执行的任务并不是Job接口的实例，而是用反射的方式实例化的一个JobDetail实例    
		JobDetail job = newJob(MyJob.class).withIdentity("job1", "group1").build();

		System.out.println("开始时间: " + sdf.format(startTime) + ",结束时间: " + sdf.format(endTime));

		simpleSchedule();
		simpleSchedule();
		// 定义一个触发器，startAt方法定义了任务应当开始的时间 .即下一个整数分钟执行  
		SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(startTime)
				.endAt(endTime).withSchedule(SimpleScheduleBuilder.repeatSecondlyForever()).build();

		// 注册并进行调度    
		Date ft = scheduler.scheduleJob(job, trigger);

		// 启动  
		scheduler.start();
		System.out.println(" ----------------- 任务调度已经启动 -----------------");

		try {
			//10分钟  
			Thread.sleep(600L * 1000L);

		} catch (Exception e) {
		}

		//调度器停止  
		scheduler.shutdown(true);
		System.out.println(" ----------------- 任务调度结束 -----------------");

		SchedulerMetaData metaData = scheduler.getMetaData();
		System.out.println("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
	}

	public static void main(String[] args) throws Exception {
		Start start = new Start();
		start.run();
	}
}
