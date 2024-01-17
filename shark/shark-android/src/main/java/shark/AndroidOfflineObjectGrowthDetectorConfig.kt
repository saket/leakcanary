package shark

data class AndroidOfflineObjectGrowthDetectorConfig(
  val referenceMatchers: List<ReferenceMatcher> = AndroidHeapGrowthIgnoredReferences.defaults
) {
  fun create(): OfflineObjectGrowthDetector {
    return OfflineObjectGrowthDetector.Config(
      referenceReaderFactory = AndroidReferenceReaderFactory(referenceMatchers),
      gcRootProvider = MatchingGcRootProvider(referenceMatchers)
    ).create()
  }
}
