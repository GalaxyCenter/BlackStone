package apollo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import apollo.app.R;

public class AnimationTabHost extends TabHost {

	private Animation slideLeftIn;// ����Ļ��߽���
	private Animation slideLeftOut;// ����Ļ��߳�ȥ
	private Animation slideRightIn;// ����Ļ�ұ߽���
	private Animation slideRightOut;// ����Ļ�ұ߳�ȥ

	/** ��¼�Ƿ�򿪶���Ч�� */
	private boolean isOpenAnimation;
	/** ��¼��ǰ��ǩҳ������ */
	private int mTabCount;

	public AnimationTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		/** ��ʼ��Ĭ�϶��� */
		slideLeftIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_out);
		isOpenAnimation = false;// ����Ĭ�Ϲر�

	}

	/**
	 * �����Ƿ�򿪶���Ч��
	 * 
	 * @param isOpenAnimation
	 *            true����
	 */
	public void setOpenAnimation(boolean isOpenAnimation) {
		this.isOpenAnimation = isOpenAnimation;
	}

	/**
	 * 
	 * @return ���ص�ǰ��ǩҳ������
	 */

	public int getTabCount() {
		return mTabCount;
	}

	@Override
	public void addTab(TabSpec tabSpec) {
		mTabCount++;
		super.addTab(tabSpec);
	}

	// ��дsetCurrentTab(int index) ������������붯�����ؼ�������⡣
	@Override
	public void setCurrentTab(int index) {
		// �л�ǰ����ҳ��ҳ��
		int mCurrentTabID = getCurrentTab();
		if (null != getCurrentView()) {
			// ��һ������ Tab ʱ����ֵΪ null��
			if (isOpenAnimation) {
				// �뿪��ҳ��
				// ѭ��ʱ��ĩҳ����һҳ(�߽紦��)
				if (mCurrentTabID == (mTabCount - 1) && index == 0) {
					getCurrentView().startAnimation(slideLeftOut);
				}
				// ѭ��ʱ����ҳ��ĩҳ
				else if (mCurrentTabID == 0 && index == (mTabCount - 1)) {
					getCurrentView().startAnimation(slideRightOut);
				}
				// �л����ұߵĽ��棬������뿪
				else if (index > mCurrentTabID) {
					getCurrentView().startAnimation(slideLeftOut);
				}
				// �л�����ߵĽ��棬���ұ��뿪
				else if (index < mCurrentTabID) {
					getCurrentView().startAnimation(slideRightOut);
				}
			}
		}
		// ���õ�ǰҳ
		super.setCurrentTab(index);

		if (isOpenAnimation) {
			// ��ǰҳ�����Ƕ���
			// ѭ��ʱ��ĩҳ����һҳ
			if (mCurrentTabID == (mTabCount - 1) && index == 0) {
				getCurrentView().startAnimation(slideRightIn);
			}
			// ѭ��ʱ����ҳ��ĩҳ(�߽紦��)
			else if (mCurrentTabID == 0 && index == (mTabCount - 1)) {
				getCurrentView().startAnimation(slideLeftIn);
			}
			// �л����ұߵĽ��棬���ұ߽���
			else if (index > mCurrentTabID) {
				getCurrentView().startAnimation(slideRightIn);
			}
			// �л�����ߵĽ��棬����߽���
			else if (index < mCurrentTabID) {
				getCurrentView().startAnimation(slideLeftIn);
			}
		}
	}
}