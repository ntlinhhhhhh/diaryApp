package com.diary.moonpage.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Data class đại diện cho một Icon có màu đi kèm.
 */
data class MoonIcon(
    val vector: ImageVector,
    val color: Color
)

/**
 * Tập hợp các Icon được phân loại cho ứng dụng Moon Page với màu sắc phù hợp.
 */
object MoonIcons {

    // 1. Thời tiết (Weather)
    object Weather {
        val Sunny = MoonIcon(Icons.Rounded.WbSunny, Color(0xFFFFD700))
        val Cloudy = MoonIcon(Icons.Rounded.Cloud, Color(0xFF9E9E9E))
        val Rainy = MoonIcon(Icons.Rounded.Umbrella, Color(0xFF03A9F4))
        val Snowy = MoonIcon(Icons.Rounded.AcUnit, Color(0xFFB2EBF2))
        val Windy = MoonIcon(Icons.Rounded.Air, Color(0xFF009688))
        val Stormy = MoonIcon(Icons.Rounded.Thunderstorm, Color(0xFF673AB7))
        val Foggy = MoonIcon(Icons.Rounded.CloudQueue, Color(0xFFBDBDBD)) // Fix: Foggy not found, using CloudQueue
    }

    // 2. Mối quan hệ xã hội (Social / People)
    object Social {
        val Alone = MoonIcon(Icons.Rounded.Person, Color(0xFF2196F3))
        val Family = MoonIcon(Icons.Rounded.Groups, Color(0xFFE91E63))
        val Friends = MoonIcon(Icons.Rounded.Group, Color(0xFF4CAF50))
        val Partner = MoonIcon(Icons.Rounded.Favorite, Color(0xFFFF5252))
        val Colleagues = MoonIcon(Icons.Rounded.Badge, Color(0xFF3F51B5))
        val Acquaintance = MoonIcon(Icons.Rounded.PersonOutline, Color(0xFF607D8B))
        val Strangers = MoonIcon(Icons.Rounded.NoAccounts, Color(0xFF795548))
        val Pet = MoonIcon(Icons.Rounded.Pets, Color(0xFFFF9800))
    }

    // 3. Công việc và Học tập (Productivity / Work & Study)
    object Productivity {
        val Work = MoonIcon(Icons.Rounded.Work, Color(0xFF455A64))
        val Study = MoonIcon(Icons.Rounded.School, Color(0xFF303F9F))
        val Meeting = MoonIcon(Icons.Rounded.CalendarMonth, Color(0xFFFF5722))
        val Chores = MoonIcon(Icons.Rounded.CleaningServices, Color(0xFF388E3C))
        val Commute = MoonIcon(Icons.Rounded.DirectionsBus, Color(0xFFFBC02D))
        val BusinessTrip = MoonIcon(Icons.Rounded.Flight, Color(0xFF1976D2))
        val Exam = MoonIcon(Icons.Rounded.Assignment, Color(0xFFD32F2F))
    }

    // 4. Sở thích và Hoạt động (Hobbies / Activities)
    object Hobbies {
        val Exercise = MoonIcon(Icons.Rounded.FitnessCenter, Color(0xFFFF1744))
        val Reading = MoonIcon(Icons.Rounded.AutoStories, Color(0xFF8D6E63))
        val Gaming = MoonIcon(Icons.Rounded.SportsEsports, Color(0xFF9C27B0))
        val Movies = MoonIcon(Icons.Rounded.Movie, Color(0xFF212121))
        val Music = MoonIcon(Icons.Rounded.MusicNote, Color(0xFFF48FB1))
        val Drawing = MoonIcon(Icons.Rounded.Palette, Color(0xFF00BCD4))
        val Cooking = MoonIcon(Icons.Rounded.Restaurant, Color(0xFFFFAB40))
        val Shopping = MoonIcon(Icons.Rounded.ShoppingBag, Color(0xFFEC407A))
        val Traveling = MoonIcon(Icons.Rounded.TravelExplore, Color(0xFF00C853))
        val Photography = MoonIcon(Icons.Rounded.PhotoCamera, Color(0xFF757575))
        val Gardening = MoonIcon(Icons.Rounded.Grass, Color(0xFF43A047))
    }

    // 5. Sức khỏe và Thể chất (Health / Wellness)
    object Health {
        val GoodHealth = MoonIcon(Icons.Rounded.HealthAndSafety, Color(0xFF4CAF50))
        val Sick = MoonIcon(Icons.Rounded.Sick, Color(0xFFCDDC39))
        val Hospital = MoonIcon(Icons.Rounded.MedicalServices, Color(0xFFF44336))
        val Period = MoonIcon(Icons.Rounded.WaterDrop, Color(0xFFD32F2F))
        val Medicine = MoonIcon(Icons.Rounded.Medication, Color(0xFF03A9F4))
        val Meditation = MoonIcon(Icons.Rounded.SelfImprovement, Color(0xFFCE93D8))
        val Therapy = MoonIcon(Icons.Rounded.Psychology, Color(0xFF80CBC4))
        val Injury = MoonIcon(Icons.Rounded.PersonalInjury, Color(0xFFFF9800))
    }

