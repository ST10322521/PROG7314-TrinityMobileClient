package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.ui.LoginFragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // pad for the status/nav bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginFragmentContainer)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.loginFragmentContainer,
                    LoginFragment()
                )
                .commit()
        }
    }
}
