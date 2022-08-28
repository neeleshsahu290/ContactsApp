package com.example.contactsapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsapp.R
import com.example.contactsapp.databinding.ContactCardBinding
import com.example.contactsapp.listners.ContactsDetailsListener
import com.example.contactsapp.models.Contacts
import com.example.contactsapp.utils.Utility


class HomeAdapter(var context: Context, var itemlist: List<Contacts>? = null,var listner: ContactsDetailsListener) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_card, parent, false)
        return HomeViewHolder(view)
    }


    fun filterList(filterllist: ArrayList<Contacts>) {
        // below line is to add our filtered
        // list in our course array list.
        itemlist = filterllist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = itemlist?.get(position)

        val index: Int = position % Utility().colorArray.size
        val color= Utility().colorArray[index]

        holder.binding.frame.setBackgroundResource(color)
        val mN= item?.middlename?:""
        val sN= item?.surname?:""

        holder.binding.personName.text= item?.firstname+" "+mN+" "+sN

        item?.let {
            holder.binding.root.setOnClickListener {
                listner.onContactsClicked(item,position,color)
            }
        }
        item?.has_profile_image?.let {
            if (it){

                Utility().setImageUrl(context,item.profile_img_url.toString(), holder.binding.personImg)

            }
        }




    }







    override fun getItemCount(): Int {
        return itemlist?.size ?: 0
    }

    inner class HomeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ContactCardBinding.bind(itemView)
    }
}