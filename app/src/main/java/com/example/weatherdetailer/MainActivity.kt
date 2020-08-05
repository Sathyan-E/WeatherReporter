package com.example.weatherdetailer

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherdetailer.adapter.MyStateAdapter
import com.firebase.ui.auth.AuthUI
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    lateinit var adapter: MyStateAdapter
    lateinit var viewPager2: ViewPager2
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var authStateListener:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       firebaseAuth=FirebaseAuth.getInstance()
        supportActionBar


        //binding views
       val tablayout:TabLayout =findViewById(R.id.tabs)
       viewPager2=findViewById(R.id.viewpager2)
       //instantiating adapter
       adapter= MyStateAdapter(this)
       //setting adapter to viewpager2
       viewPager2.adapter=adapter
       //By default the user preference is celsius
       save("unit","celsius")
       //Array titles for tabs
       val num= arrayOf("Current","Date","Report","Preference")
       //Tab layout mediator
       TabLayoutMediator(tablayout,viewPager2){tab, position ->
           tab.text=num[position]
       }.attach()
   }


    override fun onDestroy() {
        super.onDestroy()
        //Clear all the preferences
        val  sharedPreferences=getSharedPreferences("weather", Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
    //Method for saving data in Sharedpreferences
    private  fun save( key:String,value:String){

        val  sharedPreferences=getSharedPreferences("weather",Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()

    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.addAuthStateListener(authStateListener)

    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    override fun onStart() {
        super.onStart()
        authStateListener=FirebaseAuth.AuthStateListener {
                firebaseAuth ->
            val muser=firebaseAuth.currentUser
            if (muser!=null){
                Log.i("Main Activity","User is already signed in")
            }else{
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .build(),10
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==10){
            if (resultCode== Activity.RESULT_OK){
                Toast.makeText(this,"You're Signed In",Toast.LENGTH_SHORT).show()
            }
            else if (resultCode== Activity.RESULT_CANCELED){
                Toast.makeText(this,"Sign In Cancelled",Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_options,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId==R.id.sign_out){

            AuthUI.getInstance().signOut(this)
            Toast.makeText(this,"Sign out button clciked",Toast.LENGTH_SHORT).show()
        }
        else if(item.itemId==R.id.refresh){
            Toast.makeText(this,"Refresh is clicked",Toast.LENGTH_SHORT).show()
          //  adapter.notifyDataSetChanged()
        }
        return true
    }
}