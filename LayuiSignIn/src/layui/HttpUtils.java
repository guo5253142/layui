package layui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	
	private static Map<String,String> CookieContiner=new HashMap<String,String>() ;

	/**
	* 保存Cookie
	* @param resp
	*/
	public static void saveCookies(HttpResponse httpResponse) {
		Header[] headers = httpResponse.getHeaders("Set-Cookie");
		String headerstr = headers.toString();
		if (headers == null)
			return;
		for (int i = 0; i < headers.length; i++) {
			String cookie = headers[i].getValue();
			String[] cookievalues = cookie.split(";");
			for (int j = 0; j < cookievalues.length; j++) {
				String[] keyPair = cookievalues[j].split("=");
				String key = keyPair[0].trim();
				String value = keyPair.length > 1 ? keyPair[1].trim() : "";
				CookieContiner.put(key, value);
			}
		}
	}

	/**
	 * 增加Cookie
	 * 
	 * @param request
	 */
	public static String getCookies() {
		StringBuilder sb = new StringBuilder();
		Iterator iter = CookieContiner.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = entry.getKey().toString();
			if(key.equals("fly-layui")){
				String val = entry.getValue().toString();
				sb.append(key);
				sb.append("=");
				sb.append(val);
				sb.append(";");
			}
		}
		return sb.toString();
	}
	
	public static void clearCookies(){
		CookieContiner=new HashMap<String,String>();
	}
	
	public static String sendGet(HttpClient client, String url) throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Connection","keep-alive");
		String cookies=getCookies();
		if(!"".equals(cookies)){
			httpGet.addHeader(new BasicHeader("Cookie", cookies));
		}
		RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
		httpGet.setConfig(defaultConfig);
		httpGet.setHeader("Connection","keep-alive");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
		HttpResponse response = client.execute(httpGet);
		saveCookies(response);
		HttpEntity entity = response.getEntity();
		String content = EntityUtils.toString(entity, "GBK");
		return content;
	}
	
	
	
	 public static String sendPOST(CloseableHttpClient httpClient,String url,List<NameValuePair> urlParameters) throws IOException {
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");

	        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
	        httpPost.setEntity(postParams);
	        String cookies=getCookies();
	        RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
	        httpPost.setConfig(defaultConfig);
	        httpPost.setHeader("Cookies",cookies);
	        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        httpPost.setHeader("X-Requested-With","XMLHttpRequest");
	        httpPost.setHeader("Connection","keep-alive");
	        httpPost.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
	        //httpPost.setHeader("Referer","https://fly.layui.com/user/login/");
	        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);


	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                httpResponse.getEntity().getContent()));

	        String inputLine;
	        StringBuffer response = new StringBuffer();

	        while ((inputLine = reader.readLine()) != null) {
	            response.append(inputLine);
	        }
	        reader.close();

	        // print result
	        String content=(response.toString());
	        return content;

	    }
	 
	 
	 public static String httpsPost(String url,Map<String, String> parameters) {
		String result = "";
		try {
			BufferedReader in = null;
			OutputStream os = null;

			String cookies = getCookies();
			HttpsURLConnection conn = getHttpsURLConnection(url);
			conn.setRequestProperty("user-agent",
					"Mozilla/5.0 (Linux; Android 7.0; EVA-AL00 Build/HUAWEIEVA-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/55.0.2883.91 Mobile Safari/537.36[android/1.0.23/2d67ce6e432835d8267b3d3f94c5efcf/07262a32d5b3551bca2bfca211600524]");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cookie", cookies);
			os = conn.getOutputStream();
			// 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致  
			String content = "";
			if (null != parameters && parameters.size() > 0) {
				for (Map.Entry<String, String> entry : parameters.entrySet()) {
					content += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8")+"&";
				}
			}
			if(!"".equals(content)){
				content=content.substring(0,content.length()-1);
			}
			
			os.write(content.getBytes());

			os.flush();
			os.close();

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		}
	 
	 public static HttpsURLConnection getHttpsURLConnection(String uri) throws IOException {
	        SSLContext ctx = null;
	        try {
	            ctx = SSLContext.getInstance("TLS");
	            ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
	        } catch (KeyManagementException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (NoSuchAlgorithmException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        SSLSocketFactory ssf = ctx.getSocketFactory();
	        
	        URL url = new URL(uri);
	        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
	        httpsConn.setSSLSocketFactory(ssf);
	        httpsConn.setHostnameVerifier(new HostnameVerifier() {
	            @Override
	            public boolean verify(String arg0, SSLSession arg1) {
	                return true;
	            }
	        });
	        httpsConn.setDoInput(true);
	        httpsConn.setDoOutput(true);
	       
	        return httpsConn;
	    }
	 
	 private static class DefaultTrustManager implements X509TrustManager {
	        @Override
	        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	        }

	        @Override
	        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	        }

	        @Override
	        public X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }
	    }
	 
	 
	
}
