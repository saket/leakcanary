package leakcanary

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JvmHeapGrowthDetectorConfigTest {

  class Leaky

  private val leakies = mutableListOf<Leaky>()

  @Test
  fun `leaky increase leads to heap growth`() {
    val detector = JvmLiveObjectGrowthDetectorConfig().create()

    val growingNodes = detector.findRepeatedlyGrowingObjects {
      leakies += Leaky()
    }

    assertThat(growingNodes).hasSize(1)
  }
}
