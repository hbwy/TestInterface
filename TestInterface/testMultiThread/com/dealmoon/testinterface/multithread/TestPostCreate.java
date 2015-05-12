package com.dealmoon.testinterface.multithread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月28日 下午2:54:53
 * @description: PostCreate 多线程测试 多个用户同时上传1张图片,2张图片,3张图片,4张图片
 */
public class TestPostCreate {

	private static final int NUM_THREAD = 500; // 测试线程总数
	private static final int USER_COUNT = 125;// 用户token的数量

	private static List<String> reqJsons;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postcreate");
	}

	//*************************上传1张图片*********************************
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
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(1) + "}";
			System.out.println(reqJson);
			// textMap用于存文本,fileMap用于存图片
			Map<String, String> textMap = new HashMap<String, String>();
			Map<String, String> fileMap = new HashMap<String, String>();

			textMap.put("requestData", reqJson);
			String image_path1 = "image/1.jpg";
			fileMap.put("images", image_path1);

			String response = MyUtils.postUpload(textMap, fileMap, reqJson);
			System.out.println(response);
		}
	}

	//*************************上传2张图片*********************************	
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
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(2) + "}";
			// textMap用于存文本,fileMap用于存图片
			Map<String, String> textMap = new HashMap<String, String>();
			Map<String, String> fileMap = new HashMap<String, String>();

			textMap.put("requestData", reqJson);
			String image_path1 = "image/1.jpg";
			String image_path2 = "image/2.jpg";
			fileMap.put("images", image_path1);
			fileMap.put("images", image_path2);

			String response = MyUtils.postUpload(textMap, fileMap, reqJson);
		}
	}

	//*************************上传3张图片*********************************	
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
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(3) + "}";
			// textMap用于存文本,fileMap用于存图片
			Map<String, String> textMap = new HashMap<String, String>();
			Map<String, String> fileMap = new HashMap<String, String>();

			textMap.put("requestData", reqJson);
			String image_path1 = "image/1.jpg";
			String image_path2 = "image/2.jpg";
			String image_path3 = "image/3.jpg";

			fileMap.put("images", image_path1);
			fileMap.put("images", image_path2);
			fileMap.put("images", image_path3);

			String response = MyUtils.postUpload(textMap, fileMap, reqJson);
			System.out.println(response);
		}
	}

	//*************************上传4张图片*********************************	
	@Test
	// 高并发测试
	public void testMultiThread4() throws Throwable {
		// 生成所有测试线程
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread4();
		}
		// 生成测试线程运行器
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// 运行测试线程
		mttr.runTestRunnables();
	}

	// 测试线程类定义
	private class ChildThread4 extends TestRunnable {

		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(4) + "}";
			// textMap用于存文本,fileMap用于存图片
			Map<String, String> textMap = new HashMap<String, String>();
			Map<String, String> fileMap = new HashMap<String, String>();

			textMap.put("requestData", reqJson);
			String image_path1 = "image/1.jpg";
			String image_path2 = "image/2.jpg";
			String image_path3 = "image/3.jpg";
			String image_path4 = "image/4.jpg";
			fileMap.put("images", image_path1);
			fileMap.put("images", image_path2);
			fileMap.put("images", image_path3);
			fileMap.put("images", image_path4);

			String response = MyUtils.postUpload(textMap, fileMap, reqJson);
			System.out.println(response);
		}
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}
}
