// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PexelsScreen(
    navController: NavController,
    initialPage: Int = 1,
    perPage: Int = 20
) {
    var loading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var page by rememberSaveable { mutableStateOf(initialPage) }
    var query by rememberSaveable { mutableStateOf("") }

    var photos by remember { mutableStateOf<List<PexelsPhoto>>(emptyList()) }
    var currentCall by remember { mutableStateOf<Call<PexelsResponse>?>(null) }

    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val db by remember { mutableStateOf(AppModule.provideDatabase(context)) }
    val photoDao = remember { db.photoDao() }
    val queryDao = remember { db.recentQueryDao() }

    var recentQueries by remember { mutableStateOf<List<RecentQueryEntity>>(emptyList()) }

    // Cargar búsquedas recientes al inicio
    LaunchedEffect(Unit) {
        recentQueries = queryDao.getRecentQueries()
    }

    DisposableEffect(Unit) {
        onDispose { currentCall?.cancel() }
    }

    fun fetch(newPage: Int = page, reset: Boolean = false) {
        currentCall?.cancel()
        loading = true
        error = null

        val normalizedQuery = query.trim().lowercase()
        val call = if (normalizedQuery.isBlank()) {
            PexelsService.api.getCurated(page = newPage, perPage = perPage)
        } else {
            PexelsService.api.searchPhotos(query = normalizedQuery, page = newPage, perPage = perPage)
        }

        currentCall = call
        call.enqueue(object : Callback<PexelsResponse> {
            override fun onResponse(call: Call<PexelsResponse>, response: Response<PexelsResponse>) {
                loading = false
                if (response.isSuccessful) {
                    val list = response.body()?.photos.orEmpty()
                    photos = if (reset) list else photos + list
                    page = newPage

                    // guardar en Room
                    val entities = list.map {
                        PhotoEntity(
                            id = it.id,
                            photographer = it.photographer,
                            width = it.width,
                            height = it.height,
                            url = it.url,
                            thumbnailUrl = it.src.medium ?: it.src.large ?: it.src.original,
                            queryKey = normalizedQuery.ifBlank { "curated" },
                            pageIndex = newPage,
                            isFavorite = it.liked
                        )
                    }

                    coroutineScope.launch {
                        if (reset) photoDao.clearQuery(normalizedQuery)
                        photoDao.insertAll(entities)
                        queryDao.insertOrUpdate(RecentQueryEntity(normalizedQuery))
                        recentQueries = queryDao.getRecentQueries()
                    }
                } else {
                    error = "HTTP ${response.code()}"
                }
            }

            override fun onFailure(call: Call<PexelsResponse>, t: Throwable) {
                if (call.isCanceled) return
                loading = false
                error = t.message ?: "Network error"

                // Intentar cargar desde Room si no hay red
                coroutineScope.launch {
                    val cached = photoDao.getPhotosByQuery(normalizedQuery.ifBlank { "curated" })
                    if (cached.isNotEmpty()) {
                        photos = cached.map {
                            PexelsPhoto(
                                id = it.id,
                                width = it.width,
                                height = it.height,
                                url = it.url,
                                photographer = it.photographer,
                                photographerUrl = "",
                                photographerId = 0L,
                                avgColor = null,
                                src = PexelsSrc(
                                    original = it.thumbnailUrl ?: it.url,
                                    large2x = null,
                                    large = null,
                                    medium = it.thumbnailUrl,
                                    small = null,
                                    portrait = null,
                                    landscape = null,
                                    tiny = null
                                ),
                                liked = it.isFavorite,
                                alt = ""
                            )
                        }
                    }
                }
            }
        })
    }

    // Debounce para la busqueda
    LaunchedEffect(query) {
        snapshotFlow { query.trim() }
            .debounce(500)
            .collect {
                page = 1
                photos = emptyList()
                fetch(newPage = 1, reset = true)
            }
    }

    // Scroll infinito
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisible ->
                if (lastVisible != null && lastVisible >= photos.size - 4 && !loading && photos.isNotEmpty()) {
                    fetch(page + 1)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotos") },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text("Buscar fotos...") }
            )

            // mostrar las busquedas recientes
            if (recentQueries.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    items(recentQueries.size) { index ->
                        val q = recentQueries[index]
                        androidx.compose.material3.AssistChip(
                            onClick = { query = q.query },
                            label = { Text(q.query) },
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }
            }

            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                state = gridState,
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos, key = { it.id }) { p ->
                    PhotoCard(
                        title = p.alt ?: p.photographer,
                        url = p.src.medium ?: p.src.large ?: p.src.original,
                        isFavorite = p.liked,
                        onToggleFavorite = {
                            coroutineScope.launch {
                                val newValue = !p.liked
                                photoDao.updateFavorite(p.id, newValue)
                                photos = photos.map {
                                    if (it.id == p.id) it.copy(liked = newValue) else it
                                }
                            }
                        },
                        modifier = Modifier.clickable {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("photo", p)
                            navController.navigate("details/${p.id}")
                        }
                    )
                }

                if (loading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}