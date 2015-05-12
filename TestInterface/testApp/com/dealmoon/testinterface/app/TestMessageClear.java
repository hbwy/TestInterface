package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015��4��23�� ����11:53:04
 * @description:������ɾ����Ϣ type=null ɾ��ȫ����Ϣ ��Ҫ��¼
 */
public class TestMessageClear {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("messageclear");
		token = MyUtils.getRandomToken();
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤��Ϣɾ��   �鿴   ɾ��   �鿴      ���followd�б������ݾ�ɾ��followd�ģ����û�о�ɾ��like��
	@Test
	public void testNotFind() {
		//��ȡ�鿴��Ϣ�б�ָ��
		List<String> reqJsons = (List<String>) reqData.get("messagegetlist");
		//���Ͳ鿴followd��ָ��
		String response0 = MyUtils.sendPost("{" + token + reqJsons.get(0) + "}");
		//�鿴followd��Ϣ�б�Ĵ�С
		int size = JSONObject.fromObject(response0).getJSONObject("responseData").getJSONArray("messages").size();
		if (size > 0) { //followd�б����0��ɾ��
			//ɾ��followd��Ϣ�б�
			this.getReqJson0Response();
			//�鿴followd��Ϣ�б�
			String _response0 = MyUtils.sendPost("{" + token + reqJsons.get(0) + "}");
			int _size = JSONObject.fromObject(_response0).getJSONObject("responseData").getJSONArray("messages").size();

			Assert.assertTrue("Error:clear followd failed", _size == 0);
		} else {
			String response1 = MyUtils.sendPost("{" + token + reqJsons.get(1) + "}");
			int size1 = JSONObject.fromObject(response1).getJSONObject("responseData").getJSONArray("messages").size();
			if (size1 > 0) { //like�б����0��ɾ��
				//ɾ��like��Ϣ�б�
				MyUtils.sendPost("{" + token + this.reqJsons.get(1) + "}");
				//�鿴like��Ϣ�б�
				String _response1 = MyUtils.sendPost("{" + token + reqJsons.get(1) + "}");
				int _size1 = JSONObject.fromObject(_response1).getJSONObject("responseData").getJSONArray("messages")
						.size();
				Assert.assertTrue("Error:clear like failed", _size1 == 0);
			}
		}

	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
	@Test
	public void testMustLogin() {

		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String _response = MyUtils.sendPost(reqJson0);

		int code = JSONObject.fromObject(_response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		return MyUtils.sendPost(reqJson0);
	}

}
