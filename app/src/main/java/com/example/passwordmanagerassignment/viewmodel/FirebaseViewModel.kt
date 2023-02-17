package com.example.passwordmanagerassignment.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passwordmanagerassignment.Resource
import com.example.passwordmanagerassignment.model.Password
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class FirebaseViewModel: ViewModel() {
    private val _getPasswordList = MutableLiveData<Resource<List<Password>>>()
    val getPasswordList: LiveData<Resource<List<Password>>>
        get() = _getPasswordList

    private val _addPassword = MutableLiveData<Resource<String>>()
    val addPassword: LiveData<Resource<String>>
        get() = _addPassword

    private val _updatePassword = MutableLiveData<Resource<String>>()
    val updatePassword: LiveData<Resource<String>>
        get() = _updatePassword

    private val _deletePassword = MutableLiveData<Resource<String>>()
    val deletePassword: LiveData<Resource<String>>
        get() = _deletePassword

    private val auth = Firebase.auth
    private val collectionRef = Firebase.firestore
        .collection("users")
        .document(auth.currentUser?.uid!!)
        .collection("passwords")

    fun getPasswords(){
        _getPasswordList.value = Resource.loading()
        collectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let {
                _getPasswordList.value = Resource.error(error.message.toString())
            }
            querySnapshot?.let {
                _getPasswordList.value= Resource.success(it.toObjects())
            }
        }
    }

    fun addPassword(password: HashMap<String, String>){
        _addPassword.value = Resource.loading()
        collectionRef.add(password)
            .addOnSuccessListener {
                _addPassword.value = Resource.success("Successfully Added!")
            }
            .addOnFailureListener{
                _addPassword.value = Resource.error(it.message.toString())
            }
    }

    fun updatePassword(password: Password, passwordObj: Map<String, String>){
        _updatePassword.value = Resource.loading()
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
                                _updatePassword.value = Resource.success("Successfully Updated!")
                            }
                            .addOnFailureListener {
                                _updatePassword.value = Resource.error(it.message.toString())
                            }
                    }
                }
            }
    }

    fun deletePassword(password: Password){
        _deletePassword.value = Resource.loading()
        collectionRef
            .whereEqualTo("website", password.website)
            .whereEqualTo("password", password.password)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()){
                    it.forEach{ document->
                        collectionRef.document(document.id).delete()
                            .addOnSuccessListener {
                                _deletePassword.value = Resource.success("Successfully Deleted")
                            }
                            .addOnFailureListener {
                                _deletePassword.value = Resource.error("Failed!")
                            }
                    }
                }
            }
    }
}