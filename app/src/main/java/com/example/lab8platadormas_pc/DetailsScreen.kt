// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun DetailsScreen(
    photo: PexelsPhoto,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppModule.provideDatabase(context) }
    val photoDao = remember { db.photoDao() }

    var photoState by remember { mutableStateOf(photo) }
    var isFavorite by remember { mutableStateOf(photo.liked) }
    var loading by remember { mutableStateOf(false) }

    // cargar version de room por ID
    LaunchedEffect(photo.id) {
        GlobalScope.launch {
            val saved = photoDao.getPhotoById(photo.id)
            if (saved != null) {
                isFavorite = saved.isFavorite
            } else {
                val call = PexelsService.api.getCurated()
                call.enqueue(object : Callback<PexelsResponse> {
                    override fun onResponse(
                        call: Call<PexelsResponse>,
                        response: Response<PexelsResponse>
                    ) {
                        val list = response.body()?.photos ?: return
                        val found = list.find { it.id == photo.id }
                        found?.let {
                            photoState = it
                            GlobalScope.launch {
                                photoDao.insertAll(
                                    listOf(
                                        PhotoEntity(
                                            id = it.id,
                                            photographer = it.photographer,
                                            width = it.width,
                                            height = it.height,
                                            url = it.url,
                                            thumbnailUrl = it.src.medium ?: it.src.original,
                                            queryKey = "curated",
                                            pageIndex = 1,
                                            isFavorite = false
                                        )
                                    )
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<PexelsResponse>, t: Throwable) {}
                })
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isFavorite = !isFavorite
                        GlobalScope.launch {
                            photoDao.updateFavorite(photo.id, isFavorite)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite)
                                Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                AsyncImage(
                    model = photoState.src.original,
                    contentDescription = photoState.alt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(Modifier.height(12.dp))
                Text("Autor: ${photoState.photographer}")
                Text("Dimensiones: ${photoState.width}x${photoState.height}")
                Spacer(Modifier.height(12.dp))
                Button(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, photoState.url)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Compartir con:"))
                }) {
                    Text("Compartir")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
