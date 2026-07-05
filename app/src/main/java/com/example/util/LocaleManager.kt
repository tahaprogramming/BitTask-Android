package com.example.util

object LocaleManager {

    enum class AppLanguage(val code: String, val displayName: String) {
        ENGLISH("en", "English"),
        PERSIAN("fa", "فارسی")
    }

    enum class CalendarMode(val key: String, val displayNameEn: String, val displayNameFa: String) {
        GREGORIAN("gregorian", "Gregorian", "میلادی"),
        SOLAR("solar", "Solar/Jalali", "شمسی")
    }

    sealed class AppStrings {
        abstract val appLogo: String
        abstract val appSubTitle: String
        abstract val tabTasks: String
        abstract val tabCalendar: String
        abstract val tabSettings: String
        abstract val tabHelp: String

        // Task Board Filters
        abstract val searchPlaceholder: String
        abstract val advancedFiltersTitle: String
        abstract val filterAll: String
        abstract val filterActive: String
        abstract val filterCompleted: String
        abstract val filterPeriodTitle: String
        abstract val filterPeriodAll: String
        abstract val filterPeriodDay: String
        abstract val filterPeriodWeek: String
        abstract val filterPriorityTitle: String
        abstract val filterStatusTitle: String
        abstract val sortByTitle: String
        abstract val sortByNone: String
        abstract val sortByDate: String
        abstract val sortByPriority: String
        abstract val sortByStatus: String

        // Tasks and Priority Labels
        abstract val priorityHigh: String
        abstract val priorityMedium: String
        abstract val priorityLow: String
        abstract val noTasksMessage: String
        abstract val noTasksSubMessage: String
        abstract val taskDueText: String
        abstract val subtasksTitle: String
        abstract val subtaskPlaceholder: String
        abstract val addSubtaskButton: String
        abstract val completePercent: String

        // Create Task Dialog
        abstract val createNewTaskTitle: String
        abstract val editTaskTitle: String
        abstract val fieldTitle: String
        abstract val fieldTitlePlaceholder: String
        abstract val fieldDescription: String
        abstract val fieldDescriptionPlaceholder: String
        abstract val fieldDueDate: String
        abstract val fieldDueTime: String
        abstract val fieldTags: String
        abstract val fieldTagsPlaceholder: String
        abstract val buttonCancel: String
        abstract val buttonSave: String
        abstract val fieldPriority: String

        // Settings View
        abstract val settingsLanguage: String
        abstract val settingsCalendarMode: String
        abstract val settingsTheme: String
        abstract val settingsThemeSystem: String
        abstract val settingsThemeLight: String
        abstract val settingsThemeDark: String
        abstract val settingsAlertOffsetTitle: String
        abstract val settingsAlertOffsetDesc: String
        abstract val settingsAlertInstant: String
        abstract val settingsAlert5m: String
        abstract val settingsAlert15m: String
        abstract val settingsAlert1h: String
        abstract val settingsTestNotification: String
        abstract val settingsTestNotificationBtn: String
        abstract val settingsEngineStatus: String
        abstract val settingsEngineType: String
        abstract val settingsEngineDesc: String

        // Detail Alert Notifications
        abstract val notificationTitle: String
        abstract val notificationBodyPrefix: String
        abstract val notificationPriorityText: String
        abstract val notificationStatusText: String
        abstract val notificationDueText: String

        // Help Page Content
        abstract val helpTitle: String
        abstract val helpSubtitle: String
        abstract val helpGetStartedTitle: String
        abstract val helpGetStartedDesc: String
        abstract val helpCalendarTitle: String
        abstract val helpCalendarDesc: String
        abstract val helpNotificationsTitle: String
        abstract val helpNotificationsDesc: String
        abstract val helpOfflineTitle: String
        abstract val helpOfflineDesc: String
        abstract val helpLocalizationTitle: String
        abstract val helpLocalizationDesc: String

