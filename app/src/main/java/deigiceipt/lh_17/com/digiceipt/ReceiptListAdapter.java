package deigiceipt.lh_17.com.digiceipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LH-17 on 2/26/2016.
 */
public class ReceiptListAdapter extends BaseExpandableListAdapter {
    private FragmentMain.ReceiptActionListener receiptInterface;
    Context context;
    ArrayList<ArrayList<ParseObject>> receipts;

    public ReceiptListAdapter(Context context, ArrayList<ParseObject> objects){
        this.context = context;
        organizeReceipts(objects);
        if (context instanceof FragmentMain.ReceiptActionListener) {
            receiptInterface = (FragmentMain.ReceiptActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReceiptActionListener");
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_receipts_header, parent, false);
        }
        ParseObject firstReceipt = receipts.get(groupPosition).get(0);
        TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
        txtDate.setText(Receipts.getDateString(firstReceipt));

        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_receipts, parent, false);
        }
        final ParseObject receipt = receipts.get(groupPosition).get(childPosition);
        TextView txtName = (TextView) v.findViewById(R.id.txtName);
        TextView txtPrice = (TextView) v.findViewById(R.id.txtPrice);
        TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
        TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);

        txtName.setText(receipt.getString(Receipts.PARSE_FIELD_NAME));
        txtAddress.setText(receipt.getString(Receipts.PARSE_FIELD_ADDRESS));
        txtPrice.setText(Receipts.formatPrice(Receipts.getTotalPrice(receipt)));
        txtDate.setText(Receipts.getTimeString(receipt));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiptInterface.onOpenReceipt(receipt);
            }
        });
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    //swap the items if needed
    public void swapItems(ArrayList<ParseObject> objects){
        organizeReceipts(objects);
        notifyDataSetChanged();
    }

    //organizes the receipts by the given value
    public void organizeReceipts(ArrayList<ParseObject> objects){
        receipts = new ArrayList<>();
        if(objects != null && objects.size() > 0){
            //initialize first receipt
            ArrayList<ParseObject> list = new ArrayList<>();
            list.add(objects.get(0));
            receipts.add(list);
            for(int a=1; a<objects.size(); a++){
                ParseObject receipt = objects.get(a);
                ArrayList<ParseObject> group = receipts.get(receipts.size()-1);
                ParseObject dateObj = group.get(0);   //first object in the list to indicate the date
                DateTime groupDate = new DateTime(dateObj.getDate(Receipts.PARSE_FIELD_DATE));  //date of the group
                DateTime date = new DateTime(receipt.getDate(Receipts.PARSE_FIELD_DATE));
                groupDate = groupDate.withTimeAtStartOfDay();
                date = date.withTimeAtStartOfDay();
                //if different dates
                if(!date.toString(Receipts.getDateFormatter()).equals(groupDate.toString(Receipts.getDateFormatter())) ){
                    list = new ArrayList<>();
                    list.add(receipt);
                    receipts.add(list);
                }
                else {
                    group.add(receipt);
                    receipts.set(receipts.size()-1, group);
                }
            }

        }
    }


    @Override
    public int getGroupCount() {
        return receipts.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return receipts.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
