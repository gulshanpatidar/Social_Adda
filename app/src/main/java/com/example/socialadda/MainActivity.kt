package com.example.socialadda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialadda.R.layout.activity_sign_in
import com.example.socialadda.daos.PostDao
import com.example.socialadda.databinding.ActivityMainBinding
import com.example.socialadda.databinding.ActivitySignInBinding
import com.example.socialadda.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        //add the click listener for the add post button and take user to the add post activity
        binding.addPostButton.setOnClickListener{
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

        //create instance of the PostDao
        postDao = PostDao()
        //call this method and see the magic their xd:)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        //create instance of the postCollection using the PostDao
        val postCollection = postDao.postCollection
        //create a query from the postCollection in which posts will be sorted by the createdAt time and in descending order
        val query = postCollection.orderBy("createdAt",Query.Direction.DESCENDING)
        //create instance of the recyclerViewOptions to pass to the adapter by passing the query into it
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()

        //create instance of the adapter using the above variables
        adapter = PostAdapter(recyclerViewOptions,this)

        //set the adapter to the recyclerView
        binding.recyclerView.adapter = adapter
        //set the layout manager to be linear
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    //in the onStart method, start listening the changes
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    //in onStop, stop listening the changes
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        //call the updateLikes method of the postDao and pass the post id
        postDao.updateLikes(postId)
    }
}