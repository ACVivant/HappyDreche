package com.vivant.annecharlotte.happydreche

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.TextValueSanitizer
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    lateinit var essaiTV:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        essaiTV=findViewById(R.id.essai)

        val myText:String =intent.extras.get("projectId").toString()
        essaiTV.text=myText
    }

}
