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
 * @data:2015��4��28�� ����5:27:02
 * @description: ��ע�û� ���̲߳��� ���û�ͬʱ��עͬһ�û�/��ͬ�û�
 */
public class TestUserFollow {
	private static final int NUM_THREAD = 10; // �����߳�����
	private static final int USER_COUNT = 197;// �û�token������

	private static List<String> reqJsons;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("userfollow");
	}

	//*************************���û�ͬʱ��עͬһ�û�*********************************
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
			String reqJson = "{" + MyUtils.getRandomToken() + reqJsons.get(0) + "}";
			MyUtils.sendPost(reqJson);
		}
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
	}

}
