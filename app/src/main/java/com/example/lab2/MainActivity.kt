package com.example.lab2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

// --- 1. Реалізація ролей через sealed class ---
// Використовуємо sealed class, як зазначено у завданні (можна було б і enum)
sealed class Role {
    object Admin : Role() { override fun toString() = "Admin" }
    object Manager : Role() { override fun toString() = "Manager" }
    object User : Role() { override fun toString() = "User" }
}

// --- 2. Data Class для користувача ---
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val roles: List<Role>
)

// --- 3. Extension function для валідації email ---
// Розширюємо стандартний клас String новою функцією
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
    return this.matches(emailRegex)
}

// --- 4. Singleton для управління користувачами ---
// Object в Kotlin — це вбудована реалізація патерну Singleton
object UserManager {
    private val users = mutableListOf<User>()

    // Функція генерації безпечного пароля
    fun generatePassword(length: Int = 12): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        return (1..length)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }

    // Додавання користувача з перевіркою email
    fun addUser(name: String, email: String, roles: List<Role>) {
        if (email.isValidEmail()) {
            val id = users.size + 1
            val newUser = User(id, name, email, roles)
            users.add(newUser)
            Log.d("UserManager", "Користувач успішно доданий: $newUser")
        } else {
            Log.e("UserManager", "Помилка: Невірний формат email для користувача $name")
        }
    }

    // Отримати список всіх користувачів
    fun getUsers(): List<User> {
        return users
    }
}

// --- 5. Main Activity (Точка входу) ---
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Якщо у вас Empty Views Activity, тут буде R.layout.activity_main
        setContentView(R.layout.activity_main)

        // --- ТЕСТУВАННЯ СИСТЕМИ (Дивіться результат у Logcat) ---
        Log.i("Lab2_Test", "--- Початок тестування ---")

        // 1. Генеруємо пароль
        val newPassword = UserManager.generatePassword()
        Log.i("Lab2_Test", "Згенерований пароль: $newPassword")

        // 2. Спробуємо додати валідного користувача (Admin)
        UserManager.addUser(
            name = "Олексій",
            email = "alex@gmail.com",
            roles = listOf(Role.Admin)
        )

        // 3. Спробуємо додати користувача з неправильним email (Має бути помилка)
        UserManager.addUser(
            name = "Іван",
            email = "ivan-bez-sobaky.com",
            roles = listOf(Role.User)
        )

        // 4. Додаємо менеджера
        UserManager.addUser(
            name = "Марія",
            email = "maria.manager@work.ua",
            roles = listOf(Role.Manager, Role.User) // Дві ролі
        )

        // 5. Виводимо список всіх користувачів у системі
        Log.i("Lab2_Test", "--- Список користувачів у системі ---")
        val allUsers = UserManager.getUsers()
        allUsers.forEach { user ->
            Log.i("Lab2_Test", "ID: ${user.id}, Ім'я: ${user.name}, Ролі: ${user.roles}")
        }
    }
}