        // Custom Settings Properties
        abstract val settingsPanelTitle: String
        abstract val settingsPanelSubtitle: String
        abstract val settingsLangCalendarCell: String
        abstract val settingsCalendarModeLabel: String
        abstract val calendarMiladiLabel: String
        abstract val calendarKhorshidiLabel: String
        abstract val settingsLookFeelSec: String
        abstract val settingsNotificationSubtitle: String
        abstract val settingsSecureLocalTitle: String
        abstract val settingsSecureLocalDesc: String
        abstract val buttonTestNotify: String
        abstract val settingsSecureLocalLogs: String
        abstract val settingsSecureLocalInfo: String
        abstract val taskSectionDeadlines: String
        abstract val noTasksFound: String

        object English : AppStrings() {
            override val appLogo = "BitTask"
            override val appSubTitle = "Improved Workspace Productivity"
            override val tabTasks = "Tasks"
            override val tabCalendar = "Calendar"
            override val tabSettings = "Settings"
            override val tabHelp = "Help Guide"

            override val searchPlaceholder = "Search your tasks..."
            override val advancedFiltersTitle = "Advanced Filters & Sort Options"
            override val filterAll = "All"
            override val filterActive = "Active"
            override val filterCompleted = "Completed"
            override val filterPeriodTitle = "Time Interval"
            override val filterPeriodAll = "All Dates"
            override val filterPeriodDay = "Today"
            override val filterPeriodWeek = "This Week"
            override val filterPriorityTitle = "Filter Priority"
            override val filterStatusTitle = "Filter Status"
            override val sortByTitle = "Sort By"
            override val sortByNone = "None"
            override val sortByDate = "Due Date"
            override val sortByPriority = "Priority"
            override val sortByStatus = "Status"

            override val priorityHigh = "High"
            override val priorityMedium = "Medium"
            override val priorityLow = "Low"
            override val noTasksMessage = "No tasks found!"
            override val noTasksSubMessage = "Tap the '+' floating action button below to create your very first BitTask."
            override val taskDueText = "Due Date:"
            override val subtasksTitle = "Sub-Tasks Checklist"
            override val subtaskPlaceholder = "Add a quick subtask step..."
            override val addSubtaskButton = "Add"
            override val completePercent = "completed"

            override val createNewTaskTitle = "Create New Task"
            override val editTaskTitle = "Edit BitTask"
            override val fieldTitle = "Task Title"
            override val fieldTitlePlaceholder = "E.g. Code database schema and entities"
            override val fieldDescription = "Task Description"
            override val fieldDescriptionPlaceholder = "Describe project deliverables and deadlines..."
            override val fieldDueDate = "Due Date"
            override val fieldDueTime = "Due Time"
            override val fieldTags = "Tags (Comma-separated)"
            override val fieldTagsPlaceholder = "E.g. architecture, db, security"
            override val buttonCancel = "Cancel"
            override val buttonSave = "Save Task"
            override val fieldPriority = "Task Urgency"

            override val settingsLanguage = "Language Setting"
            override val settingsCalendarMode = "Calendar System"
            override val settingsTheme = "App Visual theme"
            override val settingsThemeSystem = "System Settings"
            override val settingsThemeLight = "Light Theme"
            override val settingsThemeDark = "Dark Theme"
            override val settingsAlertOffsetTitle = "Alarm Reminders Trigger"
            override val settingsAlertOffsetDesc = "How long before the task due time should the exact alarm notify you?"
            override val settingsAlertInstant = "At due time"
            override val settingsAlert5m = "5 minutes before"
            override val settingsAlert15m = "15 minutes before"
            override val settingsAlert1h = "1 hour before"
            override val settingsTestNotification = "Bilingual Notification Diagnostic Tool"
            override val settingsTestNotificationBtn = "Send Test Push Notification"
            override val settingsEngineStatus = "BitTask Client Status"
            override val settingsEngineType = "Local Sandbox Encrypted Storage"
            override val settingsEngineDesc = "All operations are sandboxed locally. No servers are contacted, ensuring complete privacy."

