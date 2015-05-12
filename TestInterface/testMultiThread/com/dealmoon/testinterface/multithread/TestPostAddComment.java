package com.dealmoon.testinterface.multithread;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月28日 下午3:07:56
 * @description: 给晒单添加评论 多线程测试 多个用户同时添加不带敏感词评论 多个用户同时添加带敏感词评论
 */
public class TestPostAddComment {

	private static final int NUM_THREAD = 10; // 测试线程总数
	private static final int USER_COUNT = 197;// 用户token的数量

	private static List<String> reqJsons;
	private static Map<String, Object> reqData;
	private static List<Integer> postIds; // 最新的post的id的集合
	private static int postId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postaddcomment");
		// 查询最新的post的id
		postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	// *************************多个用户同时评论一个post
	// 不带敏感词评论*********************************
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
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(0)
					+ "}";
			reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
			String response = MyUtils.sendPost(reqJson);
		}
	}

	// *************************多个用户同时评论一个post
	// 带敏感词评论*********************************
	@Test
	// 高并发测试
	public void testMultiThread2() throws Throwable {
		// 生成所有测试线程
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread2();
		}
		// 生成测试线程运行器
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// 运行测试线程
		mttr.runTestRunnables();
	}

	// 测试线程类定义
	private class ChildThread2 extends TestRunnable {

		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(1)
					+ "}";
			reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
			String response = MyUtils.sendPost(reqJson);
		}
	}

	// *************************多个用户同时评论不同post
	// 不带敏感词评论*********************************
	@Test
	// 高并发测试
	public void testMultiThread3() throws Throwable {
		// 生成所有测试线程
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread3();
		}
		// 生成测试线程运行器
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// 运行测试线程
		mttr.runTestRunnables();
	}

	// 测试线程类定义
	private class ChildThread3 extends TestRunnable {

		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(0)
					+ "}";
			reqJson = MyUtils.replaceIdinBackCommand(reqJson,
					postIds.get(new Random().nextInt(postIds.size())));
			String response = MyUtils.sendPost(reqJson);
		}
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		reqData = null;
		postIds = null;
	}
}
