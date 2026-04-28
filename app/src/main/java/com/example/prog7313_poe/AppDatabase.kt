package com.example.prog7313_poe

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Transaction::class, Account::class, Category::class, Member::class, RecurringItem::class], version = 6)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase()
{
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun memberDao(): MemberDao
    abstract fun accountDao(): AccountDao
    abstract fun recurringItemDao(): RecurringItemDao



    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? =null

        fun getDatabase(context: Context): AppDatabase{
        return  INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "budget_Database"
            ).fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }

        }
    }
}