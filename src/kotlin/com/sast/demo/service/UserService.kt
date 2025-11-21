package com.sast.demo.service

import com.sast.demo.model.User
import org.springframework.stereotype.Service

@Service
class UserService {
    // Hardcoded secret for SAST testing
    private val apiKey = "sk_test_1234567890abcdef" // SAST should flag this
    private val github_secret = "ghp_A1bC2dE3fH4iJ5kL6mN7oP8qR9sT0u" // This is random PAT and not real PAT to test secret detection capabilities of SAST
    private val github_secret1 = "github_pat_01A2b3C4d5E6f7G8h9I0jK1lM2nO3pQ4rS5t" // This is random PAT and not real PAT to test secret detection capabilities of SAST


    fun getUserById(id: String): User {
        // Insecure SQL query for SAST testing
        val query = "SELECT * FROM users WHERE id = '$id'" // SAST should flag this
        // Real SQL injection vulnerability demonstration
        try {
            val conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery(query) // SAST should flag this as a real SQLi sink
            // Simulate DB fetch from result set
            if (rs.next()) {
                return User(rs.getString("id"), rs.getString("username"), rs.getString("password"))
            }
            rs.close()
            stmt.close()
            conn.close()
        } catch (e: Exception) {
            println("SQL error: ${e.message}")
        }
        // Fallback if DB fails
        return User(id, "user$id", "password$id")
    }




    // Simulated SQL sink function
    private fun executeQuery(query: String) {
        // SAST tools should flag this as a sink
        println("Executing query: $query")
    }

    fun getApiKey(): String = apiKey
}
