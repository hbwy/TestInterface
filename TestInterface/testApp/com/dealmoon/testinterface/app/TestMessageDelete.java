package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年4月23日 上午11:47:50
 * @description: 删除消息 需要登录
 */
public class TestMessageDelete {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;
	private static int messageId;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("messagedelete");
		token = MyUtils.getRandomToken();
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证消息删除   查看   删除   查看
	@Test
	public void testNotFind() {
		//随机获取消息并删除
		String response = getReqJson0Response();
		//查看消息列表是否存在该消息
		List<Integer> messageList = MyUtils.getMessageList(token);
		int _id = -1;
		for (Integer id : messageList) {
			if (messageId == id) {
				_id = id;
				break;
			}
		}
		Assert.assertFalse("Error:The messages contain the message", messageId == _id);
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
		//随机获取消息列表中的消息
		List<Integer> messageList = MyUtils.getMessageList(token);
		int size = messageList.size();
		messageId = 1;
		if(size>0){
			messageId = messageList.get(new Random().nextInt(messageList.size()));
		}
		//删除消息
		String reqJson0 = "{" + token + reqJsons.get(0) + "}";
		reqJson0 = MyUtils.replaceMessageid(reqJson0, messageId);
		return MyUtils.sendPost(reqJson0);
	}

}
