package deigiceipt.lh_17.com.digiceipt;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tyler on 6/29/2014.
 */
public class ReceiptItemListAdapter extends RecyclerView.Adapter<ReceiptItemListAdapter.ViewHolder>{
    public static final String TAG = "ReceiptItemListAdapter";
    public ArrayList<JSONObject> dataList;
    private Context context;

    public ReceiptItemListAdapter(Context c, ParseObject obj){
        context = c;
        dataList = new ArrayList<>();
        if(obj != null) {
            List<String> receipts = obj.getList(Receipts.PARSE_FIELD_RECEIPTS);
            for (String receipt : receipts) {
                try {
                    JSONObject objReceipt = new JSONObject(receipt);
                    dataList.add(objReceipt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_receipt_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        JSONObject receipt = dataList.get(i);
        TextView txtName = viewHolder.txtName;
        TextView txtPrice = viewHolder.txtPrice;

        try {
            txtName.setText(receipt.getString(Receipts.JSON_FIELD_DESCRIPTION));
            txtPrice.setText(Receipts.formatPrice(receipt.getDouble(Receipts.JSON_FIELD_PRICE)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtPrice, txtName;
        public ViewHolder(View itemLayoutView){
            super(itemLayoutView);
            txtName = (TextView) itemLayoutView.findViewById(R.id.txtName);
            txtPrice = (TextView) itemLayoutView.findViewById(R.id.txtPrice);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}




