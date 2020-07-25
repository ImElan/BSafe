package app.android.bsafe;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    private ArrayList<String> numberList;
    private Context mContext;
    private DatabaseHelper ContactDatabase;

    public RecyclerViewAdapter(ArrayList<String> numberList, Context mContext) {
        this.numberList = numberList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_contact_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.serial_no.setText(position+1+".");
        holder.phone_number.setText(numberList.get(position));

        holder.EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext,"Edit",Toast.LENGTH_SHORT).show();
                String phoneNumber = numberList.get(position);
                CustomEditAlertDialog customEditAlertDialog = new CustomEditAlertDialog(phoneNumber);
                customEditAlertDialog.show(((ContactsActivity)mContext).getSupportFragmentManager(),"CUSTOM_EDIT_DIALOG");
                customEditAlertDialog.setCancelable(false);
            }
        });

        holder.DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext,"Delete",Toast.LENGTH_SHORT).show();
                final String number = numberList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete")
                        .setMessage("Are you Sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContactDatabase = new DatabaseHelper(mContext);
                                int result = ContactDatabase.DeletaData(number);
                                if(result==1)
                                {
                                    numberList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,numberList.size());
                                    Toast.makeText(mContext,"Contact Deleted Successfully",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return numberList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView serial_no;
        TextView phone_number;
        Button EditButton;
        Button DeleteButton;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            serial_no = itemView.findViewById(R.id.count_id);
            phone_number = itemView.findViewById(R.id.number_tag);
            EditButton = itemView.findViewById(R.id.edit_button);
            DeleteButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