            override val notificationTitle = "BitTask Alert: Time to Execute"
            override val notificationBodyPrefix = "Your task is due now: "
            override val notificationPriorityText = "Priority Level: "
            override val notificationStatusText = "Current Status: Active"
            override val notificationDueText = "Target Due: "

            override val helpTitle = "BitTask 0 to 100 Guide"
            override val helpSubtitle = "Master offline task management, custom alerts, and bilingual calendars effortlessly."
            override val helpGetStartedTitle = "1. Creating & Editing Tasks"
            override val helpGetStartedDesc = "Tap the '+' button on the Tasks or Calendar tabs. Enter titles, detailed notes, specific tags (comma-separated), and pick an urgency priority rating (High, Medium, or Low). You can tap any card to edit it anytime or add instant micro subtask steps on the fly."
            override val helpCalendarTitle = "2. Gregorian & Lunar (Solar/Jalali) Calendars"
            override val helpCalendarDesc = "Toggle between Gregorian and Solar Jalali calendars inside the Settings. If Persian language is selected, the Calendar system automatically defaults to Jalali. The Calendar tab displays your days with interactive task summaries."
            override val helpNotificationsTitle = "3. Detailed Notifications Alerts"
            override val helpNotificationsDesc = "BitTask registers precise system alarms even when your screen is locked. In the Settings view, you can set the exact minute offset to get notified early. To test alerts instantly, click the diagnostic notification button."
            override val helpOfflineTitle = "4. Zero Internet Mode (100% Offline)"
            override val helpOfflineDesc = "BitTask does not sync, connect, or send bytes over the internet. Everything is persistent, sandboxed, and kept highly secure on your local device."
            override val helpLocalizationTitle = "5. Beautiful Persian RTL Layouts"
            override val helpLocalizationDesc = "When selecting 'فارسی' (Persian), the layout dynamically mirrors right-to-left (RTL). All digits are parsed into eastern Arabic formats automatically for elegant readability on both compact and expanded layouts."

            // Custom Settings Properties
            override val settingsPanelTitle = "Application Control Panel"
            override val settingsPanelSubtitle = "Configure user preferences, system variables, and diagnostics."
            override val settingsLangCalendarCell = "Language Settings"
            override val settingsCalendarModeLabel = "Calendar Engine System"
            override val calendarMiladiLabel = "Gregorian (Miladi)"
            override val calendarKhorshidiLabel = "Solar (Jalali)"
            override val settingsLookFeelSec = "App Color Theme Appearance"
            override val settingsNotificationSubtitle = "Set how early you should be notified before the deadline."
            override val settingsSecureLocalTitle = "Secure Local Database Ops"
            override val settingsSecureLocalDesc = "Manage secure database encryption and perform device storage backups locally."
            override val buttonTestNotify = "Direct Notification Diagnostic"
            override val settingsSecureLocalLogs = "Standalone Engine Terminal Monitor"
            override val settingsSecureLocalInfo = "Kernel Diagnostic & Standalone Diagnostics"
            override val taskSectionDeadlines = "Deadlines for"
            override val noTasksFound = "No scheduled tasks found for this day."
        }

        object Persian : AppStrings() {
            override val appLogo = "BitTask"
            override val appSubTitle = "بهبود بهره‌وری فضای کاری"
            override val tabTasks = "وظایف"
            override val tabCalendar = "تقویم"
            override val tabSettings = "تنظیمات"
            override val tabHelp = "راهنمای برنامه"

            override val searchPlaceholder = "جستجوی وظایف شما..."
            override val advancedFiltersTitle = "فیلترهای پیشرفته و گزینه‌های مرتب‌سازی"
            override val filterAll = "همه"
            override val filterActive = "فعال"
            override val filterCompleted = "کامل شده"
            override val filterPeriodTitle = "بازه زمانی"
            override val filterPeriodAll = "همه زمان‌ها"
            override val filterPeriodDay = "امروز"
            override val filterPeriodWeek = "این هفته"
            override val filterPriorityTitle = "فیلتر اولویت"
            override val filterStatusTitle = "فیلتر وضعیت"
            override val sortByTitle = "مرتب‌سازی بر اساس"
            override val sortByNone = "هیچ‌کدام"
            override val sortByDate = "تاریخ سررسید"
            override val sortByPriority = "اولویت"
            override val sortByStatus = "وضعیت"

