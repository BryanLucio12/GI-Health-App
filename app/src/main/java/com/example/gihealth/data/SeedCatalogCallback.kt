package com.example.gihealth.data

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.runBlocking
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

class SeedCatalogCallback(
    private val appContext: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Run seeding in a background thread
        Executors.newSingleThreadExecutor().execute {
            val database = FoodDatabase.getDatabase(appContext)
            seedCatalogIfNeeded(appContext, database.foodCatalogDao())
        }
    }
}

// Matches the shape of each object in foods_cleaned.json
private data class CleanFoodJson(
    val id: Long,
    val name: String,
    val ingredients: String
)

private fun seedCatalogIfNeeded(
    context: Context,
    catalogDao: FoodCatalogDao
) {
    val prefs = context.getSharedPreferences("seed_prefs", Context.MODE_PRIVATE)
    if (prefs.getBoolean("catalog_seeded", false)) {
        Log.d("SeedCatalog", "Already seeded → skipping")
        return
    }

    Log.d("SeedCatalog", "Seeding from foods_cleaned.json...")

    try {
        context.assets.open("foods_cleaned.json").use { stream ->
            JsonReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
                val gson = Gson()

                reader.beginArray() // top-level is [ {id,name,ingredients}, ... ]

                val batch = mutableListOf<FoodCatalogEntity>()
                val batchSize = 500
                var total = 0

                while (reader.hasNext()) {
                    // 👇 Explicit type so Kotlin knows what this is
                    val item: CleanFoodJson =
                        gson.fromJson(reader, CleanFoodJson::class.java)

                    batch.add(
                        FoodCatalogEntity(
                            id = item.id,
                            name = item.name.trim(),
                            ingredients = item.ingredients.trim()
                        )
                    )

                    total++
                    if (batch.size >= batchSize) {
                        runBlocking { catalogDao.insertAll(batch) }
                        Log.d("SeedCatalog", "Inserted batch → $total")
                        batch.clear()
                    }
                }

                if (batch.isNotEmpty()) {
                    runBlocking { catalogDao.insertAll(batch) }
                }

                reader.endArray()

                prefs.edit().putBoolean("catalog_seeded", true).apply()
                Log.d("SeedCatalog", "Done seeding → $total rows")
            }
        }
    } catch (e: Exception) {
        Log.e("SeedCatalog", "ERROR seeding catalog", e)
    }
}
