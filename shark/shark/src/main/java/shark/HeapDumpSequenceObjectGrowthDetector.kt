package shark

class HeapDumpSequenceObjectGrowthDetector(
  private val heapGrowthDetector: HeapDumpObjectGrowthDetector
) {

  fun findRepeatedlyGrowingObjects(
    heapDumps: Sequence<HeapGraphAfterRoundTrips>
  ): GrowingObjectNodes {
    var i = 1
    var lastDiffResult: InputHeapTraversal = NoHeapTraversalYet
    for (heapDump in heapDumps) {
      val diffResult =
        heapGrowthDetector.findGrowingObjects(
          heapDump.heapGraph, heapDump.scenarioRoundTrips, lastDiffResult
        )
      if (diffResult is HeapTraversalWithDiff) {
        val iterationCount = i * heapDump.scenarioRoundTrips
        SharkLog.d {
          "After $iterationCount (+ ${heapDump.scenarioRoundTrips}) iterations and heap dump $i: ${diffResult.growingNodes.size} growing nodes"
        }
        if (diffResult.growingNodes.isEmpty()) {
          return emptyList()
        }
      }
      lastDiffResult = diffResult
      i++
    }
    val finalDiffResult = lastDiffResult
    check(finalDiffResult is HeapTraversalWithDiff) {
      "finalDiffResult $finalDiffResult should be a HeapDiff as i ${i - 1} should be >= 2"
    }
    return finalDiffResult.growingNodes
  }
}
