package apollo.data.networkdal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import apollo.data.idal.IGalleryDataProvider;
import apollo.data.model.Gallery;
import apollo.data.model.Photo;
import apollo.data.model.User;
import apollo.exceptions.SystemException;
import apollo.net.RequestMethod;
import apollo.net.WebRequest;
import apollo.net.WebResponse;
import apollo.util.DataSet;
import apollo.util.DateTime;

public class GalleryDataProvider extends DataProvider implements IGalleryDataProvider {

	private Photo parsePhoto(JSONObject json) throws JSONException {
		Photo photo = null;
		
		photo = new Photo();
		
		photo.setId(json.getInt("pid"));
		photo.setBigImg(json.getString("bigurl"));
		photo.setMediumImg(json.getString("mediumurl"));
		photo.setSmallImg(json.getString("url"));
		photo.setSubject(json.getString("caption"));
		photo.setPostDate(new DateTime(json.getLong("create")));
		
		return photo;
	}
	
	private Gallery parseGallery(JSONObject json) throws JSONException {
		Gallery g = null;
		
		g = new Gallery();
		g.setId(json.getInt("gid"));
		g.setPhotos(json.getInt("count"));
		g.setCover(json.getString("cover"));
		g.setSubject(json.getString("caption"));
		
		return g;
	}
	
	@Override
	public DataSet<Gallery> getGallerys(User user, int pageIndex, int pageSize) {
		DataSet<Gallery> datas =null;
		List<Gallery> gallerys = null;
		String body = null;
		String url = null;
		String err = null;
		JSONObject json = null;
		JSONObject jga = null;
		JSONObject jd = null;
		JSONArray jarr = null;
			
		url = "http://photo.tianya.cn/gallery?act=getgallery&_=1360999288091&ps=" + pageSize + "&pn=" + pageIndex;
		body = super.getContent(url, RequestMethod.GET, null, null, user);
		gallerys = new ArrayList<Gallery>();
		datas = new DataSet<Gallery>();
		datas.setObjects(gallerys);
		try {			
			json = new JSONObject(body);
			
			if (json.getInt("result") != 1) {
				err = json.getString("error");
				throw new SystemException(err);
			}
			
			jarr = json.getJSONObject("data").getJSONArray("gallery");
			for (int idx=0; idx<jarr.length(); idx++) {
				jga = jarr.getJSONObject(idx);
				gallerys.add(parseGallery(jga));
			}
			
			jd = json.getJSONObject("data").getJSONObject("page");
			datas.setTotalRecords(jd.getInt("count"));
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		} 
		return datas;
	}

	@Override
	public DataSet<Photo> getPhotos(User user, int galleryId, int pageIndex, int pageSize) {
		DataSet<Photo> datas =null;
		List<Photo> photos = null;
		String body = null;
		String url = null;
		String err = null;
		JSONObject json = null;
		JSONObject jp = null;
		JSONObject jd = null;
		JSONArray jarr = null;
		
		url = "http://photo.tianya.cn/photo?act=getphoto&_=1361001344495&gid=" + galleryId + "&ps=" + pageSize + "&pn=" + pageIndex;
		body = super.getContent(url, RequestMethod.GET, null, null, user);
		photos = new ArrayList<Photo>();
		datas = new DataSet<Photo>();
		datas.setObjects(photos);
		try {			
			json = new JSONObject(body);
			if (json.getInt("result") != 1) {
				err = json.getString("error");
				throw new SystemException(err);
			}
			
			jarr = json.getJSONObject("data").getJSONArray("photo");
			for (int idx=0; idx<jarr.length(); idx++) {
				jp = jarr.getJSONObject(idx);
				photos.add(parsePhoto(jp));
			}
			
			jd = json.getJSONObject("data").getJSONObject("page");
			datas.setTotalRecords(jd.getInt("count"));
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return datas;
	}

	@Override
	public Gallery createGallery(User user, String subject, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Photo add(User user, String subject, String description, String filepath) {
		WebRequest _req = null;
		WebResponse _resp = null;
		HashMap<String, String> params = null;
		HashMap<String, String> propertys = null;
		String body = null;
		String url = null;
		File file = null;
		Photo photo = null;
		Pattern pattern = null;
		Matcher matcher = null;
		JSONObject json = null;
		String err = null;
		
		url = "http://photo.tianya.cn/photo?act=uploadphoto";
		
		propertys = new HashMap<String, String>();
		propertys.put("Referer", "http://www.tianya.cn");
		propertys.put("Cookie", user.getTicket());
		
		_req = new WebRequest();
		_req.setResponseCharset("utf-8");
		_req.setContentCharset("utf-8");
 
		params = new HashMap<String, String>();
		params.put("app", "bbs");
		params.put("watermark", "1");
		
		file = new File(filepath);
		try {
			_resp = _req.create(url, params, propertys, file);
			body = new String(_resp.getContent(), _resp.getContentEncoding());
		} catch (IOException ex) {
			throw new SystemException(ex.getMessage());
		}
		
		pattern = Pattern.compile("<body>(.*?)</body>");
		matcher = pattern.matcher(body);
		if (matcher.find()) {
			body = matcher.group(1);
		} else {
			throw new SystemException("Create photo fail.");
		}
		
		try {
			json = new JSONObject(body);
			
			if (json.getInt("result") != 1) {
				err = json.getString("error");
				throw new SystemException(err);
			}
			
			
			json = json.getJSONObject("data").getJSONArray("photo").getJSONObject(0);
			photo = parsePhoto(json);
			
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		
		return photo;
	}

	@Override
	public int getPhotos(User user) {
		int count = 0;
		String body = null;
		String url = null;
		String err = null;
		JSONObject json = null;	
		
		url = "http://photo.tianya.cn/other/gallery?act=getphotocount&uid=" + user.getUserId();
		body = super.getContent(url, RequestMethod.GET, null, null, user);
		try {			
			json = new JSONObject(body);
			if (json.getInt("result") != 1) {
				err = json.getString("error");
				throw new SystemException(err);
			}
			count = json.getJSONObject("data").getInt("count");
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return count;
	}

}
