package apollo.data.idal;

import apollo.data.model.Gallery;
import apollo.data.model.Photo;
import apollo.data.model.User;
import apollo.util.DataSet;

public interface IGalleryDataProvider {

	DataSet<Gallery> getGallerys(User user, int pageIndex, int pageSize);
	DataSet<Photo> getPhotos(User user, int galleryId, int pageIndex, int pageSize);

	Gallery createGallery(User user, String subject, String description);
	Photo add(User user, String subject, String description, String filepath);
	
	int getPhotos(User user);
}
