package io.github.tdavi156.listingwatch

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.time.Instant

/** Sends money as a JSON string like "19.99" so no precision is lost. */
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.github.tdavi156.listingwatch.BigDecimal", PrimitiveKind.STRING)

    // TODO(tyler): Write the BigDecimal as a string with encoder.encodeString(...).
    //  Watch out: BigDecimal.toString() can produce scientific notation like "1E+3".
    //  Find the method that never does.
    override fun serialize(encoder: Encoder, value: BigDecimal) {
        TODO()
    }

    // TODO(tyler): Read the string back with decoder.decodeString() and turn it into a BigDecimal.
    override fun deserialize(decoder: Decoder): BigDecimal {
        TODO()
    }
}

/** Sends timestamps as ISO-8601 strings like "2026-10-19T09:30:00Z". */
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.github.tdavi156.listingwatch.Instant", PrimitiveKind.STRING)

    // TODO(tyler): Same pattern as above. Instant has a toString() and a parse() that use ISO-8601.
    override fun serialize(encoder: Encoder, value: Instant) {
        TODO()
    }

    override fun deserialize(decoder: Decoder): Instant {
        TODO()
    }
}
