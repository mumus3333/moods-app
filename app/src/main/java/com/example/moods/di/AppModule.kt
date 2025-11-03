package com.example.moods.di

import android.content.Context
import androidx.room.Room
import com.example.moods.data.database.ActivityTagDao
import com.example.moods.data.database.AppDatabase
import com.example.moods.data.database.MoodEntryDao
import com.example.moods.data.repository.MoodRepository
import com.example.moods.data.repository.OfflineMoodRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer las dependencias de la app.
 * Le enseña a Hilt cómo construir los objetos que necesitamos.
 */
@Module
@InstallIn(SingletonComponent::class) // Dependencias disponibles a nivel de aplicación
object AppModule {

    /**
     * Provee la instancia de la base de datos (AppDatabase).
     * Se marca como @Singleton para que solo exista una instancia en toda la app.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "moods_database" // Nombre del archivo de la base de datos
        )
            // .fallbackToDestructiveMigration() // Opcional: Usar en desarrollo si cambias el esquema
            .build()
    }

    /**
     * Provee el DAO para MoodEntry.
     * Depende de AppDatabase (Hilt sabe cómo proveerla gracias al método anterior).
     */
    @Provides
    fun provideMoodEntryDao(database: AppDatabase): MoodEntryDao {
        return database.moodEntryDao()
    }

    /**
     * Provee el DAO para ActivityTag.
     * Depende de AppDatabase.
     */
    @Provides
    fun provideActivityTagDao(database: AppDatabase): ActivityTagDao {
        return database.activityTagDao()
    }

    /**
     * Provee la implementación del Repositorio (OfflineMoodRepository)
     * cuando se solicite la interfaz (MoodRepository).
     *
     * @Singleton asegura que solo haya un repositorio.
     * Hilt sabe cómo proveer moodEntryDao y activityTagDao gracias a los métodos anteriores.
     */
    @Provides
    @Singleton
    fun provideMoodRepository(
        moodEntryDao: MoodEntryDao,
        activityTagDao: ActivityTagDao
    ): MoodRepository {
        // Retornamos la implementación "Offline"
        return OfflineMoodRepository(moodEntryDao, activityTagDao)
    }
}
