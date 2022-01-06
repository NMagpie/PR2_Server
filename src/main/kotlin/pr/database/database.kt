package pr.database

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import kotlinx.serialization.Serializable
import org.litote.kmongo.*
import org.slf4j.LoggerFactory
import java.security.MessageDigest

@Serializable
data class User(
    val _id: String,
    var username: String,
    var password: String,
    var email: String,
    var roles: HashSet<ERole>,
    var activated: Boolean
)

enum class ERole {
    ADMIN,
    USER
}

lateinit var client: MongoClient
lateinit var database: MongoDatabase
lateinit var usersCollection: MongoCollection<User>

fun initDB() {
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF

    client = KMongo.createClient()
    database = client.getDatabase("pr_serverDB")
    usersCollection = database.getCollection<User>("users")
}

fun findByUsername(username: String): User? {
    return usersCollection.findOne(User::username eq username)
}

fun updateActivation(username: String) {
    usersCollection.updateOne(User::username eq username, User::activated setTo true)
}

fun updatePassword(username: String, password: String) {
    val md5 = MessageDigest.getInstance("MD5").digest(password.toByteArray(Charsets.UTF_8))
    val user = findByUsername(username)
    if (user != null) {
        user.password = String(md5)
        usersCollection.updateOne(User::username eq user.username, User::password setTo user.password)
    }
}