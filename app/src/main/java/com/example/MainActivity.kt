package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateDarkBg
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ActivePink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PortfolioScreen()
            }
        }
    }
}

// Data structures for interactive elements
data class CoreValue(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

data class ExpertiseItem(
    val title: String,
    val services: List<String>,
    val icon: ImageVector,
    val accentColor: Color
)

data class ProjectItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val client: String,
    val highlightMetric: String,
    val overview: String,
    val solutions: List<String>,
    val results: List<String>,
    val accentColor: Color
)

data class TimelineItem(
    val year: String,
    val title: String,
    val institution: String,
    val description: String,
    val icon: ImageVector
)

data class ContactSubmission(
    val id: Long,
    val name: String,
    val email: String,
    val projectType: String,
    val message: String,
    val timestamp: String
)

data class Testimonial(
    val id: Int,
    val name: String,
    val title: String,
    val company: String,
    val quote: String,
    val rating: Int = 5,
    val accentColor: Color,
    val category: String,
    val initials: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen() {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Contact Submissions local state
    val submissions = remember { mutableStateListOf<ContactSubmission>() }

    // Dialog & Detail states
    var showResumeDialog by remember { mutableStateOf(false) }
    var expandedValueId by remember { mutableStateOf<Int?>(null) }
    var expandedProjectId by remember { mutableStateOf<Int?>(null) }

    // Derived State for scroll synchronization with bottom tabs
    val activeIndex by remember {
        derivedStateOf {
            val firstIdx = lazyListState.firstVisibleItemIndex
            firstIdx.coerceAtMost(6)
        }
    }

    val tabs = listOf(
        "Hero" to 0,
        "The Edge" to 1,
        "Expertise" to 2,
        "Projects" to 3,
        "Testimonials" to 4,
        "Timeline" to 5,
        "Contact" to 6
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SlateDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ElectricTeal, NeonPurple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RYY",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "Rosemond Yaa Yeboah",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Digital Marketer & MBA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = ElectricTeal
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(6) // Scroll to contact
                            }
                        },
                        modifier = Modifier.testTag("nav_contact_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AlternateEmail,
                            contentDescription = "Contact Rosemond",
                            tint = ElectricTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SlateDarkBg.copy(alpha = 0.95f),
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp), // Padding to avoid overlap with bottom tabs bar
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Section 1: Hero Section
                item {
                    HeroSection(
                        onViewWorkClick = {
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(3) // Projects is Index 3
                            }
                        },
                        onDownloadResumeClick = { showResumeDialog = true }
                    )
                }

                // Section 2: About / The Edge Section
                item {
                    AboutSection(
                        expandedValueId = expandedValueId,
                        onValueClick = { id ->
                            expandedValueId = if (expandedValueId == id) null else id
                        }
                    )
                }

                // Section 3: My Expertise
                item {
                    ExpertiseSection()
                }

                // Section 4: Featured Projects
                item {
                    FeaturedProjectsSection(
                        expandedProjectId = expandedProjectId,
                        onProjectToggle = { id ->
                            expandedProjectId = if (expandedProjectId == id) null else id
                        }
                    )
                }

                // Section 5: Professional Testimonials Carousel
                item {
                    TestimonialsSection()
                }

                // Section 6: Education & Certifications Timeline
                item {
                    TimelineSection()
                }

                // Section 7: Contact Section
                item {
                    ContactSection(
                        submissions = submissions,
                        onSubmit = { submission ->
                            submissions.add(0, submission)
                            Toast.makeText(
                                context,
                                "Inquiry submitted! Rosemond will contact you soon.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }

                // Section 8: Footer
                item {
                    FooterSection()
                }
            }

            // Bottom Floating Tab bar for fluid single-page UX
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(64.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = SlateDarkBg.copy(alpha = 0.92f),
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    tabs.forEach { (title, index) ->
                        val isActive = activeIndex == index
                        val tabBg by animateColorAsState(
                            targetValue = if (isActive) SlateSurfaceVariant else Color.Transparent,
                            label = "TabBgState"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isActive) ElectricTeal else TextSecondary,
                            label = "TabTextColorState"
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(tabBg)
                                .clickable {
                                    coroutineScope.launch {
                                        lazyListState.animateScrollToItem(index)
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = textColor,
                                fontSize = 12.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Resume Dialog
            if (showResumeDialog) {
                ResumeSummaryDialog(
                    onDismiss = { showResumeDialog = false },
                    dialogContext = context
                )
            }
        }
    }
}

// --- SUBSECTION COMPOSABLES ---

@Composable
fun HeroSection(
    onViewWorkClick: () -> Unit,
    onDownloadResumeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent),
                    radius = 800f
                )
            )
            .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Award Badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(ElectricTeal.copy(alpha = 0.1f))
                .border(1.dp, ElectricTeal.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "Award Icon",
                tint = ElectricTeal,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "🏆 Best Graduating Student in Marketing, UPSA (2025)",
                color = ElectricTeal,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Photo with Sophisticated Accent Border
        Box(
            modifier = Modifier
                .size(145.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(ElectricTeal, NeonPurple)
                    )
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Rosemond Yaa Yeboah Portrait",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Name and Titles
        Text(
            text = "Rosemond Yaa Yeboah",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Data-Driven Marketing. Audience-Focused Results.",
            style = MaterialTheme.typography.titleMedium,
            color = ElectricTeal,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sub-headline Bio
        Text(
            text = "An MBA Marketing graduate and Digital Marketer blending strategic consumer insights with hands-on execution in SEO, content strategy, and performance analytics.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CTA buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onViewWorkClick,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricTeal),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("hero_view_work_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "View My Work",
                    color = SlateDarkBg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            OutlinedButton(
                onClick = onDownloadResumeClick,
                border = BorderStroke(1.dp, NeonPurple),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("hero_download_resume_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPurple)
            ) {
                Text(
                    text = "Download Resume",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun AboutSection(
    expandedValueId: Int?,
    onValueClick: (Int) -> Unit
) {
    val coreValues = listOf(
        CoreValue(
            title = "Analytical Backbone",
            subtitle = "From Healthcare & Finance Reporting",
            description = "My professional experience in structured healthcare operations and financial audits equips me to interpret datasets, audit SEO performances rigorously, and translate complex telemetry logs into actionable market forecasts with extreme precision.",
            icon = Icons.Default.TrendingUp,
            color = ElectricTeal
        ),
        CoreValue(
            title = "Strategic Mindset",
            subtitle = "MBA in Marketing",
            description = "Acquiring my Master of Business Administration in Marketing at UPSA gave me advanced competence in strategic business planning, consumer psychology modeling, budget allocation efficiency, and value-proposition framing.",
            icon = Icons.Default.WorkspacePremium,
            color = NeonPurple
        ),
        CoreValue(
            title = "Content Storytelling",
            subtitle = "Crafting Resonant Narratives",
            description = "Applying years of storytelling techniques learned in educational settings, I design high-impact content blueprints, social campaign scripts, and visual graphic guidelines that forge authentic trust with target demographics.",
            icon = Icons.Default.Star,
            color = ActivePink
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SlateSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "THE EDGE",
            color = ElectricTeal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "My Unique Interdisciplinary Advantage",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Previous professional roles across education and healthcare operations have given me an unconventional advantage. I don't just push pixels; I deliver bulletproof communications, precise target-audience analysis, strict data standards, and unwavering client trust.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Value Cards Stack
        coreValues.forEachIndexed { index, value ->
            val isExpanded = expandedValueId == index
            val cardBg by animateColorAsState(
                targetValue = if (isExpanded) SlateSurfaceVariant else SlateSurface,
                label = "ValueCardBg"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .border(
                        1.dp,
                        if (isExpanded) value.color.copy(alpha = 0.5f) else SlateBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onValueClick(index) }
                    .testTag("value_card_$index"),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(value.color.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = value.icon,
                                contentDescription = null,
                                tint = value.color,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = value.title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = value.subtitle,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand details",
                            tint = TextMuted
                        )
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = value.description,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpertiseSection() {
    val expertise = listOf(
        ExpertiseItem(
            title = "Strategy & SEO",
            services = listOf(
                "Search Engine Optimization (SEO)",
                "Content Marketing Blueprints",
                "Strategic Market Research",
                "Brand Positioning Studies"
            ),
            icon = Icons.Default.AdsClick,
            accentColor = ElectricTeal
        ),
        ExpertiseItem(
            title = "Content & Design",
            services = listOf(
                "Social Media Strategy & Calendars",
                "Canva Graphic Design Production",
                "Video Editing (CapCut, Google Vids)",
                "Email Copywriting & Automation"
            ),
            icon = Icons.Default.Star,
            accentColor = NeonPurple
        ),
        ExpertiseItem(
            title = "Data & Analytics",
            services = listOf(
                "Google Analytics Dashboard Auditing",
                "Meta Ads Insights Synthesis",
                "In-Depth KPI Report Compilation",
                "Conversion Rate Optimization (CRO)"
            ),
            icon = Icons.Default.AutoGraph,
            accentColor = ActivePink
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "EXPERTISE",
            color = ElectricTeal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Areas of Strategic Execution",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Columns Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            expertise.forEachIndexed { _, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateSurface)
                        .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(item.accentColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        item.services.forEach { service ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Included service",
                                    tint = item.accentColor,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Text(
                                    text = service,
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedProjectsSection(
    expandedProjectId: Int?,
    onProjectToggle: (Int) -> Unit
) {
    val projects = listOf(
        ProjectItem(
            id = 1,
            title = "Strategic Business Overhaul",
            subtitle = "Market Research & Business Remodeling",
            client = "Inter5 IT Solutions (Lagos, Nigeria)",
            highlightMetric = "Market Repositioned",
            overview = "Conducted end-to-end strategic market restructuring and competitive analysis to pivot an established IT systems vendor into premium Enterprise cloud consultation.",
            solutions = listOf(
                "In-depth local B2B competitive intelligence research in Nigeria.",
                "Formulated premium cloud services packaging with modular SLA frameworks.",
                "Produced executive sales deck & content pitch matrices."
            ),
            results = listOf(
                "Pivot completed within 45 days.",
                "Unlocked 3 enterprise consultancies within Q1.",
                "Repositioned the firm's brand image as high-authority cloud integrators."
            ),
            accentColor = ElectricTeal
        ),
        ProjectItem(
            id = 2,
            title = "3-Month Digital Marketing Strategy",
            subtitle = "Conversion-Optimized Integrated Campaign",
            client = "HMD Events (Simulation Blueprint)",
            highlightMetric = "Target: 20 Bookings",
            overview = "Created a meticulously detailed end-to-end digital audit, buyer persona profiles, and ad funnel layouts designed to achieve twenty high-ticket bookings.",
            solutions = listOf(
                "Constructed high-affinity buyer personas and social content triggers.",
                "Designed a 3-tier Meta Ads traffic funnel with customized retargeting pools.",
                "Engineered Local SEO event planning hooks and strategic landing page layouts."
            ),
            results = listOf(
                "Calculated direct ROAS prediction of 3.8x with local cost metrics.",
                "Established unified tracking KPIs for multi-channel attribution.",
                "Ready-for-deployment assets across SEO, Social, and Meta campaigns."
            ),
            accentColor = NeonPurple
        ),
        ProjectItem(
            id = 3,
            title = "Integrated Fashion Launch Strategy",
            subtitle = "Multi-Channel Premium Luxury Campaign",
            client = "Christopher John Rogers (Simulation Blueprint)",
            highlightMetric = "+140% Lead Rate Goal",
            overview = "Engineered a sophisticated 8-week launch framework incorporating granular budget weights, strict SMART analytics, and high-conversion retargeting loops.",
            solutions = listOf(
                "Laid out comprehensive budget allocations for optimal channel weighting.",
                "Integrated custom-tailored luxury SEO keyword clustering.",
                "Mapped a high-retention automated email nurture flow with segmented offers."
            ),
            results = listOf(
                "8-Week execution timeline organized with strict stakeholder milestones.",
                "Maximized content syndication leverage across premium channels.",
                "KPI infrastructure designed for precise ROAS and engagement attribution."
            ),
            accentColor = ActivePink
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "FEATURED WORK",
            color = ElectricTeal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Case Studies & Strategic Outlines",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        projects.forEach { project ->
            val isExpanded = expandedProjectId == project.id
            val borderBrush = Brush.linearGradient(
                colors = listOf(project.accentColor, SlateBorder)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        1.dp,
                        if (isExpanded) borderBrush else Brush.linearGradient(listOf(SlateBorder, SlateBorder)),
                        RoundedCornerShape(18.dp)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onProjectToggle(project.id) }
                    .testTag("project_card_${project.id}"),
                colors = CardDefaults.cardColors(containerColor = SlateSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = project.client,
                                color = project.accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = project.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = project.subtitle,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Badge showing highlight KPI Metric
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(project.accentColor.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = project.highlightMetric,
                                color = project.accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = project.overview,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Expand / Collapse Affordance
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "Show Less" else "Expand Case Study",
                            color = project.accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = project.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Divider(color = SlateBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Execution Steps
                        Text(
                            text = "STRATEGIC SOLUTIONS",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        project.solutions.forEach { sol ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "⚡",
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = sol,
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Quantitative Impact
                        Text(
                            text = "PROJECTED / MEASURED KPI IMPACT",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        project.results.forEach { res ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = project.accentColor,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .padding(top = 3.dp)
                                )
                                Text(
                                    text = res,
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestimonialsSection() {
    val testimonials = remember {
        listOf(
            Testimonial(
                id = 1,
                name = "Sarah Mensah",
                title = "Director of Growth",
                company = "Apex Retail Group",
                quote = "Rosemond completely overhauled our digital strategy. Her data-driven approach and focus on ROI resulted in a 45% increase in conversion rate within three months. An absolute strategic power player!",
                rating = 5,
                accentColor = ElectricTeal,
                category = "Client",
                initials = "SM"
            ),
            Testimonial(
                id = 2,
                name = "Prof. David Vance",
                title = "MBA Program Director",
                company = "UPSA Business School",
                quote = "Rosemond is an exceptional analytical thinker. Her ability to synthesize complex marketing methodologies with financial models set her apart as one of the top students in our MBA cohort. Her drive is remarkable.",
                rating = 5,
                accentColor = NeonPurple,
                category = "Academic",
                initials = "DV"
            ),
            Testimonial(
                id = 3,
                name = "Marcus Thorne",
                title = "CEO",
                company = "Elevate Consulting",
                quote = "Working with Rosemond on our brand launch was a game changer. Her knowledge of audience targeting, combined with her structured communication style, made the collaboration seamless and highly profitable.",
                rating = 5,
                accentColor = ActivePink,
                category = "Colleague",
                initials = "MT"
            ),
            Testimonial(
                id = 4,
                name = "Elena Rostova",
                title = "Senior Marketing Manager",
                company = "Global Tech Inc.",
                quote = "She doesn't just manage campaigns—she understands the entire business model. Rosemond's capability to bridge the gap between creative execution and business results makes her an invaluable asset.",
                rating = 5,
                accentColor = ElectricTeal,
                category = "Client",
                initials = "ER"
            )
        )
    }

    var currentIndex by remember { mutableStateOf(0) }
    val currentTestimonial = testimonials[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SlateSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .animateContentSize()
    ) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonPurple.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = "SOCIAL PROOF & TRUST",
                    color = NeonPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "What People Say",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Read professional reviews and testimonials from academic directors, corporate clients, and close industry colleagues who have collaborated with me.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Main Testimonial Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SlateSurfaceVariant.copy(alpha = 0.4f))
                .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // Elegant large quote watermark behind the text
            Text(
                text = "“",
                fontSize = 120.sp,
                fontWeight = FontWeight.Black,
                color = currentTestimonial.accentColor.copy(alpha = 0.1f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-8).dp, y = (-50).dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stars & Category Chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star Rating",
                                tint = NeonPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(currentTestimonial.accentColor.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = currentTestimonial.category,
                            color = currentTestimonial.accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Quote text
                Text(
                    text = currentTestimonial.quote,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Divider(color = SlateBorder.copy(alpha = 0.6f))

                // Author Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile Initials Circle
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(currentTestimonial.accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentTestimonial.initials,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Name and Title
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = currentTestimonial.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${currentTestimonial.title} @ ${currentTestimonial.company}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation controls & dots indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            IconButton(
                onClick = {
                    currentIndex = if (currentIndex > 0) currentIndex - 1 else testimonials.size - 1
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SlateSurfaceVariant)
                    .border(1.dp, SlateBorder, CircleShape)
                    .testTag("testimonial_prev_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Testimonial",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                testimonials.forEachIndexed { idx, _ ->
                    val isSelected = idx == currentIndex
                    val dotWidth by animateFloatAsState(
                        targetValue = if (isSelected) 18f else 6f,
                        label = "DotWidthState"
                    )
                    val dotColor by animateColorAsState(
                        targetValue = if (isSelected) currentTestimonial.accentColor else SlateBorder,
                        label = "DotColorState"
                    )

                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(dotWidth.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(dotColor)
                    )
                }
            }

            // Next button
            IconButton(
                onClick = {
                    currentIndex = if (currentIndex < testimonials.size - 1) currentIndex + 1 else 0
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SlateSurfaceVariant)
                    .border(1.dp, SlateBorder, CircleShape)
                    .testTag("testimonial_next_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Testimonial",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Divider(color = SlateBorder)

        Spacer(modifier = Modifier.height(12.dp))

        // Tap-to-select quick avatar selector for maximum interactivity
        Text(
            text = "QUICK SELECT",
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            testimonials.forEachIndexed { idx, testimonial ->
                val isSelected = idx == currentIndex
                val borderAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    label = "BorderAlpha"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { currentIndex = idx }
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) testimonial.accentColor.copy(alpha = 0.15f)
                                else SlateSurfaceVariant
                            )
                            .border(
                                width = 2.dp,
                                color = testimonial.accentColor.copy(alpha = borderAlpha),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = testimonial.initials,
                            color = if (isSelected) testimonial.accentColor else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = testimonial.name.substringBefore(" "),
                        color = if (isSelected) testimonial.accentColor else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineSection() {
    val items = listOf(
        TimelineItem(
            year = "2026",
            title = "Generation Digital Marketing Program",
            institution = "Generation",
            description = "Intense industry-aligned program specializing in SEO, Google Analytics, social storytelling, and Meta attribution modeling.",
            icon = Icons.Default.School
        ),
        TimelineItem(
            year = "2025",
            title = "MBA in Marketing",
            institution = "University of Professional Studies, Accra (UPSA)",
            description = "Awarded Best Graduating Student in Marketing. Rigorous mastery of corporate consumer research, brand stewardship, and statistical marketing analytics.",
            icon = Icons.Default.WorkspacePremium
        ),
        TimelineItem(
            year = "2026",
            title = "Advanced AI Bootcamps",
            institution = "Afriment",
            description = "Deep dive specializations in 'AI for Creatives' and 'AI Workflow Automation' focusing on prompt execution, content scalability, and tooling architectures.",
            icon = Icons.Default.Code
        ),
        TimelineItem(
            year = "Specialization",
            title = "SEO Content Writing & IT Operations",
            institution = "Professional Experience Matrix",
            description = "Bridging strict administrative operations from healthcare and educational trust leadership into highly structured, optimized web copywriting and SEO frameworks.",
            icon = Icons.Default.CheckCircle
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "ACADEMIC & CERTIFICATIONS",
            color = ElectricTeal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Qualifications & Background Timeline",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Custom Canvas Timeline Drawer
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val isLast = index == items.size - 1
                            if (!isLast) {
                                drawLine(
                                    color = SlateBorder,
                                    start = Offset(size.width / 2, 24.dp.toPx()),
                                    end = Offset(size.width / 2, size.height),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(SlateSurfaceVariant)
                                .border(1.5.dp, ElectricTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = ElectricTeal,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 24.dp, start = 8.dp)
                    ) {
                        Text(
                            text = item.year,
                            color = ElectricTeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.institution,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Text(
                            text = item.description,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSection(
    submissions: List<ContactSubmission>,
    onSubmit: (ContactSubmission) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("Select Project Type") }
    var message by remember { mutableStateOf("") }
    var projectTypeExpanded by remember { mutableStateOf(false) }
    var inquiriesExpanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val projectOptions = listOf(
        "Full-Scale Digital Strategy",
        "SEO Audit & Repositioning",
        "Social Content & Graphic Layouts",
        "Performance Data Analytics",
        "General Consultation / Speaking"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SlateSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "CONTACT",
            color = ElectricTeal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Initiate a Strategic Partnership",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Looking to scale your brand's digital footprints, auditing metrics, or consulting strategic market restructures? Submit your inquiry blueprint below.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Input: Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name", color = TextSecondary) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("contact_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricTeal,
                unfocusedBorderColor = SlateBorder
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input: Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = TextSecondary) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("contact_email_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricTeal,
                unfocusedBorderColor = SlateBorder
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input: Project Type
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = projectType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Project Type", color = TextSecondary) },
                trailingIcon = {
                    IconButton(onClick = { projectTypeExpanded = true }) {
                        Icon(
                            imageVector = if (projectTypeExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Dropdown icon",
                            tint = ElectricTeal
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { projectTypeExpanded = true }
                    .testTag("contact_project_type_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = ElectricTeal,
                    unfocusedBorderColor = SlateBorder
                )
            )

            DropdownMenu(
                expanded = projectTypeExpanded,
                onDismissRequest = { projectTypeExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(SlateSurfaceVariant)
                    .border(1.dp, SlateBorder)
            ) {
                projectOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option, color = TextPrimary) },
                        onClick = {
                            projectType = option
                            projectTypeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input: Message
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Project Brief / Message", color = TextSecondary) },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("contact_message_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricTeal,
                unfocusedBorderColor = SlateBorder
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Send Button
        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank()) {
                    val newSubmission = ContactSubmission(
                        id = System.currentTimeMillis(),
                        name = name.trim(),
                        email = email.trim(),
                        projectType = projectType,
                        message = message.trim(),
                        timestamp = "Just Now"
                    )
                    onSubmit(newSubmission)
                    name = ""
                    email = ""
                    projectType = "Select Project Type"
                    message = ""
                    focusManager.clearFocus()
                } else {
                    Toast.makeText(context, "Please enter your Name and Email.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricTeal),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("contact_submit_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = SlateDarkBg,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Send Secure Message",
                color = SlateDarkBg,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Local Inquiries Viewer
        if (submissions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = SlateBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { inquiriesExpanded = !inquiriesExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Submitted Inquiries (${submissions.size})",
                    color = ElectricTeal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (inquiriesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = ElectricTeal,
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(
                visible = inquiriesExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    submissions.forEach { sub ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateSurfaceVariant)
                                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = sub.name,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = sub.timestamp,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = sub.email,
                                    color = ElectricTeal,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Type: ${sub.projectType}",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (sub.message.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = sub.message,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
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

@Composable
fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialPill(
                label = "LinkedIn",
                icon = Icons.Default.WorkspacePremium,
                color = ElectricTeal,
                onClick = {}
            )

            SocialPill(
                label = "GitHub",
                icon = Icons.Default.Code,
                color = NeonPurple,
                onClick = {}
            )

            SocialPill(
                label = "Email",
                icon = Icons.Default.AlternateEmail,
                color = ActivePink,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "© 2026 Rosemond Yaa Yeboah. All rights reserved.",
            color = TextMuted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SocialPill(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(SlateSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(50.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ResumeSummaryDialog(
    onDismiss: () -> Unit,
    dialogContext: Context
) {
    val resumeLink = "https://rosemondyeboahyaa.github.io/resume"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .border(1.dp, ElectricTeal.copy(alpha = 0.5f), RoundedCornerShape(28.dp)),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = ElectricTeal
                )
                Text(
                    text = "Rosemond's Resume",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "An MBA in Marketing graduate, awarded Best Graduating Student in Marketing. Deep digital competence in SEO strategy, audience target analysis, and performance dashboard auditing.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Divider(color = SlateBorder)

                Column {
                    Text(
                        text = "CORE EXPERTISE MATRIX",
                        color = ElectricTeal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("SEO Audit", "Meta Ads", "Analytics", "Strategy").forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateSurfaceVariant)
                                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = skill,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Divider(color = SlateBorder)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateSurfaceVariant)
                        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            val clipboard = dialogContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Rosemond Resume Link", resumeLink)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(dialogContext, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DIGITAL RESUME URL",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = resumeLink,
                                color = ElectricTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Copy resume URL",
                            tint = ElectricTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Close", color = SlateDarkBg, fontWeight = FontWeight.Bold)
            }
        }
    )
}
