package com.vivant.annecharlotte.happydreche

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.database.*
import com.vivant.annecharlotte.happydreche.firestore.Project

class ListActivity : AppCompatActivity() {

    lateinit var ref: DatabaseReference
    lateinit var projectsList: MutableList<Project>
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        listView = findViewById(R.id.listing_all_projects)

        projectsList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("projects")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    projectsList.clear()

                    for(h in p0.children) {
                        val project = h.getValue(Project::class.java)
                        projectsList.add(project!!)
                    }
                    val adapter = ListingProjectsAdapter(applicationContext, R.layout.project_list_item, projectsList)
                    listView.adapter = adapter
                }
            }
        })
    }
}
