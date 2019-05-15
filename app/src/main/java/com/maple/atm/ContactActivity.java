package com.maple.atm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACTS = 66;
    private static final String TAG = ContactsContract.Contacts.class.getSimpleName();
    private List<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if(permission == PackageManager.PERMISSION_GRANTED){
            readContacts();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},REQUEST_CONTACTS);
        }
    }

    private void readContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null/*你要多少欄位,,全部用null*/,
                null/*select * from*/,null/*sql中的where*/,null/*order by*/);
        contacts = new ArrayList<>();
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            Contact contact = new Contact(id,name);
            Log.d(TAG, "readContacts: " + name);
            if(hasPhone == 1){//有電話的(可能不只一個電話)要去查的是電話號碼的uri
                Cursor cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?"/*看是哪個聯絡人where xxx*/,
                        new String[]{String.valueOf(id)}/*where id = xxx*/,
                        null);
                while(cursor2.moveToNext()){
                    String phone = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                    Log.d(TAG, "readContacts: \t" + phone);
                    contact.getPhones().add(phone);
                }
            }
            contacts.add(contact);
        }
        ContactAdapter adapter = new ContactAdapter(contacts);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
        List<Contact> contacts;
        public ContactAdapter(List<Contact> contacts){
            this.contacts = contacts;
        }
        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_2,viewGroup,false);
            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int i) {
            Contact contact = contacts.get(i);
            contactViewHolder.nameText.setText(contact.getName());
            Log.d(TAG, "onBindViewHolder: "+ contact.getName());
            StringBuilder sb = new StringBuilder(); //電話號碼可能有1筆以上，號碼間用" "隔開
            for (String phone : contact.getPhones()) {
                sb.append(phone);
                sb.append(" ");
            }
            contactViewHolder.phoneText.setText(sb.toString());
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView phoneText;
            public ContactViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(android.R.id.text1);
                phoneText = itemView.findViewById(android.R.id.text2);
            }
        }
    }

    //ActivityCompat.requestPermissions回來時自動呼叫此function
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CONTACTS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                readContacts();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_upload){
            //uptoFirebase
            String userid = getSharedPreferences("atm",MODE_PRIVATE)
                    .getString("USERID",null);
            if (userid != null) {
                FirebaseDatabase.getInstance().getReference("users")
                        .child(userid)
                        .child("contacts")
                        .setValue(contacts);
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
