/*
 * Copyright (c) 2017 Washington State Department of Transportation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package gov.wa.wsdot.android.wsdot.ui.ferries.bulletins;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import gov.wa.wsdot.android.wsdot.R;
import gov.wa.wsdot.android.wsdot.ui.BaseActivity;
import gov.wa.wsdot.android.wsdot.ui.WsdotApplication;
import gov.wa.wsdot.android.wsdot.util.MyLogger;

public class FerriesRouteAlertsBulletinDetailsActivity extends BaseActivity {

    private static final String TAG = FerriesRouteAlertsBulletinDetailsActivity.class.getSimpleName();
    private Toolbar mToolbar;

	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ferries_route_alerts_bulletin_details);

		Bundle b = getIntent().getExtras();

		String mAlertFullTitle = "Unavailable";

		if (b != null){
			if (b.getString("AlertFullTitle") != null) {
				mAlertFullTitle = b.getString("AlertFullTitle");
			}

            if ( b.getBoolean("from_notification")){
                mTracker = ((WsdotApplication) getApplication()).getDefaultTracker();
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Notification")
                        .setAction("Message Opened")
                        .setLabel("ferries")
                        .build());
            }

        }

		mToolbar = findViewById(R.id.toolbar);
		mToolbar.setTitle(mAlertFullTitle);
		setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

		MyLogger.crashlyticsLog("Ferries", "Tap", "FerriesRouteAlertsBulletinDetailsActivity " + mAlertFullTitle, 1);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
	    case android.R.id.home:
	    	finish();
	    	return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
