package com.diary.moonpage.di

import com.diary.moonpage.data.repository.AuthRepositoryImpl
import com.diary.moonpage.data.repository.MomentRepositoryImpl
import com.diary.moonpage.data.repository.ThemeRepositoryImpl
import com.diary.moonpage.data.repository.UserRepositoryImpl
import com.diary.moonpage.domain.repository.AuthRepository
import com.diary.moonpage.domain.repository.MomentRepository
import com.diary.moonpage.domain.repository.ThemeRepository
import com.diary.moonpage.domain.repository.UserRepository
import com.diary.moonpage.data.repository.DailyLogRepositoryImpl
import com.diary.moonpage.domain.repository.DailyLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        themeRepositoryImpl: ThemeRepositoryImpl
    ): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindMomentRepository(
        momentRepositoryImpl: MomentRepositoryImpl
    ): MomentRepository

    @Binds
    @Singleton
    abstract fun bindDailyLogRepository(
        dailyLogRepositoryImpl: DailyLogRepositoryImpl
    ): DailyLogRepository
}
