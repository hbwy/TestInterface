package com.dealmoon.testinterface.multithread;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月28日 下午5:27:02
 * @description: 关注用户 多线程测试 多用户同时关注同一用户/不同用户
 */
public class TestUserFollow {
	private static final int NUM_THREAD = 10; // 测试线程总数
	private static final int USER_COUNT = 197;// 用户token的数量

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("userfollow");
	}

	//*************************多用户同时关注同一用户*********************************
	@Test
	// 高并发测试
	public void testMultiThread1() throws Throwable {
		// 生成所有测试线程
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread1();
		}
		// 生成测试线程运行器
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// 运行测试线程
		mttr.runTestRunnables();
	}

	// 测试线程类定义
	private class ChildThread1 extends TestRunnable {

		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(0) + "}";
			MyUtils.sendPost(reqJson);
		}
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

}
