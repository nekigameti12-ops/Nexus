package com.example.data.db

import com.example.data.model.AuditLogEntity
import com.example.data.model.AutomationEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MemoryItemEntity
import com.example.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

class NexusRepository(private val dao: NexusDao) {

    val messages: Flow<List<ChatMessageEntity>> = dao.getAllMessages()
    val projects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val automations: Flow<List<AutomationEntity>> = dao.getAllAutomations()
    val memoryItems: Flow<List<MemoryItemEntity>> = dao.getAllMemoryItems()
    val auditLogs: Flow<List<AuditLogEntity>> = dao.getAuditLogs()

    suspend fun saveMessage(message: ChatMessageEntity): Long = dao.insertMessage(message)
    suspend fun clearChat() = dao.clearAllMessages()

    suspend fun saveProject(project: ProjectEntity): Long = dao.insertProject(project)
    suspend fun updateProject(project: ProjectEntity) = dao.updateProject(project)
    suspend fun deleteProject(id: Long) = dao.deleteProjectById(id)

    suspend fun saveAutomation(automation: AutomationEntity): Long = dao.insertAutomation(automation)
    suspend fun updateAutomation(automation: AutomationEntity) = dao.updateAutomation(automation)
    suspend fun deleteAutomation(id: Long) = dao.deleteAutomationById(id)

    suspend fun saveMemory(item: MemoryItemEntity): Long = dao.insertMemoryItem(item)
    suspend fun updateMemory(item: MemoryItemEntity) = dao.updateMemoryItem(item)
    suspend fun deleteMemory(id: Long) = dao.deleteMemoryItemById(id)
    suspend fun clearMemory() = dao.clearAllMemory()

    suspend fun logAction(action: String, risk: String, status: String, details: String) {
        dao.insertAuditLog(
            AuditLogEntity(
                actionName = action,
                riskLevel = risk,
                status = status,
                details = details
            )
        )
    }
}
