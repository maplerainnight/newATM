package com.maple.atm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOGIN = 100;
    boolean logon = false;
    private List<Function> functions;
    //String[] functions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!logon){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivityForResult(intent,REQUEST_LOGIN);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //Recycler
        RecyclerView recyclerView = setupFunctions();

        recyclerView.setHasFixedSize(true);//固定大小
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));//排列方式
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        //Adapter
        //FunctionAdapter adapter = new FunctionAdapter(this);
        IconAdapter adapter = new IconAdapter();
        recyclerView.setAdapter(adapter);
    }

    private RecyclerView setupFunctions() {
        functions = new ArrayList<>();
        String[] funcs = getResources().getStringArray(R.array.functions);
        functions.add(new Function(funcs[0],R.drawable.func_transaction));
        functions.add(new Function(funcs[1],R.drawable.func_balance));
        functions.add(new Function(funcs[2],R.drawable.func_finance));
        functions.add(new Function(funcs[3],R.drawable.func_contacts));
        functions.add(new Function(funcs[4],R.drawable.func_exit));
        return findViewById(R.id.recycler);
    }

    public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {

        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_icon,viewGroup,false);
            return new IconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IconViewHolder iconViewHolder, int i) {
            Function function = functions.get(i);
            iconViewHolder.nameText.setText(function.getName());
            iconViewHolder.iconImage.setImageResource(function.getIcon());
        }

        @Override
        public int getItemCount() {
            return functions.size();
        }

        public class IconViewHolder extends RecyclerView.ViewHolder{
            ImageView iconImage;
            TextView nameText;

            public IconViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.item_icon);
                nameText = itemView.findViewById(R.id.item_name);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_LOGIN){ //判斷是不是從LoginActivity回來
            if(resultCode != RESULT_OK){ //判斷登入是否正常(是否回傳Result_ok
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
