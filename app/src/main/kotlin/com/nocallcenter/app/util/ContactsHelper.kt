package com.nocallcenter.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

object ContactsHelper {

    /**
     * Checks if the given phone number exists in the device contacts.
     * Returns the contact display name if found, or null if not found or permission missing.
     */
    fun findContactName(context: Context, phoneNumber: String?): String? {
        if (phoneNumber.isNullOrBlank()) return null

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return try {
            val lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            val cursor = context.contentResolver.query(lookupUri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIndex != -1) it.getString(nameIndex) else null
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun isContact(context: Context, phoneNumber: String?): Boolean {
        return findContactName(context, phoneNumber) != null
    }
}
