package com.example.moods.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moods.data.model.ActivityTag
import com.example.moods.data.model.EntryTagCrossRef
import com.example.moods.data.model.MoodEntry

/**
 * La clase principal de la base de datos Room para la aplicación.
 *
 * Define las entidades (tablas) y proporciona acceso a los DAOs.
 */
@Database(
    entities = [
        MoodEntry::class,
        ActivityTag::class,
        EntryTagCrossRef::class
    ],
    version = 1,
    exportSchema = false // Recomendado para no guardar el esquema en control de versiones
)
abstract class AppDatabase : RoomDatabase() {

    // Room implementará estas funciones abstractas por nosotros.
    abstract fun moodEntryDao(): MoodEntryDao
    abstract fun activityTagDao(): ActivityTagDao

    companion object {
        // @Volatile asegura que la instancia sea siempre visible para todos los hilos.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia singleton de la base de datos.
         * Utiliza un patrón 'double-check' para seguridad entre hilos.
         *
         * @param context El contexto de la aplicación.
         * @return La instancia singleton de AppDatabase.
         */
        fun getInstance(context: Context): AppDatabase {
            // Si la instancia ya existe, la devuelve.
            return INSTANCE ?: synchronized(this) {
                // Si la instancia aún no existe, la crea.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moods_database" // Nombre del archivo de la base de datos
                )
                    // .addMigrations(...) // Aquí se añadirían migraciones en el futuro
                    .build()

                INSTANCE = instance
                // Devuelve la instancia recién creada
                instance
            }
        }
    }
}
