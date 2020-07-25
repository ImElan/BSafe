package app.android.bsafe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputLayout;

public class CustomEditAlertDialog extends AppCompatDialogFragment
{
    private TextInputLayout Phone_Number;
    DatabaseHelper ContactsDatabase;

    CustomEditDialogListener listener;

    private String phone_number;
    private String phone_number_previous;

    public CustomEditAlertDialog(String phone_number) {
        this.phone_number = phone_number;
        this.phone_number_previous = phone_number;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContactsDatabase = new DatabaseHelper(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_add_alert_dialog,null);

        Phone_Number = view.findViewById(R.id.phone_number_tag);

        Phone_Number.getEditText().setText(phone_number);

        builder.setView(view)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Update", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phoneNumber = Phone_Number.getEditText().getText().toString();
                        if(phoneNumber.equals(phone_number))
                        {
                            listener.ApplyEditDataText(phone_number,phoneNumber);
                        }
                        else if(!ContactsDatabase.checkAlreadyExist(phoneNumber))
                            listener.ApplyEditDataText(phone_number,phoneNumber);
                        else
                        {
                            CustomEditAlertDialog customEditAlertDialog = new CustomEditAlertDialog(phone_number_previous);
                            customEditAlertDialog.show(getActivity().getSupportFragmentManager(),"CUSTOM_EDIT_DIALOG");
                            customEditAlertDialog.setCancelable(false);
                            Toast.makeText(getActivity(),"Number Already exists",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener =(CustomEditDialogListener)context;

        } catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "Error:Must implement Custom Dialog");
        }
    }

    public interface CustomEditDialogListener
    {
        void ApplyEditDataText(String phone_id,String phoneNumber);
    }
}
