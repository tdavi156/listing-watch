package io.github.tdavi156.listingwatch

/**
 * Storage for brands. Routes depend on this interface, not on a specific storage,
 * so Milestone 2 can swap in Postgres without changing them.
 *
 * The functions are `suspend` now because database calls will be in Milestone 2.
 */
interface BrandRepository {
    suspend fun create(request: CreateBrandRequest): Brand
    suspend fun findById(id: Long): Brand?
}

class InMemoryBrandRepository : BrandRepository {
    // TODO(tyler): Add a ConcurrentHashMap<Long, Brand> for storage
    //  and an AtomicLong to hand out ids. Be ready to explain why a plain HashMap
    //  and a plain Long counter would be unsafe here.

    // TODO(tyler): Build a Brand from the request with the next id and the current time, store it, return it.
    override suspend fun create(request: CreateBrandRequest): Brand {
        TODO()
    }

    // TODO(tyler): Return the brand, or null if there isn't one.
    override suspend fun findById(id: Long): Brand? {
        TODO()
    }
}
