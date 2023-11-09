import io.realm.kotlin.ext.toRealmDictionary
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmAny
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

fun RealmAny.Companion.toJsonElement(value: RealmAny?): JsonElement = when (value?.type) {
    RealmAny.Type.INT -> JsonPrimitive(value.asLong())
    RealmAny.Type.BOOL -> JsonPrimitive(value.asBoolean())
    RealmAny.Type.STRING -> JsonPrimitive(value.asString())
    RealmAny.Type.FLOAT -> JsonPrimitive(value.asFloat())
    RealmAny.Type.DOUBLE -> JsonPrimitive(value.asDouble())
    RealmAny.Type.SET -> JsonArray(value.asSet().map { toJsonElement(it) })
    RealmAny.Type.LIST -> JsonArray(value.asList().map { toJsonElement(it) })
    RealmAny.Type.DICTIONARY -> JsonObject(value.asDictionary().mapValues { toJsonElement(it.value) })
    null -> JsonNull
//    RealmAny.Type.BINARY,
//    RealmAny.Type.TIMESTAMP,
//    RealmAny.Type.DECIMAL128,
//    RealmAny.Type.OBJECT_ID,
//    RealmAny.Type.UUID,
//    RealmAny.Type.OBJECT,
    else -> throw IllegalArgumentException("Cannot convert RealmAny $value to JsonElement")
}

fun RealmAny.Companion.fromJsonElement(jsonElement: JsonElement): RealmAny? {
    return when (jsonElement) {
        is JsonArray -> create(jsonElement.map { fromJsonElement(it) } .toRealmList())
        is JsonObject -> create(jsonElement.mapValues { fromJsonElement(it.value) }.toRealmDictionary())
        is JsonPrimitive -> {
            if (jsonElement.isString) return create(jsonElement.content)
            jsonElement.booleanOrNull?.let { return create(it) }
            jsonElement.intOrNull?.let { return create(it) }
            jsonElement.longOrNull?.let { return create(it) }
            jsonElement.floatOrNull?.let { return create(it) }
            jsonElement.doubleOrNull?.let { return create(it) }
            jsonElement.contentOrNull?.let { return create(it) }
        }
        JsonNull -> null
        else -> throw IllegalArgumentException("Cannot convert JsonElement $jsonElement to RealmAny")
    }
}

internal object RealmAnyJsonSerializer : KSerializer<RealmAny?> {
    private val serializer = JsonElement.serializer()
    override val descriptor: SerialDescriptor = serializer.descriptor
    override fun deserialize(decoder: Decoder): RealmAny? {
        return RealmAny.fromJsonElement(decoder.decodeSerializableValue(serializer))
    }

    override fun serialize(encoder: Encoder, value: RealmAny?) {
        encoder.encodeSerializableValue(serializer, RealmAny.toJsonElement(value))
    }
}

fun RealmAny.toEJson(): String =
    Json.encodeToString(RealmAnyEJsonSerializer, this)

fun String.fromEJson(): RealmAny? =
    Json.decodeFromString(RealmAnyEJsonSerializer, this)
