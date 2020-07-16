package com.example.project1;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
public class SingleItem {
    private String PHOTO_URI;
    private String DISPLAY_NAME;
    private String COMPANY;
    private String NUMBER;
    private String ADDRESS;
    private String WEBPAGE;
    private String NOTE;
    private String STARRED;
    private String CONTACT_ID;
    private final String new_line = "\n";
    private final String own_delimiter = "__";
    public SingleItem() {
        this.PHOTO_URI = "";
        this.DISPLAY_NAME = "";
        this.COMPANY = "";
        this.NUMBER = "";
        this.ADDRESS = "";
        this.WEBPAGE = "";
        this.NOTE = "";
        this.STARRED = "";
        this.CONTACT_ID = "";
    }
    @Override
    public String toString() {
        return  PHOTO_URI + own_delimiter +
                DISPLAY_NAME + own_delimiter +
                COMPANY + own_delimiter +
                NUMBER + own_delimiter +
                ADDRESS + own_delimiter +
                WEBPAGE + own_delimiter +
                NOTE + own_delimiter +
                STARRED + own_delimiter +
                CONTACT_ID + own_delimiter;
    }
    public SingleItem(String PHOTO_URI, String DISPLAY_NAME, String COMPANY, String NUMBER, String ADDRESS, String WEBPAGE, String NOTE, String STARRED, String CONTACT_ID) {
        this.PHOTO_URI = PHOTO_URI;
        this.DISPLAY_NAME = DISPLAY_NAME;
        this.COMPANY = COMPANY;
        this.NUMBER = NUMBER;
        this.ADDRESS = ADDRESS;
        this.WEBPAGE = WEBPAGE;
        this.NOTE = NOTE;
        this.STARRED = STARRED;
        this.CONTACT_ID = CONTACT_ID;
    }
    public static List<SingleItem> getContacts(Context context) {
        // where to save loaded data
        // here, to list with elements of type "SingleItem" named "data"
        List<SingleItem> data = new ArrayList<>();
        // resolver is context-specific.
        ContentResolver resolver = context.getContentResolver();
        Uri contact_application_uri = ContactsContract.Contacts.CONTENT_URI; Log.d("URI",contact_application_uri.toString());
        String[] projection = null;
        String selection = null;
        String selectionArgs = null;
        String sortOrder = "starred DESC, display_name ASC";
        Cursor cursor = resolver.query(contact_application_uri, null, null, null, sortOrder); Log.d("auaicn", "number of items read is " + cursor.getCount());
        ArrayList<SingleItem> items = new ArrayList<SingleItem>();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int index_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    int index_PHOTO_URI = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
                    int index_DISPLAY_NAME = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    int index_STARRED = cursor.getColumnIndex(ContactsContract.Contacts.STARRED);
                    int index_CONTACT_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    String data_ID = cursor.getString(index_ID);
                    String data_PHOTO_URI = cursor.getString(index_PHOTO_URI);
                    String data_DISPLAY_NAME = cursor.getString(index_DISPLAY_NAME);
                    String data_STARRED = cursor.getString(index_STARRED);
                    String data_CONTACT_ID = cursor.getString(index_CONTACT_ID);
                    Log.d("index data ID : " , data_ID);
                    SingleItem item = new SingleItem();
                    item.setPHOTO_URI(data_PHOTO_URI);
                    item.setDISPLAY_NAME(data_DISPLAY_NAME);
                    item.setSTARRED(data_STARRED);
                    item.setCONTACT_ID(data_CONTACT_ID);
                    // get MULTIPLE EMAIL ADDRESS
                    Cursor cursor_detail = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{data_ID},
                            null);
                    Log.d("see if equal",data_CONTACT_ID + " & " + ContactsContract.CommonDataKinds.Email.CONTACT_ID);
                    while(cursor_detail.moveToNext()) {
                        String email = cursor_detail.getString(cursor_detail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        if(item.getADDRESS() == "")
                            item.setADDRESS(email);
                        else
                            item.addADDRESS(email);
                        Log.d("email address : ", email);
                    }
                    // get PHONE NUMBER
                    cursor_detail = resolver.query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID + " = ?"
                                    + " and " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",
                            new String[]{data_ID},
                            null);
                    while(cursor_detail.moveToNext()) {
                        String phone_number = cursor_detail.getString(cursor_detail.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(item.getNUMBER() == "") {
                            Log.d("auaicn","first phone number add");
                            item.setNUMBER(phone_number);
                        } else {
                            Log.d("auaicn","additional phone number add");
                            item.addNumber(phone_number);
                        }
                        Log.d("phone number : ", phone_number);
                    }
                    // get COMPANY
                    cursor_detail = resolver.query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID + " = " + data_ID
                                    + " and " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "'",
                            null,
                            null);
                    while(cursor_detail.moveToNext()) {
                        String copmany_name = cursor_detail.getString(cursor_detail.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                        item.setCOMPANY(copmany_name);
                        //Log.d("company name : ", copmany_name);
                    }
                    // GETTING WEBSITE
                    projection = new String[]{
                            ContactsContract.CommonDataKinds.Website.URL,
                            ContactsContract.CommonDataKinds.Website.TYPE
                    };
                    cursor_detail = resolver.query(ContactsContract.Data.CONTENT_URI,
                            projection,
                            ContactsContract.Data.CONTACT_ID + " = " + data_ID
                                    + " and " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE + "'",
                            null,
                            null);
                    while(cursor_detail.moveToNext()) {
                        String website_url = cursor_detail.getString(cursor_detail.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                        if (item.getWEBPAGE() == ""){
                            Log.d("auaicn","first webpage add");
                            item.setWEBPAGE(website_url);
                        }else{
                            Log.d("auaicn","additional webpage add");
                            item.addWEBPAGE(website_url);
                        }
                        item.setWEBPAGE(website_url);
                        Log.d("company name : ", website_url);
                    }
                    // NOTE
                    cursor_detail = resolver.query(ContactsContract.Data.CONTENT_URI,
                            projection,
                            ContactsContract.Data.CONTACT_ID + " = " + data_ID
                                    + " and " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'",
                            null,
                            null);
                    while(cursor_detail.moveToNext()) {
                        String Notes_contents = cursor_detail.getString(cursor_detail.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                        item.setNOTE(Notes_contents);
                        Log.d("company name : ", Notes_contents);
                    }
                    cursor_detail.close();
                    items.add(item);
                }
            } else {
                // nothing found for query
                /*
                 * Insert code here to notify the user that the search was unsuccessful. This isn't necessarily
                 * an error. You may want to offer the user the option to insert a new row, or re-type the
                 * search term.
                 */
            }
        } else {
            // On Error
            // Some providers return null if an error occurs, others throw an exception
        }
        for (int i = 0; i < items.size(); i++)
            data.add(items.get(i));
        cursor.close();
        return data;
    }
    private void addNumber(String phone_number) { setNUMBER(getNUMBER() + new_line + phone_number); }
    private void addADDRESS(String email) {
        setADDRESS(getADDRESS() + new_line + email);
    }
    private void addWEBPAGE(String WEBPAGE) {
        setWEBPAGE(getWEBPAGE() + new_line + WEBPAGE);
    }
    public String getPHOTO_URI () { return PHOTO_URI; }
    public void setPHOTO_URI (String PHOTO_URI){ this.PHOTO_URI = PHOTO_URI; }
    public String getDISPLAY_NAME () { return DISPLAY_NAME; }
    public void setDISPLAY_NAME (String DISPLAY_NAME){ this.DISPLAY_NAME = DISPLAY_NAME; }
    public String getCOMPANY () { return COMPANY; }
    public void setCOMPANY (String COMPANY){ this.COMPANY = COMPANY; }
    public String getNUMBER () { return NUMBER; }
    public void setNUMBER (String NUMBER){ this.NUMBER = NUMBER; }
    public String getADDRESS () { return ADDRESS; }
    public void setADDRESS (String ADDRESS){ this.ADDRESS = ADDRESS; }
    public String getWEBPAGE () { return WEBPAGE; }
    public void setWEBPAGE (String WEBPAGE){ this.WEBPAGE = WEBPAGE; }
    public String getNOTE () { return NOTE; }
    public void setNOTE (String NOTE){ this.NOTE = NOTE; }
    public String getSTARRED () { return STARRED; }
    public void setSTARRED (String STARRED){ this.STARRED = STARRED; }
    public String getCONTACT_ID() { return CONTACT_ID; }
    public void setCONTACT_ID(String CONTACT_ID) { this.CONTACT_ID = CONTACT_ID; }
}