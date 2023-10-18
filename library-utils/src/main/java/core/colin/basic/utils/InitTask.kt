//package core.colin.basic.utils
//
//import android.app.Application
//import android.os.AsyncTask
//
///**
// *
// */
//class InitTaskRunner(private val application: Application) {
//
//    private val mTasks: ArrayList<InitTask> = arrayListOf()
//
//    fun add(task: InitTask): InitTaskRunner {
//        mTasks.add(task)
//        return this
//    }
//
//    fun run() {
//        val isMainProcess = ProcessUtils.isMainProcess()
//        val syncTasks: ArrayList<InitTask> = arrayListOf()
//        val asyncTasks: ArrayList<InitTask> = arrayListOf()
//        for (task in mTasks) {
//            if (!isMainProcess && task.onlyMainProcess()) {
//                continue
//            }
//            if (task.sync()) {
//                syncTasks.add(task)
//            } else {
//                asyncTasks.add(task)
//            }
//        }
//        runSync(syncTasks)
//        runAsync(asyncTasks)
//    }
//
//    private fun runSync(tasks: ArrayList<InitTask>) {
//        tasks.sortBy { it.level() }
//        for (task in tasks) {
//            try {
//                task.init(application)
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun runAsync(tasks: ArrayList<InitTask>) {
//        val tasksMap = hashMapOf<String, ArrayList<InitTask>>()
//        for (task in tasks) {
//            val name: String = task.asyncTaskName()
//            var list = tasksMap[name]
//            if (list == null) {
//                list = arrayListOf()
//                tasksMap[name] = list
//            }
//            list.add(task)
//        }
//        for (map in tasksMap) {
//            val task = map.value
//            AsyncRunner(application, task).execute()
//        }
//    }
//}
//
//
//class AsyncRunner(
//    private val application: Application,
//    private val tasks: ArrayList<InitTask>
//) :
//    AsyncTask<Unit, Unit, Unit>() {
//    override fun doInBackground(vararg params: Unit?) {
//        tasks.sortBy { it.level() }
//        for (task in tasks) {
//            try {
//                task.init(application)
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
//
///**
// * 异步任务
// */
//abstract class AsyncInitTask : InitTask {
//    override fun sync(): Boolean {
//        return false
//    }
//
//    override fun level(): Int {
//        return 0
//    }
//
//    override fun onlyMainProcess(): Boolean {
//        return true
//    }
//
//    override fun asyncTaskName(): String {
//        return toString()
//    }
//}
//
///**
// * 同步任务
// */
//abstract class SyncInitTask : InitTask {
//    override fun sync(): Boolean {
//        return true
//    }
//
//    override fun level(): Int {
//        return 0
//    }
//
//    override fun onlyMainProcess(): Boolean {
//        return true
//    }
//
//    override fun asyncTaskName(): String {
//        return ""
//    }
//}
//
//
//interface InitTask {
//    fun sync(): Boolean
//    fun asyncTaskName(): String
//    fun level(): Int
//    fun onlyMainProcess(): Boolean
//    fun init(application: Application)
//}
