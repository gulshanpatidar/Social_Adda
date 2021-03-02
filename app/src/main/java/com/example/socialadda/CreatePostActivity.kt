package com.example.socialadda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.socialadda.daos.PostDao
import com.example.socialadda.daos.UserDao
import com.example.socialadda.databinding.ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var postDao: PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_post)

        //create instance of PostDao
        postDao = PostDao()

        binding.postButton.setOnClickListener {
            //get the input string
            val input = binding.postInput.text.toString().trim()
            //check that it is not empty
            if (input.isNotEmpty()){
                //add the post using postDao
                postDao.addPost(input)
                //show a toast message that post is created successfully
                Toast.makeText(this,"Post created successfully",Toast.LENGTH_SHORT).show()
                //finish the create post activity
                finish()
            }
        }
    }
}