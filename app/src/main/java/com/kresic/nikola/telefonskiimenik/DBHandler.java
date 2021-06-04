package com.kresic.nikola.telefonskiimenik;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String DB_NAME = "ContactsDB";

    private static final String CONTACTS_TABLE = "contacts";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String EMAIL = "email";
    private static final String ORGANIZATION = "organization";
    private static final String RELATIONSHIP_ID = "relationship_id";

    private static final String RELATIONSHIP_TABLE = "relationship";
    private static final String ID_RELATIONSHIP = "id_relationship";
    private static final String NAME_RELATIONSHIP = "name_relationship";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + CONTACTS_TABLE
                        + "(" + ID + " integer PRIMARY KEY autoincrement,"
                        + NAME + " TEXT,"
                        + NUMBER + " TEXT,"
                        + EMAIL + " TEXT,"
                        + ORGANIZATION + " TEXT,"
                        + RELATIONSHIP_ID + " integer, FOREIGN KEY(" + RELATIONSHIP_ID + ") REFERENCES "
                        + RELATIONSHIP_TABLE + "( " + ID_RELATIONSHIP + " )" +
                        ")";


        String CREATE_RELATIONSHIP_TABLE =
                "CREATE TABLE " + RELATIONSHIP_TABLE
                        + "(" + ID_RELATIONSHIP + " integer PRIMARY KEY autoincrement,"
                        + NAME_RELATIONSHIP + " TEXT)";

        sqLiteDatabase.execSQL(CREATE_RELATIONSHIP_TABLE);
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);

        fillRelationship(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sqlContactsTable = "DROP TABLE IF EXISTS " + CONTACTS_TABLE;
        String sqlRelationshipTable = "DROP TABLE IF EXISTS " + RELATIONSHIP_TABLE;
        sqLiteDatabase.execSQL(sqlContactsTable);
        sqLiteDatabase.execSQL(sqlRelationshipTable);
        onCreate(sqLiteDatabase);
    }

    public void addContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME,contact.getName());
        values.put(NUMBER,contact.getNumber());
        values.put(EMAIL,contact.getEmail());
        values.put(ORGANIZATION,contact.getOrganization());
        values.put(RELATIONSHIP_ID,contact.getRelationshipID());

        long success = db.insert(CONTACTS_TABLE,null,values);
        Log.d("insert", "addContact: " + success);
        db.close();
    }



public void fillRelationship(SQLiteDatabase sqLiteDatabase){
        Relationship relationship1 = new Relationship(1, "Other");
        createRelationship(relationship1,sqLiteDatabase);
        Relationship relationship2 = new Relationship(2, "Family");
    createRelationship(relationship2,sqLiteDatabase);
        Relationship relationship3 = new Relationship(3, "Friend");
    createRelationship(relationship3,sqLiteDatabase);
        Relationship relationship4 = new Relationship(4, "Business");
    createRelationship(relationship4,sqLiteDatabase);
}

    public void createRelationship(Relationship relationship,SQLiteDatabase sqLiteDatabase){


        ContentValues values = new ContentValues();
        values.put(ID_RELATIONSHIP, relationship.getRelationshipID());
        values.put(NAME_RELATIONSHIP, relationship.getRelationshipName());

        long success = sqLiteDatabase.insert(RELATIONSHIP_TABLE, null, values);
        Log.d("insert", "addRelationship: " + success);

    }



    public List<Contact> getAllContacts(){
        SQLiteDatabase db = getReadableDatabase();
        List<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM " + CONTACTS_TABLE;

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();

                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setNumber(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                contact.setOrganization(cursor.getString(4));
                contact.setRelationshipID(cursor.getInt(5));

                contacts.add(contact);
            }
            while(cursor.moveToNext());
        }

        return contacts;
    }

    public List<Contact> getContactsFromSearch(int relationshipId){
        SQLiteDatabase db = getReadableDatabase();
        List<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM " + CONTACTS_TABLE + " WHERE " + RELATIONSHIP_ID + "="+relationshipId+"";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();

                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setNumber(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                contact.setOrganization(cursor.getString(4));
                contact.setRelationshipID(cursor.getInt(5));

                contacts.add(contact);
            }
            while(cursor.moveToNext());
        }

        return contacts;
    }







    public int updateContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME,contact.getName());
        values.put(NUMBER,contact.getNumber());
        values.put(EMAIL,contact.getEmail());
        values.put(ORGANIZATION,contact.getOrganization());
        values.put(RELATIONSHIP_ID,contact.getRelationshipID());

        return db.update(
                CONTACTS_TABLE,
                values,
                ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );
    }

    public void deleteContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                CONTACTS_TABLE,
                ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );
        db.close();
    }




    public List<Relationship> getAllRelationship(){
        SQLiteDatabase db = getReadableDatabase();
        List<Relationship> relationships = new ArrayList<>();

        String query = "SELECT * FROM " + RELATIONSHIP_TABLE;

        Cursor cursor = db.rawQuery(query,null);
        Relationship emptyRelationship = new Relationship();
        emptyRelationship.setRelationshipID(0);
        emptyRelationship.setRelationshipName("");
        relationships.add(emptyRelationship);


        if(cursor.moveToFirst()){
            do{
                Relationship relationship = new Relationship();

                relationship.setRelationshipID(Integer.parseInt(cursor.getString(0)));
                relationship.setRelationshipName(cursor.getString(1));


                relationships.add(relationship);
            }
            while(cursor.moveToNext());
        }

        return relationships;
    }


    public int getIdRelationship(String sName){
        SQLiteDatabase db = getReadableDatabase();


        String query = "SELECT "+ID_RELATIONSHIP+" FROM " + RELATIONSHIP_TABLE + " WHERE " + NAME_RELATIONSHIP + "='"+sName+"'";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
         return  Integer.parseInt(cursor.getString(0));

        }

        return 0;
    }


}
