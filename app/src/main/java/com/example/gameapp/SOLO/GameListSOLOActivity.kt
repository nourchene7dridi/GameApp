package com.example.gameapp.SOLO

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameapp.ui.theme.GameAppTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.gameapp.R
import com.example.gameapp.SOLO.BreishQuiz.QuizActivity
import com.example.gameapp.SOLO.CatchMe.CatchMeGame
import com.example.gameapp.SOLO.ShakeIt.ShakeItGame


class GameListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GameListScreen()
                }
            }
        }
    }
}

data class GameItem(val title: String, val imageResId: Int)

@Composable
fun GameListScreen() {
    val games = listOf(
        GameItem("3 Défis aléatoires", R.drawable.jeu),
        GameItem("Catch Me (if you can)", R.drawable.classroom),
        GameItem("Breizh Quiz", R.drawable.breizh),
        GameItem("Solo Pong", R.drawable.ping_solo),
        GameItem("Shake It", R.drawable.shaker1),
        GameItem("Tap Race", R.drawable.jeu),
        GameItem("Reflex Master", R.drawable.jeu)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(games.size) { index ->
            GameCard(game = games[index])
        }
    }
}

@Composable
fun GameCard(game: GameItem) {
    val context = LocalContext.current


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                if (game.title == "Breizh Quiz") {
                    val intent = Intent(context, QuizActivity::class.java)
                    context.startActivity(intent)
                }
                if (game.title == "Solo Pong") {
                    val intent = Intent(context, com.example.gameapp.SOLO.Pong.PongGameActivity::class.java)
                    context.startActivity(intent)
                }
                if (game.title == "Catch Me (if you can)") {
                    val intent = Intent(context, CatchMeGame::class.java)
                    context.startActivity(intent)
                }
                if (game.title == "Shake It") {
                    val intent = Intent(context, ShakeItGame::class.java)
                    context.startActivity(intent)
                }
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = game.imageResId),
                contentDescription = game.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = game.title,
                color = when (game.title) {
                    "Breizh Quiz" -> Color.Red
                    "Solo Pong" -> Color.White
                    else -> Color.Gray
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
