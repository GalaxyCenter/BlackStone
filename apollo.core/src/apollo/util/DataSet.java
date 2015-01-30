package apollo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSet<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5224320348416963938L;
	private List<T> objects = new ArrayList<T>();
	private int totalRecords = 0;
	
	public List<T> getObjects() {
		return objects;
	}
	public void setObjects(List<T> objects) {
		this.objects = objects;
	}
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public boolean HasResults() {
		if (objects.size() > 0)
			return true;
		return false;
	}
}
