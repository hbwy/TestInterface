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
 * @data:2015��4��28�� ����2:54:53
 * @description: PostCreate ���̲߳��� ����û�ͬʱ�ϴ�1��ͼƬ,2��ͼƬ,3��ͼƬ,4��ͼƬ
 */
public class TestPostCreate {

	private static final int NUM_THREAD = 500; // �����߳�����
	private static final int USER_COUNT = 125;// �û�token������

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postcreate");
	}

	//*************************�ϴ�1��ͼƬ*********************************
	@Test
	// �߲�������
	public void testMultiThread1() throws Throwable {
		// �������в����߳�
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread1();
		}
		// ���ɲ����߳�������
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// ���в����߳�
		mttr.runTestRunnables();
	}

	// �����߳��ඨ��
	private class ChildThread1 extends TestRunnable {

		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(1) + "}";
			System.out.println(reqJson);
			// textMap���ڴ��ı�,fileMap���ڴ�ͼƬ
			Map<String, String> textMap = new HashMap<String, String>();
			Map<String, String> fileMap = new HashMap<String, String>();

			textMap.put("requestData", reqJson);
			String image_path1 = "image/1.jpg";
			fileMap.put("images", image_path1);

			String response = MyUtils.postUpload(textMap, fileMap, reqJson);
			System.out.println(response);
		}
	}

	//*************************�ϴ�2��ͼƬ*********************************	
	@Test
	// �߲�������
	public void testMultiThread2() throws Throwable {
		// �������в����߳�
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread2();
		}
		// ���ɲ����߳�������
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// ���в����߳�
		mttr.runTestRunnables();
	}

	// �����߳��ඨ��
	private class ChildThread2 extends TestRunnable {

		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(2) + "}";
			// textMap���ڴ��ı�,fileMap���ڴ�ͼƬ
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

	//*************************�ϴ�3��ͼƬ*********************************	
	@Test
	// �߲�������
	public void testMultiThread3() throws Throwable {
		// �������в����߳�
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread3();
		}
		// ���ɲ����߳�������
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// ���в����߳�
		mttr.runTestRunnables();
	}

	// �����߳��ඨ��
	private class ChildThread3 extends TestRunnable {

		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(3) + "}";
			// textMap���ڴ��ı�,fileMap���ڴ�ͼƬ
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

	//*************************�ϴ�4��ͼƬ*********************************	
	@Test
	// �߲�������
	public void testMultiThread4() throws Throwable {
		// �������в����߳�
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread4();
		}
		// ���ɲ����߳�������
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// ���в����߳�
		mttr.runTestRunnables();
	}

	// �����߳��ඨ��
	private class ChildThread4 extends TestRunnable {

		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(4) + "}";
			// textMap���ڴ��ı�,fileMap���ڴ�ͼƬ
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
