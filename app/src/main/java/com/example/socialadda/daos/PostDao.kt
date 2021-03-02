package com.example.socialadda.daos

import com.example.socialadda.models.Post
import com.example.socialadda.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {

    //create instance of the database
    val db = FirebaseFirestore.getInstance()
    //get the collection from the database, if there will be no such collection then it will be created automatically
    val postCollection = db.collection("posts")
    //create an auth instance for finding the user data
    val auth = Firebase.auth

    //this method will add post to the database by taking a string as a argument
    fun addPost(text: String){
        //get the current user id from the auth and assert that this user id will not be null
        val currentUserId = auth.currentUser!!.uid
        //get the user instance with the help of id in the background thread
        GlobalScope.launch {
            //create instance of the user dao so that we can use its methods
            val userDao = UserDao()
            //get the user instance with the help of dao's method and assert that this user will not be null
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!

            //get the current time from the system
            val currentTime = System.currentTimeMillis()
            //create instance of the post with the help of all these parameters and leave the arrayList of users liked because it is empty now
            val post = Post(text, user, currentTime)
            //set this post in the firebase database
            postCollection.document().set(post)
        }
    }

}