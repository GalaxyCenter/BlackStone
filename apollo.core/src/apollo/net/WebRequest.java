package apollo.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import apollo.util.Encoding;
import apollo.util.FileUtil;

/**
 * HTTP�������
 */
public class WebRequest {
	public static final byte[] FIELD_SEPARATOR = { 0x0d, 0x0a };
	public static final byte[] STREAM_TERMINATOR = { 0x2d, 0x2d };
	public static String BOUNDARY = "";

	private String responseCharset;
	private String contentCharset;
	private RequestMethod method;
	private String host;
	
	static {
		for (int idx = 0; idx < 0x20; idx++) {
			BOUNDARY += (char) (97 + Math.random() * 25);
		}
	}
	
	public WebRequest() {
		this.responseCharset = Charset.defaultCharset().name();
		this.contentCharset = Charset.defaultCharset().name();
		this.method = RequestMethod.GET;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}
	public RequestMethod getMethod() {
		return this.method;
	}
	
	public String getResponseCharset() {
		return this.responseCharset;
	}
	
	public String getContentCharset() {
		return contentCharset;
	}

	public void setContentCharset(String contentCharset) {
		this.contentCharset = contentCharset;
	}
	
	public String getHost() {
		return this.host;
	}
 
	/**
	 * ����Ĭ�ϵ���Ӧ�ַ���
	 */
	public void setResponseCharset(String responseCharset) {
		this.responseCharset = responseCharset;
	}
	
	/**
	 * ��������
	 * 
	 * @param urlString
	 *            URL��ַ
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public WebResponse create(String url) throws IOException {
		return this.create(url, this.method, null, null);
	}
 
	/**
	 * ��������
	 * 
	 * @param urlString
	 *            URL��ַ
	 * @param params
	 *            ��������
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public WebResponse create(String url, Map<String, String> params) throws IOException {
		return this.create(url, this.method, params, null);
	}
 
	/**
	 * ��������
	 * 
	 * @param urlString
	 *            URL��ַ
	 * @param params
	 *            ��������
	 * @param propertys
	 *            ��������
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public WebResponse create(String url, Map<String, String> params, Map<String, String> propertys) throws IOException {
		return this.create(url, this.method, params, propertys);
	}
	
	public WebResponse create(String url, Map<String, String> parameters, Map<String, String> propertys, File file) throws IOException {
		HttpURLConnection conn = null;
		URL uri = null;
		OutputStream os = null;
		byte[] datas = null;
		
		uri = new URL(url);
		conn = (HttpURLConnection) uri.openConnection();
		conn.setRequestMethod(RequestMethod.POST.toString());
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		
		if (propertys != null) {
			for (String key : propertys.keySet()) {
				conn.addRequestProperty(key, propertys.get(key));
			}
		}
		conn.addRequestProperty("Content-type", "multipart/form-data; boundary=" + BOUNDARY);
		
		os = conn.getOutputStream();

		if (parameters != null) {
			for (String key : parameters.keySet()) {
				os.write(STREAM_TERMINATOR);
				os.write(BOUNDARY.getBytes(this.contentCharset));
				os.write(FIELD_SEPARATOR);
                os.write(("Content-Disposition: form-data; name=\"" + key + "\"").getBytes(this.contentCharset));
                os.write(FIELD_SEPARATOR);
                os.write(FIELD_SEPARATOR);
                os.write(parameters.get(key).getBytes(this.contentCharset));
                os.write(FIELD_SEPARATOR);
			}
			
			os.write(STREAM_TERMINATOR);
			os.write(BOUNDARY.getBytes(this.contentCharset));
			os.write(FIELD_SEPARATOR);
		}
		
		// д���ϴ�����	
        os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").getBytes(this.contentCharset));
        os.write(FIELD_SEPARATOR);// \r\n
        os.write("Content-Type: application/octet-stream".getBytes(this.contentCharset));
        os.write(FIELD_SEPARATOR);// \r\n
        os.write(FIELD_SEPARATOR);// \r\n
        
        datas = FileUtil.getFileData(file);
        os.write(datas);
        os.write(FIELD_SEPARATOR);// \r\n
        os.write(STREAM_TERMINATOR);
        os.write(BOUNDARY.getBytes(this.contentCharset));
                  
        // д���ļ���β�ķָ���
        os.write(STREAM_TERMINATOR);// --
        os.write(FIELD_SEPARATOR);// \r\n

		return this.createResponse(url, conn);
	}
	/**
	 * ����HTTP����
	 * 
	 * @param url_str
	 * @return ��ӳ����
	 * @throws IOException
	 */
	private WebResponse create(String url_str, RequestMethod method, Map<String, String> parameters, Map<String, String> propertys)
			throws IOException {
		HttpURLConnection conn = null;
		URL url = null;
		
		if (RequestMethod.GET == method && parameters != null) {
			StringBuffer param = new StringBuffer();
			int i = 0;
			for (String key : parameters.keySet()) {
				if (i == 0)
					param.append("?");
				else
					param.append("&");
				param.append(key).append("=").append(Encoding.urlEncode(parameters.get(key), this.contentCharset));
				i++;
			}
			url_str += param;
		}
		
		url = new URL(url_str);
		this.host = url.getHost();
		conn = (HttpURLConnection) url.openConnection();
 
		conn.setRequestMethod(method.toString());
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
 
		if (propertys != null)
			for (String key : propertys.keySet()) {
				conn.addRequestProperty(key, propertys.get(key));
			}
 
		if (RequestMethod.POST == method && parameters != null) {
			StringBuffer param = new StringBuffer();
			int idx = 0;
			for (String key : parameters.keySet()) {
				if (key.length() == 0) {
					param.append(parameters.get(key));
				} else {
					if (idx != 0) {
						param.append("&");
					}
					param.append(key).append("=").append(parameters.get(key));
				}
				idx ++;
			}
			conn.getOutputStream().write(param.toString().getBytes(this.contentCharset));
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
		}
 
		return this.createResponse(url_str, conn);
	}
 
