package com.kresic.nikola.telefonskiimenik;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.database.ContentProviderSchema;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.itextpdf.text.Annotation.FILE;

public class MainActivity extends AppCompatActivity  {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    Context context;
    private DBHandler dbHandler;

    private ListView contactsList;
    private Button add, print;
    private Spinner spinnerGroup;

    private List<Contact> contacts;
    private List<Relationship> relationship;
    ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);

        context = this;
        dbHandler = new DBHandler(context);

        contactsList = findViewById(R.id.contact_list);
        add = findViewById(R.id.add);
        print = findViewById(R.id.print);
        spinnerGroup = findViewById(R.id.spinner_filter_group);

        contacts = new ArrayList<>();
        relationship = new ArrayList<>();
        requestPermission();
        //loadContacts();
        loadRelationship();

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Contact contact = contacts.get(i);
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                intent.putExtra("id", contact.getId());
                intent.putExtra("name", contact.getName());
                intent.putExtra("number", contact.getNumber());
                intent.putExtra("email", contact.getEmail());
                intent.putExtra("organization", contact.getOrganization());
                intent.putExtra("id_relationship", contact.getRelationshipID());
                startActivity(intent);

            }
        });

        contactsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete contact?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int f) {
                                Contact contact = contacts.get(i);
                                dbHandler.deleteContact(contact);
                                loadContacts();
                                Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();


                return true;
            }
        });



        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AddContactActivity.class);
                startActivity(intent);
            }
        });

        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int idRelationship=dbHandler.getIdRelationship(spinnerGroup.getSelectedItem().toString());
                if(idRelationship==0){
                    loadContacts();
                }else{
                    searchContacts(idRelationship);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        print.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    createPdf();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void loadContacts() {
        contacts.clear();
        contacts = dbHandler.getAllContacts();

        String[] namesArray = new String[contacts.size()];

        for(int i=0; i<contacts.size(); i++){
            namesArray[i] = contacts.get(i).getName() + "\n" + contacts.get(i).getNumber();
        }

        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,namesArray);
        contactsList.setAdapter(adapter);
    }

    private void searchContacts(int idRelationship) {
        contacts.clear();
        contacts = dbHandler.getContactsFromSearch(idRelationship);

        String[] namesArray = new String[contacts.size()];

        for(int i=0; i<contacts.size(); i++){
            namesArray[i] = contacts.get(i).getName() + "\n" + contacts.get(i).getNumber();
        }

        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,namesArray);
        contactsList.setAdapter(adapter);
    }

    private  void loadRelationship(){
        relationship = dbHandler.getAllRelationship();

        String[] namesArray = new String[relationship.size()];


        for(int i=0; i<relationship.size(); i++){
            namesArray[i] = relationship.get(i).getRelationshipName();
        }

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, namesArray);
        spinnerGroup.setAdapter(adapter);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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




    private void viewPdf(String file, String directory) {

        File pdfFile = new File(  directory + "/" + file);

        Uri apkUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdfFile);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(apkUri, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        try {
            startActivity(pdfIntent);
        } catch (Exception e) {
            Log.d("test",e.getMessage());
            Toast.makeText(MainActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

   private void createPdf() throws FileNotFoundException, DocumentException {

       File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

       if (!directory.exists()) {
           directory.mkdir();
       }

       File  file = new File(directory,"ispis.pdf");
       if(!file.exists())
           file.delete();

       //OutputStream output = new FileOutputStream(file);


       Document document = new Document();

       PdfWriter writer = PdfWriter.getInstance(document,  new FileOutputStream(file));
       document.open();

       PdfPTable table = new PdfPTable(4);

       PdfPCell cell1 = new PdfPCell(new Phrase("Name"));
       PdfPCell cell2 = new PdfPCell(new Phrase("Number"));
       PdfPCell cell3 = new PdfPCell(new Phrase("Email"));
       PdfPCell cell4 = new PdfPCell(new Phrase("Organization"));
       table.addCell(cell1);
       table.addCell(cell2);
       table.addCell(cell3);
       table.addCell(cell4);

       for (int i = 0; i < contacts.size(); i++){
           cell1 = new PdfPCell(new Phrase(contacts.get(i).getName()));
           table.addCell(cell1);
           cell2 = new PdfPCell(new Phrase(contacts.get(i).getNumber()));
           table.addCell(cell2);
           cell3 = new PdfPCell(new Phrase(contacts.get(i).getEmail()));
           table.addCell(cell3);
           cell4 = new PdfPCell(new Phrase(contacts.get(i).getOrganization()));
           table.addCell(cell4);


       }

       document.add(table);
       document.close();
       writer.close();

 viewPdf("ispis.pdf", directory.getPath());
       Toast.makeText(getApplicationContext(), "Document ispis.pdf is created", Toast.LENGTH_LONG).show();

   }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}