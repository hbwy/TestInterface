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
 * @data:2015��4��28�� ����3:07:56
 * @description: ��ɹ��������� ���̲߳��� ����û�ͬʱ��Ӳ������д����� ����û�ͬʱ��Ӵ����д�����
 */
public class TestPostAddComment {

	private static final int NUM_THREAD = 10; // �����߳�����
	private static final int USER_COUNT = 197;// �û�token������

	private static List<String> reqJsons;
	private static Map<String, Object> reqData;
	private static List<Integer> postIds; // ���µ�post��id�ļ���
	private static int postId;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("postaddcomment");
		// ��ѯ���µ�post��id
		postIds = MyUtils.getPostList("new", 1, 20);
		postId = postIds.get(new Random().nextInt(postIds.size()));
	}

	// *************************����û�ͬʱ����һ��post
	// �������д�����*********************************
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
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(0)
					+ "}";
			reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
			String response = MyUtils.sendPost(reqJson);
		}
	}

	// *************************����û�ͬʱ����һ��post
	// �����д�����*********************************
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
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(1)
					+ "}";
			reqJson = MyUtils.replaceIdinBackCommand(reqJson, postId);
			String response = MyUtils.sendPost(reqJson);
		}
	}

	// *************************����û�ͬʱ���۲�ͬpost
	// �������д�����*********************************
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
