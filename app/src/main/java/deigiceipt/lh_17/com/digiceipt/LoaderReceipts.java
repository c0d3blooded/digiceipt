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

    private static final String TAG = "LoaderReceipts";

    private Context context;
    private String mQuery = null;
    private boolean local;

    public LoaderReceipts(Context _context, String query, boolean local) {
        super(_context);
        context = _context;
        this.local = local;
        if(query != null)
            this.mQuery = query.trim();
    }

    @Override
    public ArrayList<ParseObject> loadInBackground() {
        ArrayList<ParseObject> results = new ArrayList<>();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Receipts.PARSE_CLASS_RECEIPTS);

        //if the query is filtered by String
        if(mQuery != null && mQuery.length() > 0) {
            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            ParseQuery<ParseObject> queryName = new ParseQuery<ParseObject>(Receipts.PARSE_CLASS_RECEIPTS);
            queryName.whereMatches(Receipts.PARSE_FIELD_NAME, mQuery, "i");
            //queryName.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

            ParseQuery<ParseObject> queryReceipts = new ParseQuery<ParseObject>(Receipts.PARSE_CLASS_RECEIPTS);
            queryReceipts.whereMatches(Receipts.PARSE_FIELD_NAME, mQuery, "i");
            //queryName.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

            queries.add(queryName);
            queries.add(queryReceipts);

            query = ParseQuery.or(queries);
        }

        query.orderByDescending(Receipts.PARSE_FIELD_DATE);
        if(local)
            query.fromLocalDatastore();
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
