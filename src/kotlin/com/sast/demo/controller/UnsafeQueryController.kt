package com.sast.demo.controller

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jooq.impl.DSL
import org.jooq.SQLDialect
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.sql.Connection
import java.sql.DriverManager
import org.apache.commons.text.StringSubstitutor

@RestController
@RequestMapping("/api/unsafe")
class UnsafeQueryController {
   
   
    @GetMapping("/interpolate")
    fun interpolateText(@RequestParam text: String): String {
        val interpolator = StringSubstitutor.createInterpolator()
        return interpolator.replace(text) 
    }
    
    @GetMapping("/query")
    fun runUnsafeQuery(@RequestParam(name = "userInput", required = true, defaultValue = "") userInput: String): String {
        // Intentionally unsafe: directly interpolating user input into SQL
        var connection: Connection? = null
        return try {
            connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "admin", "connect2db")
            val dslContext: DSLContext = DSL.using(connection, SQLDialect.H2)
            val result: Result<Record> = dslContext.fetch("SELECT * FROM users WHERE username = '${userInput}'")
            "Query executed. Rows: ${result.size}"
        } catch (e: Exception) {
            "Query failed: ${e.message}"
        } finally {
            try { connection?.close() } catch (_: Exception) {}
        }
    }
}
