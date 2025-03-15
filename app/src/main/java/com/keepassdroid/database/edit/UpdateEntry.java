/*
 * Copyright 2009-2011 Brian Pellin.
 *     
 * This file is part of KeePassDroid.
 *
 *  KeePassDroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  KeePassDroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePassDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.keepassdroid.database.edit;

import android.content.Context;

import com.keepassdroid.Database;
import com.keepassdroid.database.PwEntry;
import com.keepassdroid.database.PwGroup;

public class UpdateEntry extends RunnableOnFinish {
	private Database mDb;
	private PwEntry mOldE;
	private PwEntry mNewE;
	private Context ctx;

	/**
	 * SYNCHRONIZER
	 * This field is for synchronization purpose
	 * It determines whether the last modification time should be set to Date.now() upon update
	 * Default: True
	 * The default value is set to true because that is the native behaviour of the function
	 */
	private boolean shouldUpdateModificationTime = true;
	
	public UpdateEntry(Context ctx, Database db, PwEntry oldE, PwEntry newE, OnFinish finish) {
		super(finish);
		
		mDb = db;
		mOldE = oldE;
		mNewE = newE;
		this.ctx = ctx;
		
		// Keep backup of original values in case save fails
		PwEntry backup;
		backup = (PwEntry) mOldE.clone();
		
		mFinish = new AfterUpdate(backup, finish);
	}

	/**
	 * SYNCHRONIZER
	 * @param ctx
	 * @param db
	 * @param oldE
	 * @param newE
	 * @param finish
	 * @param shouldUpdateModificationTime - Whether the last modification time should be updated automatically or not
	 * This constructor is made to support synchronization feature where we need to silence the auto update of last modification time
	 */
	public UpdateEntry(Context ctx, Database db, PwEntry oldE, PwEntry newE, OnFinish finish, boolean shouldUpdateModificationTime) {
		super(finish);

		mDb = db;
		mOldE = oldE;
		mNewE = newE;
		this.ctx = ctx;
		this.shouldUpdateModificationTime = shouldUpdateModificationTime;

		// Keep backup of original values in case save fails
		PwEntry backup;
		backup = (PwEntry) mOldE.clone();

		mFinish = new AfterUpdate(backup, finish);
	}

	@Override
	public void run() {
		// Update entry with new values
		mOldE.assign(mNewE);
		mOldE.touch(this.shouldUpdateModificationTime, true);
		
		
		// Commit to disk
		SaveDB save = new SaveDB(ctx, mDb, mFinish);
		save.run();
	}
	
	private class AfterUpdate extends OnFinish {
		private PwEntry mBackup;
		
		public AfterUpdate(PwEntry backup, OnFinish finish) {
			super(finish);
			
			mBackup = backup;
		}
		
		@Override
		public void run() {
			if ( mSuccess ) {
				// Mark group dirty if title or icon changes
				if ( ! mBackup.getTitle().equals(mNewE.getTitle()) || ! mBackup.getIcon().equals(mNewE.getIcon()) ) {
					PwGroup parent = mBackup.getParent();
					if ( parent != null ) {
						// Resort entries
						parent.sortEntriesByName();

						// Mark parent group dirty
						mDb.dirty.add(parent);
						
					}
				}
			} else {
				// If we fail to save, back out changes to global structure
				mOldE.assign(mBackup);
			}
			
			super.run();
		}
		
	}


}
