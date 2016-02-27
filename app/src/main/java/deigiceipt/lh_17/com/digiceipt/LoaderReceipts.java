package deigiceipt.lh_17.com.digiceipt;

/**
 * Created by LH-17 on 2/27/2016.
 */
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class LoaderReceipts extends AsyncTaskLoader<ArrayList<ParseObject>> {

    private static final String TAG = "LoaderCommunityPosts";
    public static final int LOAD_FOLLOWING_POSTS = 0;
    public static final int LOAD_GROUPS_POSTS = 1;
    public static final int LOAD_USER_POSTS = 2;

    private Context context;
    private boolean isLocal;
    private String relationID;
    private int relationType = LOAD_FOLLOWING_POSTS;

    public LoaderReceipts(Context _context) {
        super(_context);
        context = _context;
    }

    @Override
    public ArrayList<ParseObject> loadInBackground() {
        ArrayList<ParseObject> results = new ArrayList<>();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Receipts.PARSE_CLASS_RECEIPTS);
        query.orderByDescending(Receipts.PARSE_FIELD_DATE);
        //query.fromLocalDatastore();
        try {
            List<ParseObject> objs = query.find();
            if(objs != null) {
                results.addAll(objs);
                ParseObject.pinAll(objs);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return results;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public void deliverResult(ArrayList<ParseObject> data) {
        if(isStarted()) {
            super.deliverResult(data);
        }
    }
}
