package com.kresic.nikola.telefonskiimenik;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AddContactActivity extends AppCompatActivity {

    Context context;
    DBHandler dbHandler;

    EditText et_name,et_number,et_email,et_organization;
    Button add, cancel;
    Spinner spinnerAddGroup;

    ArrayAdapter<String> adapter;

    private List<Relationship> relationshipGroup;
    int id,id_relationship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        context = this;
        dbHandler = new DBHandler(context);

        et_name = findViewById(R.id.name);
        et_number = findViewById(R.id.number);
        et_email = findViewById(R.id.email);
        et_organization = findViewById(R.id.org);
        spinnerAddGroup = findViewById(R.id.spinner_add_group);

        add = findViewById(R.id.add);
        cancel = findViewById(R.id.cancel);

        relationshipGroup = new ArrayList<>();


        String name;
        String number;
        String email;
        String organization;

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            id = bundle.getInt("id");
            name = bundle.getString("name");
            number = bundle.getString("number");
            email = bundle.getString("email");
            organization = bundle.getString("organization");
            id_relationship = bundle.getInt("id_relationship");

            et_name.setText(name);
            et_number.setText(number);
            et_email.setText(email);
            et_organization.setText(organization);



        }
        loadRelationship();



        if(bundle == null){

            add.setText("add contact");
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = et_name.getText().toString();
                    String number = et_number.getText().toString();
                    String email = et_email.getText().toString();
                    String organization = et_organization.getText().toString();
                    int idRelationship=dbHandler.getIdRelationship(spinnerAddGroup.getSelectedItem().toString());



                    if(name.trim().length() > 0 && number.trim().length() > 0 && email.trim().length() > 0 && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            && organization.trim().length() > 0){
                        Log.d("test",""+idRelationship);
                        Contact contact = new Contact(name,number,email,organization,idRelationship);
                        dbHandler.addContact(contact);
                        startActivity(new Intent(context,MainActivity.class));
                        Toast.makeText(getApplicationContext(), "New contact added", Toast.LENGTH_SHORT).show();
                    }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Invalid email address")
                                .setNegativeButton("OK",null)
                                .show();

                    } else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Please fill all the fields")
                                .setNegativeButton("OK",null)
                                .show();
                    }
                }
            });

        }else if(bundle != null){

            add.setText("update contact");
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = et_name.getText().toString();
                    String number = et_number.getText().toString();
                    String email = et_email.getText().toString();
                    String organization = et_organization.getText().toString();
                    int idRelationship=dbHandler.getIdRelationship(spinnerAddGroup.getSelectedItem().toString());

                    int id = bundle.getInt("id");

                    if (name.trim().length() > 0 && number.trim().length() > 0 && email.trim().length() > 0 && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            && organization.trim().length() > 0){


                        Contact contact = new Contact(id, name, number, email, organization, idRelationship);
                        dbHandler.updateContact(contact);
                        startActivity(new Intent(context,MainActivity.class));
                        Toast.makeText(getApplicationContext(), "Contact updated", Toast.LENGTH_SHORT).show();

                    }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Invalid email address")
                                .setNegativeButton("OK",null)
                                .show();

                    } else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Please fill all the fields")
                                .setNegativeButton("OK",null)
                                .show();
                    }



                }
            });


        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,MainActivity.class));

            }
        });

    }


    private  void loadRelationship(){
        relationshipGroup = dbHandler.getAllRelationship();
        relationshipGroup.remove(0);
        String[] namesArray = new String[relationshipGroup.size()];

        for(int i=0; i<relationshipGroup.size(); i++){
                namesArray[i] = relationshipGroup.get(i).getRelationshipName();
        }

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, namesArray);
        spinnerAddGroup.setAdapter(adapter);

        if(!TextUtils.isEmpty(String.valueOf(id_relationship))){
            for(int i=0; i<relationshipGroup.size(); i++){
                Log.d("provjera",""+relationshipGroup.get(i).getRelationshipID());
                Log.d("druga_provjera",""+id_relationship);
               if(relationshipGroup.get(i).getRelationshipID() == id_relationship){
                   spinnerAddGroup.setSelection(i);
               };
            }

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_close){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIconAttribute(android.R.attr.alertDialogIcon);
            builder.setTitle("Exit");
            builder.setCancelable(true);
            builder.setMessage("Do you want to exit?");

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAndRemoveTask();
                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        return true;
    }

}