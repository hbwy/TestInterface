package com.dealmoon.testinterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.util.encoders.Base64;

/**
 * @author: WY
 * @data:2015年4月3日 上午11:11:55
 * @description:工具类
 */
public class MyUtils {
	private final static String url = "http://api2.apps.dealmoon.com";
	/**
	 * 替换字符串中的空白字符,例如换行/空格/回车等
	 * 
	 * @param strSource 待处理的字符串
	 * @return 处理后的字符串
	 */
	public static String removeBlank(String strSource) {
		return (strSource.replaceAll("\\s*", ""));
	}

	/**
	 * 用正则表达式匹配字符串,选择某个组
	 * 
	 * @param strSource 要匹配的字符串
	 * @param pattern 正则表达式
	 * @param groupId 分组匹配，选取哪个组
	 * @return 对应组的结果字符串
	 */
	public static String matchString(String strSource, String pattern, int groupId) {
		Pattern _pattern = Pattern.compile(pattern);
		Matcher matcher = _pattern.matcher(strSource);
		String strResult = null;
		if (matcher.find()) {
			strResult = matcher.group(groupId);
		}
		return strResult;
	}

	/**
	 * MD5加密字符串，形成32位密文
	 * 
	 * @param plainText 待加密的字符串
	 * @return 加密后的字符串
	 */
	public static String MD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 把字符串写到文件中
	 * 
	 * @param fileName 文件名
	 * @param strContent 需要写入的内容
	 */
	public static void writeFile(String fileName, String strContent) {
		FileWriter fw = null;
		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(strContent, 0, strContent.length() - 1);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把用户的id和token写到pro/token.properties中
	 * 
	 * @param token token中有用户id,采用截取字符串的形式拿到id
	 */
	public static synchronized void writeUserIdToken(String token) {
		String filename = "pro/token.properties";
		HashMap<String, String> map = new HashMap<String, String>();
		String[] id_token = token.split("\\|");
		map.put(id_token[0], token);
		PropertiesReader.proWriter(filename, map);
	}

	/**
	 * 用post请求发送文本和上传图片到服务器
	 * 
	 * @param urlStr url地址
	 * @param textMap 文本Map
	 * @param fileMap 文件Map
	 * @param requestData requestData
	 * @return responseData
	 */
	public static String postUpload(Map<String, String> textMap, Map<String, String> fileMap, String reqJson) {
		String res = null;
		HttpURLConnection conn = null;
		// boundary就是request头和上传文件内容的分隔符
		String BOUNDARY = "---------------------------123821742118716";
		try {
			URL relUrl = new URL(url+"/Post");
			conn = (HttpURLConnection) relUrl.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");

			// 构造DMAuthorization验证信息
			String DMAuthorization = MyUtils.MD5("deal" + reqJson + "moon");
			conn.setRequestProperty("DMAuthorization", DMAuthorization);

			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// 发送文本
			if (textMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator<?> iter = textMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}

			// 上传文件
			if (fileMap != null) {
				Iterator iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();
					String contentType = new MimetypesFileTypeMap().getContentType(file);
					if (filename.endsWith(".png")) {
						contentType = "image/png";
					}
					if (contentType == null || contentType.equals("")) {
						contentType = "application/octet-stream";
					}

					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

					out.write(strBuf.toString().getBytes());

					DataInputStream in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					//加入时间戳,使 传的图片不重复
					out.write(String.valueOf(System.currentTimeMillis() + Math.random()).getBytes());
					in.close();
				}
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			if (conn.getResponseCode() == 200) {
				// 读取返回数据
				StringBuffer strBuf = new StringBuffer();
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					strBuf.append(line).append("\n");
				}
				res = strBuf.toString();
				reader.close();
				reader = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}

	/**
	 * App发送请求
	 * @param jsonData 请求的完整json串
	 * @return response
	 */
	public static String sendPost(String jsonData){
		return sendRequest(url,jsonData);
	}
	
	/**
	 * 后台发送请求
	 * @param jsonData 请求的完整json串
	 * @return response
	 */
	public static String sendBackPost(String jsonData){
		String realurl = url+":8080";
		return sendRequest(realurl, jsonData);
	}
	/**
	 * 向指定 URL 发送POST请求
	 * 
	 * @param url 请求的 URL
	 * @param jsonData 请求的完整json串
	 * @return response
	 */
	public static String sendRequest(String url,String jsonData) {
		HttpURLConnection conn = null;
		StringBuffer sbf = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			// 开启http连接
			conn = (HttpURLConnection) realUrl.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);

			// 构造DMAuthorization验证信息
			String DMAuthorization = MyUtils.MD5("deal" + jsonData + "moon");
			conn.setRequestProperty("DMAuthorization", DMAuthorization);
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.connect();

			// 把jsonData 发送到服务器
			PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
			out.println(jsonData);
			out.flush();
			out.close();

			if (conn.getResponseCode() == 200) {
				// 读取回复信息
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String lines;
				while ((lines = reader.readLine()) != null) {
					lines = new String(lines.getBytes(), "utf-8");
					sbf.append(lines);
				}
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return sbf.toString();
	}

	/**
	 * 传入comment id 生成postGetComment请求数据
	 * 
	 * @param id comment id
	 * @return postGetComment请求数据
	 */
	/*
	 * public static String postGetCommentCommand(int id) { Map<String, Object>
	 * reqData = PropertiesReader.getAppRequestData();
	 * 
	 * List<String> _reqJsons = (List<String>) reqData.get("postgetcomment");
	 * 
	 * if (_reqJsons != null) { String _reqJson0 = "{" + _reqJsons.get(0) + "}";
	 * int obj =
	 * JSONObject.fromObject(_reqJson0).getJSONObject("commandInfo").getInt
	 * ("id"); _reqJson0 = _reqJson0.replace("\"id\":" + obj, "\"id\":" + id);
	 * return _reqJson0; } return null; }
	 */

	/**
	 * 判断id是否在一个某结构的数组中
	 * 
	 * @param response 请求返回值
	 * @param arrayName 结构的名称
	 * @param id 需要查的id
	 * @return true 存在 false 不存在
	 */
	public static boolean idInArray(String response, String arrayName, int id) {
		JSONArray array = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray(arrayName);
		int _id = -1;
		for (Iterator iterator = array.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			if (obj.getInt("id") == id) {
				_id = id;
				break;
			}
		}
		return id == _id;
	}

	/**
	 * 获取对应类型的post列表
	 * 
	 * @param url 请求url
	 * @param type post类型
	 * @param pageNum 页码
	 * @param pageSize 页面大小
	 * @return
	 */
	public static List<Integer> getPostList(String type, int pageNum, int pageSize) {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		List<String> reqJsons = (List<String>) reqData.get("postgetlist");
		List<Integer> ids = new ArrayList<Integer>();

		String token = (String) reqData.get("token");
		String reqJson;
		if (type.equals("follow")) {
			reqJson = "{" + token + reqJsons.get(0) + "}";
		} else {
			reqJson = "{" + reqJsons.get(0) + "}";
		}

		String commandInfo = JSONObject.fromObject(reqJson).getJSONObject("commandInfo").toString();
		reqJson = reqJson.replace(commandInfo, "{" + "\"type\":\"" + type + "\",\"pageNum\":" + pageNum
				+ ",\"pageSize\":" + pageSize + "}");
		String response = MyUtils.sendPost(reqJson);
		JSONArray array = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("posts");
		for (Iterator iterator = array.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			ids.add(obj.getInt("id"));
		}
		return ids;
	}

	/**
	 * 根据传入的postId查询post信息
	 * 
	 * @param url
	 * @param poatId
	 */
	public static String getPostInfo(int postId) {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		List<String> reqJsons = (List<String>) reqData.get("postinfo");

		String reqJson0 = "{" + reqJsons.get(0) + "}";
		JSONObject obj = JSONObject.fromObject(reqJson0).getJSONObject("commandInfo");
		reqJson0 = reqJson0.replace(obj.toString(), "{\"id\":" + postId + "}");

		return MyUtils.sendPost(reqJson0);
	}

	/**
	 * 获取一个随机的token
	 * 
	 * @return
	 */
	public static String getRandomToken() {
		Map<String, String> user_token = PropertiesReader.getTokens();
		String[] tokens = user_token.values().toArray(new String[0]);
		Random random = new Random();
		return "\"token\":" + "\"" + tokens[random.nextInt(tokens.length - 1)] + "\",";
	}

	/**
	 * 获取一个随机的token,除去指定user对应的token
	 * 
	 * @param userId
	 * @return
	 */
	public static String getRandomToken(int userId) {
		Map<String, String> user_token = PropertiesReader.getTokens();
		user_token.remove(userId);
		String[] tokens = user_token.values().toArray(new String[0]);

		Random random = new Random();
		return "\"token\":" + "\"" + tokens[random.nextInt(tokens.length - 1)] + "\",";
	}

	/**
	 * 获取一个随机的id,除去指定user对应的id
	 * 
	 * @param userId
	 * @return
	 */
	public static int getRandomUserId(int userId) {
		Map<String, String> user_token = PropertiesReader.getTokens();
		user_token.remove(userId);
		String[] ids = user_token.keySet().toArray(new String[0]);

		Random random = new Random();
		return Integer.parseInt(ids[random.nextInt(ids.length - 1)]);

	}

	/**
	 * 随机拿到一个用户的id和token
	 * 
	 * @return
	 */
	public static Map getRandomIdToken() {
		Map<String, String> user_token = PropertiesReader.getTokens();
		String[] userIds = user_token.keySet().toArray(new String[0]);
		String userId = userIds[new Random().nextInt(userIds.length - 1)];
		String userToken = user_token.get(userId);
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", userId);
		map.put("token", "\"token\":" + "\"" + userToken + "\",");

		return map;
	}

	/**
	 * app替换commandInfo中的id
	 * 
	 * @param reqJson
	 * @param newId
	 * @return
	 */
	public static String replaceIdinAppCommand(String reqString, int newId) {
		int oldId = JSONObject.fromObject("{" + reqString + "}").getJSONObject("commandInfo").getInt("id");
		return reqString.replace("\"id\":" + oldId + ",", "\"id\":" + newId + ",");
	}

	/**
	 * 后台替换commandInfo中的id
	 * 
	 * @param reqJson
	 * @param newId
	 * @return
	 */
	public static String replaceIdinBackCommand(String reqJson, int newId) {
		int oldId = JSONObject.fromObject(reqJson).getJSONObject("commandInfo").getInt("id");
		return reqJson.replace("\"id\":" + oldId, "\"id\":" + newId);
	}

	/**
	 * 替换getpostlikelist中userId
	 * 
	 * @param reqJson
	 * @param userId
	 * @return
	 */
	public static String replaceUserid(String reqJson, int userId) {
		int oldId = JSONObject.fromObject(reqJson).getJSONObject("commandInfo").getInt("userId");
		return reqJson.replace("\"userId\":" + oldId, "\"userId\":" + userId);
	}

	/**
	 * 替换deal/subscription/getUnRead中since
	 * 
	 * @param reqJson
	 * @return
	 */
	public static String replaceSince(String reqJson) {
		String commandInfo = JSONObject.fromObject(reqJson).getJSONObject("commandInfo").toString();
		reqJson = reqJson.replace(commandInfo, "{\"since\":\"" + Math.round(System.currentTimeMillis() / 1000) + "\"}");
		return reqJson;
	}

	/**
	 * 替换getmessagelist中messageId
	 * 
	 * @param reqJson
	 * @param userId
	 * @return
	 */
	public static String replaceMessageid(String reqJson, int messageId) {
		String commandInfo = JSONObject.fromObject(reqJson).getJSONObject("commandInfo").toString();
		return reqJson.replace(commandInfo, "{\"id\":[" + messageId + "]}");
	}

	/**
	 * 获取品牌列表
	 * 
	 * @param url
	 * @return
	 */
	public static List<Integer> getBrandList() {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		List<String> reqJsons = (List<String>) reqData.get("brandlist");
		List<Integer> ids = new ArrayList<Integer>();

		String reqJson = "{" + reqJsons.get(1) + "}";
		String response = MyUtils.sendPost(reqJson);
		JSONArray array = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("brands");

		for (Iterator iterator = array.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			ids.add(obj.getInt("id"));
		}
		return ids;
	}

	/**
	 * 获取用户消息列表
	 * 
	 * @param url
	 * @return
	 */
	public static List<Integer> getMessageList(String token) {
		Map<String, Object> reqData = PropertiesReader.getAppRequestData();
		List<String> reqJsons = (List<String>) reqData.get("messagegetlist");
		List<Integer> ids = new ArrayList<Integer>();
		String reqJson = "{" + token + reqJsons.get(2) + "}";
		String response = MyUtils.sendPost(reqJson);
		JSONArray array = JSONObject.fromObject(response).getJSONObject("responseData").getJSONArray("messages");

		for (Iterator iterator = array.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			ids.add(obj.getInt("id"));
		}
		return ids;
	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr 公钥数据字符串
	 * @throws Exception 加载公钥时产生的异常
	 */
	public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
		try {
			byte[] buffer = Base64.decode(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	/**
	 * 从文件中加载私钥
	 * 
	 * @param keyFileName 私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	public static RSAPrivateKey loadPrivateKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return loadPrivateKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}

	public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {

		try {
			byte[] buffer = Base64.decode(privateKeyStr);
			RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure(
					(ASN1Sequence) ASN1Sequence.fromByteArray(buffer));
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(),
					asn1PrivKey.getPrivateExponent());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * 加密过程
	 * 
	 * @param plainTextData 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	public static byte[] encrypt(byte[] plainTextData) throws Exception {
		String _publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/ucgTqTXbHwGQHl5k+jImN48L" + "\r"
				+ "q/meEm9GIZ/BRWeE7OgavWTVVc/EAe4bJ8DGHdSeIazG8LmsUohuedHx6Q2eYdwa" + "\r"
				+ "MEQWS/G0GKL5U+85GMv4QHK20e6bDy+BdqmNGNNBQUNBnFTca/GqOYMPEhKGuRdj" + "\r"
				+ "YK18MuZzDsk1XU9PhQIDAQAB" + "\r";
		RSAPublicKey publicKey = loadPublicKey(_publicKey);
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	/**
	 * 解密过程
	 * 
	 * @param privateKey 私钥
	 * @param cipherData 密文数据
	 * @return 明文
	 * @throws Exception 解密过程中的异常信息
	 */
	public static byte[] decrypt(byte[] cipherData) throws Exception {
		InputStream inputStream = new FileInputStream(new File("pro/rsa_private_key.pem"));
		RSAPrivateKey privateKey = loadPrivateKey(inputStream);
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");

			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}
}
