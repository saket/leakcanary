package leakcanary

import shark.HeapTraversal
import shark.JdkReferenceMatchers
import shark.LiveObjectGrowthDetector
import shark.MatchingGcRootProvider
import shark.OpenJdkReferenceReaderFactory
import shark.ReferenceMatcher

data class JvmLiveObjectGrowthDetectorConfig(
  val maxHeapDumps: Int = 5,
  val scenarioLoopsPerDump: Int = 5,
  val referenceMatchers: List<ReferenceMatcher> = JdkReferenceMatchers.defaults +
    HeapTraversal.ignoredReferences,
  val heapDumpFileProvider: HeapDumpFileProvider = TempHeapDumpFileProvider,
  val heapDumper: HeapDumper = HotSpotHeapDumper
) {

  fun create(): LiveObjectGrowthDetector {
    val heapGraphProvider =
      DumpingDeletingOnCloseHeapGraphProvider(heapDumpFileProvider, heapDumper)
    return LiveObjectGrowthDetector.Config(
      maxHeapDumps = maxHeapDumps,
      scenarioLoopsPerDump = scenarioLoopsPerDump,
      heapGraphProvider = heapGraphProvider,
      referenceReaderFactory = OpenJdkReferenceReaderFactory(referenceMatchers),
      gcRootProvider = MatchingGcRootProvider(referenceMatchers)
    ).create()
  }
}
