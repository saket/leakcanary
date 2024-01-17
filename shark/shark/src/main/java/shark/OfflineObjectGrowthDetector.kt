package shark

import java.io.File
import shark.HprofHeapGraph.Companion.openHeapGraph

fun interface OfflineObjectGrowthDetector {

  fun findRepeatedlyGrowingObjects(heapDumps: List<HeapGraphAfterRoundTrips>): GrowingObjectNodes

  data class Config(
    val gcRootProvider: GcRootProvider,
    val referenceReaderFactory: ReferenceReader.Factory<HeapObject>,
  ) {
    fun create(): OfflineObjectGrowthDetector {
      val detector = HeapDumpSequenceObjectGrowthDetector(
        HeapDumpObjectGrowthDetector(
          gcRootProvider, referenceReaderFactory
        )
      )
      return OfflineObjectGrowthDetector {
        check(it.size >= 2) {
          "There should be at least 2 heap dumps"
        }
        detector.findRepeatedlyGrowingObjects(it.asSequence())
      }
    }
  }
}

// Must be an extension function otherwise this won't compile as method has the same signature.
fun OfflineObjectGrowthDetector.findRepeatedlyGrowingObjects(heapDumps: List<File>): GrowingObjectNodes {
  return findRepeatedlyGrowingObjects(heapDumps.map {
    HeapGraphAfterRoundTrips(it.openHeapGraph(), 1)
  })
}
