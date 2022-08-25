package com.sureyyanurturk.recipebook

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterRecyclerRecipeList(val foodList: ArrayList<String>, val foodIdList : ArrayList<Int>,val foodKindList: ArrayList<String>,val foodImgList: ArrayList<Bitmap>) : RecyclerView.Adapter<AdapterRecyclerRecipeList.MainViewHolder>(){

    class MainViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var foodImg: ImageView = itemView.findViewById(R.id.foodImage)
        var foodName: TextView = itemView.findViewById(R.id.foodName)
        var foodKind: TextView = itemView.findViewById(R.id.foodKind)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_recipes_item,parent,false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.foodName.text= foodList[position]
        holder.foodKind.text= foodKindList[position]
        holder.foodImg.setImageBitmap(foodImgList[position])


        holder.itemView.setOnClickListener {
            val action = FoodListFragmentDirections.actionFoodListFragmentToAddRecipeFragment("withrecycler",foodIdList[position])
            Navigation.findNavController(it).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}