	/**
	 * �õ���Ӧ����
	 * 
	 * @param conn
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	private WebResponse createResponse(String url_str, HttpURLConnection conn) throws IOException {
		WebResponse resp = null;
		InputStream in = null;
		ByteArrayOutputStream baos = null;
		//StringBuffer buffer = null;
		//String line = null;
		byte[] bytes = null;
		
		resp = new WebResponse();
		//resp.urlString = url_str;
		resp.defaultPort = conn.getURL().getDefaultPort();
		resp.file = conn.getURL().getFile();
		resp.host = conn.getURL().getHost();
		resp.path = conn.getURL().getPath();
		resp.port = conn.getURL().getPort();
		resp.protocol = conn.getURL().getProtocol();
		resp.query = conn.getURL().getQuery();
		resp.ref = conn.getURL().getRef();
		resp.userInfo = conn.getURL().getUserInfo();
		resp.code = conn.getResponseCode();
		resp.urlString = conn.getURL().toString();
		resp.message = conn.getResponseMessage();
		resp.contentType = conn.getContentType();
		resp.method = conn.getRequestMethod();
		resp.connectTimeout = conn.getConnectTimeout();
		resp.readTimeout = conn.getReadTimeout();
		resp.m_conn = conn;
		
		if ("".equals(resp.contentType) == false) {
			int char_pos = resp.contentType.indexOf("charset=");
			if (char_pos > 0) {
				int end_pos = resp.contentType.length();
				resp.contentEncoding = resp.contentType.substring(char_pos + 8,
						end_pos);
			}
		}
		if (resp.contentEncoding == null)
			resp.contentEncoding = this.responseCharset;
		
		try {
			in = conn.getInputStream();
			baos = new ByteArrayOutputStream();
			bytes = new byte[4096];
			int len = 0;
			while ((len = in.read(bytes)) != -1) {
				baos.write(bytes, 0, len);
			}
			baos.close();
			in.close();
			resp.content = baos.toByteArray();
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return resp;
	}
}