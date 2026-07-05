package com.example.ui

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.util.LocaleManager
import com.example.util.JalaliCalendar
import com.example.util.JalaliCalendar.toPersianDigits
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SubTaskEntity
import com.example.data.TaskEntity
import com.example.util.DateTimeUtils
import com.example.viewmodel.FilterPeriod
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ThemeSelection
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(TabDestination.TASKS) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Observe State flows from viewModel
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOfflineMode.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val lang by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isPersian = lang == "fa"
    val strings = LocaleManager.getStrings(lang)
    val layoutDirection = if (isPersian) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Request Notification Permissions on Startup (Android 13+)
    var hasNotificationPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, if (isPersian) "اعلان‌های هوشمند فعال شدند!" else "Notifications enabled for BitTask!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermission = true
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
            topBar = {
                BitTaskTopAppBar(
                    lang = lang
                )
            },
            bottomBar = {
                BitTaskBottomNavigation(
                    currentTab = currentTab,
                    lang = lang,
                    onTabSelected = { currentTab = it }
                )
            },
            floatingActionButton = {
                if (currentTab == TabDestination.TASKS || currentTab == TabDestination.CALENDAR) {
                    FloatingActionButton(
                        onClick = { showAddTaskDialog = true },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(bottom = 16.dp).testTag("add_task_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = strings.createNewTaskTitle,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    )
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                    },
                    label = "NavigationTransition"
                ) { targetDestination ->
                    when (targetDestination) {
                        TabDestination.TASKS -> TasksDestinationView(viewModel, tasks, selectedDate)
                        TabDestination.CALENDAR -> CalendarDestinationView(viewModel, tasks, selectedDate)
                        TabDestination.SETTINGS -> SettingsDestinationView(viewModel)
                        TabDestination.HELP -> HelpDestinationView(lang)
                    }
                }
            }
        }

        // Add Task Dialog Sheet
        if (showAddTaskDialog) {
            AddTaskDialog(
                viewModel = viewModel,
                initialTimestamp = selectedDate,
                onDismiss = { showAddTaskDialog = false }
            )
        }
    }
}

// ==========================================
// 1. TOOLBAR & FOOTER NAVIGATION
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitTaskTopAppBar(
    lang: String
) {
    val strings = LocaleManager.getStrings(lang)
    val isPersian = lang == "fa"
    Surface(
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.shadow(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Task,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp).padding(end = 4.dp)
                    )
                    Text(
                        text = "BitTask",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = strings.appSubTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Removed Offline Toggle and Sync Action Button
            }
        }
    }
}

private fun Modifier.animateLoopRotate(): Modifier = this // simplified animation placeholder helper

@Composable
fun BitTaskBottomNavigation(
    currentTab: TabDestination,
    lang: String,
    onTabSelected: (TabDestination) -> Unit
) {
    val strings = LocaleManager.getStrings(lang)
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding().testTag("bottom_nav_bar")
    ) {
        TabDestination.values().forEach { destination ->
            val isSelected = currentTab == destination
            val label = when (destination) {
                TabDestination.TASKS -> strings.tabTasks
                TabDestination.CALENDAR -> strings.tabCalendar
                TabDestination.SETTINGS -> strings.tabSettings
                TabDestination.HELP -> strings.tabHelp
            }
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.activeIcon else destination.inactiveIcon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                ),
                modifier = Modifier.testTag("tab_${destination.name.lowercase()}")
            )
        }
    }
}

