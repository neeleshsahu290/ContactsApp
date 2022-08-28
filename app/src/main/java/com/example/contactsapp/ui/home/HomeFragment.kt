package com.example.contactsapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactsapp.R
import com.example.contactsapp.activity.CreateNewActivity
import com.example.contactsapp.databinding.ContactDetailsBinding
import com.example.contactsapp.databinding.FragmentHomeBinding
import com.example.contactsapp.listners.ContactsDetailsListener
import com.example.contactsapp.models.Contacts
import com.example.contactsapp.utils.ContactData
import com.example.contactsapp.utils.Utility
import com.example.contactsapp.utils.retrieveAllContacts
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog


class HomeFragment : Fragment(), ContactsDetailsListener {

    private var _binding: FragmentHomeBinding? = null
    lateinit var viewmodel: HomeViewModel

    lateinit var contactList: ArrayList<Contacts>
     var homeAdapter: HomeAdapter?=null

   /* lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private var isCallPermissionGranted=false
    private var isReadContactsPermissionGranted=false*/
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setValues()
        viewmodel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        getCollapsableBar()

        setListners()
       // fetchPhoneContact()


        /*   val textView: TextView = binding.textHome
           homeViewModel.text.observe(viewLifecycleOwner) {
               textView.text = it
           }*/

        Log.d("neeleshhello","helloworld")
        return root
    }

    private fun setValues() {


        isLoading(true)
        contactList= ArrayList()
        binding.modelrecycler.apply {
            layoutManager = LinearLayoutManager(context)
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("MissingPermission")
    private fun fetchPhoneContact() {
        val mobConList: List<ContactData> = requireContext().retrieveAllContacts()
        var contactList: ArrayList<Contacts> = ArrayList()
        for (i in mobConList.indices) {
            val con = Contacts(
                firstname = mobConList[i].name,
                profile_img_url = mobConList[i].avatar?.path,
                phone = mobConList[i].phoneNumber.toString()
            )
            contactList.add(con)
        }


    }



    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<Contacts> = ArrayList()

        // running a for loop to compare elements.
        for (item in contactList) {
            // checking if the entered string matched with any item of our recycler view.
            item.firstname?.let {
                if (item.firstname.toLowerCase().contains(text.toLowerCase())) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredlist.add(item)
                }
            }

        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            // Toast.makeText(requireContext(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.

            homeAdapter?.filterList(filteredlist)
        }
    }
    private fun getViewModelData(context: Context) {
        viewmodel.familyModels(requireActivity())
        viewmodel.familyMembers.observe(viewLifecycleOwner) { list ->
            viewmodel.hasUser.observe(viewLifecycleOwner) {
                Log.d("has_user_data",it.toString())
                val hasUser = it
                if (hasUser) {
                    if (list != null) {
                        if (list.size > 0) {
                             contactList = list
                            homeAdapter = HomeAdapter(context, list, this@HomeFragment)
                            binding.modelrecycler.adapter=homeAdapter
                            isLoading(false)


                        }
                    }

                } else {
                    isEmptyUsers(true)
                    isLoading(false)


                }
            }

        }
    }

    private  fun getCollapsableBar(){
        val collapsingToolbar = binding.collapsingToolbar
        binding.appbarlayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (collapsingToolbar.height + verticalOffset < collapsingToolbar.scrimVisibleHeightTrigger) {
          binding.txtcontactscoll.text= ""
                binding.txtcontactbar.text="Contacts"
            } else {
                binding.txtcontactscoll.text= "Contacts"
                binding.txtcontactbar.text=""



            }
        })
    }

    private fun isLoading(loading: Boolean) {
        binding.loading.loadinglyt.isVisible = loading
    }

    private fun isEmptyUsers(users: Boolean) {
        binding.noUser.txtNoUser.text = "No Contact Found"
        binding.noUser.noUserLayout.isVisible = users
    }

    private fun setListners() {
        binding.fab.setOnClickListener {
            startActivity(Intent(activity, CreateNewActivity::class.java))
        }

    /*    val searchView = SearchView(this)
        val editText = binding.searchbtn.edit.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(Color.BLACK)
*/
        binding.searchbtn.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(msg)
                return false
            }
        })
        /*  binding.addmember.setOnClickListener {
              val intent= Intent(activity,AddNewMemberActivity::class.java)
              startActivity(intent)
          }
          binding.profileImg.setOnClickListener {
              UtiltyView().setProfileViewPopup(binding.profileImg,requireContext())
          }*/
    }

    @SuppressLint("MissingPermission")
    private fun contactDetailsDialog(contacts: Contacts,color: Int) {
        val contactDetailsSheet = ContactDetailsBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(contactDetailsSheet.root)

        val mN= contacts?.middlename?:""
        val sN= contacts?.surname?:""
        contactDetailsSheet.CTName.text =
            contacts?.firstname + " " + mN + " " + sN

        val contactNo = contacts?.phone
        contactDetailsSheet.phoneMO.text= contactNo
        contactDetailsSheet.frame.setBackgroundResource(color)

        contactDetailsSheet.backbtn.setOnClickListener {
            dialog.dismiss()
        }
        contacts.has_profile_image?.let {
            if (it) {
                Utility().setImageUrl(
                    requireContext(),
                    contacts.profile_img_url.toString(),
                    contactDetailsSheet.DTImg
                )
            }
        }

        contactDetailsSheet.llcall.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + "$contactNo")
            this@HomeFragment.startActivity(intent)
        }
        contactDetailsSheet.llmessage.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$contactNo"))
            // intent.putExtra("sms_body", message)
            this@HomeFragment.startActivity(intent)
        }

        contactDetailsSheet.more.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(),contactDetailsSheet.more)
            popupMenu.menuInflater.inflate(R.menu.card_more,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.viewcontact->{
                        Toast.makeText(activity, "You Clicked : View Item", Toast.LENGTH_SHORT).show()

                    }
                    R.id.edit -> {
                        Toast.makeText(activity, "You Clicked : Edit Item", Toast.LENGTH_SHORT).show()

                    }
                    R.id.delete ->
                        Toast.makeText(activity, "You Clicked : Delete Item", Toast.LENGTH_SHORT).show()

                }
                true
            }
            popupMenu.show()
        }


// or to retrieve all contacts matching specific search pattern:
        //   List<ContactData> contacts = ContactUtilsKt.retrieveAllContacts(context, "John"


        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        getViewModelData(requireActivity())

    }

    override fun onContactsClicked(contacts: Contacts, index: Int, color: Int) {
        contactDetailsDialog(contacts,color)
    }
}