    // 6. Thói quen ăn uống (Food / Meals)
    object Food {
        val HomeCooked = MoonIcon(Icons.Rounded.Home, Color(0xFFFFC107))
        val DineOut = MoonIcon(Icons.Rounded.Restaurant, Color(0xFFEF5350))
        val FastFood = MoonIcon(Icons.Rounded.Fastfood, Color(0xFFFF9800))
        val Healthy = MoonIcon(Icons.Rounded.Spa, Color(0xFF8BC34A))
        val Sweets = MoonIcon(Icons.Rounded.Cake, Color(0xFFF06292))
        val Coffee = MoonIcon(Icons.Rounded.Coffee, Color(0xFF6D4C41))
        val Alcohol = MoonIcon(Icons.Rounded.LocalBar, Color(0xFFBA68C8))
        val Overeating = MoonIcon(Icons.Rounded.SentimentDissatisfied, Color(0xFFFF7043))
    }

    // 7. Giấc ngủ (Sleep)
    object Sleep {
        val GoodSleep = MoonIcon(Icons.Rounded.Bedtime, Color(0xFF3F51B5))
        val BadSleep = MoonIcon(Icons.Rounded.BedtimeOff, Color(0xFF90A4AE))
        val Insomnia = MoonIcon(Icons.Rounded.BrightnessLow, Color(0xFF616161))
        val Overslept = MoonIcon(Icons.Rounded.AlarmOff, Color(0xFFFFCA28))
        val EarlyBird = MoonIcon(Icons.Rounded.WbTwilight, Color(0xFFFFEB3B))
        val Nightmares = MoonIcon(Icons.Rounded.MoodBad, Color(0xFF512DA8))
    }

    // 8. Cảm xúc chi tiết (Emotions / Feelings)
    object Emotions {
        val Happy = MoonIcon(Icons.Rounded.SentimentVerySatisfied, Color(0xFFFFEB3B))
        val Calm = MoonIcon(Icons.Rounded.SentimentSatisfied, Color(0xFF81D4FA))
        val Excited = MoonIcon(Icons.Rounded.Celebration, Color(0xFFFFD700))
        val Sad = MoonIcon(Icons.Rounded.SentimentVeryDissatisfied, Color(0xFF42A5F5))
        val Angry = MoonIcon(Icons.Rounded.Whatshot, Color(0xFFF44336))
        val Anxious = MoonIcon(Icons.Rounded.SentimentNeutral, Color(0xFFBDBDBD))
        val Tired = MoonIcon(Icons.Rounded.Face, Color(0xFFA1887F))
        val Stressed = MoonIcon(Icons.Rounded.PriorityHigh, Color(0xFFFF5722))
        val Bored = MoonIcon(Icons.Rounded.SentimentNeutral, Color(0xFFB0BEC5))
        val Confused = MoonIcon(Icons.Rounded.QuestionMark, Color(0xFF4DB6AC))
    }
    
    fun getAllIcons(): List<MoonIcon> {
        return listOf(
            Weather.Sunny, Weather.Cloudy, Weather.Rainy, Weather.Snowy, Weather.Windy, Weather.Stormy, Weather.Foggy,
            Social.Alone, Social.Family, Social.Friends, Social.Partner, Social.Colleagues, Social.Acquaintance, Social.Strangers, Social.Pet,
            Productivity.Work, Productivity.Study, Productivity.Meeting, Productivity.Chores, Productivity.Commute, Productivity.BusinessTrip, Productivity.Exam,
            Hobbies.Exercise, Hobbies.Reading, Hobbies.Gaming, Hobbies.Movies, Hobbies.Music, Hobbies.Drawing, Hobbies.Cooking, Hobbies.Shopping, Hobbies.Traveling, Hobbies.Photography, Hobbies.Gardening,
            Health.GoodHealth, Health.Sick, Health.Hospital, Health.Period, Health.Medicine, Health.Meditation, Health.Therapy, Health.Injury,
            Food.HomeCooked, Food.DineOut, Food.FastFood, Food.Healthy, Food.Sweets, Food.Coffee, Food.Alcohol, Food.Overeating,
            Sleep.GoodSleep, Sleep.BadSleep, Sleep.Insomnia, Sleep.Overslept, Sleep.EarlyBird, Sleep.Nightmares,
            Emotions.Happy, Emotions.Calm, Emotions.Excited, Emotions.Sad, Emotions.Angry, Emotions.Anxious, Emotions.Tired, Emotions.Stressed, Emotions.Bored, Emotions.Confused
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MoonIconsPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Moon Page Icons Preview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 60.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(MoonIcons.getAllIcons()) { icon ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(icon.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon.vector,
                                contentDescription = null,
                                tint = icon.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
