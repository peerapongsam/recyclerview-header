package com.peerapongsam.recyclerview.stickyheaders.sample

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peerapongsam.recyclerview.stickyheader.StickyHeaderRecyclerViewItemDecoration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    @Suppress("PropertyName")
    private val TAG = "MainActivity"

    private lateinit var adapter: TopicsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = TopicsAdapter()
        adapter.setOnClickListener { topic ->
            Log.d(TAG, "setOnClickListener() called with: topic = [$topic]")
        }
        rv_topics.adapter = adapter
        rv_topics.layoutManager = LinearLayoutManager(this)
        val decoration = StickyHeaderRecyclerViewItemDecoration(adapter)
//        val touchListener = StickyHeaderRecyclerViewTouchListener(rv_topics, decoration)
//        touchListener.setOnHeaderClickListener { header, position, headerId ->
//            Log.d(TAG, "setOnHeaderClickListener() called with: header = [${header.tv_title.text}]")
////            Log.d(
////                TAG,
////                "setOnHeaderClickListener() called with: header = [$header], position = [$position], headerId = [$headerId]"
////            )
//        }
//        rv_topics.addOnItemTouchListener(touchListener)
        rv_topics.addItemDecoration(decoration)
        GenerateTopic().execute()
    }

    inner class GenerateTopic : AsyncTask<Void, Void, List<Topic>>() {
        override fun doInBackground(vararg params: Void?): List<Topic>? {
            val result = mutableListOf<Topic>()
            for (i in 1..100) {
                result.add(Topic(i, "Topic $i", "Description $i"))
            }
            return result
        }

        override fun onPostExecute(result: List<Topic>?) {
            super.onPostExecute(result)
            adapter.submitList(result)
        }
    }
}
