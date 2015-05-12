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
 * @data:2015��4��16�� ����11:28:40
 * @description: BrandDelFavorite ȡ����עƷ�� ��Ҫ��¼
 */
public class TestBrandDelLike {

	private static List<String> reqJsons;
	private static String token;
	private static int userId;
	private static int brandId;
	private static Map<String, Object> reqData;

	// ��ʼ��,���ݽӿ����������ļ��ж�ȡrequestData
	@BeforeClass
	public static void init() {
		reqData = PropertiesReader.getAppRequestData();
		reqJsons = (List<String>) reqData.get("branddellike");
		//����õ�һ���û�id token
		Map user_token = MyUtils.getRandomIdToken();
		userId = Integer.parseInt((String) user_token.get("id"));
		token = (String) user_token.get("token");
		// �����ȡһ��Ʒ��Id
		List<Integer> brandIds = MyUtils.getBrandList();
		brandId = 1;
		int size = brandIds.size();
		if (size > 0) {
			brandId = brandIds.get(new Random().nextInt(size));
		}
	}

	// ��֤result code = 0
	@Test
	public void testCodeZero() {
		String response = this.getReqJson0Response();
		int code = JSONObject.fromObject(response).getJSONObject("result").getInt("code");
		Assert.assertEquals("Error:The result code is not 0", 0, code);
	}

	// ��֤ȡ��ϲ���Ƿ�ɹ� ϲ��--�鿴--ȡ��ϲ��--�鿴
	@Test
	public void testNotFind() {
		// ���--ȡ��ϲ��Ʒ��
		getReqJson0Response();
		// ��ȡ��ѯ����ϲ����Ʒ�Ƶ��б��ָ��
		List<String> reqJson2 = (List<String>) reqData.get("brandlikelist");
		String listJson = "{" + reqJson2.get(0) + "}";
		listJson = MyUtils.replaceUserid(listJson, userId);
		System.out.println(brandId);
		// ���Ͳ�ѯ����
		String response3 = MyUtils.sendPost(listJson);
		System.out.println(response3);
		// �ڹ�ע�б��в�ѯ��ע���û�
		Assert.assertFalse("Error��The brand still exist", MyUtils.idInArray(response3, "brands", brandId));
	}

	// ��ִ֤�иò��������¼,���û��¼ִ�в���,����code 1004 , ��¼״̬���ڣ������µ�¼
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
		// ��ȡϲ��Ʒ��ָ��
		List<String> reqJson1 = (List<String>) reqData.get("brandaddlike");
		String addJson = "{" + token + reqJson1.get(0) + "}";
		addJson = MyUtils.replaceIdinBackCommand(addJson, brandId);
		// ����ϲ��Ʒ�Ƶ�����
		MyUtils.sendPost(addJson);

		// ����ȡ��ϲ����ָ��
		String delJson = "{" + token + this.reqJsons.get(0) + "}";
		delJson = MyUtils.replaceIdinBackCommand(delJson, brandId);
		return MyUtils.sendPost(delJson);
	}

}
