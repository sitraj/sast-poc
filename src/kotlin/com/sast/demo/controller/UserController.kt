package com.sast.demo.controller

import com.sast.demo.model.User
import java.io.File
import com.sast.demo.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import kotlin.io.readText
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

data class Message(val id: String, val text: String)


object database {
    fun useConnection(block: (conn: MockConnection) -> List<Message>): List<Message> {
        val conn = MockConnection()
        return block(conn)
    }
}

class MockConnection {
    fun prepareStatement(sql: String): MockStatement {
        return MockStatement(sql)
    }
}

class MockStatement(private val sql: String) : AutoCloseable {
    fun executeQuery(): MockResultSet {
        // Return a mock result set
        return MockResultSet()
    }
    override fun close() {}
}

class MockResultSet {
    private val data = listOf(
        Message("1", "hi"),
        Message("2", "hello")
    )
    private var index = -1
    fun asIterable(): Iterable<Message> = data
    fun getString(i: Int): String = if (i == 1) data[index].id else data[index].text
    fun next(): Boolean {
        index++
        return index < data.size
    }
}

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService, private val vulnobjectMapper: ObjectMapper) {
    
    private val logger = LoggerFactory.getLogger(UserController::class.java);
    // API keys for external services
    companion object {
        // AWS Keys
        const val aws_super_secret_key_id="AKIAY34FZKBOKMUTZV7A" 
        const val aws_super_secret_val="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" 
        const val db_url="mongodb://testuser:hub24aoeu@gg-is-awesome-gg273.mongodb.net/test"
        // tokens to fetch API results from third-party services
        const val standardAccountFeatures="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
        // active token
        const val semProductionToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsIm5hbWUiOiJBZG1pbkFjY291bnQxMjMxMjMiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.jjYjoPXzTPDf6m-934QdcPA7k2EpfstO9_or8WLw3bo"
        const val token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
        const val access_token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
}
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): User {
        return userService.getUserById(id)
    }

    @GetMapping("/apikey")
    fun getApiKey(): String {
        return userService.getApiKey()
    }

    @GetMapping("/external")
    fun callExternal(): String {
        val string = userService.sanitizeHTML("<script>alert('xss')</script><p>Safe Content</p>")
        logger.info("Sanitized content: $string")   
        val restTemplate = RestTemplate()
        val response = restTemplate.getForObject("http://apihostINTERNAL.com", String::class.java) // SAST should flag this
        return response ?: "No response"
    }

    @GetMapping("/test1")
    fun foobar1(@RequestParam(name = "q", required = false) search: String): List<Message> {
        return database.useConnection { conn ->
            val sql = "SELECT * FROM messages WHERE text = ${search}"
            conn.prepareStatement(sql).use { statement ->
                statement.executeQuery().asIterable().toList()
            }
        }
    }


//https://semgrep.dev/r?q=kotlin.spring.prepare-statetment-sqli.prepare-statement-sqli
// this is from their own example.
/*
  @GetMapping("/test88")
  fun foobar5(@RequestParam(name = "q") search: String): List<Message> {
    val dataBase = Database.connect("jdbc:postgresql://host:port/database").apply {
      this.useConnection {
        val sql = """
          SELECT * FROM messages WHERE text = '${search}'
        """.trimIndent()
        // ruleid: prepare-statement-sqli
        it.prepareStatement(sql).execute()

      }
    }
    return dataBase.sequenceOf(Message)
  }
*/


    @PostMapping("/postUser")
    fun createUser(@RequestBody user: String): String { 
        
        val apiKey = "as@13@9iojlskd"
        val userDecompiled = vulnobjectMapper.readValue(user, Any::class.java)
        val restTemplate = RestTemplate()
        val headers = org.springframework.http.HttpHeaders()
        headers.setBearerAuth(apiKey)
        val entity = org.springframework.http.HttpEntity<String>(headers)
        try {
        val response = restTemplate.exchange(
            "http://apihostINTERNAL.com",
            org.springframework.http.HttpMethod.GET,
            entity,
            String::class.java
        ).body
    } catch (e: Exception) {
        logger.error("Error during external API call: ${e.message} , ${user}")
    }
        logger.warn("Create attempt: ${user}") 
        logger.info("DB Attempt: ${db_url} , ${apiKey}")
        return "Create Logged"
    }


    @GetMapping("/readfile")
    fun readFile(@RequestParam(name = "path", required = true) path: String, request: HttpServletRequest): String {
        // Intentionally vulnerable code: directly uses user-supplied path
        val file = File(path).inputStream().readBytes().toString(Charsets.UTF_8)
      /*  if (!file.exists() || !file.isFile) {
            return "File not found: $path"
        }*/ 
       // val result = file.readText();     
        // ^ I think I understood why- since readText is a Kotlin add-on and not io.File method  
        return file //this is NOT xss...
    }
        @GetMapping("/rmkfile")
    fun makefile(@RequestParam(name = "path", required = true) path: String, request: HttpServletRequest): String {
        // Intentionally vulnerable code: directly uses user-supplied path
        val file = File(path)
        file.mkdir()
        return "done"
    }
        @GetMapping("/rdelfile")
    fun deleteFile(@RequestParam(name = "path", required = true) path: String, request: HttpServletRequest): String {
        val file = File(path)
        file.delete()
        return "done"
    }

    @GetMapping("/redirect2")
    fun redirect(@RequestParam(name = "next", required = false, defaultValue = "/") next: String, request: HttpServletRequest): org.springframework.http.ResponseEntity<Void> {
        // open redirect?
       // val target = if (next.isBlank()) "/" else next
        return org.springframework.http.ResponseEntity.status(302).header("Location", next).build()
    }


     @GetMapping("/redirect")
  fun redirect2(@RequestParam(name = "url", required = false, defaultValue = "/") url: String): String {
      return "redirect:$url"
  }

    @GetMapping("/enter")
    fun enterValue(@RequestParam(name = "value", required = true) value: String): String {
        // Intentionally vulnerable: SQL injection with string parameter
        database.useConnection { conn ->
            val sql = "INSERT INTO data (value) VALUES ('${value}')"
            conn.prepareStatement(sql).use { statement ->
                statement.executeQuery()
            }
            emptyList()
        }
        return "Value ${value} inserted successfully"
    }

    @PostMapping("/execute")
    fun executeCommand(@RequestBody command: String): String {
        // Intentionally vulnerable: Command Injection - user input directly passed to Runtime.exec()
        try {
            val sancommand = if (command.equals("")) "ls -la" else command
            val process = Runtime.getRuntime().exec(sancommand)
            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            logger.info("Executed command: $command with exit code: $exitCode")
            return "Command executed successfully:\n$output"
        } catch (e: Exception) {
            logger.error("Command execution failed: ${e.message}")
            return "Command execution failed: ${e.message}"
        }
    }

//below

}
