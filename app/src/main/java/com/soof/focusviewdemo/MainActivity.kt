package com.soof.focusviewdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.soof.focusview.FocusCompose
import com.github.soof.focusview.FocusViewType
import com.soof.focusviewdemo.data.ShapeType
import com.soof.focusviewdemo.ui.theme.FocusViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusViewTheme {
                TopAppBar()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("FocusView Demo")
                }
            )
        },
    ) { innerPadding ->
        NavHost(navController = navController, innerPadding)
    }
}

@Composable
fun NavHost(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            FocusViewDemo(paddingValues, navController)
        }
        composable(
            "detail/{shapeType}"
        ) { backStackEntry ->
            val shapeType = backStackEntry.arguments?.getString("shapeType")
            DetailScreen(shapeType = ShapeType.valueOf(shapeType ?: "Circle"))
        }
    }
}

@Composable
fun FocusViewDemo(paddingValues: PaddingValues, navController: NavController) {
    val items = listOf(
        ShapeType.CIRCLE to "This is a circle shape",
        ShapeType.SQUARE to "This is a rounded square shape",
        ShapeType.ROUNDED_SQUARE to "This is a rounded square shape",
        ShapeType.TRIANGLE to "This is a triangle shape",
        ShapeType.RHOMBUS to "This is a rhombus shape"
    )
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxHeight()
            .padding(vertical = 10.dp, horizontal = 10.dp)
    ) {
        items(items) { (shapeType, description) ->
            FocusViewCard(shapeType, description) {
                navController.navigate("detail/${shapeType.name}")
            }
        }
    }
}

@Composable
fun FocusViewCard(shapeType: ShapeType, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = shapeType.name, style = MaterialTheme.typography.titleLarge)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DetailScreen(shapeType: ShapeType) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp)
    ) {
        VideoPlayer(uri = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4".toUri())
        FocusCompose(
            modifier = Modifier.fillMaxSize(),
            viewType = when (shapeType) {
                ShapeType.CIRCLE -> FocusViewType.CIRCLE
                ShapeType.SQUARE -> FocusViewType.SQUARE
                ShapeType.ROUNDED_SQUARE -> FocusViewType.ROUNDED_SQUARE
                ShapeType.TRIANGLE -> FocusViewType.TRIANGLE
                ShapeType.RHOMBUS -> FocusViewType.RHOMBUS
            },
            cornerRadius = when (shapeType) {
                ShapeType.CIRCLE -> 0f
                ShapeType.SQUARE -> 0f
                ShapeType.ROUNDED_SQUARE -> 16f
                ShapeType.TRIANGLE -> 0f
                ShapeType.RHOMBUS -> 0f
            },
            backgroundColor = Color.Black.copy(alpha = 0.7f),
            backgroundGradient = Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.7f), Color.Black)),
            insideColor = Color.Transparent,
            borderColor = Color.Transparent,
            borderWidth = 0f,
            focusWidthMultiplier = 0.8f,
            focusHeightMultiplier = 0.7f,
            focusContent = { }
        )
    }
}