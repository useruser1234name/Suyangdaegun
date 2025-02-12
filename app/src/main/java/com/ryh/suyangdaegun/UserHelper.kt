import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserHelper {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //  특정 UID의 유저 닉네임 가져오기
    fun getParticipantName(userUid: String, callback: (String) -> Unit) {
        firestore.collection("users").document(userUid).get()
            .addOnSuccessListener { snapshot ->
                val nickname = snapshot.getString("nickname") ?: "알 수 없음"
                callback(nickname)
            }
    }

    //  현재 로그인한 유저의 닉네임 가져오기
    fun getCurrentUserNickname(callback: (String) -> Unit) {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            callback("알 수 없음")
            return
        }

        firestore.collection("users").document(currentUserUid).get()
            .addOnSuccessListener { snapshot ->
                val nickname = snapshot.getString("nickname") ?: "알 수 없음"
                callback(nickname)
            }
    }
}
