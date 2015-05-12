package com.dealmoon.testinterface.app;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月23日 上午11:12:43
 * @description:读消息 需要登录
 */
public class TestMessageRead {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("messageread");
		token = MyUtils.getRandomToken();
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	//验证消息已读   查看 读消息 查看
	@Test
	public void testRead() {
		//查看消息列表 获取一条未读消息的id
		List<String> reqJsons = (List<String>) reqData.get("messagegetlist");
		String response0 = MyUtils.sendPost("{" + token + reqJsons.get(2) + "}");
		JSONArray messages = JSONObject.fromObject(response0).getJSONObject("responseData").getJSONArray("messages");
		//默认是第一条消息的id,无论第一条是已读还是未读
		int messageId = 1;
		for (Iterator iterator = messages.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			if (!obj.getBoolean("isRead")) {
				messageId = obj.getInt("id");
				break;
			}
		}
		//获取读消息的指令
		String reqJson0 = "{" + token + this.reqJsons.get(0) + "}";
		String commandInfo = JSONObject.fromObject(reqJson0).getString("commandInfo");
		reqJson0 = reqJson0.replace(commandInfo, "{\"id\":[" + messageId + "]}");
		//发送读消息指令
		MyUtils.sendPost(reqJson0);
		//查看该消息的状态   isRead=true
		String response1 = MyUtils.sendPost("{" + token + reqJsons.get(2) + "}");
		JSONArray messages1 = JSONObject.fromObject(response1).getJSONObject("responseData").getJSONArray("messages");
		boolean isRead = false;
		for (Iterator iterator = messages1.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			if (obj.getInt("id") == messageId) {
				isRead = obj.getBoolean("isRead");
				break;
			}
		}

		Assert.assertTrue("Error:Read message failed", isRead);
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
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
