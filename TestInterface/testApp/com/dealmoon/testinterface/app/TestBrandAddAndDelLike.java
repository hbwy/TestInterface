package com.dealmoon.testinterface.app;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.dealmoon.testinterface.MyUtils;
import com.dealmoon.testinterface.PropertiesReader;

/**
 * @author: WY
 * @data:2015年6月15日 上午10:52:46
 * @description:喜欢/取消喜欢品牌
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBrandAddAndDelLike {

	private static List<String> delReqJsons; //取消关注品牌的请求数据
	private static List<String> addReqJsons; //关注品牌的请求数据
	private static List<String> listReqJsons; //关注品牌的请求数据

	private static int userId; //随机获取的用户id
	private static String token; //userId对应的token

	private static int brandId; //随机获取的品牌id

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();

		delReqJsons = (List<String>) reqData.get("branddellike");
		addReqJsons = (List<String>) reqData.get("brandaddlike");
		listReqJsons = (List<String>) reqData.get("brandlikelist");

		//随机拿到一个用户id token
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
		// 随机获取一个品牌Id
		List<Integer> brandIds = MyUtils.getBrandList();
		brandId = 1;
		int size = brandIds.size();
		if (size > 0) {
			brandId = brandIds.get(new Random().nextInt(size));
		}
	}

	// 喜欢品牌
	@Test
	public void test1AddLike() {
		// 获取喜欢品牌指令
		String addJson = "{" + token + addReqJsons.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, brandId);
		// 发送喜欢品牌的请求
		String addResponse = MyUtils.sendPost(addJson);

		int code = JSONObject.fromObject(addResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证添加喜欢品牌成功
	@Test
	public void test2Find() {
		String response = getBrandLikeList();
		Assert.assertTrue("Error:Like brand failed", MyUtils.idInArray(response, "brands", brandId));
	}

	// 取消喜欢品牌
	@Test
	public void test3DelLike() {
		// 发送取消喜欢的指令
		String delJson = "{" + token + delReqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, brandId);
		String delResponse = MyUtils.sendPost(delJson);

		int code = JSONObject.fromObject(delResponse).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证取消喜欢是否成功
	@Test
	public void test4NotFind() {
		String response = getBrandLikeList();
		Assert.assertFalse("Error：The brand still exist", MyUtils.idInArray(response, "brands", brandId));
	}

	// 验证执行关注品牌必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void test5AddLikeMustLogin() {
		String reqJson0 = "{" + addReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	// 验证执行取消关注品牌必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void test6DelLikeMustLogin() {
		String reqJson0 = "{" + delReqJsons.get(0) + "}";
		mustLogin(reqJson0);
	}

	private String getBrandLikeList() {
		String reqJson = "{" + listReqJsons.get(0) + "}";
		reqJson = MyUtils.replaceUserid(reqJson, userId);
		return MyUtils.sendPost(reqJson);
	}

	private void mustLogin(String reqJson) {
		reqJson = MyUtils.replaceIdinBackCommand(reqJson, brandId);
		String response = MyUtils.sendPost(reqJson);
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);
	}

	@AfterClass
	public static void release() {
		delReqJsons = null;
		addReqJsons = null;
		token = null;
	}
}
