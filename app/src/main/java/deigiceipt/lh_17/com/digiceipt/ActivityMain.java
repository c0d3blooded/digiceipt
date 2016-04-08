package deigiceipt.lh_17.com.digiceipt;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

public class ActivityMain extends AppCompatActivity implements FragmentMain.ReceiptActionListener {


    public static final String TAG = "ActivityMain";
    private static final int LOGIN_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() < 1) {
            if(ParseUser.getCurrentUser() != null) {
                FragmentMain fragmentMain = new FragmentMain();
                FragmentTransaction ft = getFragmentTransaction();
                ft.add(R.id.container, fragmentMain, FragmentMain.TAG);
                ft.commit();
            }
            else {
                // User clicked to log in.
                ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                        ActivityMain.this);
                loginBuilder.setFacebookLoginEnabled(false);
                loginBuilder.setTwitterLoginEnabled(false);
                startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
            }
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ParseUser.getCurrentUser() != null) {
            FragmentMain fragmentMain = new FragmentMain();
            FragmentTransaction ft = getFragmentTransaction();
            ft.add(R.id.container, fragmentMain, FragmentMain.TAG);
            ft.commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
            onNewReceipt(intent);
    }


    @Override
    public void onLogout() {
        ParseUser.logOut();
        // User clicked to log in.
        ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                ActivityMain.this);
        loginBuilder.setFacebookLoginEnabled(false);
        loginBuilder.setTwitterLoginEnabled(false);
        startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
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
        Toast.makeText(this, "Receipt has been deleted", Toast.LENGTH_SHORT).show();
    }

    //default fragment transaction for the activity
    public FragmentTransaction getFragmentTransaction(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        return ft;
    }
}
