package com.nocallcenter.app.service

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.nocallcenter.app.R
import com.nocallcenter.app.data.db.CallLogDatabaseHelper
import com.nocallcenter.app.data.model.CallScreeningOutcome
import com.nocallcenter.app.data.preferences.AppPreferences
import com.nocallcenter.app.util.BillingCycleHelper
import com.nocallcenter.app.util.ContactsHelper
import com.nocallcenter.app.util.NotificationHelper

class NoCallCenterScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        // Only screen incoming calls
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
                respondToCall(callDetails, CallResponse.Builder().build())
                return
            }
        }

        val prefs = AppPreferences(applicationContext)
        val db = CallLogDatabaseHelper(applicationContext)
        val notifier = NotificationHelper(applicationContext)

        if (!prefs.isScreeningEnabled) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val rawNumber = callDetails.handle?.schemeSpecificPart
        if (rawNumber.isNullOrBlank()) {
            // Private / Hidden number
            if (prefs.blockNonContacts) {
                blockCall(callDetails)
                val unknownLabel = getString(R.string.caller_unknown)
                db.insertCall(
                    phoneNumber = unknownLabel,
                    callerName = null,
                    outcome = CallScreeningOutcome.BLOCKED_SMS_DISABLED,
                    notes = "Private / Anonymous"
                )
                notifier.showCallScreenedNotification(unknownLabel, CallScreeningOutcome.BLOCKED_SMS_DISABLED)
            } else {
                respondToCall(callDetails, CallResponse.Builder().build())
            }
            return
        }

        // Check if number is in contacts
        val contactName = ContactsHelper.findContactName(applicationContext, rawNumber)
        if (contactName != null) {
            // In contacts: let call through normally
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        // NOT in contacts: Block and Screen!
        blockCall(callDetails)

        // Process Auto-SMS logic
        handleAutoSms(rawNumber, prefs, db, notifier)
    }

    private fun blockCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(false)
            .setSkipNotification(true)
            .setSilenceCall(true)
            .build()
        respondToCall(callDetails, response)
    }

    private fun handleAutoSms(
        phoneNumber: String,
        prefs: AppPreferences,
        db: CallLogDatabaseHelper,
        notifier: NotificationHelper
    ) {
        if (!prefs.isAutoSmsEnabled) {
            db.insertCall(
                phoneNumber = phoneNumber,
                outcome = CallScreeningOutcome.BLOCKED_SMS_DISABLED
            )
            notifier.showCallScreenedNotification(phoneNumber, CallScreeningOutcome.BLOCKED_SMS_DISABLED)
            return
        }

        // Anti-Flood check: max 1 SMS per number every X hours (default 48h)
        if (prefs.isAntiFloodEnabled) {
            val lastSmsTime = db.getLastSmsTimestampForNumber(phoneNumber)
            if (lastSmsTime != null) {
                val floodWindowMillis = prefs.antiFloodHours * 3600 * 1000L
                if (System.currentTimeMillis() - lastSmsTime < floodWindowMillis) {
                    db.insertCall(
                        phoneNumber = phoneNumber,
                        outcome = CallScreeningOutcome.BLOCKED_ANTI_FLOOD,
                        notes = "Anti-Flood: SMS già inviato nelle ultime ${prefs.antiFloodHours}h"
                    )
                    notifier.showCallScreenedNotification(phoneNumber, CallScreeningOutcome.BLOCKED_ANTI_FLOOD)
                    return
                }
            }
        }

        // Smart SMS Budgeting check (if enabled by user)
        if (prefs.isBudgetingEnabled) {
            val cycleStart = BillingCycleHelper.getCycleStartTimestamp(prefs.billingCycleDay)
            val cycleEnd = BillingCycleHelper.getCycleEndTimestamp(prefs.billingCycleDay)
            val smsCountInCycle = db.getSmsSentCountInCycle(cycleStart, cycleEnd)

            if (smsCountInCycle >= prefs.smsBudgetLimit) {
                db.insertCall(
                    phoneNumber = phoneNumber,
                    outcome = CallScreeningOutcome.BLOCKED_BUDGET_EXCEEDED,
                    notes = "Budget mensile raggiunto ($smsCountInCycle/${prefs.smsBudgetLimit})"
                )
                notifier.showCallScreenedNotification(phoneNumber, CallScreeningOutcome.BLOCKED_BUDGET_EXCEEDED)
                notifier.showBudgetExhaustedAlert()
                return
            }
        }

        // Permission check for SEND_SMS
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            db.insertCall(
                phoneNumber = phoneNumber,
                outcome = CallScreeningOutcome.BLOCKED_SMS_DISABLED,
                notes = "Permesso SEND_SMS non autorizzato"
            )
            notifier.showCallScreenedNotification(phoneNumber, CallScreeningOutcome.BLOCKED_SMS_DISABLED)
            return
        }

        // Dispatch SMS
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                applicationContext.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val text = prefs.customSmsTemplate
            val parts = smsManager.divideMessage(text)
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)

            db.insertCall(
                phoneNumber = phoneNumber,
                outcome = CallScreeningOutcome.BLOCKED_SMS_SENT,
                notes = "SMS inviato con successo"
            )
            notifier.showCallScreenedNotification(phoneNumber, CallScreeningOutcome.BLOCKED_SMS_SENT)
        } catch (e: Exception) {
            db.insertCall(
                phoneNumber = phoneNumber,
                outcome = CallScreeningOutcome.BLOCKED_SMS_DISABLED,
                notes = "Errore invio SMS: ${e.localizedMessage}"
            )
            notifier.showCallScreenedNotification(phoneNumber, CallScreeningOutcome.BLOCKED_SMS_DISABLED)
        }
    }
}
