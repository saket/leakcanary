package shark

interface LiveObjectGrowthDetector {

  fun findRepeatedlyGrowingObjects(roundTripScenario: () -> Unit): GrowingObjectNodes

  data class Config(
    val maxHeapDumps: Int,
    val scenarioLoopsPerDump: Int,
    val heapGraphProvider: HeapGraphProvider,
    private val gcRootProvider: GcRootProvider,
    private val referenceReaderFactory: ReferenceReader.Factory<HeapObject>,
  ) {
    fun create(): LiveObjectGrowthDetector {
      return HeapDumpingObjectGrowthDetector(
        maxHeapDumps = maxHeapDumps,
        heapGraphProvider = heapGraphProvider,
        scenarioRoundTripsPerDump = scenarioLoopsPerDump,
        detector = HeapDumpSequenceObjectGrowthDetector(
          HeapDumpObjectGrowthDetector(
            gcRootProvider = gcRootProvider,
            referenceReaderFactory = referenceReaderFactory
          )
        )
      )
    }
  }
}