enum class TabDestination(
    val activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    TASKS(Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
    CALENDAR(Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    SETTINGS(Icons.Filled.Settings, Icons.Outlined.Settings),
    HELP(Icons.Filled.Help, Icons.Outlined.Help)
}

@Composable
fun HelpDestinationView(lang: String) {
    val strings = LocaleManager.getStrings(lang)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = strings.helpTitle,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = strings.helpSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        val items = listOf(
            strings.helpGetStartedTitle to strings.helpGetStartedDesc,
            strings.helpCalendarTitle to strings.helpCalendarDesc,
            strings.helpNotificationsTitle to strings.helpNotificationsDesc,
            strings.helpOfflineTitle to strings.helpOfflineDesc,
            strings.helpLocalizationTitle to strings.helpLocalizationDesc
        )

        items(items) { (title, desc) ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================
// 2. DESTINATION: TASKS BOARD VIEW
// ==========================================

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TasksDestinationView(
    viewModel: MainViewModel,
    tasks: List<TaskEntity>,
    selectedDate: Long
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterPeriod by viewModel.filterPeriod.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()
    val priorityFilter by viewModel.priorityFilter.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    var showAdvancedFilters by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search panel row with Advanced filter button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search your tasks...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_bar"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
            )

            // Trigger for professional filter board
            IconButton(
                onClick = { showAdvancedFilters = !showAdvancedFilters },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (showAdvancedFilters) MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    )
                    .border(
                        1.dp, 
                        if (showAdvancedFilters) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 
                        CircleShape
                    )
                    .testTag("filter_tune_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Tune Filters",
                    tint = if (showAdvancedFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Advanced Filter Drawer (Slide out expandable)
        AnimatedVisibility(
            visible = showAdvancedFilters,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .shadow(3.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Professional Filter Desk",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // 1. Completion State Status Filters
                    Text("Completion Status:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            com.example.viewmodel.TaskStatusFilter.ALL to "All Items",
                            com.example.viewmodel.TaskStatusFilter.ACTIVE to "Active",
                            com.example.viewmodel.TaskStatusFilter.COMPLETED to "Completed"
                        ).forEach { (status, label) ->
                            val isSelected = statusFilter == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setStatusFilter(status) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Priority Selector
                    Text("Filter Priority Class:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            null to "All Priority",
                            "High" to "High 🔴",
                            "Medium" to "Medium 🟡",
                            "Low" to "Low 🟢"
                        ).forEach { (pKey, label) ->
                            val isSelected = priorityFilter == pKey
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setPriorityFilter(pKey) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Sorting Filters
                    Text("Sort Sequence Order:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            com.example.viewmodel.TaskSortOption.NONE to "Default",
                            com.example.viewmodel.TaskSortOption.PRIORITY_HIGH_TO_LOW to "Priority High ⚡",
                            com.example.viewmodel.TaskSortOption.DUE_DATE_ASC to "Nearest Due 📅",
                            com.example.viewmodel.TaskSortOption.TITLE_A_Z to "Alphabetical [A-Z]"
                        ).forEach { (sort, label) ->
                            val isSelected = sortBy == sort
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSortBy(sort) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Quick Category Tag Carousel (Horizontal scroll)
        val tags = listOf("All", "Work", "Tech", "Study", "Shopping", "Health", "Finance")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().testTag("tag_carousel")
        ) {
            items(tags) { tag ->
                val isSelected = if (tag == "All") selectedTag == null else selectedTag == tag
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (tag == "All") viewModel.setTagFilter(null) else viewModel.setTagFilter(tag)
                    },
                    label = { Text(tag) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time Filter Timeline Buttons (Day vs Week vs All)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    FilterPeriod.ALL to "All Time",
                    FilterPeriod.DAY to "Today",
                    FilterPeriod.WEEK to "This Week"
                ).forEach { (period, title) ->
                    val isSelected = filterPeriod == period
                    ElevatedButton(
                        onClick = { viewModel.setFilterPeriod(period) },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp).testTag("filter_period_${period.name.lowercase()}")
                    ) {
                        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active filters list details
        if (filterPeriod != FilterPeriod.ALL) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val descriptor = buildString {
                        if (filterPeriod == FilterPeriod.DAY) append("Active Target: ${DateTimeUtils.formatDate(selectedDate)} ")
                        if (filterPeriod == FilterPeriod.WEEK) append("Active Target: Week View ")
                    }
                    Text(descriptor, style = MaterialTheme.typography.labelSmall)
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tasks list builder
        if (tasks.isEmpty()) {
            EmptyStateView()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag("tasks_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Tasks Found",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Keep track of deadlines by adding a task below. Tap 'Cloud Sync' or join a team board to explore syncing capabilities.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    viewModel: MainViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val subTasksFlow = remember(task.id) { viewModel.getSubtasksForTask(task.id) }
    val subTasks by subTasksFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    var newSubtaskText by remember { mutableStateOf("") }

    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFE57373)
        "Medium" -> Color(0xFFFFD54F)
        else -> Color(0xFF81C784)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .testTag("task_card_${task.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Completability checkbox custom sized with tactile ripple
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { viewModel.updateTaskCompletion(task, it) },
                        modifier = Modifier.testTag("checkbox_${task.id}")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = DateTimeUtils.formatDate(task.dueDate),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Priority tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(priorityColor.copy(alpha = 0.2f))
                            .border(1.dp, priorityColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.priority,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (task.priority == "High") Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.deleteTask(task) },
                        modifier = Modifier.testTag("delete_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Expandable area
            if (task.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Top) {
                    if (task.encrypted) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "AES E2E Encrypted",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(14.dp).padding(top = 2.dp, end = 2.dp)
                        )
                    }
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }

            // Interactive smart tag pills
            if (task.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    task.tags.split(",").forEach { tag ->
                        if (tag.trim().isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "#${tag.trim()}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // If collaborative, show assignment details
            if (task.isCollaborative) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentInd,
                        contentDescription = "Collaborator",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Assigned to: ${task.assignedTeamMember.ifEmpty { "Unassigned" }} (Code: ${task.sharedCode})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // Nested expanding lists with sub-tasks
            if (expanded) {
                Divider(modifier = Modifier.padding(vertical = 10.dp))
                Text(
                    text = "Sub-tasks Checklist",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                subTasks.forEach { subItem ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = subItem.isCompleted,
                                onCheckedChange = { viewModel.updateSubtaskSelected(subItem, it) }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = subItem.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = if (subItem.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                color = if (subItem.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { viewModel.deleteSubtask(subItem) }) {
                            Icon(Icons.Default.Close, "Remove Subtask", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Inline quick subtask input
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newSubtaskText,
                        onValueChange = { newSubtaskText = it },
                        placeholder = { Text("Add subtask...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(48.dp),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (newSubtaskText.isNotEmpty()) {
                                viewModel.addSubtaskDirectly(task.id, newSubtaskText)
                                newSubtaskText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Add", fontSize = 11.sp)
                    }
                }
            } else {
                // Peek indicator
                if (subTasks.isNotEmpty()) {
                    val count = subTasks.count { it.isCompleted }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sub-tasks: $count/${subTasks.size} completed (Tap to expand)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. DESTINATION: CALENDAR PLANNER
// ==========================================

@Composable
fun CalendarDestinationView(
    viewModel: MainViewModel,
    tasks: List<TaskEntity>,
    selectedDate: Long
) {
    val calMode by viewModel.calendarMode.collectAsStateWithLifecycle()
    val lang by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isSolar = calMode == "solar"
    val isPersian = lang == "fa"
    val strings = LocaleManager.getStrings(lang)

    // Solar calendar state
    var jalaliYear by remember { mutableStateOf(JalaliCalendar.getJalaliDateFromMillis(selectedDate).year) }
    var jalaliMonth by remember { mutableStateOf(JalaliCalendar.getJalaliDateFromMillis(selectedDate).month) } // 1..12

    // Gregorian calendar state
    var calendarMonth by remember { mutableStateOf(Calendar.getInstance().apply { timeInMillis = selectedDate }) }

    val daysInMonth = if (isSolar) {
        when {
            jalaliMonth in 1..6 -> 31
            jalaliMonth in 7..11 -> 30
            else -> {
                val isLeap = (jalaliYear % 33) in listOf(1, 5, 9, 13, 17, 22, 26, 30)
                if (isLeap) 30 else 29
            }
        }
    } else {
        calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val startDayOfWeekOffset = if (isSolar) {
        val firstDayGCal = JalaliCalendar.j2g(jalaliYear, jalaliMonth, 1)
        firstDayGCal.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday, etc.
    } else {
        val firstDayCal = Calendar.getInstance().apply {
            timeInMillis = calendarMonth.timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
        firstDayCal.get(Calendar.DAY_OF_WEEK) - 1
    }

    // Get month names
    val currentYear = calendarMonth.get(Calendar.YEAR)
    val currentMonth = calendarMonth.get(Calendar.MONTH)
    val monthNamesEn = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    val bannerText = if (isSolar) {
        val monthName = if (isPersian) JalaliCalendar.monthNamesFa[jalaliMonth - 1] else JalaliCalendar.monthNamesEn[jalaliMonth - 1]
        val yearStr = if (isPersian) jalaliYear.toString().toPersianDigits() else jalaliYear.toString()
        "$monthName $yearStr"
    } else {
        "${monthNamesEn[currentMonth]} $currentYear"
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(12.dp))
        
        // Month Controller Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (isSolar) {
                        if (jalaliMonth == 1) {
                            jalaliMonth = 12
                            jalaliYear--
                        } else {
                            jalaliMonth--
                        }
                    } else {
                        val next = Calendar.getInstance().apply {
                            timeInMillis = calendarMonth.timeInMillis
                            add(Calendar.MONTH, -1)
                        }
                        calendarMonth = next
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, if (isPersian) "ماه قبل" else "Prev Month")
                }

                Text(
                    text = bannerText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                IconButton(onClick = {
                    if (isSolar) {
                        if (jalaliMonth == 12) {
                            jalaliMonth = 1
                            jalaliYear++
                        } else {
                            jalaliMonth++
                        }
                    } else {
                        val next = Calendar.getInstance().apply {
                            timeInMillis = calendarMonth.timeInMillis
                            add(Calendar.MONTH, 1)
                        }
                        calendarMonth = next
                    }
                }) {
                    Icon(Icons.Default.ArrowForward, if (isPersian) "ماه بعد" else "Next Month")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Weekdays Headers Row
        val weekdays = if (isPersian) {
            listOf("ی", "د", "س", "چ", "پ", "ج", "ش")
        } else {
            listOf("S", "M", "T", "W", "T", "F", "S")
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid matrix
        val dayRows = (daysInMonth + startDayOfWeekOffset + 6) / 7
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in 0 until dayRows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val dayIndex = row * 7 + col - startDayOfWeekOffset + 1
                        val isValidDay = dayIndex in 1..daysInMonth
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isValidDay) {
                                val cellTime = if (isSolar) {
                                    JalaliCalendar.j2g(jalaliYear, jalaliMonth, dayIndex).timeInMillis
                                } else {
                                    Calendar.getInstance().apply {
                                        timeInMillis = calendarMonth.timeInMillis
                                        set(Calendar.DAY_OF_MONTH, dayIndex)
                                        set(Calendar.HOUR_OF_DAY, 12)
                                    }.timeInMillis
                                }
                                val isToday = DateTimeUtils.isSameDay(cellTime, System.currentTimeMillis())
                                val isSelected = DateTimeUtils.isSameDay(cellTime, selectedDate)
                                val hasTasks = tasks.any { DateTimeUtils.isSameDay(it.dueDate, cellTime) }
                                
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            else Color.Transparent
                                        )
                                        .border(
                                            width = if (isToday) 2.dp else 0.dp,
                                            color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            viewModel.setDate(cellTime)
                                            viewModel.setFilterPeriod(FilterPeriod.DAY)
                                        }
                                        .testTag("day_cell_$dayIndex"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (isPersian) dayIndex.toString().toPersianDigits() else dayIndex.toString(),
                                            fontWeight = if (isSelected || isToday) FontWeight.Black else FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                                    else if (isToday) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (hasTasks) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Red)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        val selectedDateFormatted = if (isSolar) {
            JalaliCalendar.formatJalali(selectedDate, isPersian)
        } else {
            DateTimeUtils.formatDate(selectedDate)
        }

        Text(
            text = "${strings.taskSectionDeadlines}: $selectedDateFormatted",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Scheduled items list helper
        val datedTasks = tasks.filter { DateTimeUtils.isSameDay(it.dueDate, selectedDate) }
        if (datedTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(strings.noTasksFound, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(datedTasks) { task ->
                    TaskCard(task = task, viewModel = viewModel)
                }
            }
        }
    }
}
     // ==========================================
// 4. DESTINATION: ENGINE SETTINGS
// ==========================================

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsDestinationView(viewModel: MainViewModel) {
    val context = LocalContext.current
    val darkThemeMode by viewModel.darkThemeMode.collectAsStateWithLifecycle()
    val notificationOffset by viewModel.notificationOffsetMinutes.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val calendarMode by viewModel.calendarMode.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOfflineMode.collectAsStateWithLifecycle()
    val syncLogs by viewModel.syncLogs.collectAsStateWithLifecycle()

    val isPersian = appLanguage == "fa"
    val strings = LocaleManager.getStrings(appLanguage)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_destination"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Section header
        item {
            Column(modifier = Modifier.padding(bottom = 6.dp)) {
                Text(
                    text = strings.settingsPanelTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = strings.settingsPanelSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. LANGUAGE & CALENDAR ENGINES (Bilingual Controls)
        item {
            GlassBox(
                title = strings.settingsLangCalendarCell,
                icon = Icons.Outlined.Translate
            ) {
                // Selector
                Text(
                    text = strings.settingsLangCalendarCell,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "en" to "English (EN)",
                        "fa" to "فارسی (FA)"
                    ).forEach { (code, label) ->
                        val isSelected = appLanguage == code
                        Button(
                            onClick = { viewModel.setAppLanguage(code) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                            ),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calendar System Mode
                Text(
                    text = strings.settingsCalendarModeLabel,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "gregorian" to strings.calendarMiladiLabel,
                        "solar" to strings.calendarKhorshidiLabel
                    ).forEach { (modeCode, label) ->
                        val isSelected = calendarMode == modeCode
                        Button(
                            onClick = { viewModel.setCalendarMode(modeCode) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                            ),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. BASIC PREFERENCES
        item {
            GlassBox(
                title = strings.settingsLookFeelSec,
                icon = Icons.Outlined.Build
            ) {
                // Theme selector
                Text(
                    text = strings.settingsLookFeelSec,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        ThemeSelection.SYSTEM to "Default",
                        ThemeSelection.LIGHT to "Light",
                        ThemeSelection.DARK to "Dark"
                    ).forEach { (mode, label) ->
                        val isSelected = darkThemeMode == mode
                        Button(
                            onClick = { viewModel.setDarkThemeMode(mode) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                            ),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notification offsets
                Text(
                    text = strings.settingsEngineType,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = strings.settingsNotificationSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        0 to "0m",
                        5 to "5m",
                        15 to "15m",
                        60 to "1h"
                    ).forEach { (offset, label) ->
                        val isSelected = notificationOffset == offset
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateNotificationOffset(offset) },
                            label = { Text(label, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        // 3. SECURE LOCAL STANDALONE OPERATIONS
        item {
            GlassBox(
                title = strings.settingsSecureLocalTitle,
                icon = Icons.Outlined.DynamicForm
            ) {
                // Standalone description
                Text(
                    text = strings.settingsSecureLocalDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Actions panel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.triggerManualSync() },
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPersian) "تهیه فایل پشتیبان" else "Backup DB", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { viewModel.triggerInstantTestNotification() },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.NotificationsActive, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.buttonTestNotify, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Standalone Activity terminal Logger
                Text(
                    text = strings.settingsSecureLocalLogs,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(syncLogs) { log ->
                            Text(
                                text = log,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. TELEMETRY, SYSTEM & MOBILE DIAGNOSTIC INFO
        item {
            GlassBox(
                title = strings.settingsSecureLocalInfo,
                icon = Icons.Outlined.Info
            ) {
                TelemetryRow(label = if (isPersian) "نام محصول" else "Product Name", value = "BitTask Suite")
                TelemetryRow(label = if (isPersian) "توسعه‌دهنده سیستم" else "System Lead", value = "taha (tahaprogramming)")
                TelemetryRow(label = if (isPersian) "معماری ذخیره‌سازی داده" else "Database Storage Engine", value = "SQLite / Room Standalone Local Client")
                TelemetryRow(label = if (isPersian) "نسخه سیستم‌عامل هدف" else "Kernel System Release", value = System.getProperty("os.version") ?: "Linux x86_64")
                TelemetryRow(label = if (isPersian) "مدل دستگاه میزبان" else "Host Platform", value = "${Build.MANUFACTURER} ${Build.MODEL}")
                TelemetryRow(label = if (isPersian) "لایه امنیتی رمزنگاری" else "Database Crypto Layer", value = "AES-256 local-secured")
            }
        }
    }
}

@Composable
fun GlassBox(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgAlpha = if (isDark) 0.65f else 0.85f
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF0A84FF) else Color(0xFF3B82F6)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor.copy(alpha = bgAlpha)
        ),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
fun TelemetryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ==========================================
// 6. TASK CREATION DIALOG & SMART TAGGING
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTaskDialog(
    viewModel: MainViewModel,
    initialTimestamp: Long,
    onDismiss: () -> Unit
) {
    val calMode by viewModel.calendarMode.collectAsStateWithLifecycle()
    val lang by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isSolar = calMode == "solar"
    val isPersian = lang == "fa"
    val strings = LocaleManager.getStrings(lang)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var tagsInput by remember { mutableStateOf("") }

    // Time state
    var hour by remember { mutableStateOf(12) }
    var minute by remember { mutableStateOf(0) }

    // Date Picker state matching the chosen calendar system
    val selectCal = Calendar.getInstance().apply { timeInMillis = initialTimestamp }
    
    // Solar coordinates
    val initJalali = JalaliCalendar.getJalaliDateFromMillis(initialTimestamp)
    var solarYear by remember { mutableStateOf(initJalali.year) }
    var solarMonth by remember { mutableStateOf(initJalali.month) }
    var solarDay by remember { mutableStateOf(initJalali.day) }

    // Gregorian coordinates
    var gregYear by remember { mutableStateOf(selectCal.get(Calendar.YEAR)) }
    var gregMonth by remember { mutableStateOf(selectCal.get(Calendar.MONTH) + 1) } // 1..12
    var gregDay by remember { mutableStateOf(selectCal.get(Calendar.DAY_OF_MONTH)) }

    // Live parsed coordinates check & preview builder
    val liveDueDateMillis = remember(isSolar, solarYear, solarMonth, solarDay, gregYear, gregMonth, gregDay, hour, minute) {
        try {
            if (isSolar) {
                JalaliCalendar.j2g(solarYear, solarMonth, solarDay).apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            } else {
                Calendar.getInstance().apply {
                    set(Calendar.YEAR, gregYear)
                    set(Calendar.MONTH, gregMonth - 1)
                    set(Calendar.DAY_OF_MONTH, gregDay)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
        } catch (e: Exception) {
            initialTimestamp
        }
    }

    val previewText = if (isSolar) {
        val datePart = JalaliCalendar.formatJalali(liveDueDateMillis, isPersian)
        val timePart = String.format("%02d:%02d", hour, minute).let { if (isPersian) it.toPersianDigits() else it }
        "$datePart - $timePart"
    } else {
        val datePart = DateTimeUtils.formatDate(liveDueDateMillis)
        val timePart = String.format("%02d:%02d", hour, minute)
        "$datePart - $timePart"
    }

    // Smart Predicted tags based on input
    val predictedTags = viewModel.predictSmartTags(title, description)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.createNewTaskTitle, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Task Title
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(strings.fieldTitle) },
                        placeholder = { Text(strings.fieldTitlePlaceholder) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_title")
                    )
                }

                // Description
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(strings.fieldDescription) },
                        placeholder = { Text(strings.fieldDescriptionPlaceholder) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("input_description")
                    )
                }

                // Urgency Priority
                item {
                    Text(
                        text = if (isPersian) "شاخص ضرورت اجرا" else "Urgency Priority",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Low", "Medium", "High").forEach { choice ->
                            val choiceLabel = when (choice) {
                                "Low" -> strings.priorityLow
                                "Medium" -> strings.priorityMedium
                                else -> strings.priorityHigh
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { priority = choice }
                            ) {
                                RadioButton(selected = priority == choice, onClick = { priority = choice })
                                Text(choiceLabel, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Multi-Mode Date Selector
                item {
                    Text(
                        text = strings.fieldDueDate,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isSolar) {
                            OutlinedTextField(
                                value = solarDay.toString(),
                                onValueChange = { solarDay = it.toIntOrNull() ?: solarDay },
                                label = { Text(if (isPersian) "روز" else "Day") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = solarMonth.toString(),
                                onValueChange = { solarMonth = it.toIntOrNull() ?: solarMonth },
                                label = { Text(if (isPersian) "ماه" else "Month") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = solarYear.toString(),
                                onValueChange = { solarYear = it.toIntOrNull() ?: solarYear },
                                label = { Text(if (isPersian) "سال" else "Year") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.5f)
                            )
                        } else {
                            OutlinedTextField(
                                value = gregDay.toString(),
                                onValueChange = { gregDay = it.toIntOrNull() ?: gregDay },
                                label = { Text("Day") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = gregMonth.toString(),
                                onValueChange = { gregMonth = it.toIntOrNull() ?: gregMonth },
                                label = { Text("Month") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = gregYear.toString(),
                                onValueChange = { gregYear = it.toIntOrNull() ?: gregYear },
                                label = { Text("Year") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.5f)
                            )
                        }
                    }
                }

                // Exact Hour & Minute selectors
                item {
                    Text(
                        text = strings.fieldDueTime,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hour.toString(),
                            onValueChange = { hour = (it.toIntOrNull() ?: hour).coerceIn(0, 23) },
                            label = { Text(if (isPersian) "ساعت" else "Hour") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minute.toString(),
                            onValueChange = { minute = (it.toIntOrNull() ?: minute).coerceIn(0, 59) },
                            label = { Text(if (isPersian) "دقیقه" else "Minute") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Live calculated Preview banner Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isPersian) "برنامه‌ریزی دقیق کلاینت:" else "Accurate Due Preview:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = previewText,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Smart Tagging Section
                item {
                    Column {
                        OutlinedTextField(
                            value = tagsInput,
                            onValueChange = { tagsInput = it },
                            label = { Text(strings.fieldTags) },
                            placeholder = { Text(strings.fieldTagsPlaceholder) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_tags")
                        )

                        if (predictedTags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isPersian) "برچسب‌های پیشنهادی هوشمند (کلیک جهت ثبت):" else "Suggested Tags (Tap to add):",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                                predictedTags.forEach { predTag ->
                                    val contains = tagsInput.split(",").map { it.trim().lowercase() }.contains(predTag.lowercase())
                                    ElevatedFilterChip(
                                        selected = contains,
                                        onClick = {
                                            val parts = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                                            if (!parts.map { it.lowercase() }.contains(predTag.lowercase())) {
                                                parts.add(predTag)
                                                tagsInput = parts.joinToString(", ")
                                            }
                                        },
                                        label = { Text(predTag, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        viewModel.saveTask(
                            title = title,
                            description = description,
                            dueDate = liveDueDateMillis,
                            tags = tagsInput,
                            priority = priority,
                            isCollaborative = false,
                            assignedMember = ""
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("submit_task_button")
            ) {
                Text(strings.buttonSave)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.buttonCancel)
            }
        }
    )
}
