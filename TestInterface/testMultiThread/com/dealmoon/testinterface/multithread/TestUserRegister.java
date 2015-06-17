package com.dealmoon.testinterface.multithread;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;
import com.sun.jersey.core.util.Base64;

public class TestUserRegister {

	private static final int NUM_THREAD = 100; //ִ�еĴ���
	private static List<String> reqJsons;
	private static Map<String, Object> reqData;
	private static int count = 0;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("userregister");
	}

	@Test
	// �߲�������
	public void testMultiThread() throws Throwable {
		// �������в����߳�
		TestRunnable[] threads = new TestRunnable[NUM_THREAD];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ChildThread();
		}
		// ���ɲ����߳�������
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);
		// ���в����߳�
		mttr.runTestRunnables();
	}

	// �����߳��ඨ��
	private class ChildThread extends TestRunnable {
		Count cc = new Count();
		
		@Override
		public void runTest() throws Throwable {
			String reqJson = "{" + reqJsons.get(0) + "}";
			JSONObject userInfo = JSONObject.fromObject(reqJson).getJSONObject("commandInfo").getJSONObject("userInfo");
			String username = userInfo.getString("name");
			String email = userInfo.getString("email");
			String _password = userInfo.getString("password");
			String password = "";
			try {
				password = new String(Base64.encode(MyUtils.encrypt(_password.getBytes())));
			} catch (Exception e) {
				e.printStackTrace();
			}

			reqJson = reqJson.replace("\"name\":\"" + username + "\",", "\"name\":\"" + "nana" + count + "\",")
					.replace("\"email\":\"" + email + "\",", "\"email\":\"na" + count + "@wy.com" + "\",")
					.replace("\"password\":\"" + _password + "\"", "\"password\":\"" + password + "\"");
			synchronized (cc) {
				cc.ccount();
			}
			String response = MyUtils.sendPost(reqJson);
			System.out.println(response);
			try {
				String token = JSONObject.fromObject(response).getJSONObject("responseData").getString("token");
				MyUtils.writeUserIdToken(token);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
			Thread.sleep(5000);
		}
	}

	private class Count {
		public void ccount() {
			count++;
		}
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		reqData = null;
	}

}
