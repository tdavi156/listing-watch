package io.github.tdavi156.listingwatch

import kotlinx.serialization.Serializable

/** A brand as the API returns it. The server assigns id and createdAt. */
@Serializable
data class Brand(
    val id: Long,
    val name: String,
    // TODO(tyler): Add keywords, minPrice, maxPrice, and createdAt (see docs/PLAN.md).
    //  Use BigDecimal for prices and java.time.Instant for createdAt.
    //  Each of those needs @Serializable(with = ...) pointing at a serializer in Serializers.kt.
    //  Optional: use a value class like BrandId instead of Long for the id, and be ready to explain why.
)

/** What a client sends to create a brand. */
@Serializable
data class CreateBrandRequest(
    val name: String,
    // TODO(tyler): Add the fields a client provides. Leave out what the server assigns.
) {
    // TODO(tyler): Check every rule, collect the broken ones as messages,
    //  and throw ValidationException(errors) if there are any.
    //  Decide the rules yourself. Some ideas: name not blank, a max name length,
    //  at least one keyword, prices not negative, minPrice not above maxPrice.
    fun validate() {
        TODO()
    }
}
