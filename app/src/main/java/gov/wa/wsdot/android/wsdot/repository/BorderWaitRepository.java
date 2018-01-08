package gov.wa.wsdot.android.wsdot.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import gov.wa.wsdot.android.wsdot.database.borderwaits.BorderWaitDao;
import gov.wa.wsdot.android.wsdot.database.borderwaits.BorderWaitEntity;
import gov.wa.wsdot.android.wsdot.database.caches.CacheEntity;
import gov.wa.wsdot.android.wsdot.util.APIEndPoints;
import gov.wa.wsdot.android.wsdot.util.AppExecutors;
import gov.wa.wsdot.android.wsdot.util.network.ResourceStatus;

/**
 *  Handles access to the border_wait database
 */
@Singleton
public class BorderWaitRepository extends NetworkResourceSyncRepository {

    private static String TAG = BorderWaitRepository.class.getSimpleName();

    private final BorderWaitDao borderWaitDao;

    @Inject
    public BorderWaitRepository(BorderWaitDao borderWaitDao, AppExecutors appExecutors, CacheRepository cacheRepository) {
        // Supply the super class with data needed for super.refreshData()
        super(appExecutors, cacheRepository, (15 * DateUtils.MINUTE_IN_MILLIS),"border_wait");
        this.borderWaitDao = borderWaitDao;
    }

    public LiveData<List<BorderWaitEntity>> getBorderWaitsFor(String direction, MutableLiveData<ResourceStatus> status) {
        super.refreshData(status, false);
        return borderWaitDao.loadBorderWaitsFor(direction);
    }

    @Override
    void fetchData(MutableLiveData<ResourceStatus> status) throws Exception {

        URL url = new URL(APIEndPoints.BORDER_WAITS);
        URLConnection urlConn = url.openConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        StringBuilder jsonFile = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null)
            jsonFile.append(line);
        in.close();

        JSONObject obj = new JSONObject(jsonFile.toString());

        JSONObject result = obj.getJSONObject("waittimes");
        JSONArray items = result.getJSONArray("items");

        List<BorderWaitEntity> waits = new ArrayList<>();

        int numItems = items.length();

        for (int j=0; j < numItems; j++) {
            JSONObject item = items.getJSONObject(j);

            BorderWaitEntity wait = new BorderWaitEntity();

            wait.setBorderWaitId(item.getInt("id"));
            wait.setTitle(item.getString("name"));
            wait.setDirection(item.getString("direction"));
            wait.setLane(item.getString("lane"));
            wait.setRoute(item.getInt("route"));
            wait.setWait(item.getInt("wait"));
            wait.setUpdated(item.getString("updated"));
            wait.setIsStarred(0);
            Log.e(TAG, "checking waits");
            waits.add(wait);
        }

        BorderWaitEntity[] waitsArray = new BorderWaitEntity[waits.size()];
        waitsArray = waits.toArray(waitsArray);

        borderWaitDao.deleteAndInsertTransaction(waitsArray);

        CacheEntity borderCache = new CacheEntity("border_wait", System.currentTimeMillis());
        getCacheRepository().setCacheTime(borderCache);

    }
}
