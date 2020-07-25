package app.android.bsafe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class CustomDialog extends AppCompatDialogFragment
{
    private CustomDialogListener listener;
    private Spinner messageSpinner;
    private ArrayAdapter<CharSequence> message_adapter;
    private String message;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_message_dialog,null);
        messageSpinner = view.findViewById(R.id.message_spinner);


        message_adapter = ArrayAdapter.createFromResource(getContext(),R.array.messages,android.R.layout.simple_spinner_item);
        message_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        messageSpinner.setAdapter(message_adapter);

        messageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                message = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(view)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Change Message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.ApplyText(message);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try
        {
            listener = (CustomDialogListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "Error:Must implement Custom Dialog");
        }
    }

    public interface CustomDialogListener
    {
        void ApplyText(String message);
    }
}
