package com.sureyyanurturk.recipebook

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.findFragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FoodListFragment : Fragment() {

    lateinit var toAddPageBtn : FloatingActionButton
    var foodNameList = ArrayList<String>()
    var foodKindList = ArrayList<String>()
    var foodImgList = ArrayList<Bitmap>()
    var foodIdList = ArrayList<Int>()
    lateinit var listAdapter: AdapterRecyclerRecipeList
    lateinit var recyclerViewRecipe: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       toAddPageBtn = view.findViewById<FloatingActionButton?>(R.id.floatingActionButton)
        toAddPageBtn.setOnClickListener {
            val action = FoodListFragmentDirections.actionFoodListFragmentToAddRecipeFragment("withbtn",0)
            Navigation.findNavController(it).navigate(action)
             }

        recyclerViewRecipe= view.findViewById(R.id.recyclerViewRecipe)
        listAdapter= AdapterRecyclerRecipeList(foodNameList,foodIdList,foodKindList,foodImgList)
        recyclerViewRecipe.layoutManager= LinearLayoutManager(context)
        recyclerViewRecipe.adapter= listAdapter

         sqlDataTake()
        }

    fun sqlDataTake(){
        try {
            activity?.let {
                val database =it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                val cursor = database.rawQuery("SELECT * FROM foods", null)
                val foodNameIndex = cursor.getColumnIndex("food_name")
                val foodKindIndex = cursor.getColumnIndex("food_kind")
                val foodImgIndex = cursor.getColumnIndex("img")
                val foodIdIndex = cursor.getColumnIndex("id")

                foodNameList.clear()
                foodKindList.clear()
                foodImgList.clear()
                foodIdList.clear()

                while (cursor.moveToNext()){
                   foodNameList.add(cursor.getString(foodNameIndex))
                   foodKindList.add(cursor.getString(foodKindIndex))
                    foodIdList.add(cursor.getInt(foodIdIndex))
                    val byteArray = cursor.getBlob(foodImgIndex)
                    val bitmap= BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                    foodImgList.add(bitmap)
                }

                listAdapter.notifyDataSetChanged()
                cursor.close()

            }
        }catch (e:Exception) {e.printStackTrace()}
    }
}



