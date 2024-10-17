package electrovese.linkedin_share.session.internals

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class QueueManager private constructor(context: Context) {

    private val ctx: Context = context.applicationContext
    private val requestQueue: RequestQueue = Volley.newRequestQueue(ctx)

    companion object {
        private var queueManager: QueueManager? = null

        fun initQueueManager(ctx: Context) {
            getInstance(ctx)
        }

        @Synchronized
        fun getInstance(context: Context): QueueManager {
            if (queueManager == null) {
                queueManager = QueueManager(context)
            }
            return queueManager!!
        }
    }

    fun getRequestQueue(): RequestQueue {
        return requestQueue
    }
}
