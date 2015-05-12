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
 * @data:2015年4月27日 下午4:05:18
 * @description: 修改晒货评论 (批量修改,单条修改均走此方法)
 */
public class TestPostMultiUpdateCommentStatus {

	private static List<String> reqJsons;
	private static Map<String, List<String>> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getBackendRequestData();
		reqJsons = (List<String>) reqData.get("PostmultiUpdateCommentStatus");
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");

		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	/**
	 * 更新完验证 更新--查
	 */
	@Test
	public void testUpdateSuccess() {
		String reqJson0 = this.reqJsons.get(0);
		MyUtils.sendBackPost(reqJson0);
		JSONObject commandInfo = JSONObject.fromObject(reqJson0).getJSONObject("commandInfo");
		String status = commandInfo.getString("status");
		Object[] ids = commandInfo.getJSONArray("ids").toArray();

		//获取postInfo指令
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
