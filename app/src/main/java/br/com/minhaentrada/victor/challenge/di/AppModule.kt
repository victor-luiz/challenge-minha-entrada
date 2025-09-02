package br.com.minhaentrada.victor.challenge.di

import android.content.Context
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.event.EventRepository
import br.com.minhaentrada.victor.challenge.data.SessionManager
import br.com.minhaentrada.victor.challenge.data.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(database: AppDatabase): UserRepository {
        return UserRepository(database.userDao())
    }

    @Provides
    @Singleton
    fun provideEventRepository(database: AppDatabase): EventRepository {
        return EventRepository(database.eventDao())
    }

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
}