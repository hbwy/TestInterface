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
 * @data:2015��4��27�� ����4:05:18
 * @description: �޸�ɹ������ (�����޸�,�����޸ľ��ߴ˷���)
 */
public class TestPostMultiUpdateCommentStatus {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("PostmultiUpdateCommentStatus");
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
		String reqJson0 = this.reqJsons.get(0);
		MyUtils.sendBackPost(reqJson0);
		JSONObject commandInfo = JSONObject.fromObject(reqJson0).getJSONObject("commandInfo");
		String status = commandInfo.getString("status");
		Object[] ids = commandInfo.getJSONArray("ids").toArray();

		//��ȡpostInfoָ��
		List<String> reqJsons = (List<String>) reqData.get("PostcommentList");
		String postcommentListCommand = reqJsons.get(0);

		JSONObject _commandInfo = JSONObject.fromObject(postcommentListCommand).getJSONObject("commandInfo");
		String _status = _commandInfo.getJSONArray("status").toString();
		int _userId = _commandInfo.getInt("userId");
		String _oderBy = _commandInfo.getString("oderBy");
		String command = postcommentListCommand.replace("\"status\":" + _status, "\"status\":[\"" + status + "\"]")
				.replace("\"oderBy\":\"" + _oderBy + "\"", "\"oderBy\":\"\"").replace(",\"userId\":" + _userId, "");

		String response0 = MyUtils.sendBackPost(command);
		for (int i = 0; i < ids.length; i++) {
			Assert.assertTrue("Error:Update failed", MyUtils.idInArray(response0, "comments", (int) ids[i]));
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
