package core.colin.basic.utils

/**
 * Author: PL
Date: 2023/10/17
Desc: 线程安全的带参数的单例模式
使用示例：
class MyManager private constructor(context: Context) {
fun doSomething() {
//...
}
companion object : SingletonHolder<MyManager, Context>(::MyManager) {}
}
 */
open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        return instance ?: synchronized(this) {
            instance ?: constructor(arg).also { instance = it }
        }
    }
}
