package com.example.recipesdetail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlin.collections.filter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RecipeApp()
                }
            }
        }
    }
}

enum class Status {
    NotSelected,
    Want,
    Cooking,
    Cooked
}

data class Recipe(
    val id: Int,
    val name: String,
    val cookTime: String,
    val difficulty: String,
    val status: Status = Status.NotSelected
)

val sampleRecipeList = listOf(
    Recipe(1, "Navy-style pasta", "25 min", "low"),
    Recipe(2, "Fried eggs", "10 min", "low"),
    Recipe(3, "Omelette with cheese", "15 min", "low"),
    Recipe(4, "Chicken soup", "40 min", "medium"),
    Recipe(5, "Mashed potatoes", "30 min", "low"),
    Recipe(6, "Grilled chicken", "35 min", "medium"),
    Recipe(7, "Pancakes", "20 min", "low"),
    Recipe(8, "Vegetable salad", "10 min", "low"),
    Recipe(9, "Rice with vegetables", "25 min", "low"),
    Recipe(10, "Baked fish", "30 min", "medium"),
    Recipe(11, "Spaghetti with tomato sauce", "25 min", "low"),
    Recipe(12, "French toast", "15 min", "low")
)

data class RecipeDetails(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: String,
    val cookTime: String,
    val difficulty: String,
    val method: String,
    val status: Status = Status.NotSelected
)

val sampleRecipeDetailsList = listOf(
    RecipeDetails(
        1,
        "Navy-style pasta",
        "A simple pasta dish popular in Eastern Europe made with pasta and fried minced meat with onions.",
        "Pasta, minced beef or pork, onion, vegetable oil, salt, black pepper",
        "25 min",
        "low",
        "Boil the pasta. Fry minced meat with chopped onion in a pan. Mix everything together, add salt and pepper."
    ),
    RecipeDetails(
        2,
        "Fried eggs",
        "Classic fried eggs cooked in a pan. Often served for breakfast with bread or vegetables.",
        "Eggs, butter or vegetable oil, salt, black pepper",
        "10 min",
        "low",
        "Heat oil or butter in a pan. Crack the eggs into the pan. Cook until desired doneness, then season with salt."
    ),
    RecipeDetails(
        3,
        "Omelette with cheese",
        "A quick omelette made from beaten eggs cooked with butter and melted cheese.",
        "Eggs, butter, cheddar cheese, salt, black pepper",
        "15 min",
        "low",
        "Beat the eggs with salt. Pour into a heated pan with butter. Add grated cheese, fold the omelette in half."
    ),
    RecipeDetails(
        4,
        "Chicken soup",
        "Traditional chicken soup made with chicken broth, vegetables and simple seasonings.",
        "Chicken, carrot, onion, potato, salt, black pepper, water",
        "40 min",
        "medium",
        "Boil chicken in water. Add chopped carrots, potatoes, and onions. Cook until vegetables are soft. Season to taste."
    ),
    RecipeDetails(
        5,
        "Mashed potatoes",
        "Soft mashed potatoes mixed with butter and milk until creamy.",
        "Potatoes, butter, milk, salt",
        "30 min",
        "low",
        "Boil potatoes until tender. Drain the water. Add butter and milk. Mash until smooth and creamy."
    ),
    RecipeDetails(
        6,
        "Grilled chicken",
        "Chicken breast grilled with simple spices until golden and juicy.",
        "Chicken breast, olive oil, garlic, salt, black pepper, paprika",
        "35 min",
        "medium",
        "Season chicken with spices and oil. Grill or pan-fry until golden brown and fully cooked."
    ),
    RecipeDetails(
        7,
        "Pancakes",
        "Thin pancakes cooked in a pan and served with syrup, jam or fruit.",
        "Flour, eggs, milk, sugar, butter, salt",
        "20 min",
        "low",
        "Mix flour, eggs, milk, and sugar into a batter. Cook thin pancakes in a hot pan on both sides."
    ),
    RecipeDetails(
        8,
        "Vegetable salad",
        "Fresh salad made with raw vegetables and light dressing.",
        "Tomatoes, cucumber, lettuce, olive oil, salt, pepper",
        "10 min",
        "low",
        "Chop fresh vegetables (tomatoes, cucumbers, lettuce). Add olive oil, salt, and mix."
    ),
    RecipeDetails(
        9,
        "Rice with vegetables",
        "Simple rice dish cooked with mixed vegetables.",
        "Rice, carrot, peas, onion, vegetable oil, salt, soy sauce",
        "25 min",
        "low",
        "Boil rice. Sauté vegetables separately. Mix rice with vegetables and season."
    ),
    RecipeDetails(
        10,
        "Baked fish",
        "Fish fillet baked in the oven with lemon and herbs.",
        "Fish fillet, lemon, olive oil, garlic, salt, black pepper",
        "30 min",
        "medium",
        "Season fish with salt, pepper, and lemon. Place in a baking dish. Bake in the oven until cooked."
    ),
    RecipeDetails(
        11,
        "Spaghetti with tomato sauce",
        "Classic pasta served with tomato sauce made from tomatoes and garlic.",
        "Spaghetti, tomatoes, garlic, olive oil, salt, basil",
        "25 min",
        "low",
        "Boil spaghetti. Prepare tomato sauce with garlic in a pan. Mix with pasta."
    ),
    RecipeDetails(
        12,
        "French toast",
        "Bread slices dipped in an egg and milk mixture and fried until golden.",
        "Bread, eggs, milk, sugar, butter, cinnamon",
        "15 min",
        "low",
        "Beat eggs with milk. Dip bread into the mixture. Fry in a pan until golden brown."
    )
)

