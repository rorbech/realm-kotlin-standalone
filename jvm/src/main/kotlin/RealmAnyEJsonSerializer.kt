import io.realm.kotlin.ext.toRealmDictionary
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.internal.toDuration
import io.realm.kotlin.types.RealmAny
import io.realm.kotlin.types.RealmInstant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.mongodb.kbson.BsonArray
import org.mongodb.kbson.BsonBinary
import org.mongodb.kbson.BsonBinarySubType
import org.mongodb.kbson.BsonBoolean
import org.mongodb.kbson.BsonDateTime
import org.mongodb.kbson.BsonDocument
import org.mongodb.kbson.BsonDouble
import org.mongodb.kbson.BsonInt64
import org.mongodb.kbson.BsonNull
import org.mongodb.kbson.BsonString
import org.mongodb.kbson.BsonType
import org.mongodb.kbson.BsonValue

fun RealmAny.Companion.toBsonValue(value: RealmAny?): BsonValue = when (value?.type) {
    RealmAny.Type.INT -> BsonInt64(value.asLong())
    RealmAny.Type.BOOL -> BsonBoolean(value.asBoolean())
    RealmAny.Type.STRING -> BsonString(value.asString())
    RealmAny.Type.BINARY -> BsonBinary(value.asByteArray())
    RealmAny.Type.TIMESTAMP -> {
        BsonDateTime(value.asRealmInstant().toDuration().inWholeMilliseconds)
    }
    RealmAny.Type.FLOAT -> BsonDouble(value.asFloat().toDouble())
    RealmAny.Type.DOUBLE -> BsonDouble(value.asDouble())
    RealmAny.Type.DECIMAL128 -> value.asDecimal128()
    RealmAny.Type.OBJECT_ID -> value.asObjectId()
    RealmAny.Type.UUID -> BsonBinary(
        BsonBinarySubType.UUID_STANDARD,
        value.asRealmUUID().bytes
    )
    RealmAny.Type.OBJECT -> throw IllegalArgumentException("Cannot convert object in RealmAny to BsonValue")
    RealmAny.Type.SET -> BsonArray(value.asSet().map { toBsonValue(it) })
    RealmAny.Type.LIST -> BsonArray(value.asList().map { toBsonValue(it) })
    RealmAny.Type.DICTIONARY -> BsonDocument(
        value.asDictionary().mapValues { toBsonValue(it.value) })
    else -> BsonNull
}

fun RealmAny.Companion.fromBsonValue(bsonValue: BsonValue): RealmAny? {
    return when (bsonValue.bsonType) {
        BsonType.DOUBLE -> create(bsonValue.asDouble().value)
        BsonType.STRING -> create(bsonValue.asString().value)
        BsonType.BINARY -> create(bsonValue.asBinary().data)
        BsonType.OBJECT_ID -> create(bsonValue.asObjectId())
        BsonType.BOOLEAN -> create(bsonValue.asBoolean().value)
        BsonType.DATE_TIME -> create(
            RealmInstant.from(bsonValue.asDateTime().value, 0)
        )
        BsonType.TIMESTAMP -> {
            // Should we rather just error on these
            val epoch = bsonValue.asTimestamp().time.toLong()
            create(RealmInstant.from(epoch, 0))
        }
        BsonType.INT32 -> create(bsonValue.asInt32().value)
        BsonType.INT64 -> create(bsonValue.asInt64().value)
        BsonType.DECIMAL128 -> create(bsonValue.asDecimal128())
        BsonType.ARRAY -> create(bsonValue.asArray().values.map { fromBsonValue(it) }
            .toRealmList())
        BsonType.DOCUMENT -> create(
            bsonValue.asDocument().mapValues { fromBsonValue(it.value) }.toRealmDictionary()
        )
        BsonType.NULL -> null
        /*
        BsonType.DB_POINTER,
        BsonType.JAVASCRIPT,
        BsonType.SYMBOL,
        BsonType.JAVASCRIPT_WITH_SCOPE,
        BsonType.REGULAR_EXPRESSION,
        BsonType.UNDEFINED,
        BsonType.MIN_KEY,
        BsonType.MAX_KEY,
        BsonType.END_OF_DOCUMENT,
         */
        else -> throw IllegalArgumentException("Cannot convert BsonValue($bsonValue) to RealmAny")
    }
}

internal object RealmAnyEJsonSerializer : KSerializer<RealmAny?> {
    private val serializer = BsonValue.serializer()
    override val descriptor: SerialDescriptor = serializer.descriptor
    override fun deserialize(decoder: Decoder): RealmAny? {
        return RealmAny.fromBsonValue(decoder.decodeSerializableValue(serializer))
    }

    override fun serialize(encoder: Encoder, value: RealmAny?) {
        encoder.encodeSerializableValue(serializer, RealmAny.toBsonValue(value))
    }
}

fun RealmAny.toJson(): String =
    Json.encodeToString(RealmAnyJsonSerializer, this)

fun String.fromJson(): RealmAny? =
    Json.decodeFromString(RealmAnyJsonSerializer, this)
