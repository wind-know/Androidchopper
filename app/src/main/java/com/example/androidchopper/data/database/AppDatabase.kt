package com.example.androidchopper.data.database
import androidx.room.*
import android.content.Context
// 标记为Room数据库类，声明包含的实体类和数据库版本号
@Database(
    entities = [QuestionEntity::class], // 定义该数据库包含的所有表对应的实体类
    version = 1,                         // 数据库版本号（修改表结构时需要递增）
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // 声明获取DAO接口的抽象方法（Room会在编译时自动生成实现）
    abstract fun questionDao(): QuestionDao
    // 伴生对象（实现单例模式）
    companion object {
        // 使用@Volatile保证INSTANCE的可见性和原子性
        @Volatile
        private var INSTANCE: AppDatabase? = null
        /**
         * 获取数据库单例实例（线程安全）
         * @param context 应用上下文（建议使用ApplicationContext）
         */
        fun getInstance(context: Context): AppDatabase {
            // 第一次检查（非空时快速返回）
            return INSTANCE ?: synchronized(this) { // 同步代码块（防止多线程并发创建实例）
                // 第二次检查（进入同步块后再次验证）
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        // 私有方法：构建数据库实例
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext, // 使用ApplicationContext避免内存泄漏
                AppDatabase::class.java,   // 数据库类类型
                "question.db"              // 数据库文件名（推荐.db后缀）
            ).build()                      // 创建数据库实例
        }
    }
}