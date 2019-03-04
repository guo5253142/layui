package layui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * layui自动签到
 * @author guopeng1
 *
 */
public class App {

	public static CloseableHttpClient httpClient = HttpClients.custom().build();
	static Logger log = Logger.getLogger("ScheduleJob");  
	private static String DATA_SWAP="D:\\code\\result.txt";//解析后的验证码保存地址
	private static String imgCodePath="D:\\code\\code.png";//下载的验证图片
	private static String pyUrl="python D:\\code\\codeORC\\demo.py"; //解析验证码py脚本地址
	private static String loginName="layui账号";//账号
	private static String pass="密码";//密码
	
	
	/**
	 * 获取cookie
	 */
	public static void prelogin(){
		try {
			String content=HttpUtils.sendGet(httpClient, "https://fly.layui.com/user/login/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取验证码
	 */
	public static String getCodeImg(){
		String img="";
		try {
			String url="https://fly.layui.com/auth/imagecode?t="+(new Date()).getTime();
			String content=HttpUtils.sendGet(httpClient, url);
			img=content.substring(content.indexOf("<svg"),content.indexOf("</svg>")+6);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public static boolean login(String imagecode){
		String url = "https://fly.layui.com/user/login";
		List<NameValuePair> parms = new ArrayList<NameValuePair>();
		parms.add(new BasicNameValuePair("loginName", loginName));
		parms.add(new BasicNameValuePair("pass", pass));
		parms.add(new BasicNameValuePair("imagecode", imagecode));
		try {
			String content = HttpUtils.sendPOST(httpClient, url, parms);
			log.info(content);
			JSONObject json=(JSONObject)JSON.parse(content);
			if(json.getInteger("status")==0){
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void vistmain(){
		try {
			String content=HttpUtils.sendGet(httpClient, "https://fly.layui.com/user");
			log.info(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取签到token
	 */
	public static String getSignToken(){
		String token="";
		String url = "https://fly.layui.com/sign/status";
		List<NameValuePair> parms = new ArrayList<NameValuePair>();
		parms.add(new BasicNameValuePair("encoding", "UTF-8"));
		try {
			String content = HttpUtils.httpsPost(url,null);
			log.info(content);
			JSONObject json=(JSONObject)JSON.parse(content);
			JSONObject json2=(JSONObject)json.get("data");
			if(json2.getBoolean("signed")){
				log.info("已签到！");
				return "";
			}
			token=json2.get("token").toString();
			log.info(token);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}	
		return token;
	}
	/**
	 * 签到
	 */
	public static void signIn(String token){
		String url = "https://fly.layui.com/sign/in";
		Map<String, String> requestText=new HashMap<>();
		requestText.put("token", token);
		try {
			String content = HttpUtils.httpsPost(url,requestText);
			log.info(content);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	/**
	 * 读取识别后的验证码
	 * @return
	 */
	 public static String readAnswer() {
	        BufferedReader br;
	        String answer = null;
	        try {
	            br = new BufferedReader(new FileReader(new File(DATA_SWAP)));
	            answer = br.readLine();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return answer;
	    }
	 /**
	  * 获取验证码并识别
	  * @throws Exception
	  */
	public static void imgORC() throws Exception{
		String svg=getCodeImg();
		SvgToImgUtl.saveImg(svg,imgCodePath);
		//调用python程序识别验证码
		 Process proc = null;
         proc = Runtime.getRuntime().exec(pyUrl);
         proc.waitFor();
	}
	
	public static void domain() throws Exception{
		//获取cookie
		log.info("获取cookie=====");
		prelogin();
		log.info("获取验证码并识别=====");
		imgORC();
		
		String imagecode=readAnswer();
		log.info("读取识别验证码："+imagecode);
		int faileCount=1;
		while(!login(imagecode)){
			log.info("登录失败"+(faileCount++)+"次");
			log.info("重新获取验证码");
			imgORC();
			imagecode=readAnswer();
			log.info("读取识别验证码："+imagecode);
		}
		log.info("登录成功");
		vistmain();
		log.info("开始获取token=====");
		String token=getSignToken();
		if(!"".equals(token)){
			signIn(token);
			log.info("自动签到成功！");
		}
		//清空cookies
		HttpUtils.clearCookies();
	}
	static{
		log.setLevel(Level.ALL);  
        try {
			FileHandler fileHandler = new FileHandler("ScheduleJob.log");  
			fileHandler.setLevel(Level.ALL);  
			fileHandler.setFormatter(new LogFormatter());  
			log.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	public static void main(String[] args) throws Exception {
		ScheduleJob job=new ScheduleJob(log);
		job.runJob();
	}
	
	

}
