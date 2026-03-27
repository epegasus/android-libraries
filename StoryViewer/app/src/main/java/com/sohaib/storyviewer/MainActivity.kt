package com.sohaib.storyviewer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sohaib.storyview.StoryView
import com.sohaib.storyview.dataClasses.MyStory
import com.sohaib.storyview.listeners.OnStoryChangeListener
import com.sohaib.storyview.listeners.OnStoryClickListener
import com.sohaib.storyviewer.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mbShow.setOnClickListener { onShowClick() }
    }

    private fun onShowClick() {
        val myStories = ArrayList<MyStory>()
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH)

        val story1 = MyStory(
            url = "https://media.istockphoto.com/id/517188688/photo/mountain-landscape.jpg?s=1024x1024&w=0&k=20&c=z8_rWaI8x4zApNEEG9DnWlGXyDIXe-OmsAyQ5fGPVV8="
        )
        val story2 = MyStory(
            url = "https://static.boredpanda.com/blog/wp-content/uploads/2017/11/My-most-popular-pic-since-I-started-dog-photography-5a0b38cbd5e1e__880.jpg",
            date = simpleDateFormat.parse("16-09-2025 9:00:00")
        )
        val story3 = MyStory(
            url = "https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            date = simpleDateFormat.parse("16-09-2025 8:00:00"),
            description = "This is a description"
        )
        myStories.add(story1)
        myStories.add(story2)
        myStories.add(story3)

        StoryView.Builder(supportFragmentManager)
            .setStoriesList(myStories)
            .setStoryDuration(5000)
            .setHeaderTitleText("Story Viewer")
            .setHeaderSubtitleText("Android is Love")
            .setHeaderTitleLogoUrl("https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500")
            .setOnStoryClickListener(object : OnStoryClickListener {
                override fun onTitleIconClickListener(position: Int) {

                }

                override fun onDescriptionClickListener(position: Int) {
                    showToast(myStories[position].description.toString())
                }
            })
            .setOnStoryChangeListener(object : OnStoryChangeListener {
                override fun storyChanged(position: Int) {
                    //showToast("Showing Story # $position")
                }
            })
            .setStartingIndex(0)
            .setRtl(false)
            .build()
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}