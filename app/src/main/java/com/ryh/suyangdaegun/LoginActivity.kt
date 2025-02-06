import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryh.suyangdaegun.MainActivity
import com.ryh.suyangdaegun.R
import com.ryh.suyangdaegun.SuyangdaegunApp
import com.ryh.suyangdaegun.auth.AuthManager

class LoginActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = (application as SuyangdaegunApp).authManager

        val googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    authManager.handleSignInResult(
                        data = result.data,
                        onSuccess = { isExistingUser, uid ->
                            if (isExistingUser) {
                                navigateToMain()
                            } else {
                                navigateToAccession(uid)
                            }
                        },
                        onFailure = { e -> Log.e("LoginActivity", "로그인 실패", e) }
                    )
                } else {
                    Log.e("LoginActivity", "Google 로그인 취소됨")
                }
            }

        setContent {
            LoginScreen(
                onGoogleSignInClick = {
                    authManager.signInWithGoogle(
                        launcher = googleSignInLauncher,
                        onFailure = { e -> Log.e("LoginScreen", "Google 로그인 실패", e) }
                    )
                },
                onKakaoSignInClick = {
                    navigateToMain() // ✅ 카카오 로그인 클릭 시 바로 메인 화면 이동
                }
            )
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // ✅ 로그인 화면 종료
    }

    private fun navigateToAccession(uid: String) {
        val intent = Intent(this, AccessionActivity::class.java).apply {
            putExtra("uid", uid)
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(onGoogleSignInClick: () -> Unit, onKakaoSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.suyang),
                contentDescription = "Logo Image",
                modifier = Modifier.size(width = 230.dp, height = 50.dp),
                contentScale = ContentScale.Inside
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "새로운 인연", fontSize = 36.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(230.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ 카카오 로그인 버튼 클릭 시 즉시 MainActivity로 이동
            Button(
                onClick = onKakaoSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F8)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kakao),
                        contentDescription = "Kakao icon",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Text("카카오로 로그인", color = Color.Black, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(23.dp))

            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F8)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "google icon",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Text("Google로 로그인", color = Color.Black, fontSize = 20.sp)
                }
            }
        }
    }
}
