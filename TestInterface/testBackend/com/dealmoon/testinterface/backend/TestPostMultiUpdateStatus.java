package com.dealmoon.testinterface.backend;

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
 * @data:2015��4��27�� ����12:06:08
 * @description: �����޸�post ֻ�ı�isRecommend state status��������
 */
public class TestPostMultiUpdateStatus {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("PostmultiUpdateStatus");
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	/**
	 * ��������֤ ����--��
	 */
	@Test
	public void testUpdateSuccess() {
		String response = this.getReqJson0Response();
		String reqJson0 = reqJsons.get(0);

		JSONObject commandInfo = JSONObject.fromObject(reqJson0).getJSONObject("commandInfo");
		boolean isRecommend = commandInfo.getBoolean("isRecommend");
		String status = commandInfo.getString("status");
		Object[] ids = commandInfo.getJSONArray("ids").toArray();

		//��ȡpostInfoָ��
		List<String> reqJsons = (List<String>) reqData.get("Postlist");
		String postListCommand = reqJsons.get(0);

		JSONObject _commandInfo = JSONObject.fromObject(postListCommand).getJSONObject("commandInfo");
		String _status = _commandInfo.getJSONArray("status").toString();
		boolean _isRecommend = _commandInfo.getBoolean("isRecommend");
		int _userId = _commandInfo.getInt("userId");
		String command = postListCommand.replace("\"status\":" + _status, "\"status\":[\"" + status + "\"]")
				.replace("\"isRecommend\":" + _isRecommend, "\"isRecommend\":" + isRecommend)
				.replace(",\"userId\":" + _userId, "");
		String response0 = MyUtils.sendBackPost(command);
		for (int i = 0; i < ids.length; i++) {
			Assert.assertTrue("Error:Update failed", MyUtils.idInArray(response0, "posts", (int) ids[i]));
		}
	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		String reqJson0 = reqJsons.get(0);
		return MyUtils.sendBackPost(reqJson0);
	}

}
