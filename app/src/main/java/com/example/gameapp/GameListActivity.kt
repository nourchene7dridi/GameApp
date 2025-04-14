package com.example.gameapp

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
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent

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
        GameItem("Shake Challenge", R.drawable.jeu),
        GameItem("Nope Quiz", R.drawable.jeu),
        GameItem("Swipe Duel", R.drawable.jeu),
        GameItem("Balance Game", R.drawable.jeu),
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                // ici tu peux faire : val intent = Intent(...) startActivity(...)
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
                color = Color.Black,
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
