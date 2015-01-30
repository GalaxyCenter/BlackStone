package apollo.cache;

import java.util.Collection;

public interface ICache
{
	public static final String DUMMY_FQN = "";
	public static final String NOTIFICATION = "notification";
	
	public static final int MAX_FACTOR = Integer.MAX_VALUE;
	public static final int MONTH_FACTOR = 259200; //30天
	public static final int DAY_FACTOR = 259200;//3天
    public static final int HOUR_FACTOR = 21600;//6小时
    public static final int MINUTE_FACTOR = 1800;//30分钟
    public static final int SECOND_FACTOR = 180; //3分钟
	
    public void add(String key, Object value);
	public void add(String key, Object value, int seconds);
	
	public void add(String key, String group, Object value);
	public void add(String key, String group, Object value, int seconds);
	public void add(String key, String[] groups, Object value);
	public void add(String key, String[] groups, Object value, int seconds);
	
	public Object get(String key);
	
	public Collection<Object> getValues();
	public Collection<String> getKeys();
	
	public void remove(String key);
	public void clear();
	public void clear(String group);
}
