package apollo.app;

import org.miscwidgets.interpolator.BounceInterpolator;
import org.miscwidgets.interpolator.EasingType.Type;
import org.miscwidgets.widget.Panel;
import org.miscwidgets.widget.Panel.OnPanelListener;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class TestActivity extends BaseActivity implements OnPanelListener {

	private Panel topPanel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		super.setContentView(R.layout.activity_test);
		
		Panel panel;
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_bar);
		View layout = getLayoutInflater().inflate(R.layout.bar_search, null); 

        topPanel = panel = (Panel) layout.findViewById(R.id.layout_search);
        topPanel.setMinimumWidth(300);
        rl.addView(topPanel);
        
        panel.setOnPanelListener(this);
        panel.setInterpolator(new BounceInterpolator(Type.OUT));
        
        Button btn = (Button) findViewById(R.id.ok);
        btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 topPanel.setOpen(!topPanel.isOpen(), true);
			}

        });
        
	}
	 
	@Override
	public void onPanelClosed(Panel panel) {
	}
	@Override
	public void onPanelOpened(Panel panel) {
	}
}
