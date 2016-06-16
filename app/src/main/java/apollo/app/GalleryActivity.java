package apollo.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import apollo.bll.Gallerys;
import apollo.core.RequestResponseCode;
import apollo.data.model.Gallery;
import apollo.data.model.User;
import apollo.util.AsyncImageLoader;
import apollo.util.DataSet;
import apollo.util.AsyncImageLoader.OnImageLoaderListener;

public class GalleryActivity extends BaseActivity {

	private GridView mGridView; 
	private Button mBtnBack;
	private GalleryPickerAdapter mAdapter;
	private GalleryAsyncTask mGalleryTask;
	private AsyncImageLoader<ImageView> mAsyncImageLoader;
	private List<Gallery> mGallerys;
	private User mUser;
	
	class GalleryAsyncTask extends AsyncTask<Object, Integer, DataSet<Gallery>> {

		@Override
		protected DataSet<Gallery> doInBackground(Object... params) {
			Integer pi = null;
			Integer ps = null;
			DataSet<Gallery> datas = null;
			User user = null;
			
			user = (User) params[0];
			pi = (Integer) params[1];
			ps = (Integer) params[2];
			
			datas = Gallerys.loadGallerys(user, pi, ps);
			mGallerys.addAll(datas.getObjects());
			return datas;
		}
		
		@Override
		protected void onPostExecute(DataSet<Gallery> result) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	class GalleryPickerItem extends ImageView {
	    private Drawable mFrame;
	    private Rect mFrameBounds = new Rect();
	    private Drawable mOverlay;

	    public GalleryPickerItem(Context context) {
	        this(context, null);
	    }

	    public GalleryPickerItem(Context context, AttributeSet attrs) {
	        this(context, attrs, 0);
	    }

	    public GalleryPickerItem(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        mFrame = getResources().getDrawable(R.drawable.frame_gallery_preview);
	        mFrame.setCallback(this);
	    }

	    @Override
	    protected boolean verifyDrawable(Drawable who) {
	        return super.verifyDrawable(who) || (who == mFrame) || (who == mOverlay);
	    }

	    @Override
	    protected void drawableStateChanged() {
	        super.drawableStateChanged();
	        if (mFrame != null) {
	            int[] drawableState = getDrawableState();
	            mFrame.setState(drawableState);
	        }
	    }

	    @Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);
	        final Rect frameBounds = mFrameBounds;
	        if (frameBounds.isEmpty()) {
	            final int w = getWidth();
	            final int h = getHeight();

	            frameBounds.set(0, 0, w, h);
	            mFrame.setBounds(frameBounds);
	            if (mOverlay != null) {
	                mOverlay.setBounds(w - mOverlay.getIntrinsicWidth(),
	                        h - mOverlay.getIntrinsicHeight(), w, h);
	            }
	        }

	        mFrame.draw(canvas);
	        if (mOverlay != null) {
	            mOverlay.draw(canvas);
	        }
	    }


	    @Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);

	        mFrameBounds.setEmpty();
	    }

	    public void setOverlay(int overlayId) {
	        if (overlayId >= 0) {
	            mOverlay = getResources().getDrawable(overlayId);
	            mFrameBounds.setEmpty();
	        } else {
	            mOverlay = null;
	        }
	    }
	}
	
	class GalleryPickerAdapter extends BaseAdapter {
		List<Gallery> mItems = new ArrayList<Gallery>();
	    LayoutInflater mInflater;

	    GalleryPickerAdapter(Context context, List<Gallery> items) {
	    	mItems = items;
	        mInflater = LayoutInflater.from(context);	        
	    }

	    public void updateDisplay() {
	        notifyDataSetChanged();
	    }

	    public void clear() {
	        mItems.clear();
	    }

	    public int getCount() {
	        return mItems.size();
	    }

	    public Object getItem(int position) {
	        return this.mItems.get(position);
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(final int position, View convertView, ViewGroup parent) {
	        View v;
	        TextView titleView;
	        ImageView iv;
	        Gallery item;
	        Bitmap bmp;
	        
	        if (convertView == null) {
	            v = mInflater.inflate(R.layout.item_gallery_picker, null);
	        } else {
	            v = convertView;
	        }

	        titleView = (TextView) v.findViewById(R.id.album_subject);
	        iv = (ImageView) v.findViewById(R.id.thumbnail);
	        item = mItems.get(position);
	        
	        bmp = mAsyncImageLoader.loadImage(item.getCover(), iv, new OnImageLoaderListener<ImageView>() {
                @Override
                public void imageLoaded(Bitmap bmp, ImageView view, String url) {
                	view.setImageDrawable(new BitmapDrawable(bmp));
                }
            });
			if (bmp == null) {
				iv.setImageResource(android.R.color.transparent);
				titleView.setText(item.getSubject());
			} else {
				iv.setImageBitmap(bmp);
			}
            String title = item.getSubject() + " (" + item.getPhotos() + ")";
            titleView.setText(title);
	        titleView.requestLayout();
	        return v;
	    }
	}
	
	public static void startActivity(Activity activity, User user) {
		startActivity(activity, user, null);
	}
	
	public static void startActivity(Activity activity, User user, String action) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("user", user);
		
		intent = new Intent(activity, GalleryActivity.class);
		intent.setAction(action);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}
	
	public static void startActivityForResult(Activity activity, User user, int requestCode) {
		startActivityForResult(activity, user, requestCode, null);
	}
	
	public static void startActivityForResult(Activity activity, User user, int requestCode, String action) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("user", user);
		
		intent = new Intent(activity, GalleryActivity.class);
		intent.setAction(action);
		intent.putExtras(bundle);
		activity.startActivityForResult(intent, requestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestResponseCode.REQUEST_IMAGE_VIEW && resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finish();
		}
	}
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = null;
		
		super.onCreate(savedInstanceState);
		
		mGallerys = new ArrayList<Gallery>();
		mAsyncImageLoader = new AsyncImageLoader<ImageView>();
		intent = getIntent();
		mUser = (User)intent.getExtras().get("user");
		
		setContentView(R.layout.activity_gallery);
		initViews();
		initListeners();
		refresh();
	}
		
	private void refresh() {
		mGalleryTask = new GalleryAsyncTask();
		mGalleryTask.execute(mUser, 1, 20);
	}
	
	private void initListeners() {
		 mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            	Gallery item = null;
	            	
	            	item = mAdapter.mItems.get(position);
	            	AlbumActivity.startActivityForResult(GalleryActivity.this, item, mUser, RequestResponseCode.REQUEST_IMAGE_VIEW, GalleryActivity.this.getIntent().getAction());
	            }
	        });
		 
		 mBtnBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GalleryActivity.this.finish();
				}
			});
	}
	
	private void initViews() {
		mGridView = (GridView) findViewById(R.id.grid);
       
        mAdapter = new GalleryPickerAdapter(this, mGallerys);
        mGridView.setAdapter(mAdapter);
        mBtnBack = (Button) findViewById(R.id.back);
	}
}
