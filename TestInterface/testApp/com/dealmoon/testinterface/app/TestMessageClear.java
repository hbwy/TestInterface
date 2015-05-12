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
 * @data:2015年4月23日 上午11:53:04
 * @description:按类型删除消息 type=null 删除全部消息 需要登录
 */
public class TestMessageClear {

	private static List<String> reqJsons;
	private static String token;
	private static Map<String, Object> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("messageclear");
		token = MyUtils.getRandomToken();
	}

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证消息删除   查看   删除   查看      如果followd列表有数据就删除followd的，如果没有就删除like的
	@Test
	public void testNotFind() {
		//获取查看消息列表指令
		List<String> reqJsons = (List<String>) reqData.get("messagegetlist");
		//发送查看followd的指令
		String response0 = MyUtils.sendPost("{" + token + reqJsons.get(0) + "}");
		//查看followd消息列表的大小
		int size = JSONObject.fromObject(response0).getJSONObject("responseData").getJSONArray("messages").size();
		if (size > 0) { //followd列表大于0才删除
			//删除followd消息列表
			this.getReqJson0Response();
			//查看followd消息列表
			String _response0 = MyUtils.sendPost("{" + token + reqJsons.get(0) + "}");
			int _size = JSONObject.fromObject(_response0).getJSONObject("responseData").getJSONArray("messages").size();

			Assert.assertTrue("Error:clear followd failed", _size == 0);
		} else {
			String response1 = MyUtils.sendPost("{" + token + reqJsons.get(1) + "}");
			int size1 = JSONObject.fromObject(response1).getJSONObject("responseData").getJSONArray("messages").size();
			if (size1 > 0) { //like列表大于0才删除
				//删除like消息列表
				MyUtils.sendPost("{" + token + this.reqJsons.get(1) + "}");
				//查看like消息列表
				String _response1 = MyUtils.sendPost("{" + token + reqJsons.get(1) + "}");
				int _size1 = JSONObject.fromObject(_response1).getJSONObject("responseData").getJSONArray("messages")
						.size();
				Assert.assertTrue("Error:clear like failed", _size1 == 0);
			}
		}

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