            override val priorityHigh = "برتر / بالا"
            override val priorityMedium = "متوسط"
            override val priorityLow = "پایین / فرعی"
            override val noTasksMessage = "هیچ وظیفه‌ای پیدا نشد!"
            override val noTasksSubMessage = "دکمه شناور '+' زیر را لمس کنید تا اولین وظیفه BitTask خود را ثبت کنید."
            override val taskDueText = "تاریخ سررسید:"
            override val subtasksTitle = "چک‌لیست قدم‌های زیرمجموعه"
            override val subtaskPlaceholder = "افزودن مرحله بعدی برای این کار..."
            override val addSubtaskButton = "افزودن"
            override val completePercent = "تکمیل شده"

            override val createNewTaskTitle = "ایجاد وظیفه جدید"
            override val editTaskTitle = "ویرایش وظیفه BitTask"
            override val fieldTitle = "عنوان وظیفه"
            override val fieldTitlePlaceholder = "مثال: کدنویسی پایگاه داده و نهادهای Room"
            override val fieldDescription = "توضیحات وظیفه"
            override val fieldDescriptionPlaceholder = "جزئیات پروژه و تعهدات زمان‌بندی را بنویسید..."
            override val fieldDueDate = "تاریخ سررسید"
            override val fieldDueTime = "ساعت دقیق"
            override val fieldTags = "برچسب‌ها (جدا شده با ویرگول)"
            override val fieldTagsPlaceholder = "مثال: طرح، فنی، امنیت"
            override val buttonCancel = "انصراف"
            override val buttonSave = "ذخیره وظیفه"
            override val fieldPriority = "ضرورت اجرای کار"

            override val settingsLanguage = "تنظیمات زبان برنامه"
            override val settingsCalendarMode = "سیستم تقویم مورد استفاده"
            override val settingsTheme = "پوسته ظاهری برنامه"
            override val settingsThemeSystem = "پیش‌فرض سامانه"
            override val settingsThemeLight = "پوسته روشن"
            override val settingsThemeDark = "پوسته تاریک"
            override val settingsAlertOffsetTitle = "شدت و زمان هشدارهای اعلان"
            override val settingsAlertOffsetDesc = "چند دقیقه پیش از زمان اصلی، مایلید سیستم برای این کار هشدار دقیقی ارسال کند؟"
            override val settingsAlertInstant = "دقیقاً در زمان سررسید"
            override val settingsAlert5m = "۵ دقیقه قبل از سررسید"
            override val settingsAlert15m = "۱۵ دقیقه قبل از سررسید"
            override val settingsAlert1h = "۱ ساعت قبل از سررسید"
            override val settingsTestNotification = "تست و عیب‌یابی چندرسانه‌ای اعلان‌ها"
            override val settingsTestNotificationBtn = "ارسال اعلان دوزبانه آزمایشی"
            override val settingsEngineStatus = "وضعیت کلاینت BitTask"
            override val settingsEngineType = "ذخیره‌سازی رمزنگاری‌شده فوق امن و آفلاین"
            override val settingsEngineDesc = "تمامی عملیات به صوت محلی در دستگاه شما پردازش می‌شوند. هیچ اینترنتی مصرف نمی‌شود."

            override val notificationTitle = "یادآور وظیفه BitTask: زمان اجرا فرا رسید"
            override val notificationBodyPrefix = "زمان شروع وظیفه: "
            override val notificationPriorityText = "شاخص ضرورت: "
            override val notificationStatusText = "وضعیت برنامه: فعال"
            override val notificationDueText = "تاریخ برنامه‌ریزی: "

