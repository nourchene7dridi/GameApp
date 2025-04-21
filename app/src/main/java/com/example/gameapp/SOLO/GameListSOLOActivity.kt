package com.example.gameapp.SOLO

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameapp.R
import com.example.gameapp.ui.theme.GameAppTheme
import com.example.gameapp.SOLO.Entrainement.GamesSoloActivity
import com.example.gameapp.SOLO.DefisAleatoires.TroisDefisAleatoiresActivity

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
    val context = LocalContext.current
    val games = listOf(
        GameItem("3 Défis aléatoires", R.drawable.jeu),
        GameItem("Mode entraînement", R.drawable.entrainement),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            games.forEach { game ->
                GameCard(game = game)
            }
        }

        // Bouton pour revenir au menu principal
        Button(
            onClick = {
                (context as? ComponentActivity)?.finish() // Ferme cette activité et revient à l'écran précédent
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White
        ) ){
            Text("Revenir au menu")
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
                when (game.title) {
                    "3 Défis aléatoires" -> {
                        val intent = Intent(context, TroisDefisAleatoiresActivity::class.java)
                        context.startActivity(intent)
                    }
                    "Mode entraînement" -> {
                        val intent = Intent(context, GamesSoloActivity::class.java)
                        context.startActivity(intent)
                    }
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
                    "3 Défis aléatoires" -> Color.Red
                    else -> Color.Black
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
