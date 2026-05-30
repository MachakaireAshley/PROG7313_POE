package com.example.prog7313_poe

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MemberRepo(private val memberDao: MemberDao, private val userId: String) {

    private val database= FirebaseDatabase.getInstance()
        .getReference("users")
        .child(userId)
        .child("members")


    fun getMembers(): Flow<List<Member>> {
        return memberDao.getMembersByUser(userId)
    }

    suspend fun saveMember(member: Member){
        val updatedMember= member.copy(
            lastUpdated= System.currentTimeMillis()
        )

        val generatedId= memberDao.insert(updatedMember)

        val memberForFireBase= updatedMember.copy(id= generatedId.toInt())

        database.child(generatedId.toString()).setValue(memberForFireBase)
    }


    fun listenForCloudChanges(){
        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val localMembers= memberDao.getAll(userId).associateBy { it.id }

                    for(memberSnapshot in snapshot.children){
                        val firebaseMembers= memberSnapshot.getValue(Member::class.java)
                        firebaseMembers?.let {
                                cloudMember->
                            val localMember = localMembers[cloudMember.id]

                            if(localMember==null){
                                memberDao.insert(cloudMember)
                                android.util.Log.d("MemberRepo", "New member inserted: ${cloudMember.name}")
                            }
                            else if(cloudMember.lastUpdated>localMember.lastUpdated){
                                memberDao.update(cloudMember)
                                android.util.Log.d("CategoryRepo", "member Updated: ${cloudMember.name}")
                            }
                        }
                    }

                }
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                android.util.Log.e("MemberRepo", "Firebase listen cancelled", error.toException())
            }

        })
    }



}