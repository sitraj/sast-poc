package com.sast.demo.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserServiceTest {
    private val userService = UserService()

    @Test
    fun testGetUserById() {
        val user = userService.getUserById("1")
        assertEquals("1", user.id)
        assertEquals("user1", user.username)
    }

    @Test
    fun testGetApiKey() {
        val apiKey = userService.getApiKey()
        assertEquals("sk_test_1234567890abcdef", apiKey)
    }
}
