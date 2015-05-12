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
 * @data:2015年4月16日 上午11:28:40
 * @description: BrandDelFavorite 取消关注品牌 需要登录
 */
public class TestBrandDelLike {

	private static List<String> reqJsons;
	private static String token;
	private static int userId;
	private static int brandId;
	private static Map<String, Object> reqData;

	// 初始化,根据接口名从配置文件中读取requestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("branddellike");
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

	// 验证result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// 验证取消喜欢是否成功 喜欢--查看--取消喜欢--查看
	@Test
	public void testNotFind() {
		// 添加--取消喜欢品牌
		getReqJson0Response();
		// 获取查询本人喜欢的品牌的列表的指令
		List<String> reqJson2 = (List<String>) reqData.get("brandlikelist");
		String listJson = "{" + reqJson2.get(0) + "}";
		listJson = MyUtils.replaceUserid(listJson, userId);
		System.out.println(brandId);
		// 发送查询请求
		String response3 = MyUtils.sendPost(listJson);
		System.out.println(response3);
		// 在关注列表中查询关注的用户
		Assert.assertFalse("Error：The brand still exist", MyUtils.idInArray(response3, "brands", brandId));
	}

	// 验证执行该操作必须登录,如果没登录执行操作,返回code 1004 , 登录状态过期，请重新登录
	@Test
	public void testMustLogin() {
		String reqJson0 = "{" + reqJsons.get(0) + "}";
		String response = MyUtils.sendPost(reqJson0);
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 1004", 1004, code);

	}

	@AfterClass
	public static void release() {
		reqJsons = null;
		token = null;
		reqData = null;
	}

	private String getReqJson0Response() {
		// 获取喜欢品牌指令
		List<String> reqJson1 = (List<String>) reqData.get("brandaddlike");
		String addJson = "{" + token + reqJson1.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, brandId);
		// 发送喜欢品牌的请求
		MyUtils.sendPost(addJson);

		// 发送取消喜欢的指令
		String delJson = "{" + token + this.reqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, brandId);
		return MyUtils.sendPost(delJson);
	}

}
