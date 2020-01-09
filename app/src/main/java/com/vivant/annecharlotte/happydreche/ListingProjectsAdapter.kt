package com.vivant.annecharlotte.happydreche

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.vivant.annecharlotte.happydreche.firestore.Project

/**
 * Created by Anne-Charlotte Vivant on 06/01/2020.
 */
class ListingProjectsAdapter(val mCtx : Context, val layoutResId: Int, val projectsList: List<Project>)
    :ArrayAdapter<Project>(mCtx, layoutResId, projectsList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val projectName = view.findViewById<TextView>(R.id.project_item_name)
        val projectTown = view.findViewById<TextView>(R.id.project_item_town)
        val projectZipcode = view.findViewById<TextView>(R.id.project_item_zipcode)
        val projectIcon = view.findViewById<ImageView>(R.id.project_item_icon)

        val project = projectsList[position]

        projectName.text = project.projectName
        projectTown.text = project.projectAddressTown
        projectZipcode.text = project.projectAddressZipcode

        when (project.projectType) {
            1 -> projectIcon.setImageDrawable(view.resources.getDrawable(R.drawable.ic_shopping))
            2 -> projectIcon.setImageDrawable(view.resources.getDrawable(R.drawable.ic_restaurant))
            3 -> projectIcon.setImageDrawable(view.resources.getDrawable(R.drawable.ic_upcycle))
            4 -> projectIcon.setImageDrawable(view.resources.getDrawable(R.drawable.ic_search))
        }
        return view
    }
}