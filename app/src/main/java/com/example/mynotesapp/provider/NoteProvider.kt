package com.example.mynotesapp.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.mynotesapp.db.DatabaseContract.AUTHORITY
import com.example.mynotesapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.example.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import com.example.mynotesapp.db.NoteHelper

class NoteProvider : ContentProvider() {

    companion object {

        /*
        Integer digunakan sebagai identifier antara select all sama select by id
         */

        private const val NOTE = 1
        private const val NOTE_ID = 2
        private lateinit var noteHelper: NoteHelper


        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        /*
        Uri matcher untuk mempermudah identifier dengan menggunakan integer
        misal
        uri com.dicoding.picodiploma.mynotesapp dicocokan dengan integer 1
        uri com.dicoding.picodiploma.mynotesapp/# dicocokan dengan integer 2
         */

        init {
            // content://com.example.mynotesapp/note
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE)

            // content://com.example.mynotesapp/note/id
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", NOTE_ID)
        }
    }

    override fun onCreate(): Boolean {
        noteHelper = NoteHelper.getInstance(context as Context)
        noteHelper.open()
        return true
    }

    /*
   Method queryAll digunakan ketika ingin menjalankan queryAll Select
   Return cursor
    */

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val cursor: Cursor?
        when (sUriMatcher.match(uri)) {
            NOTE -> cursor = noteHelper.queryAll()
            NOTE_ID -> cursor = noteHelper.queryById(uri.lastPathSegment.toString())
            else -> cursor = null
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (NOTE) {
            sUriMatcher.match(uri) -> noteHelper.insert(contentValues)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(
        uri: Uri, contentValues: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {

        val updated: Int = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.update(uri.lastPathSegment.toString(), contentValues)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return updated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }
}
