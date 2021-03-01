package com.example.socialadda.daos

import com.example.socialadda.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//this dao is used to fetch the data
class UserDao {

    //instance of the database which is stored at firebase
    private val db = FirebaseFirestore.getInstance()
    //there are many collections at that database , so we have chosen users collection
    private val usersCollection = db.collection("users")

    //this method will add user to the database
    fun addUser(user: User?){
        //just a null check
        user?.let {
            //do this database work in the background thread
            GlobalScope.launch {
                //change its id to use user's uid and add it
                usersCollection.document(user.uid).set(it)
            }
        }
    }
}