package apollo.data.dalfactory;

import java.lang.reflect.Constructor;

import apollo.data.idal.IAutoPostDataProvider;
import apollo.data.idal.IBookmarkDataProvider;
import apollo.data.idal.IConfigDataProvider;
import apollo.data.idal.IGalleryDataProvider;
import apollo.data.idal.IPostDataProvider;
import apollo.data.idal.IPrivateMessageDataProvider;
import apollo.data.idal.ISectionDataProvider;
import apollo.data.idal.IThreadDataProvider;
import apollo.data.idal.IUserDataProvider;

public class DataAccess {

    private static Object createProvider(String path, String name) {
        Object instance = null;
        
        try {
            Class<?>       provider    = Class.forName(path + "." + name);
            Constructor<?> constructor = provider.getConstructor();

            instance = constructor.newInstance();
        } catch (Exception ex) {
        }

        return instance;
    }
    
    public static IUserDataProvider createRemoteUserDataProvider() {
        return (IUserDataProvider)createProvider("apollo.data.networkdal", "UserDataProvider");
    }
    
    public static IConfigDataProvider createLocalConfigDataProvider() {
        return (IConfigDataProvider)createProvider("apollo.data.sqlitedal", "ConfigDataProvider");
    }
    
    public static IAutoPostDataProvider createLocalAutoPostDataProvider() {
        return (IAutoPostDataProvider)createProvider("apollo.data.sqlitedal", "AutoPostDataProvider");
    }
    
    public static IUserDataProvider createLocalUserDataProvider() {
        return (IUserDataProvider)createProvider("apollo.data.sqlitedal", "UserDataProvider");
    }
    
    public static ISectionDataProvider createRemoteSectionDataProvider() {
        return (ISectionDataProvider)createProvider("apollo.data.networkdal", "SectionDataProvider");
    }
    
    public static ISectionDataProvider createLocalSectionDataProvider() {
        return (ISectionDataProvider)createProvider("apollo.data.sqlitedal", "SectionDataProvider");
    }
    
    public static IBookmarkDataProvider createLocalBookmarkDataProvider() {
		return (IBookmarkDataProvider)createProvider("apollo.data.sqlitedal", "BookmarkDataProvider");
	}
    
	public static IPostDataProvider createRemotePostDataProvider() {
        return (IPostDataProvider)createProvider("apollo.data.networkdal", "PostDataProvider");
    }
	
	public static IPostDataProvider createLocalPostDataProvider() {
        return (IPostDataProvider)createProvider("apollo.data.sqlitedal", "PostDataProvider");
    }
	
	public static IThreadDataProvider createRemoteThreadDataProvider() {
        return (IThreadDataProvider)createProvider("apollo.data.networkdal", "ThreadDataProvider");
    }
	
	public static IGalleryDataProvider createRemoteGalleryDataProvider() {
		return (IGalleryDataProvider)createProvider("apollo.data.networkdal", "GalleryDataProvider");
	}
	
	public static IPrivateMessageDataProvider createRemotePrivateMessageDataProvider() {
		return (IPrivateMessageDataProvider)createProvider("apollo.data.networkdal", "PrivateMessageDataProvider");
	}
	
	public static IBookmarkDataProvider createRemoteBookmarkDataProvider() {
		return (IBookmarkDataProvider)createProvider("apollo.data.networkdal", "BookmarkDataProvider");
	}
}
