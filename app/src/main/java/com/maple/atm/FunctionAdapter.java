package com.maple.atm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//Function名稱展示的Adapter
public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder> {
    private final String[] functions;
    Context context;

    public FunctionAdapter(Context context){
        this.context = context;
        functions = context.getResources().getStringArray(R.array.functions); //getResources function在context下
    }

    //畫面上有五個 產生每個前都會呼叫此程式
    @NonNull
    @Override
    public FunctionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_1,viewGroup,false
        );
        return new FunctionViewHolder(view);
    }

    //資料來了 將viewholder傳入並得知位子(第幾個)
    @Override
    public void onBindViewHolder(@NonNull FunctionViewHolder functionViewHolder, int i) {
        functionViewHolder.nameText.setText(functions[i]);
    }

    @Override
    public int getItemCount() {
        return functions.length;
    }

    public class FunctionViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(android.R.id.text1);//用預設的樣式
        }
    }

}
