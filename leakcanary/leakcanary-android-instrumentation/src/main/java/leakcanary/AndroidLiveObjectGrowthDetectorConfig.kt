package leakcanary

import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import shark.AndroidHeapGrowthIgnoredReferences
import shark.AndroidReferenceReaderFactory
import shark.LiveObjectGrowthDetector
import shark.MatchingGcRootProvider
import shark.ReferenceMatcher

data class AndroidLiveObjectGrowthDetectorConfig(
  val maxHeapDumps: Int = 5,
  val scenarioLoopsPerDump: Int = 5,
  val referenceMatchers: List<ReferenceMatcher> = AndroidHeapGrowthIgnoredReferences.defaults,
  val heapDumpFileProvider: HeapDumpFileProvider = HeapDumpFileProvider.dateFormatted(
    directory = File(
      InstrumentationRegistry.getInstrumentation().targetContext.filesDir,
      "heap-growth-hprof"
    ),
    prefix = "heap-growth-"
  ),
  val heapDumper: HeapDumper = AndroidDebugHeapDumper
) {

  fun create(): LiveObjectGrowthDetector {
    val heapGraphProvider =
      DumpingDeletingOnCloseHeapGraphProvider(heapDumpFileProvider, heapDumper)
    return LiveObjectGrowthDetector.Config(
      maxHeapDumps = maxHeapDumps,
      scenarioLoopsPerDump = scenarioLoopsPerDump,
      heapGraphProvider = heapGraphProvider,
      referenceReaderFactory = AndroidReferenceReaderFactory(referenceMatchers),
      gcRootProvider = MatchingGcRootProvider(referenceMatchers)
    ).create()
  }
}
