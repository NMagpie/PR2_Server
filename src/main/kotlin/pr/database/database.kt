package pr.database

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.slf4j.LoggerFactory
import kotlinx.serialization.*

@Serializable
data class User(val _id : String, val username : String, var password : String, val email : String, val roles : HashSet<ERole>)

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
    //return usersCollection.find(User::username eq username).toList()
    return usersCollection.findOne(User::username eq username)
}