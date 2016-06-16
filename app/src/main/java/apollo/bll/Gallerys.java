package apollo.bll;

import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IGalleryDataProvider;
import apollo.data.model.Gallery;
import apollo.data.model.Photo;
import apollo.data.model.User;
import apollo.util.DataSet;

public class Gallerys {
	private static IGalleryDataProvider remoteProvider;
	
	static {
		remoteProvider = DataAccess.createRemoteGalleryDataProvider();
	}
	
	public static DataSet<Gallery> loadGallerys(User user, int pageIndex, int pageSize) {
		return remoteProvider.getGallerys(user, pageIndex, pageSize);
	}
	
	public static DataSet<Photo> loadPhotos(User user, int galleryId, int pageIndex, int pageSize) {
		return remoteProvider.getPhotos(user, galleryId, pageIndex, pageSize);
	}
	
	public static Photo add(User user, String subject, String description, String filepath) {
		return remoteProvider.add(user, subject, description, filepath);
	}
	
	public static int getPhotos(User user) {
		return remoteProvider.getPhotos(user);
	}
}
