package deigiceipt.lh_17.com.digiceipt;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.parse.ParseObject;

public class ActivityMain extends AppCompatActivity implements FragmentMain.ReceiptActionListener {


    public static final String TAG = "ActivityMain";
    private FragmentMain fragmentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() < 1) {
            fragmentMain = new FragmentMain();
            FragmentTransaction ft = getFragmentTransaction();
            ft.add(R.id.container, fragmentMain, FragmentMain.TAG);
            ft.commit();
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
            onNewReceipt(intent);
    }


    @Override
    public void onNewReceipt(Intent intent) {
        FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentByTag(FragmentMain.TAG);
        if(fragmentMain != null)
            fragmentMain.addNewReceipt(intent);
    }

    @Override
    public void onOpenReceipt(ParseObject receipt) {
        FragmentReceipt fragmentLogin = new FragmentReceipt();
        fragmentLogin.setReceipt(receipt);
        FragmentTransaction ft = getFragmentTransaction();
        ft.add(R.id.container, fragmentLogin, FragmentReceipt.TAG);
        ft.addToBackStack(FragmentReceipt.TAG);
        ft.commit();
    }

    @Override
    public void onDeleteReceipt(ParseObject receipt) {
        receipt.deleteEventually();
        onBackPressed();
        FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentByTag(FragmentMain.TAG);
        if(fragmentMain != null)
            fragmentMain.runLoader(true);
    }

    //default fragment transaction for the activity
    public FragmentTransaction getFragmentTransaction(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        return ft;
    }
}
