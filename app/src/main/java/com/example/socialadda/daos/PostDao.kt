package com.example.socialadda.daos

import com.example.socialadda.models.Post
import com.example.socialadda.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {

    //create instance of the database
    private val db = FirebaseFirestore.getInstance()
    //get the collection from the database, if there will be no such collection then it will be created automatically
    val postCollection = db.collection("posts")
    //create an auth instance for finding the user data
    private val auth = Firebase.auth

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

    //this method will take the post id and then return the task of the post that can be converted into the post later on
    private fun getPostById(postId: String): Task<DocumentSnapshot>{
        return postCollection.document(postId).get()
    }

    //this method will update likes on the post by taking the post id as argument
    fun updateLikes(postId: String){
        //do this task in the background thread
        GlobalScope.launch {
            //get the current user id from the auth and assert that this user id will not be null
            val currentUserId = auth.currentUser!!.uid
            //create the instance of the post by converting the task received from the getPostId method
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            //create boolean for like button from the post by passing the current user id
            val isLiked = post.likedBy.contains(currentUserId)
            //if post is liked then remove it from the array else add it to the array of likedBy
            if (isLiked){
                post.likedBy.remove(currentUserId)
            }else{
                post.likedBy.add(currentUserId)
            }
            //update the post in the firebase now
            postCollection.document(postId).set(post)
        }
    }

}