data class RecipeUiState(
    val searchQuery: String = "",
    val filter: Status? = null,
    val recipeList: List<Recipe> = emptyList()
) {
    val filteredRecipe: List<Recipe>
        get() {
            return recipeList
                .filter { recipe ->
                    recipe.name.contains(searchQuery, ignoreCase = true)
                }
                .filter { recipe ->
                    filter == null || recipe.status == filter
                }
        }

    val totalCount get() = recipeList.size
    val wantCount get() = recipeList.count {it.status == Status.Want }
    val cookingCount get() = recipeList.count { it.status == Status.Cooking }
    val cookedCount get() = recipeList.count { it.status == Status.Cooked }
}

class RecipeViewModel: ViewModel() {

    var uiState by mutableStateOf(
        RecipeUiState(recipeList = sampleRecipeList)
    )
    private set

    fun onSearchChange(newValue: String){
        uiState = uiState.copy(searchQuery = newValue)
    }

    fun onFilterChange(status: Status?){
        uiState = uiState.copy(filter = status)
    }

    fun nextStatus(id: Int){
        val updatedList = uiState.recipeList.map {
            if(it.id == id) {
                val newStatus = when(it.status){
                    Status.NotSelected -> Status.Want
                    Status.Want -> Status.Cooking
                    Status.Cooking -> Status.Cooked
                    Status.Cooked -> Status.NotSelected
                }
                it.copy(status = newStatus)
            } else it
        }
        uiState = uiState.copy(recipeList = updatedList)
    }

    fun getRecipeDetails(id: Int): RecipeDetails? {
        return sampleRecipeDetailsList.find { it.id == id}
    }

    fun getRecipeById(id: Int): Recipe? {
        return uiState.recipeList.find { it.id == id }
    }
}

@Composable
fun RecipeApp() {
    val viewModel: RecipeViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            RecipeListScreen(
                uiState = viewModel.uiState,
                onSearchChange = viewModel::onSearchChange,
                onFilterChange = viewModel::onFilterChange,
                onNextStatus = viewModel::nextStatus,
                onRecipeClick = { id ->
                    navController.navigate("details/$id")
                }
            )
        }

        composable(
            "details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

            RecipeDetailsScreen(
                recipeId = id,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    uiState: RecipeUiState,
    onSearchChange: (String) -> Unit,
    onFilterChange: (Status?) -> Unit,
    onNextStatus: (Int) -> Unit,
    onRecipeClick: (Int) -> Unit
) {
    Scaffold (
        topBar = { TopAppBar(title = { Text("Recipes")}) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by name") },
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onFilterChange(null) }) {
                    Text("All")
                }

                Button(onClick = { onFilterChange(Status.Want) }) {
                    Text("Want")
                }

                Button(onClick = { onFilterChange(Status.Cooking) }) {
                    Text("Cooking")
                }

                Button(onClick = { onFilterChange(Status.Cooked) }) {
                    Text("Cooked")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Total: ${uiState.totalCount} |  Want: ${uiState.wantCount}  |  Cooking: ${uiState.cookingCount}  |  Cooked: ${uiState.cookedCount}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            if (uiState.filteredRecipe.isEmpty()) {
                Text("Ничего нет :(")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredRecipe, { it.id }) { item ->
                        RecipeCard(
                            recipe = item,
                            onClick = { onRecipeClick(item.id) },
                            onNextStatus = onNextStatus
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onNextStatus: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(recipe.name, fontWeight = FontWeight.Bold)
            Text("${ recipe.cookTime} - ${recipe.difficulty}")
            Text("Status: ${recipe.status}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onNextStatus(recipe.id) }
            ) {
                Text("Next status")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    recipeId: Int,
    viewModel: RecipeViewModel,
    onBackClick: () -> Unit
) {
    val recipe = viewModel.getRecipeDetails(recipeId)
    val recipeFromList = viewModel.getRecipeById(recipeId)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recipe Details") })
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Button(onBackClick) {
                Text("Back")
            }

            Spacer(Modifier.height(16.dp))

            if(recipe == null) {
                Text("Детали не найдены")
            } else {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Text("Cook time: ${ recipe.cookTime}")
                Text("Difficulty: ${ recipe.difficulty}")
                Text("Status: ${recipeFromList?.status}")
                Text("Ingredients: ${ recipe.ingredients}")
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(recipe.description)

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Method of preparation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(recipe.method)

                Spacer(Modifier.height(16.dp))

                Button(onClick = { viewModel.nextStatus(recipeId) }) {
                    Text("Change status")
                }
            }
        }
    }
}