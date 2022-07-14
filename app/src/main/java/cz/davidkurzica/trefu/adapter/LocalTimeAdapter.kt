package cz.davidkurzica.trefu.adapter

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import java.time.LocalTime

object LocalTimeAdapter : Adapter<LocalTime> {
    override fun fromJson(
        reader: JsonReader,
        customScalarAdapters: CustomScalarAdapters
    ): LocalTime {
        return LocalTime.parse(reader.nextString()!!)
    }

    override fun toJson(
        writer: JsonWriter,
        customScalarAdapters: CustomScalarAdapters,
        value: LocalTime
    ) {
        writer.value(value.toString())
    }
}