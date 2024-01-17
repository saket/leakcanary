package shark

class HeapDumpingObjectGrowthDetector(
  private val maxHeapDumps: Int,
  private val heapGraphProvider: HeapGraphProvider,
  private val scenarioRoundTripsPerDump: Int,
  private val detector: HeapDumpSequenceObjectGrowthDetector
) : LiveObjectGrowthDetector {

  init {
    check(maxHeapDumps >= 2) {
      "There should be at least 2 heap dumps"
    }
    check(scenarioRoundTripsPerDump >= 1) {
      "There should be at least 1 scenario loop per dump"
    }
  }

  override fun findRepeatedlyGrowingObjects(roundTripScenario: () -> Unit): List<ShortestPathObjectNode> {
    val heapDumps = dumpHeapRepeated(roundTripScenario)
    return detector.findRepeatedlyGrowingObjects(heapDumps)
  }

  private fun dumpHeapRepeated(
    repeatedScenario: () -> Unit,
  ): Sequence<HeapGraphAfterRoundTrips> {
    val heapDumps = (1..maxHeapDumps).asSequence().map {
      repeat(scenarioRoundTripsPerDump) {
        repeatedScenario()
      }
      HeapGraphAfterRoundTrips(heapGraphProvider.openHeapGraph(), scenarioRoundTripsPerDump)
    }
    return heapDumps
  }
}
