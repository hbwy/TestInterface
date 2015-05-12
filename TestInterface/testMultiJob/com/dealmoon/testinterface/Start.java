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
		System.out.println("------------- ��ʼ�� ��� Scheduler ���� -------------");

		// ��� Scheduler ����   
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler scheduler = sf.getScheduler();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		// �ڵ�ǰʱ��0�������  
		Date startTime = DateBuilder.nextGivenSecondDate(null, 1);
		//��ǰʱ��ļ���10����  
		Date endTime = DateBuilder.nextGivenMinuteDate(null, 10);

		// ����һ�� job ���󲢰�����д��  MyJob ��   
		// ����ִ�е����񲢲���Job�ӿڵ�ʵ���������÷���ķ�ʽʵ������һ��JobDetailʵ��    
		JobDetail job = newJob(MyJob.class).withIdentity("job1", "group1").build();

		System.out.println("��ʼʱ��: " + sdf.format(startTime) + ",����ʱ��: " + sdf.format(endTime));

		simpleSchedule();
		simpleSchedule();
		// ����һ����������startAt��������������Ӧ����ʼ��ʱ�� .����һ����������ִ��  
		SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(startTime)
				.endAt(endTime).withSchedule(SimpleScheduleBuilder.repeatSecondlyForever()).build();

		// ע�Ტ���е���    
		Date ft = scheduler.scheduleJob(job, trigger);

		// ����  
		scheduler.start();
		System.out.println(" ----------------- ��������Ѿ����� -----------------");

		try {
			//10����  
			Thread.sleep(600L * 1000L);

		} catch (Exception e) {
		}

		//������ֹͣ  
		scheduler.shutdown(true);
		System.out.println(" ----------------- ������Ƚ��� -----------------");

		SchedulerMetaData metaData = scheduler.getMetaData();
		System.out.println("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
	}

	public static void main(String[] args) throws Exception {
		Start start = new Start();
		start.run();
	}
}
