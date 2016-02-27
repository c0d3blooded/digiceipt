package deigiceipt.lh_17.com.digiceipt;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

/**
 * Created by LH-17 on 2/26/2016.
 */
public class ApplicationSetup extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = this.getApplicationContext();
        Parse.enableLocalDatastore(context);
        // Enable Crash Reporting
        Parse.initialize(this, "eHkMFOtxg6sNNhwGS3fTfnE86pstDkpo0BsRBn7Q", "xuxDQEgzB2CB9ckdjDjtbXu2bfBGD0GvPm2oRisS");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("");
    }
}
