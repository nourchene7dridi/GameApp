package com.example.gameapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import com.example.gameapp.ui.theme.GameAppTheme
import android.content.Intent
import com.example.gameapp.SOLO.GameListActivity
import com.example.gameapp.MULTIPLAYER.GameListActivity2
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.gameapp.R


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameAppTheme {
                // Body of the app
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}



@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        // Image de fond
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Game App",
                fontSize = 30.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = {
                    val intent = Intent(context, GameListActivity::class.java).apply {
                        putExtra("GAME_MODE", "SOLO")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFd5ecf0),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Jouer en solo", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, GameListActivity2::class.java).apply {
                        putExtra("GAME_MODE", "BLUETOOTH")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFFD3B1),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Jouer en Bluetooth", fontSize = 20.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    GameAppTheme {
        MainScreen()
    }
}