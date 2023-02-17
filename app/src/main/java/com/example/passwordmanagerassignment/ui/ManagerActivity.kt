package com.example.passwordmanagerassignment.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanagerassignment.R
import com.example.passwordmanagerassignment.adapter.ManagerAdapter
import com.example.passwordmanagerassignment.databinding.ActivityManagerBinding
import com.example.passwordmanagerassignment.databinding.AddPassAdBinding
import com.example.passwordmanagerassignment.model.Password
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class ManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerBinding

    private lateinit var auth: FirebaseAuth

    private val managerAdapter by lazy { ManagerAdapter() }

    private lateinit var collectionRef: CollectionReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (auth.currentUser != null){
            collectionRef = Firebase.firestore
                .collection("users")
                .document(auth.currentUser?.uid!!)
                .collection("passwords")
        }

        getData()
        setRecyclerView()

        binding.fabAdd.setOnClickListener {
            addData()
        }

        managerAdapter.setOnItemClickListener {
            updateData(it)
        }

        managerAdapter.setOnLongClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Item?")
                .setNegativeButton("Cancel"){_,_->}
                .setPositiveButton("Yes"){_,_->
                    deleteData(it)
                }.create().show()
        }
    }

    private fun deleteData(password: Password) {
        collectionRef
            .whereEqualTo("website", password.website)
            .whereEqualTo("password", password.password)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach{ document->
                        collectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Item Deleted!", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
    }

    private fun setRecyclerView() {
        val recyclerView = binding.rvManager
        recyclerView.adapter = managerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getData() {
        collectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
            querySnapshot?.let {
                val passwordList = it.toObjects<Password>()
                managerAdapter.differ.submitList(passwordList)
            }
        }
    }

    private fun updateData(password: Password) {
        val bindingItem = AddPassAdBinding.inflate(LayoutInflater.from(this))
        bindingItem.edtWebsite.setText(password.website)
        bindingItem.edtPassword.setText(password.password)
        AlertDialog.Builder(this)
            .setView(bindingItem.root)
            .setTitle("Update Item")
            .setNegativeButton("Cancel"){_,_->}
            .setPositiveButton("Update"){_,_->
                val website = bindingItem.edtWebsite.text.toString()
                val webPass = bindingItem.edtPassword.text.toString()
                val passwordObj = mapOf(
                    "website" to website,
                    "password" to webPass
                )
                collectionRef
                    .whereEqualTo("website", password.website)
                    .whereEqualTo("password", password.password)
                    .get()
                    .addOnSuccessListener { querySnapshot->
                        if (querySnapshot.documents.isNotEmpty()){
                            querySnapshot.documents.forEach { documentSnapshot ->
                                collectionRef.document(documentSnapshot.id)
                                    .set(passwordObj, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Successfully Updated!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                    }
            }.create().show()
    }

    private fun addData() {

        val bindingItem = AddPassAdBinding.inflate(LayoutInflater.from(this))
        AlertDialog.Builder(this)
            .setView(bindingItem.root)
            .setTitle("New Password")
            .setNegativeButton("Cancel"){_,_->}
            .setPositiveButton("Create"){_,_->
                val website = bindingItem.edtWebsite.text.toString()
                val webPass = bindingItem.edtPassword.text.toString()
                val password = hashMapOf(
                    "website" to website,
                    "password" to webPass
                )
                collectionRef.add(password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successful!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                    }
            }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manager_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuSignOut -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}