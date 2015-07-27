package cn.lisa.smartventilator.utility.system;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetHelper {

	public static String httpStringGet(String url) throws Exception {
		return httpStringGet(url, "utf-8");
	}

	/**
	 * 
	 * 
	 * @param url
	 * @return
	 */
	public static Drawable loadImage(String url) {
		try {
			return Drawable.createFromStream((InputStream) new URL(url).getContent(), "test");
		} catch (MalformedURLException e) {
			Log.e("exception", e.getMessage());
		} catch (IOException e) {
			Log.e("exception", e.getMessage());
		}
		return null;
	}

	public static String httpStringGet(String url, String enc) throws Exception {
		// This method for HttpConnection
		String page = "";
		BufferedReader bufferedReader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");

			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);

			HttpGet request = new HttpGet();
			request.setHeader("Content-Type", "text/plain; charset=utf-8");
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent(), enc));

			StringBuffer stringBuffer = new StringBuffer("");
			String line = "";

			String NL = System.getProperty("line.separator");
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line + NL);
			}
			bufferedReader.close();
			page = stringBuffer.toString();
			Log.i("page", page);
			System.out.println(page + "page");
			return page;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					Log.d("BBB", e.toString());
				}
			}
		}
	}

	public static boolean checkNetWorkStatus(Context context) {
		boolean result;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) {
			result = true;
			Log.i("NetStatus", "The net was connected");
		} else {
			result = false;
			Log.i("NetStatus", "The net was bad!");
		}
		return result;
	}

	public static String getJsonStringGet(String uri) throws Exception {
		String result = null;
		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
		conn.setConnectTimeout(6 * 1000);// 设置连接超时
		Log.i("sv", conn.getResponseCode() + conn.getResponseMessage());
		if (conn.getResponseCode() == 200) {
			Log.i("sv", "成功");
			InputStream is = conn.getInputStream();// 得到网络返回的输入流
			result = readData(is, "UTF-8");
		} else {
			Log.i("sv", "失败");
			result = "";
		}
		conn.disconnect();
		return result;

	}

	private static String readData(InputStream inSream, String charsetName) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inSream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inSream.close();
		return new String(data, charsetName);
	}
}
