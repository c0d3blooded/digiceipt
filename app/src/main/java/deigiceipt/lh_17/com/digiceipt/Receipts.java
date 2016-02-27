package deigiceipt.lh_17.com.digiceipt;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LH-17 on 2/26/2016.
 */
public class Receipts {
    public static final String PARSE_CLASS_RECEIPTS = "Receipts";

    public static final String PARSE_FIELD_NAME = "name";
    public static final String PARSE_FIELD_ADDRESS= "address";
    public static final String PARSE_FIELD_DATE = "date";
    public static final String PARSE_FIELD_RECEIPTS = "receipts";

    public static final String JSON_FIELD_UPC_CODE = "upc";
    public static final String JSON_FIELD_DESCRIPTION = "description";
    public static final String JSON_FIELD_PRICE = "price";


    public static String formatPrice(double price){
        return "$ " + String.format( "%.2f", price );
    }

    public static DateTimeFormatter getDateFormatter(){
        return DateTimeFormat.forPattern("MMM. dd, yyyy");
    }

    public static DateTimeFormatter getTimeFormatter(){
        return DateTimeFormat.forPattern("KK:mm aa");
    }
}


