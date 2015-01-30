package apollo.data.sqlitedal;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import apollo.data.R;
import apollo.data.idal.ISectionDataProvider;
import apollo.data.model.Constants;
import apollo.data.model.Section;
import apollo.data.model.SectionGroup;
import apollo.exceptions.ApplicationException;

public class SectionDataProvider implements ISectionDataProvider {

	@Override
	public List<SectionGroup> getSectionGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Section getSection(String id) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String[] columns = null;
		String[] args= null;
		String clause = null;
		Section section = null;
		
		db = DatabaseHelper.getReadDatabase();
		columns = new String[] {Section.Columns.ID, Section.Columns.NAME};
		args = new String[] {id};
		clause = Section.Columns.ID + "=?";
		
		try {
			cursor = db.query(Constants.APOLLO_DATA_TABLE_SECTION, columns, clause, args, null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				section = new Section();
				section.setSectionId(cursor.getString(0));
				section.setName(cursor.getString(1));
			} else {
				throw new ApplicationException(R.string.error_section_not_found);
			}
		} catch (SQLException ex) {
			Log.e(this.getClass().toString(), ex.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}

		return section;
	}

	@Override
	public void add(Section section) {
		SQLiteDatabase db = null;
		ContentValues values = null;
	
		values = new ContentValues(2);
		values.put(Section.Columns.ID, section.getSectionId());
		values.put(Section.Columns.NAME, section.getName());
		
		db = DatabaseHelper.getWriteDatabase();
		db.insert(Constants.APOLLO_DATA_TABLE_SECTION, null, values);
		db.close();
	}

	@Override
	public List<Section> getTopSections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Section> search(String searchTerms) {
		// TODO Auto-generated method stub
		return null;
	}

}
