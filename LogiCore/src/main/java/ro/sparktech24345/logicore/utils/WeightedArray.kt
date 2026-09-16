package ro.sparktech24345.logicore.utils

/**
 * Array that maintains elements in descending order by weight.
 * Higher weight elements are updated first during module execution.
 *
 * @param T The type of elements to store
 */
class WeightedArray<T> {
    data class Weighted<T>(
        val value: T,
        val weight: Float
    )
    private val arr: ArrayList<Weighted<T>> = arrayListOf()

    operator fun plusAssign(entry: Weighted<T>) = add(entry)

    fun add(entry: Weighted<T>) = this.add(entry.value, entry.weight)

    /**
     * Add an element with the specified weight.
     * Elements are automatically inserted in descending weight order.
     *
     * @param value The element to add
     * @param weight Priority weight (higher values = earlier execution)
     */
    fun add(value: T, weight: Float = 1.0f) {
        val newEntry = Weighted(value, weight)

        val index = arr.binarySearchBy(weight) { it.weight }
        val insertIndex = if (index >= 0) {
            var i = index
            while (i < arr.size && arr[i].weight == weight) {
                i++
            }
            i
        } else -(index + 1)

        arr.add(insertIndex, newEntry)
    }

    operator fun get(index: Int): Weighted<T> = arr[index]

    val size: Int
        get() = arr.size

    fun remove(index: Int) = arr.removeAt(index)

    fun list(): List<Weighted<T>> = arr
    fun clear() = arr.clear()
}
