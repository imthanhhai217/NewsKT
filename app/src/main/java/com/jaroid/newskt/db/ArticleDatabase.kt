package com.jaroid.newskt.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jaroid.newskt.models.Article

@Database(
    entities = [Article::class], version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDAO(): ArticlesDAO

    companion object {
        @Volatile
        private var instances: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instances ?: synchronized(lock = LOCK) {
            instances ?: createDatabase(context).also {
                instances = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context, ArticleDatabase::class.java, "articles.db").build()
    }
}