import io.realm.kotlin.ext.realmAnyDictionaryOf
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmAny
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

fun main(args: Array<String>) {

    val instance = JsonStyleObject()
    instance.value = realmAnyDictionaryOf(
        "key1" to RealmAny.create(realmListOf(RealmAny.create(1), null, RealmAny.create(3)))
    )
    println("${instance.value}")

    testJsonRoundTrip(instance)

    testEJsonRoundTrip(instance)
}

// Realm model object with json-style RealmAny property.
class JsonStyleObject : RealmObject {
    @PrimaryKey
    var id: BsonObjectId = ObjectId()
    var value: RealmAny? = null
}

fun testJsonRoundTrip(instance: JsonStyleObject) {
    val json = instance.value!!.toJson()
    println("JSON: $json")

    val realmAnyFromJson = json.fromJson()
    println("FROM JSON: $realmAnyFromJson")
}

fun testEJsonRoundTrip(instance: JsonStyleObject) {
    val ejson = instance.value!!.toJson()
    println("EJSON: $ejson")
    val realmAnyFromEJson = ejson.fromJson()
    println("FROM EJSON: $realmAnyFromEJson")
}
