/*
 * Copyright 2015 Ruoshi Li
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package li.ruoshi.nextday.db

import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import li.ruoshi.nextday.models.DailyInfo

/**
 * Created by ruoshili on 1/19/15.
 */

val DATABASE_NAME = "daily_info_db"
val DATABASE_VERSION = 1

public class DailyInfoDb(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    val CREATE_COMMAND = "create table daily_info (" +
            "date_key INTEGER PRIMARY KEY DESC, raw_json TEXT not null)"

    override fun onCreate(db: SQLiteDatabase) {
        db.beginTransaction()
        try{
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        throw UnsupportedOperationException()
    }

    public fun getLastDay() : DailyInfo?{
        return null
    }

}