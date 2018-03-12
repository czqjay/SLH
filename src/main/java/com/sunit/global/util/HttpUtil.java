package com.sunit.global.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;

import com.sunit.global.util.httpClient.HttpProtocolHandler;
import com.sunit.global.util.httpClient.HttpRequest;
import com.sunit.global.util.httpClient.HttpResponse;
import com.sunit.global.util.httpClient.HttpResultType;

public class HttpUtil {

	/**
	 * 以 jdk的API 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public static String sendPost(String url, String param) {
		// PrintWriter out = null;
		OutputStream out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("contentType", "UTF-8");
			conn.setRequestProperty("charsert", "UTF-8");
			// 发送POST请求必须设置如下两行 s
			conn.setDoOutput(true);
			conn.setDoInput(true);

			out = conn.getOutputStream();
			out.write(param.getBytes("UTF-8"));
			out.flush();

			// out = new PrintWriter(conn.getOutputStream());
			// // 发送请求参数
			//// param=new String(param.getBytes(), "iso8859-1");
			//// param=new String(param.getBytes(), "gbk");
			// System.out.println(1);
			// out.print(param);
			// // flush输出流的缓冲
			// out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	private HttpRequest request = new HttpRequest(HttpResultType.BYTES);
	private List<NameValuePair> parameters = new ArrayList();

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public void putParameter(String key, String value) {
		parameters.add(new NameValuePair(key, value));
	}

	/**
	 * 默认post方式请求 eg:
	 * 
	 * HttpUtil httpUtil = new HttpUtil(); httpUtil.putParameter("key",
	 * "d3a3b953bf6f41b98a37de4043f4fc41"); httpUtil.putParameter("keyword",
	 * "莱州诺诺电子商务有限公司"); String url =
	 * "http://i.yjapi.com/ECIV4/GetDetailsByName";
	 * System.out.println(httpUtil.doActionGet(url));
	 * 
	 * @Title: doAction
	 * @Description:
	 * @param @param
	 *            url
	 * @param @return
	 * @param @throws
	 *            HttpException
	 * @param @throws
	 *            IOException
	 * @return String
	 * @throws @author
	 *             joye 2018年3月5日 下午3:48:49
	 */
	public String doAction(String url) throws HttpException, IOException {
		request.setUrl(url);
		request.setParameters(parameters.toArray(new NameValuePair[parameters.size()]));
		HttpProtocolHandler httpHandle = HttpProtocolHandler.getInstance();
		HttpResponse response = httpHandle.execute(request, "", "");
		String str = new String(response.getByteResult(), "UTF-8");
		return str;
	}

	public String doActionGet(String url) throws HttpException, IOException {
		this.getRequest().setMethod(HttpRequest.METHOD_GET);
		return doAction(url);
	}

}
