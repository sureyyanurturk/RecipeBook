package com.sureyyanurturk.recipebook

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.google.android.material.imageview.ShapeableImageView
import java.io.ByteArrayOutputStream


class AddRecipeFragment : Fragment() {

    lateinit var addRecipeBtn : Button
    lateinit var addRecipeImage : ShapeableImageView
    lateinit var addFoodName : EditText
    lateinit var addFoodKind : EditText
    lateinit var addFoodMaterials : EditText
     var pickedImg : Uri? =null
     var pickedBitmap : Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addRecipeBtn= view.findViewById(R.id.addRecipeBtn)
        addRecipeImage= view.findViewById(R.id.addRecipeImg)
        addFoodName= view.findViewById(R.id.addRecipeFName)
        addFoodKind= view.findViewById(R.id.addRecipeFKind)
        addFoodMaterials= view.findViewById(R.id.addRecipeFMaterials)

        addRecipeImage.setOnClickListener {
            addImgClick(it)
        }
        addRecipeBtn.setOnClickListener {
            addBtnClick(it)
        }

        arguments?.let {
            val cameInfo = AddRecipeFragmentArgs.fromBundle(it).info
            if (cameInfo == "withbtn"){

                addFoodName.setText("")
                addFoodKind.setText("")
                addFoodMaterials.setText("")
               addRecipeBtn.visibility= View.VISIBLE

                val pickImgBackgrnd= BitmapFactory.decodeResource(context?.resources,R.drawable.profile_placeholder)
                addRecipeImage.setImageBitmap(pickImgBackgrnd)
            }
            else{
                addRecipeBtn.visibility= View.INVISIBLE
                val pickId = AddRecipeFragmentArgs.fromBundle(it).id
                context?.let {
                    try {

                        val db = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                        val cursor= db.rawQuery("SELECT * FROM foods WHERE id= ?", arrayOf(pickId.toString()))


                        val foodNameIndex = cursor.getColumnIndex("food_name")
                        val foodKindIndex = cursor.getColumnIndex("food_kind")
                        val foodMaterialsIndex = cursor.getColumnIndex("food_materials")
                        val foodImg = cursor.getColumnIndex("img")

                        while (cursor.moveToNext()){
                            addFoodName.setText(cursor.getString(foodNameIndex))
                            addFoodKind.setText(cursor.getString(foodKindIndex))
                            addFoodMaterials.setText(cursor.getString(foodMaterialsIndex))

                            val byteArray = cursor.getBlob(foodImg)
                            val bitmap= BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            addRecipeImage.setImageBitmap(bitmap)
                        }
                        cursor.close()

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun addImgClick(view :View){

        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            } else{
                val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
            }
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode==1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==2 && resultCode == Activity.RESULT_OK && data != null){
                pickedImg= data.data
            try {
                context?.let {
                    if (pickedImg != null ){
                        if(Build.VERSION.SDK_INT>=28){
                            val source = ImageDecoder.createSource(it.contentResolver,pickedImg!!)
                            pickedBitmap=ImageDecoder.decodeBitmap(source)
                            addRecipeImage.setImageBitmap(pickedBitmap)
                        }else{
                            pickedBitmap=MediaStore.Images.Media.getBitmap(it.contentResolver,pickedImg)
                            addRecipeImage.setImageBitmap(pickedBitmap)
                        }

                    }
                }

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun addBtnClick(view :View){

        val foodName = addFoodName.text.toString()
        val foodKind = addFoodKind.text.toString()
        val foodMaterials = addFoodMaterials.text.toString()

        if(pickedBitmap != null){
            val smallBitmap= smallBitmapCreate(pickedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray= outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS foods(id INTEGER PRIMARY KEY,food_name VARCHAR, food_kind VARCHAR,food_materials VARCHAR,img BLOB)")
                    //database.execSQL("INSERT INTO foods(food_name,food_materials,img) VALUES()")
                    val sqlString = "INSERT INTO foods(food_name,food_kind,food_materials,img) VALUES(?,?,?,?)"
                    val statement= database.compileStatement(sqlString)
                    statement.bindString(1,foodName)
                    statement.bindString(2,foodKind)
                    statement.bindString(3,foodMaterials)
                    statement.bindBlob(4,byteArray)
                    statement.execute()
                }


            }catch (e: Exception){ e.printStackTrace()}
            val action=AddRecipeFragmentDirections.actionAddRecipeFragmentToFoodListFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun smallBitmapCreate(userPickBitmap : Bitmap, maxSize: Int) : Bitmap{
        var width = userPickBitmap.width
        var height = userPickBitmap.height

        val bitmapOrani : Double = width.toDouble() / height.toDouble()
        if(bitmapOrani >1){
            width=maxSize
            val shortenedHeight = width/bitmapOrani
            height= shortenedHeight.toInt()
        }else{
            height=maxSize
            val shortenedWidth = height/bitmapOrani
            width= shortenedWidth.toInt()
        }
        return Bitmap.createScaledBitmap(userPickBitmap,width,height,true)
    }

}