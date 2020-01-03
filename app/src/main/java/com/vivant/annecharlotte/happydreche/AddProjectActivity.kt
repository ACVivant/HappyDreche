package com.vivant.annecharlotte.happydreche

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vivant.annecharlotte.happydreche.firestore.Project

class AddProjectActivity : AppCompatActivity() {

    val TAG = "AddProjectActivity"
    lateinit var addNameText: EditText
    lateinit var addBossText: EditText
    lateinit var addWebsiteText: EditText
    lateinit var addEmailText: EditText
    lateinit var addTelText: EditText
    lateinit var addDescriptionText: EditText
    lateinit var addPictureUrl: EditText
    lateinit var addNumberAddressText: EditText
    lateinit var addStreetAddressText: EditText
    lateinit var addStreet2AddressText: EditText
    lateinit var addZipcodeAddressText: EditText
    lateinit var addTownAddressText: EditText
    lateinit var addCountryAddressText: EditText

    lateinit var addSpinnerType: Spinner

    lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)

        addNameText = findViewById(R.id.add_name_text)
        addBossText = findViewById(R.id.add_boss_text)
        addWebsiteText = findViewById(R.id.add_website_text)
        addEmailText = findViewById(R.id.add_email_text)
        addTelText = findViewById(R.id.add_tel_text)
        addDescriptionText = findViewById(R.id.add_description_text)
        addPictureUrl = findViewById(R.id.add_picture_url)
        addNumberAddressText = findViewById(R.id.add_address_number)
        addStreetAddressText = findViewById(R.id.add_address_street)
        addStreet2AddressText = findViewById(R.id.add_address_street2)
        addZipcodeAddressText = findViewById(R.id.add_address_zipcode)
        addTownAddressText = findViewById(R.id.add_address_zipcode)
        addCountryAddressText = findViewById(R.id.add_address_country)

        addSpinnerType = findViewById(R.id.add_spinner_type)

        saveBtn = findViewById(R.id.add_save_button)
        saveBtn.setOnClickListener { saveProject() }

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.add_project_type, android.R.layout.simple_spinner_item
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addSpinnerType.setAdapter(adapter)
       // addSpinnerType.setOnItemSelectedListener()
    }

    private fun saveProject() {

        val nameText = addNameText.text.toString().trim()
        val bossText = addBossText.text.toString().trim()
        val websiteText = addWebsiteText.text.toString().trim()
        val emailText = addEmailText.text.toString().trim()
        val telText = addTelText.text.toString().trim()
        val descriptionText = addDescriptionText.text.toString().trim()
        val pictureUrlText = addPictureUrl.text.toString().trim()
        val numberAddressText = addNumberAddressText.text.toString().trim()
        val streetAddressText = addStreetAddressText.text.toString().trim()
        val street2AddressText = addStreet2AddressText.text.toString().trim()
        val zipcodeAddressText = addZipcodeAddressText.text.toString().trim()
        val townAddressText = addTownAddressText.text.toString().trim()
        val countryAddressText = addCountryAddressText.text.toString().trim()

        if (nameText.isEmpty() || descriptionText.isEmpty() || streetAddressText.isEmpty() || zipcodeAddressText.isEmpty() || townAddressText.isEmpty() || countryAddressText.isEmpty()) {
            Toast.makeText(applicationContext,resources.getString(R.string.save_error), Toast.LENGTH_LONG).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("projects")
        val newProjectId = ref.push().key
        val newProject = Project(
            newProjectId!!,
            nameText, bossText, emailText, websiteText, telText, descriptionText, addSpinnerType.selectedItemPosition, pictureUrlText,
            numberAddressText, street2AddressText, street2AddressText, zipcodeAddressText, townAddressText, countryAddressText)

        ref.child(newProjectId!!).setValue(newProject).addOnCompleteListener{
            Toast.makeText(applicationContext, resources.getString(R.string.save_completed), Toast.LENGTH_LONG).show()
        }


    }
}
