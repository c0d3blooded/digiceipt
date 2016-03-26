package deigiceipt.lh_17.com.digiceipt;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;


public class FragmentReceipt extends Fragment {

    public static final String TAG = "FragmentReceipt";
    private ParseObject receipt;

    public FragmentReceipt() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt, container, false);
    }

    public void setReceipt(ParseObject receipt) {
        this.receipt = receipt;
    }
}
