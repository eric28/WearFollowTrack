package eric.esteban28.wearfollowtrack.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.util.ArrayList;

import eric.esteban28.wearfollowtrack.models.TrackGPX;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "followTrack";
    public static final String GPX_TABLE_NAME = "gpx";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "create table " + GPX_TABLE_NAME + "(id INTEGER PRIMARY KEY, name text,json text)"
            );
        } catch (SQLiteException e) {
            try {
                throw new IOException(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GPX_TABLE_NAME);
        onCreate(db);
    }

    public boolean insert(String s, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", s);
        contentValues.put("json", s1);
        db.insert(GPX_TABLE_NAME, null, contentValues);
        return true;
    }

    public TrackGPX getById(long id) {
        TrackGPX track;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + GPX_TABLE_NAME + " where id = " + id, null);
        res.moveToFirst();

        long idSQL = res.getLong(res.getColumnIndex("id"));
        String name = res.getString(res.getColumnIndex("name"));
        String json = res.getString(res.getColumnIndex("json"));

        track = new TrackGPX(idSQL, name, json);

        res.close();

        return track;
    }

    public ArrayList<TrackGPX> getAllGpx() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<TrackGPX> tracks = new ArrayList<>();

        Cursor res = db.rawQuery("select * from " + GPX_TABLE_NAME, null);

        res.moveToFirst();
        while (!res.isAfterLast()) {
            long id = res.getLong(res.getColumnIndex("id"));
            String name = res.getString(res.getColumnIndex("name"));
            String json = res.getString(res.getColumnIndex("json"));

            tracks.add(new TrackGPX(id, name, json));
            res.moveToNext();
        }

        res.close();

        return tracks;
    }
}
