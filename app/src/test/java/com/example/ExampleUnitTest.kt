package com.example

import com.example.data.model.ChatMessageEntity
import com.example.ui.components.ConversationalMessage
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testConversationalMessageCreation() {
    val message = ConversationalMessage(
      text = "Hello JARVIS",
      isUser = true,
      senderName = "Commander"
    )
    assertEquals("Hello JARVIS", message.text)
    assertTrue(message.isUser)
    assertEquals("Commander", message.senderName)
  }

  @Test
  fun testChatMessageEntityMapping() {
    val entity = ChatMessageEntity(
      id = 42L,
      role = "nexus",
      content = "System status nominal.",
      mode = "coder"
    )
    val conv = ConversationalMessage(
      id = entity.id.toString(),
      text = entity.content,
      isUser = entity.role.equals("user", ignoreCase = true),
      senderName = if (entity.role == "user") "COMMAND" else "J.A.R.V.I.S.",
      tag = entity.mode.uppercase()
    )
    assertEquals("42", conv.id)
    assertEquals("System status nominal.", conv.text)
    assertFalse(conv.isUser)
    assertEquals("J.A.R.V.I.S.", conv.senderName)
    assertEquals("CODER", conv.tag)
  }
}

