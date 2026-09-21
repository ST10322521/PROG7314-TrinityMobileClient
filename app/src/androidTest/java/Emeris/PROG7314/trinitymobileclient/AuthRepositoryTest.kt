package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthError
import Emeris.PROG7314.trinitymobileclient.auth.AuthException
import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AuthRepositoryTest {
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        auth = Firebase.auth
        auth.useEmulator("127.0.0.1",9099)

        auth.signOut()

        repository = AuthRepository(auth)
    }

    @Test
    fun registerUser_successfullyCreatedUser() {
        val email = "testuser_${System.currentTimeMillis()}@gmail.com"
        val password = "Password!2"

        val latch = CountDownLatch(1)
        var result: Result<FirebaseUser>? = null

        repository.registerUser(email,password) {
            result = it
            latch.countDown()
        }

        assertTrue("Firebase registration timed out", latch.await(5, TimeUnit.SECONDS))
        assertNotNull(result)
        assertTrue("Register failed: ${result!!.exceptionOrNull()}",result!!.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun registerUser_duplicateEmail_returnsEmailAlreadyInUser(){
        val email = "duplicate_test_${System.currentTimeMillis()}@gmail.com"
        val password = "Password!2"

        val latch = CountDownLatch(1)
        var result: Result<FirebaseUser>? = null

        repository.registerUser(email,password) {
            result = it
            latch.countDown()
        }

        assertTrue("Firebase registration timed out", latch.await(5, TimeUnit.SECONDS))
        assertTrue("Register failed: ${result!!.exceptionOrNull()}",result!!.isSuccess)

        auth.signOut()

        val secondLatch = CountDownLatch(1)
        var secondResult: Result<FirebaseUser>? = null

        repository.registerUser(email,password) {
            secondResult = it
            secondLatch.countDown()
        }

        assertTrue("Firebase registration timed out", secondLatch.await(5, TimeUnit.SECONDS))
        assertNotNull(secondResult)
        assertTrue("Expected duplicate registration to fail", secondResult!!.isFailure)
        assertTrue("Expected EMAIL_ALREADY_IN_USE but got: ${secondResult!!.exceptionOrNull()}", secondResult!!.exceptionOrNull() is AuthException)
        assertEquals(AuthError.EMAIL_ALREADY_IN_USE, (secondResult!!.exceptionOrNull() as AuthException).error)
    }

    @Test
    fun loginUser_successfullyLogsInUser(){
        val email = "login_test_${System.currentTimeMillis()}@gmail.com"
        val password = "Password!2"

        val registerLatch = CountDownLatch(1)

        repository.registerUser(email,password) {
            registerLatch.countDown()
        }

        assertTrue("Firebase registration timed out", registerLatch.await(5, TimeUnit.SECONDS))

        auth.signOut()

        val loginLatch = CountDownLatch(1)
        var result: Result<FirebaseUser>? = null

        repository.loginUser(email,password) {
            result = it
            loginLatch.countDown()
        }

        assertTrue("Firebase login timed out", loginLatch.await(5, TimeUnit.SECONDS))
        assertNotNull(result)
        assertTrue("Login failed: ${result!!.exceptionOrNull()}",result!!.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun loginUser_invalidPassword_returnsInvalidCredentials(){
        val email = "invalid_password_${System.currentTimeMillis()}@gmail.com"
        val correctPassword = "Password!2"
        val wrongPassword = "WrongPassword!2"

        val registerLatch = CountDownLatch(1)

        repository.registerUser(email,correctPassword) {
            registerLatch.countDown()
        }

        assertTrue("Firebase registration timed out", registerLatch.await(5, TimeUnit.SECONDS))

        auth.signOut()

        val loginLatch = CountDownLatch(1)
        var result: Result<FirebaseUser>? = null

        repository.loginUser(email,wrongPassword) {
            result = it
            loginLatch.countDown()
        }

        assertTrue("Firebase login timed out", loginLatch.await(5, TimeUnit.SECONDS))
        assertNotNull(result)
        assertTrue("Expect Login to fail",result!!.isFailure)
        assertTrue("Expected AuthException but got: ${result.exceptionOrNull()}", result!!.exceptionOrNull() is AuthException)
        assertEquals(AuthError.INVALID_CREDENTIALS, (result!!.exceptionOrNull() as AuthException).error)
    }

    @Test
    fun loginUser_nonExistentAccount_returnsInvalidCredentials() {
        val email = "nonexistent_${System.currentTimeMillis()}@gmail.com"
        val password = "Password!2"

        val loginLatch = CountDownLatch(1)
        var result: Result<FirebaseUser>? = null

        repository.loginUser(email, password) {
            result = it
            loginLatch.countDown()
        }

        assertTrue(
            "Firebase login timed out",
            loginLatch.await(5, TimeUnit.SECONDS)
        )

        assertNotNull(result)

        assertTrue(
            "Expected login to fail",
            result!!.isFailure
        )

        assertTrue(
            "Expected AuthException but got: ${result!!.exceptionOrNull()}",
            result!!.exceptionOrNull() is AuthException
        )

        assertEquals(
            AuthError.INVALID_CREDENTIALS,
            (result!!.exceptionOrNull() as AuthException).error
        )
    }

    @Test
    fun signInWithSso_invalidToken_returnsInvalidCredentials() {
        val invalidToken = "invalid-google-id-token"

        val latch = CountDownLatch(1)
        var result: Result<FirebaseUser>? = null

        repository.signInWithSso(invalidToken) {
            result = it
            latch.countDown()
        }

        assertTrue(
            "Firebase SSO login timed out",
            latch.await(5, TimeUnit.SECONDS)
        )

        assertNotNull(result)

        assertTrue(
            "Expected SSO login to fail",
            result!!.isFailure
        )

        assertTrue(
            "Expected AuthException but got: ${result!!.exceptionOrNull()}",
            result!!.exceptionOrNull() is AuthException
        )

        assertEquals(
            AuthError.INVALID_CREDENTIALS,
            (result!!.exceptionOrNull() as AuthException).error
        )
    }
}