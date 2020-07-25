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
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputLayout;

public class CustomAddAlertDialog extends AppCompatDialogFragment
{
    private TextInputLayout Phone_Number;
    private CustomAddAlertDialog.CustomAddAlertDialogListener listener;
    DatabaseHelper ContactsDatabase;

    public CustomAddAlertDialog()
    {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContactsDatabase = new DatabaseHelper(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_add_alert_dialog,null);

        Phone_Number = view.findViewById(R.id.phone_number_tag);
        builder.setView(view)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Add Contact", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phoneNumber = Phone_Number.getEditText().getText().toString();
                        if(!ContactsDatabase.checkAlreadyExist(phoneNumber))
                            listener.ApplyAddDataText(phoneNumber);
                        else
                        {
                            CustomAddAlertDialog customAddAlertDialog = new CustomAddAlertDialog();
                            customAddAlertDialog.show((getActivity()).getSupportFragmentManager(),"CUSTOM_EDIT_DIALOG");
                            customAddAlertDialog.setCancelable(false);
                            Toast.makeText(getActivity(),"Number Already exists",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try
        {
            listener = (CustomAddAlertDialog.CustomAddAlertDialogListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "Error:Must implement Custom Dialog");
        }
    }

    public interface CustomAddAlertDialogListener
    {
        void ApplyAddDataText(String number);
    }
}
