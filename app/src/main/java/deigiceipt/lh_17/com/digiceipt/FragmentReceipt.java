package deigiceipt.lh_17.com.digiceipt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;


public class FragmentReceipt extends Fragment {

    public static final String TAG = "FragmentReceipt";
    private ParseObject receipt;

    private TextView txtName, txtAddress, txtDate, txtTime, txtSubTotal, txtTax, txtTotal;
    private RecyclerView listItems;

    private ReceiptItemListAdapter listAdapter;
    public FragmentMain.ReceiptActionListener receiptInterface;

    public FragmentReceipt() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        if (getActivity() instanceof FragmentMain.ReceiptActionListener) {
            receiptInterface = (FragmentMain.ReceiptActionListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement ReceiptActionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtDate = (TextView) view.findViewById(R.id.txtDate);
        txtTime = (TextView) view.findViewById(R.id.txtTime);
        txtSubTotal = (TextView) view.findViewById(R.id.txtSubTotal);
        txtTax = (TextView) view.findViewById(R.id.txtTax);
        txtTotal = (TextView) view.findViewById(R.id.txtTotal);
        listItems  = (RecyclerView) view.findViewById(R.id.listItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listItems.setLayoutManager(layoutManager);
        listItems.setItemAnimator(new DefaultItemAnimator());
        listItems.setHasFixedSize(true);
        listAdapter = new ReceiptItemListAdapter(getActivity(), receipt);
        listItems.setAdapter(listAdapter);
        updateViews();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateViews();
    }

    public void updateViews(){
        if(receipt != null){
            txtName.setText(receipt.getString(Receipts.PARSE_FIELD_NAME));
            txtAddress.setText(receipt.getString(Receipts.PARSE_FIELD_ADDRESS));
            txtDate.setText(Receipts.getDateString(receipt));
            txtTime.setText(Receipts.getTimeString(receipt));
            txtSubTotal.setText(Receipts.formatPrice(Receipts.getTotalPrice(receipt)));
            txtTotal.setText(Receipts.formatPrice(Receipts.getTotalPrice(receipt)));
        }
    }

    public void setReceipt(ParseObject receipt) {
        this.receipt = receipt;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.receipt_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:
                receiptInterface.onDeleteReceipt(receipt);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
