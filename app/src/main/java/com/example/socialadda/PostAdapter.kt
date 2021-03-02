package com.example.socialadda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialadda.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//this adapter is used to setup the data of the main activity
class PostAdapter(options: FirestoreRecyclerOptions<Post>,val listener: IPostAdapter) : FirestoreRecyclerAdapter<Post,PostAdapter.PostViewHolder>(
    options
) {

    //create a view holder class which contains all the views we have defined in our layout
    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
    }

    //this will create a instance of the view holder class and return it. it also setup a click listener to the like button with the help of the interface we have defined.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))
        viewHolder.likeButton.setOnClickListener{
            //call the onLikeClicked from here and pass the post id from the snapshots
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    //this will bind all the views in our layout with the data
    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        //set the post text as it is from the firebase
        holder.postText.text = model.text
        //get the user name and then set it into the user text
        holder.userText.text = model.createdBy.displayName
        //get the user image and then display it using the Glide library into our user image view
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        //set the liked count from the size of likedBy array
        holder.likeCount.text = model.likedBy.size.toString()
        //format the time into our desired format and then show it to our createdAt textView
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)

        //get the instance of the auth
        val auth = Firebase.auth
        //get the current user id and assert that it is not null
        val currentUserId = auth.currentUser!!.uid
        //if it is liked then show it's color in red, colorless otherwise
        val isLiked = model.likedBy.contains(currentUserId)
        if (isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked_icon))
        }else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_unliked_icon))
        }
    }
}

//this interface is used to handle the click listener to the like button by taking the post id as argument
interface IPostAdapter{
    fun onLikeClicked(postId: String)
}