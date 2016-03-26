package deigiceipt.lh_17.com.digiceipt;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;


public class FragmentMain extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<ParseObject>> {
    private ReceiptActionListener mListener;
    public static final String TAG = "FragmentMain";
    private FragmentMain thisFragment;
    private TextView txtNFC, txtEmpty;
    private NfcAdapter nfcAdapter;
    private ToggleButton btnToggle;
    private ExpandableListView listView;
    private ReceiptListAdapter listAdapter;
    private ProgressBar progressBar;
    private SearchView searchView;

    public static final int LOADER_ID = 1001;
    private String query = "";
    private boolean localLoading = false;
    public FragmentMain() {
        // Required empty public constructor
    }

    public interface ReceiptActionListener {
        void onNewReceipt(Intent intent);
        void onOpenReceipt(ParseObject receipt);
        void onDeleteReceipt(ParseObject receipt);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ExpandableListView) view.findViewById(R.id.listView);
        txtEmpty = (TextView) view.findViewById(R.id.txtEmpty);
        txtNFC = (TextView) view.findViewById(R.id.txtNfc);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btnToggle = (ToggleButton) view.findViewById(R.id.btnToggle);

        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        if(nfcAdapter == null){
            Toast.makeText(getActivity(), "Does not support NFC", Toast.LENGTH_SHORT).show();
        }
        else if(!nfcAdapter.isEnabled())
            Toast.makeText(getActivity(), "NFC is off", Toast.LENGTH_SHORT).show();
        listAdapter = new ReceiptListAdapter(getActivity(), new ArrayList<ParseObject>());
        listView.setAdapter(listAdapter);
        localLoading = false;
        runLoader(false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        thisFragment = this;
        if (context instanceof ReceiptActionListener) {
            mListener = (ReceiptActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReceiptActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        Intent intent = new Intent(getActivity(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0,
                intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, intentFilter,
                null);

        super.onResume();
    }

    @Override
    public void onPause() {
        nfcAdapter.disableForegroundDispatch(getActivity());
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
        //implement the searchview onto the fragment for the stored workouts page only
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        if (searchView == null)
            Log.i(TAG, "Could not set up search view, view is null.");
        else {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconified(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    searchView.clearFocus();
                    setQuery(s); //sets fragment query
                    localLoading = true;
                    runLoader(true);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    setQuery(s); //sets fragment query
                    localLoading = true;
                    runLoader(true);
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void runLoader(boolean local){
        localLoading = local;
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, thisFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                int backStackCount = fm.getBackStackEntryCount();
                if(backStackCount > 0)
                    getActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addNewReceipt(Intent intent){
        Toast.makeText(getActivity(), "NFC intent received", Toast.LENGTH_LONG).show();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(parcelables != null && parcelables.length > 0){
            readTextFromTag((NdefMessage)parcelables[0]);
        }
        else {
            Toast.makeText(getActivity(), "No Ndef messages", Toast.LENGTH_SHORT).show();
        }
    }

    private void readTextFromTag(NdefMessage ndef) {
        NdefRecord[] records = ndef.getRecords();
        if(records != null && records.length > 0) {
            NdefRecord record = records[0];
            String tagContent = getTextFromNdefRecord(record);
            Receipts.saveReceipt(tagContent);
            runLoader(true);
            txtNFC.setText(tagContent);
        }
        else {
            Toast.makeText(getActivity(), "No NDEF messages!", Toast.LENGTH_SHORT).show();
        }
    }

    private void formatTag(Tag tag, NdefMessage message){
        try{
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if(ndefFormatable == null){
                Toast.makeText(getActivity(), "Tag is not Ndef", Toast.LENGTH_SHORT).show();
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

    private void writeNdefMessage(Tag tag, NdefMessage message){
        try {
            if(tag == null){
                Toast.makeText(getActivity(), "Tag object can't be null", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Tag isnt writeable", Toast.LENGTH_SHORT).show();
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
    public Loader<ArrayList<ParseObject>> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);
        return new LoaderReceipts(getActivity(), query, localLoading);
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
