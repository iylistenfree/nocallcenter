package com.nocallcenter.app.data.model

enum class CallScreeningOutcome {
    BLOCKED_SMS_SENT,
    BLOCKED_ANTI_FLOOD,
    BLOCKED_BUDGET_EXCEEDED,
    BLOCKED_SMS_DISABLED,
    BLOCKED_FOREIGN_PREFIX,
    ALLOWED_CONTACT
}

data class ScreenedCall(
    val id: Long = 0,
    val phoneNumber: String,
    val callerName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val outcome: CallScreeningOutcome,
    val notes: String? = null
)
