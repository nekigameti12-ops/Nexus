package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.JarvisFire
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisOrange
import com.example.ui.theme.NexusAmber
import com.example.ui.theme.NexusBlue
import com.example.ui.theme.NexusCrimson
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusSurfaceVariant
import com.example.ui.theme.NexusTextDim
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusViolet
import com.example.ui.theme.NexusVoid

data class InterfaceItem(
    val screen: NexusScreen,
    val title: String,
    val hindiTitle: String,
    val description: String,
    val icon: ImageVector,
    val badge: String,
    val color: Color
)

@Composable
fun InterfacesScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val activeAccent by viewModel.accentTheme.collectAsState()
    val activeDensity by viewModel.uiDensity.collectAsState()
    val isQuickDockEnabled by viewModel.isQuickDockEnabled.collectAsState()
    val isOnline = viewModel.configStore.isCoreOnline()

    var showCustomizer by remember { mutableStateOf(true) }

    val accentColors = listOf(
        "jarvis" to ("JARVIS Gold" to JarvisGold),
        "cyan" to ("Cyber Cyan" to NexusCyan),
        "violet" to ("Electric Violet" to NexusViolet),
        "azure" to ("Neon Azure" to NexusBlue),
        "emerald" to ("Matrix Emerald" to NexusEmerald),
        "amber" to ("Solar Amber" to NexusAmber),
        "crimson" to ("Crimson Core" to NexusCrimson)
    )

    val interfacesList = listOf(
        InterfaceItem(
            screen = NexusScreen.DASHBOARD,
            title = "J.A.R.V.I.S. Command Center",
            hindiTitle = "कंट्रोल सेंटर (JARVIS आर्क रिएक्टर)",
            description = "High-energy golden Arc Reactor with rotating orbital rings, solar flares, live system telemetry, and audio engagement.",
            icon = Icons.Default.Dashboard,
            badge = if (isOnline) "ONLINE" else "STANDBY",
            color = JarvisGold
        ),
        InterfaceItem(
            screen = NexusScreen.CHAT,
            title = "Neural AI Chat & Sandbox",
            hindiTitle = "चैट इंटरफेस (AI चैट व कोडर)",
            description = "Real-time conversational streaming, markdown rendering, syntax code blocks, and instant voice playback.",
            icon = Icons.Default.Forum,
            badge = "STREAMING",
            color = NexusBlue
        ),
        InterfaceItem(
            screen = NexusScreen.VOICE,
            title = "Holographic Voice Interface",
            hindiTitle = "वॉयस इंटरफेस (JARVIS होलोग्राफिक आवाज)",
            description = "Arc reactor vortex visualizer with natural deep-timbre male voice synthesis in 4 languages.",
            icon = Icons.Default.Mic,
            badge = "LIVE RMS",
            color = JarvisGold
        ),
        InterfaceItem(
            screen = NexusScreen.AGENT,
            title = "Autonomous Agent Engine",
            hindiTitle = "एजेंट इंटरफेस (स्वायत्त टास्क इंजन)",
            description = "Autonomous task planner: HEAR → PLAN → CONNECT → EXECUTE → VERIFY → DELIVER lifecycle progress.",
            icon = Icons.Default.SmartToy,
            badge = "PLANNER",
            color = NexusViolet
        ),
        InterfaceItem(
            screen = NexusScreen.PROJECTS,
            title = "Project Workspaces",
            hindiTitle = "प्रोजेक्ट वर्कस्पेस (फाइल व कोड)",
            description = "Neural project manager tracking tech stacks, architecture specs, file requirements, and direct Nexus execution.",
            icon = Icons.Default.Workspaces,
            badge = "WORKSPACE",
            color = NexusEmerald
        ),
        InterfaceItem(
            screen = NexusScreen.AUTOMATIONS,
            title = "Nexus Automate",
            hindiTitle = "ऑटोमेशन इंटरफेस (शेड्यूल रूटीन)",
            description = "Trigger • Condition • Action scheduled workflows for recurring briefings, research, and reminders.",
            icon = Icons.Default.Schedule,
            badge = "SCHEDULER",
            color = NexusAmber
        ),
        InterfaceItem(
            screen = NexusScreen.MEMORY,
            title = "Neural Memory Vault",
            hindiTitle = "मेमोरी इंटरफेस (यूजर प्राथमिकताएं)",
            description = "Transparent user preferences, verified facts, and project context with full review and purge controls.",
            icon = Icons.Default.Psychology,
            badge = "VAULT",
            color = NexusEmerald
        ),
        InterfaceItem(
            screen = NexusScreen.TOOLS,
            title = "Connected Tool Matrix",
            hindiTitle = "टूल्स इंटरफेस (वेब, कोड, IoT हब)",
            description = "Web intelligence search, code sandbox, precision math engine, documents, and smart home IoT bulb controls.",
            icon = Icons.Default.Widgets,
            badge = "5 TOOLS",
            color = NexusCyan
        ),
        InterfaceItem(
            screen = NexusScreen.SETTINGS,
            title = "Settings & Security Vault",
            hindiTitle = "सेटिंग्स इंटरफेस (सुरक्षा व आवाज)",
            description = "Encrypted API credential manager, male voice pitch/rate tuning, risk clearance toggles, and audit trail.",
            icon = Icons.Default.Settings,
            badge = "SECURITY",
            color = NexusBlue
        ),
        InterfaceItem(
            screen = NexusScreen.INITIALIZATION,
            title = "Setup & Initialization Gate",
            hindiTitle = "सेटअप इंटरफेस (AI प्रोवाइडर गेट)",
            description = "First-launch security clearance gate, live endpoint ping validation, and offline security guard.",
            icon = Icons.Default.Lock,
            badge = "SECURITY GATE",
            color = NexusCrimson
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(NexusScreen.DASHBOARD) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NexusCyan)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "APP INTERFACES DIRECTORY",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NexusCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ऐप के सभी 10 इंटरफेस देखें व कस्टमाइज़ करें",
                        fontSize = 11.sp,
                        color = NexusTextSecondary
                    )
                }
            }

            IconButton(onClick = { showCustomizer = !showCustomizer }) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Customize Interface",
                    tint = if (showCustomizer) NexusCyan else NexusTextDim
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // INTERFACE CUSTOMIZER (INTERFACE CHANGES)
        AnimatedVisibility(visible = showCustomizer) {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NexusCyan.copy(alpha = 0.5f)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Palette, contentDescription = "Theme", tint = NexusCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "INTERFACE CUSTOMIZATION (इंटरफेस सेटिंग्स)",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = NexusCyan,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NexusCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIVE THEME",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = NexusCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "THEME ACCENT COLOR (थीम एक्सेंट रंग)",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NexusTextDim
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        accentColors.forEach { (key, pair) ->
                            val (name, color) = pair
                            val isSelected = activeAccent.equals(key, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(color.copy(alpha = 0.2f))
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) color else color.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setAccentTheme(key) },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = NexusVoid,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // UI Layout Density
                    Text(
                        text = "LAYOUT DENSITY (इंटरफेस लेआउट स्टाइल)",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NexusTextDim
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("compact" to "Compact (सघन)", "standard" to "Standard (संतुलित)", "spacious" to "Spacious (खुला)").forEach { (key, label) ->
                            val isSelected = activeDensity.equals(key, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NexusCyan.copy(alpha = 0.2f) else NexusSurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) NexusCyan else NexusTextDim.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setUiDensity(key) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NexusCyan else NexusTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Dock Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Quick Navigation Dock (क्विक नेविगेशन बार)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NexusTextPrimary
                            )
                            Text(
                                text = "Bottom dock for 1-tap switching across interfaces",
                                fontSize = 10.sp,
                                color = NexusTextSecondary
                            )
                        }

                        Switch(
                            checked = isQuickDockEnabled,
                            onCheckedChange = { viewModel.setQuickDockEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NexusCyan,
                                checkedTrackColor = NexusCyan.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // All 10 Interfaces Showcase Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ALL APP INTERFACES (सभी 10 इंटरफेस)",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NexusCyan,
                letterSpacing = 1.5.sp
            )

            Text(
                text = "10 SCREENS AVAILABLE",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = NexusTextDim
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List of all interface cards
        interfacesList.forEach { item ->
            InterfaceCard(
                item = item,
                onOpen = { viewModel.navigateTo(item.screen) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun InterfaceCard(
    item: InterfaceItem,
    onOpen: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        borderColor = item.color.copy(alpha = 0.4f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(item.color.copy(alpha = 0.15f))
                            .border(1.dp, item.color.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = NexusTextPrimary
                        )
                        Text(
                            text = item.hindiTitle,
                            fontSize = 11.sp,
                            color = item.color
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(item.color.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.badge,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = item.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                fontSize = 11.sp,
                color = NexusTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpen,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = item.color)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "OPEN INTERFACE / इंटरफेस देखें",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open",
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
