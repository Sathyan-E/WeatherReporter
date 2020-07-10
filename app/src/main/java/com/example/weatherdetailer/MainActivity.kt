package com.example.weatherdetailer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.firebase.ui.auth.AuthUI
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    lateinit var adapter: MyStateAdapter
    lateinit var viewPager2: ViewPager2
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var authStateListener:FirebaseAuth.AuthStateListener
   // lateinit var menu:Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       firebaseAuth=FirebaseAuth.getInstance()


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
        firebaseAuth.addAuthStateListener { authStateListener }
    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener {authStateListener }
    }

    override fun onStart() {
        super.onStart()
        authStateListener=FirebaseAuth.AuthStateListener {
                firebaseAuth ->
            val muser=firebaseAuth.currentUser
            if (muser!=null){
                Toast.makeText(this,"You're now signed in.",Toast.LENGTH_SHORT).show()
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



}