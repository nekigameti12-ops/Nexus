package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String = "default",
    val role: String, // "user", "nexus", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String = "chat", // "chat", "voice", "agent", "coder", "research"
    val toolStatus: String? = null,
    val executionProgress: Int? = null,
    val verified: Boolean = false
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String = "General",
    val status: String = "Active", // "Active", "Planning", "Completed"
    val requirements: String = "",
    val notes: String = "",
    val filesList: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val triggerDesc: String,
    val conditionDesc: String,
    val actionDesc: String,
    val isActive: Boolean = true,
    val scheduleTime: String = "08:00 AM Daily",
    val lastRunResult: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memory_items")
data class MemoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "preference", "project", "instruction", "fact"
    val key: String,
    val value: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionName: String,
    val riskLevel: String, // "LOW", "MEDIUM", "HIGH"
    val status: String, // "EXECUTED", "CONFIRMED", "REJECTED", "BLOCKED"
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
