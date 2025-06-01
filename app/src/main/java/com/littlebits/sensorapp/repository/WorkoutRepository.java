package com.littlebits.sensorapp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.littlebits.sensorapp.model.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutRepository extends SQLiteOpenHelper {

    private static final String DB_NAME = "workout_db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_WORKOUT = "workouts";
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_MONTH_YEAR = "monthYear";
    private static final String COL_DURATION = "duration";
    private static final String COL_TIME = "time";
    private static final String COL_STEPS = "steps";
    private static final String COL_DISTANCE = "distance";
    private static final String COL_SPEED = "speed";
    private static final String COL_ALTITUDE = "altitude";
    private static final String COL_CALORIES = "calories";

    public WorkoutRepository(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + TABLE_WORKOUT + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_MONTH_YEAR + " TEXT, " +
                COL_DURATION + " TEXT, " +
                COL_TIME + " INTEGER, " +
                COL_STEPS + " INTEGER, " +
                COL_DISTANCE + " REAL, " +
                COL_SPEED + " REAL, " +
                COL_ALTITUDE + " REAL, " +
                COL_CALORIES + " REAL)";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        onCreate(db);
    }

    public void saveWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, workout.getDate());
        values.put(COL_MONTH_YEAR, workout.getMonthYear());
        values.put(COL_DURATION, workout.getDuration());
        values.put(COL_TIME, workout.getTime());
        values.put(COL_STEPS, workout.getSteps());
        values.put(COL_DISTANCE, workout.getDistance());
        values.put(COL_SPEED, workout.getSpeed());
        values.put(COL_ALTITUDE, workout.getAltitude());
        values.put(COL_CALORIES, workout.getCalories());

        db.insert(TABLE_WORKOUT, null, values);
        db.close();
    }

    public List<Workout> getAllWorkouts() {
        List<Workout> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUT, null, null, null, null, null, COL_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Workout workout = new Workout(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH_YEAR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DURATION))
                );
                workout.setTime(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TIME)));
                workout.setSteps(cursor.getInt(cursor.getColumnIndexOrThrow(COL_STEPS)));
                workout.setDistance(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)));
                workout.setSpeed(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SPEED)));
                workout.setAltitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ALTITUDE)));
                workout.setCalories(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CALORIES)));

                workouts.add(workout);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return workouts;
    }
}
