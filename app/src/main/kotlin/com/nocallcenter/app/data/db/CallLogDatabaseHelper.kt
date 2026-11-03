package com.nocallcenter.app.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.nocallcenter.app.data.model.CallScreeningOutcome
import com.nocallcenter.app.data.model.ScreenedCall

class CallLogDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE $TABLE_CALLS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PHONE_NUMBER TEXT NOT NULL,
                $COL_CALLER_NAME TEXT,
                $COL_TIMESTAMP INTEGER NOT NULL,
                $COL_OUTCOME TEXT NOT NULL,
                $COL_NOTES TEXT
            )
        """.trimIndent()
        db.execSQL(createTableSql)
        db.execSQL("CREATE INDEX idx_calls_timestamp ON $TABLE_CALLS ($COL_TIMESTAMP)")
        db.execSQL("CREATE INDEX idx_calls_phone ON $TABLE_CALLS ($COL_PHONE_NUMBER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALLS")
        onCreate(db)
    }

    fun insertCall(
        phoneNumber: String,
        callerName: String? = null,
        outcome: CallScreeningOutcome,
        notes: String? = null,
        timestamp: Long = System.currentTimeMillis()
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PHONE_NUMBER, phoneNumber)
            put(COL_CALLER_NAME, callerName)
            put(COL_TIMESTAMP, timestamp)
            put(COL_OUTCOME, outcome.name)
            put(COL_NOTES, notes)
        }
        return db.insert(TABLE_CALLS, null, values)
    }

    fun getAllCalls(limit: Int = 100, offset: Int = 0): List<ScreenedCall> {
        val db = readableDatabase
        val calls = mutableListOf<ScreenedCall>()
        val query = """
            SELECT $COL_ID, $COL_PHONE_NUMBER, $COL_CALLER_NAME, $COL_TIMESTAMP, $COL_OUTCOME, $COL_NOTES
            FROM $TABLE_CALLS
            ORDER BY $COL_TIMESTAMP DESC
            LIMIT ? OFFSET ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(limit.toString(), offset.toString()))
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COL_ID))
                val phone = it.getString(it.getColumnIndexOrThrow(COL_PHONE_NUMBER))
                val name = it.getString(it.getColumnIndexOrThrow(COL_CALLER_NAME))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(COL_TIMESTAMP))
                val outcomeStr = it.getString(it.getColumnIndexOrThrow(COL_OUTCOME))
                val outcome = try {
                    CallScreeningOutcome.valueOf(outcomeStr)
                } catch (e: Exception) {
                    CallScreeningOutcome.BLOCKED_SMS_SENT
                }
                val notes = it.getString(it.getColumnIndexOrThrow(COL_NOTES))

                calls.add(
                    ScreenedCall(
                        id = id,
                        phoneNumber = phone,
                        callerName = name,
                        timestamp = timestamp,
                        outcome = outcome,
                        notes = notes
                    )
                )
            }
        }
        return calls
    }

    fun getBlockedCallsCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_CALLS WHERE $COL_OUTCOME != ?",
            arrayOf(CallScreeningOutcome.ALLOWED_CONTACT.name)
        )
        return cursor.use {
            if (it.moveToNext()) it.getInt(0) else 0
        }
    }

    fun getSmsSentCountInCycle(startTimestamp: Long, endTimestamp: Long): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT COUNT(*) FROM $TABLE_CALLS 
            WHERE $COL_OUTCOME = ? AND $COL_TIMESTAMP >= ? AND $COL_TIMESTAMP <= ?
            """.trimIndent(),
            arrayOf(CallScreeningOutcome.BLOCKED_SMS_SENT.name, startTimestamp.toString(), endTimestamp.toString())
        )
        return cursor.use {
            if (it.moveToNext()) it.getInt(0) else 0
        }
    }

    /**
     * Returns the timestamp of the last SMS sent to this specific number,
     * or null if no SMS has been sent to this number.
     */
    fun getLastSmsTimestampForNumber(phoneNumber: String): Long? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT $COL_TIMESTAMP FROM $TABLE_CALLS 
            WHERE $COL_PHONE_NUMBER = ? AND $COL_OUTCOME = ?
            ORDER BY $COL_TIMESTAMP DESC
            LIMIT 1
            """.trimIndent(),
            arrayOf(phoneNumber, CallScreeningOutcome.BLOCKED_SMS_SENT.name)
        )
        return cursor.use {
            if (it.moveToNext()) it.getLong(0) else null
        }
    }

    fun clearHistory() {
        val db = writableDatabase
        db.delete(TABLE_CALLS, null, null)
    }

    companion object {
        private const val DATABASE_NAME = "nocallcenter.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_CALLS = "screened_calls"
        const val COL_ID = "id"
        const val COL_PHONE_NUMBER = "phone_number"
        const val COL_CALLER_NAME = "caller_name"
        const val COL_TIMESTAMP = "timestamp"
        const val COL_OUTCOME = "outcome"
        const val COL_NOTES = "notes"
    }
}
