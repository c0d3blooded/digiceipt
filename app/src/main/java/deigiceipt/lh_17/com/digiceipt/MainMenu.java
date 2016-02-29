package deigiceipt.lh_17.com.digiceipt;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.nfc.NfcAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainMenu extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<ParseObject>>{

    public static final int LOADER_ID = 1001;

    public static final String TAG = "Main";
    private TextView txtNFC, txtEmpty;
    private NfcAdapter nfcAdapter;
    private ToggleButton btnToggle;
    private ExpandableListView listView;
    private ReceiptListAdapter listAdapter;
    private ProgressBar progressBar;
    private SearchView searchView;

    private MainMenu activity;

    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        activity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ExpandableListView) findViewById(R.id.listView);
        txtEmpty = (TextView) findViewById(R.id.txtEmpty);
        txtNFC = (TextView) findViewById(R.id.txtNfc);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnToggle = (ToggleButton) findViewById(R.id.btnToggle);
        /*btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReadWrite(v);
            }
        });*/
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this, "Does not support NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        else if(!nfcAdapter.isEnabled())
            Toast.makeText(this, "NFC is off", Toast.LENGTH_SHORT).show();
        listAdapter = new ReceiptListAdapter(this, new ArrayList<ParseObject>());
        listView.setAdapter(listAdapter);
        /*ParseObject testObject = new ParseObject(Receipts.PARSE_CLASS_RECEIPTS);
        testObject.put(Receipts.PARSE_FIELD_NAME, "Test Vendor 1");
        testObject.put(Receipts.PARSE_FIELD_DATE, new Date());
        testObject.put(Receipts.PARSE_FIELD_ADDRESS, "UMass Amherst");

        ArrayList<String> objs = new ArrayList<>();

        JSONObject item1 = new JSONObject();
        JSONObject item2 = new JSONObject();
        JSONObject item3 = new JSONObject();
        try {
            item1.put(Receipts.JSON_FIELD_DESCRIPTION, "Water bottle!");
            item1.put(Receipts.JSON_FIELD_PRICE, 50.5);
            item1.put(Receipts.JSON_FIELD_UPC_CODE, "128489301293");

            item2.put(Receipts.JSON_FIELD_DESCRIPTION, "Another Water bottle!");
            item2.put(Receipts.JSON_FIELD_PRICE, 230.5);
            item2.put(Receipts.JSON_FIELD_UPC_CODE, "145434123123");

            item3.put(Receipts.JSON_FIELD_DESCRIPTION, "Another nother Water bottle!");
            item3.put(Receipts.JSON_FIELD_PRICE, 0.5);
            item3.put(Receipts.JSON_FIELD_UPC_CODE, "3522341442353");

            objs.add(item1.toString());
            objs.add(item2.toString());
            objs.add(item3.toString());
        }
        catch (JSONException e) {e.printStackTrace();}

        testObject.put(Receipts.PARSE_FIELD_RECEIPTS, objs);

        testObject.saveInBackground();*/
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    public void toggleReadWrite(View view){
        txtNFC.setText("");
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord){
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding;
            if ((payload[0] & 128) == 0)
                textEncoding = "UTF-8";
            else
                textEncoding = "UTF-16";
            int langSize = payload[0] & 0063;
            tagContent = new String(payload, langSize + 1, payload.length - langSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return tagContent;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this, "NFC intent received", Toast.LENGTH_LONG).show();
            //read
            //if(btnToggle.isChecked()){
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if(parcelables != null && parcelables.length > 0){
                    readTextFromTag((NdefMessage)parcelables[0]);
                }
                else {
                    Toast.makeText(this, "No Ndef messages", Toast.LENGTH_SHORT).show();
                }
            //}
            //write
            /*else {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage message = createNdef("Test String");
                writeNdefMessage(tag, message);
            }*/
        }
    }

    private void readTextFromTag(NdefMessage ndef) {
        NdefRecord[] records = ndef.getRecords();
        if(records != null && records.length > 0) {
            NdefRecord record = records[0];
            String tagContent = getTextFromNdefRecord(record);
            Receipts.saveReceipt(tagContent);
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
            txtNFC.setText(tagContent);
        }
        else {
            Toast.makeText(this, "No NDEF messages!", Toast.LENGTH_SHORT).show();
        }
    }

    private void formatTag(Tag tag, NdefMessage message){
        try{
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if(ndefFormatable == null){
                Toast.makeText(this, "Tag is not Ndef", Toast.LENGTH_SHORT).show();
            }
            else {
                ndefFormatable.connect();
                ndefFormatable.format(message);
                ndefFormatable.close();
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage message){
        try {
            if(tag == null){
                Toast.makeText(this, "Tag object can't be null", Toast.LENGTH_SHORT).show();
            }
            else {
                Ndef ndef = Ndef.get(tag);
                if(ndef == null){
                    //formats the tag to ndef
                    formatTag(tag, message);
                }
                else {
                    ndef.connect();
                    if(!ndef.isWritable()){
                        Toast.makeText(this, "Tag isnt writeable", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ndef.writeNdefMessage(message);
                    }
                    ndef.close();
                }
            }
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }


    private NdefRecord createTextRecord(String str){
        try{
            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            final byte[] text = str.getBytes("UTF-8");
            final int size = lang.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + size + textLength);

            payload.write((byte) (size & 0x1F));
            payload.write(lang, 0, size);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        }
        catch (UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        return null;
    }


    private NdefMessage createNdef(String str){
        NdefRecord record = createTextRecord(str);
        NdefMessage message = new NdefMessage(new NdefRecord[]{ record });
        return message;

    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter,
                null);

        super.onResume();
    }




    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //implement the searchview onto the fragment for the stored workouts page only
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        if (searchView == null)
            Log.i(TAG, "Could not set up search view, view is null.");
        else {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconified(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    searchView.clearFocus();
                    setQuery(s); //sets fragment query
                    getSupportLoaderManager().restartLoader(LOADER_ID, null, activity);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    setQuery(s); //sets fragment query
                    getSupportLoaderManager().restartLoader(LOADER_ID, null, activity);
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<ParseObject>> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);
        return new LoaderReceipts(this, query);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ParseObject>> loader, ArrayList<ParseObject> data) {
        progressBar.setVisibility(View.GONE);
        if(data.size() > 0) {
            txtEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listAdapter.swapItems(data);
            for(int a=0; a<listAdapter.getGroupCount(); a++){
                listView.expandGroup(a);
            }
        }
        else {
            listView.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ParseObject>> loader) {
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
