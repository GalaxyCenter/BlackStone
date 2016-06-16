package apollo.preference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import apollo.util.StringUtil;

public class MultiSelectListPreference extends DialogPreference {
	private CharSequence[] mEntries;
	private CharSequence[] mEntryValues;
	private List<String> mValues;
	private String mRawValues;
	
	private boolean mChecked[];

	private static final String SEPARATOR = "u0fly_@#asdf*&_ylf0u";
	
	private static class SavedState extends BaseSavedState {
        String value;
        
        public SavedState(Parcel source) {
            super(source);
            value = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(value);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
	
	public MultiSelectListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, new int[] {
				android.R.attr.entries, android.R.attr.entryValues }, 0, 0);
		mEntries = a.getTextArray(0);
		mEntryValues = a.getTextArray(1);
		a.recycle();
	}

	public MultiSelectListPreference(Context context) {
		super(context, null);
	}

	public void setEntries(CharSequence[] entries) {
		this.mEntries = entries;
	}

	public void setEntries(int entriesResId) {
		setEntries(getContext().getResources().getTextArray(entriesResId));
	}

	public CharSequence[] getEntries() {
		return mEntries;
	}

	public void setEntryValues(CharSequence[] entryValues) {
		this.mEntryValues = entryValues;
	}

	public void setEntryValues(int entryValuesResId) {
		setEntryValues(getContext().getResources().getTextArray(
				entryValuesResId));
	}

	public CharSequence[] getEntryValues() {
		return mEntryValues;
	}

	public void setValues(List<String> values) {
		this.mValues = values;

		persistStringSet(values);
	}

	public List<String> getValues() {
		return mValues;
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);

		if (mEntries == null || mEntryValues == null) {
			throw new IllegalStateException(
					"FixedMultiSelectListPreference requires an entries array and an entryValues array.");
		}

		mChecked = new boolean[mEntryValues.length];
		List<CharSequence> list = Arrays.asList(mEntryValues);

		if (mValues != null) {
			for (String value : mValues) {
				int index = list.indexOf(value);

				if (index != -1) {
					mChecked[index] = true;
				}
			}
		}

		builder.setMultiChoiceItems(mEntries, mChecked,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						mChecked[which] = isChecked;
					}
				});
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult && mEntryValues != null) {
			List<String> newValues = new ArrayList<String>();
			for (int i = 0; i < mEntryValues.length; ++i) {
				if (mChecked[i]) {
					newValues.add(mEntryValues[i].toString());
				}
			}

			if (callChangeListener(newValues)) {
				setValues(newValues);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		CharSequence[] array = a.getTextArray(index);

		Set<String> set = new HashSet<String>();

		for (CharSequence item : array) {
			set.add(item.toString());
		}

		return set;
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		@SuppressWarnings("unchecked")
		List<String> defaultValues = (List<String>) defaultValue;

		setValues((restorePersistedValue ? getPersistedStringSet(mValues)
				: defaultValues));
	}

	private List<String> getPersistedStringSet(List<String> defaultReturnValue) {
		String key = getKey();

		/* unsupport andorid2.x
		return getSharedPreferences().getStringSet(key, defaultReturnValue);
		*/
		
		String val = null;
		
		val = getSharedPreferences().getString(key, null);
		if (val == null) 
			return defaultReturnValue;
		
		String[] vls = null;
		List<String> values = null;
		
		vls = val.split(SEPARATOR);
		values = Arrays.asList(vls);
		return values;
	}

	private boolean persistStringSet(List<String> values) {
		if (shouldPersist()) {
			// Shouldn't store null
			if (values == getPersistedStringSet(null)) {
				// It's already there, so the same as persisting
				return true;
			}
		}

		/* unsupport andorid2.x
		SharedPreferences.Editor editor = getEditor();
		editor.putStringSet(getKey(), values);
		
		// Default class does fancy stuff here
		editor.apply();
		*/
	
		SharedPreferences.Editor editor = null;
		
		mRawValues = StringUtil.join(values, SEPARATOR);
		
		editor = getEditor();
		editor.putString(getKey(), mRawValues);
		editor.apply();
		return true;
	}

	@Override
	protected Parcelable onSaveInstanceState() {		
		final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        
        final SavedState myState = new SavedState(superState);
        myState.value = mRawValues;
        return myState;
	}

}
