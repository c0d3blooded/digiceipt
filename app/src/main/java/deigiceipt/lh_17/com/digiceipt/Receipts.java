package deigiceipt.lh_17.com.digiceipt;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LH-17 on 2/26/2016.
 */
public class Receipts {
    public static final String PARSE_CLASS_RECEIPTS = "Receipts";

    public static final String PARSE_FIELD_NAME = "name";
    public static final String PARSE_FIELD_ADDRESS= "address";
    public static final String PARSE_FIELD_DATE = "date";
    public static final String PARSE_FIELD_RECEIPTS = "receipts";


    public static final String JSON_FIELD_NAME= "name";
    public static final String JSON_FIELD_DATE = "date";
    public static final String JSON_FIELD_ADDRESS = "address";
    public static final String JSON_FIELD_ITEMS = "items";
    public static final String JSON_FIELD_UPC_CODE = "upc";
    public static final String JSON_FIELD_DESCRIPTION = "description";
    public static final String JSON_FIELD_PRICE = "price";

    //saves the receipt to the server
    public static void saveReceipt(String tag){
        //trim the []
        try {
            JSONObject objTag = new JSONObject(tag);
            JSONArray itemArr = objTag.getJSONArray(JSON_FIELD_ITEMS);
            ParseObject receipt = new ParseObject(PARSE_CLASS_RECEIPTS);
            List<String> receiptItems = new ArrayList<>();
            for(int a=0; a<itemArr.length(); a++){
                try {
                    JSONObject obj = itemArr.getJSONObject(a);
                    receiptItems.add(obj.toString());
                }
                catch (JSONException e) {e.printStackTrace();}
            }
            receipt.put(PARSE_FIELD_NAME, objTag.getString(JSON_FIELD_NAME));
            receipt.put(PARSE_FIELD_ADDRESS, objTag.getString(JSON_FIELD_ADDRESS));
            receipt.put(PARSE_FIELD_RECEIPTS, receiptItems);
            receipt.put(PARSE_FIELD_DATE, new Date(objTag.getLong(JSON_FIELD_DATE)));
            if(ParseUser.getCurrentUser() != null)
                receipt.setACL(new ParseACL(ParseUser.getCurrentUser()));
            receipt.saveEventually();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String formatPrice(double price){
        return "$ " + String.format( "%.2f", price );
    }

    public static String getDateString(ParseObject receipt){
        DateTime date = new DateTime(receipt.getDate(Receipts.PARSE_FIELD_DATE));
        return date.toString(getDateFormatter());
    }

    public static String getTimeString(ParseObject receipt){
        DateTime date = new DateTime(receipt.getDate(Receipts.PARSE_FIELD_DATE));
        return date.toString(getTimeFormatter());
    }

    public static DateTimeFormatter getDateFormatter(){
        return DateTimeFormat.forPattern("MMM. dd, yyyy");
    }

    public static DateTimeFormatter getTimeFormatter(){
        return DateTimeFormat.forPattern("hh:mm aa");
    }

    public static double getTotalPrice(ParseObject obj){
        List<String> receipts = obj.getList(Receipts.PARSE_FIELD_RECEIPTS);
        double total = 0.0;
        for(String receipt: receipts){
            try {
                JSONObject objReceipt = new JSONObject(receipt);
                total += objReceipt.getDouble(Receipts.JSON_FIELD_PRICE);
            }
            catch (JSONException e) {e.printStackTrace();}
        }
        return total;
    }
}