            override val helpTitle = "راهنمای صفر تا صد برنامه BitTask"
            override val helpSubtitle = "کنترل وظایف آفلاین، هشدارهای شخصی‌سازی‌شده و تقویم دوزبانه را بدون زحمت به دست بگیرید."
            override val helpGetStartedTitle = "١. ایجاد و ویرایش وظایف"
            override val helpGetStartedDesc = "در تب وظایف یا تقویم، دکمه '+' را لمس کنید. عنوان، یادداشت تفصیلی، برچسب‌ها (با ویرگول جدا کنید) و اولویت ضرورت (بالا، متوسط یا پایین) را مشخص نمایید. جهت ویرایش یا افزودن ریزمراحل در هر زمان، می‌توانید روی کارت مربوطه بزنید."
            override val helpCalendarTitle = "٢. تغییر سیستم‌های تقویم"
            override val helpCalendarDesc = "از بخش تنظیمات، فرآیند تعویض سیستم تقویم بین میلادی و شمسی را مدیریت نمایید. با انتخاب زبان فارسی، سیستم تقویم به شکل خودکار روی شمسی (جلالی) تنظیم می‌گردد تا تاریخ‌ها منظم باشند."
            override val helpNotificationsTitle = "٣. هشدارهای سیستماتیک دقیق"
            override val helpNotificationsDesc = "سامانه هشدار BitTask کار خود را با دقت ۱۰۰٪ در پس‌زمینه انجام می‌دهد. در تب تنظیمات می‌توانید مابه التفاوت زمانی را برای جلو انداختن هشدارها برگزینید. همچنین دکمه تست جهت ارزیابی در دسترس است."
            override val helpOfflineTitle = "۴. امنیت مطلق و آفلاین بودن صد در صد"
            override val helpOfflineDesc = "برنامه BitTask با تضمین عملکرد بدون اتصال به اینترنت طراحی گردیده است. داده‌های شما صرفاً بر بستر حافظه محلی دستگاه پردازش می‌شوند و کاملاً ایمن خواهند بود."
            override val helpLocalizationTitle = "۵. ویژگی راست‌چین خودکار فارسی"
            override val helpLocalizationDesc = "با انتخاب زبان شیرین فارسی، تمام عناصر ظاهری اعم از جهت متن‌ها، برچسب‌ها، منوها و فیلدها راست‌چین شده و ارقام به شکل بومی فارسی نشان داده می‌شوند تا تجربه‌ای فوق‌العاده خلق شود."

            // Custom Settings Properties
            override val settingsPanelTitle = "پنل کنترل و تنظیمات سیستم"
            override val settingsPanelSubtitle = "پیکربندی هویت کاربر، متغیرهای عمومی دستگاه و ابزارهای عیب‌یابی"
            override val settingsLangCalendarCell = "تنظیمات زبان برنامه"
            override val settingsCalendarModeLabel = "سیستم موتور محاسبات تقویم"
            override val calendarMiladiLabel = "میلادی (فرنگی)"
            override val calendarKhorshidiLabel = "خورشیدی (جلالی)"
            override val settingsLookFeelSec = "پوسته ظاهری و رنگ‌بندی کلاینت"
            override val settingsNotificationSubtitle = "تعیین کنید چند دقیقه پیش از فرارسیدن موعد مایل به دریافت هشدار هستید"
            override val settingsSecureLocalTitle = "عملیات امن پایگاه داده محلی"
            override val settingsSecureLocalDesc = "مدیریت لایه‌های امنیتی پایگاه داده رمزنگاری‌شده محلی و فایل پشتیبان حافظه"
            override val buttonTestNotify = "شبیه‌ساز آنی اعلان سیستم"
            override val settingsSecureLocalLogs = "مانیتور ترمینال هسته خودگردان برنامه"
            override val settingsSecureLocalInfo = "مشخصات فنی دستگاه و سیستم کلاینت"
            override val taskSectionDeadlines = "لیست وظایف برای"
            override val noTasksFound = "هیچ وظیفه‌ای برای این روز ثبت نشده است."
        }
    }

    fun getStrings(langCode: String): AppStrings {
        return if (langCode == "fa") AppStrings.Persian else AppStrings.English
    }
}
