package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.ContentSafetyEngine
import com.example.data.engine.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PureShield 24/7", appName)
  }

  @Test
  fun `adult content is strictly blocked by safety engine`() {
    val adultResult = ContentSafetyEngine.inspectContent("https://example.porn/stream")
    assertTrue(adultResult.isBlocked)
    assertEquals(RiskLevel.JINA_KILLER_LOCKDOWN, adultResult.riskLevel)
  }

  @Test
  fun `explicit search queries xxx sex and girl combinations trigger instant block`() {
    val (isXxxTriggered, _, _) = ContentSafetyEngine.isExplicitTrigger("search xxx now")
    assertTrue("Expected 'xxx' to trigger instant block", isXxxTriggered)

    val (isSexTriggered, _, _) = ContentSafetyEngine.isExplicitTrigger("free sex video")
    assertTrue("Expected 'sex' to trigger instant block", isSexTriggered)

    val (isGirlTriggered, _, _) = ContentSafetyEngine.isExplicitTrigger("sexy girl")
    assertTrue("Expected 'sexy girl' to trigger instant block", isGirlTriggered)
  }

  @Test
  fun `jina killer detects romance figer hot dance and sex stories`() {
    val (isRomance, _, _) = ContentSafetyEngine.isExplicitTrigger("romance scene in bedroom")
    assertTrue("Expected romance to trigger block", isRomance)

    val (isFiger, _, _) = ContentSafetyEngine.isExplicitTrigger("hot figer of model")
    assertTrue("Expected figer to trigger block", isFiger)

    val (isDance, _, _) = ContentSafetyEngine.isExplicitTrigger("item dance and sexy song")
    assertTrue("Expected dance/song to trigger block", isDance)

    val (isSexStory, _, _) = ContentSafetyEngine.isExplicitTrigger("new sex story choti golpo")
    assertTrue("Expected sex story to trigger block", isSexStory)
  }

  @Test
  fun `jina jua and vulgar dance are strictly intercepted`() {
    val (isJinaTriggered, _, _) = ContentSafetyEngine.isExplicitTrigger("kono jina ba adultery video")
    assertTrue("Expected 'jina' to trigger block", isJinaTriggered)

    val (isJuaTriggered, _, _) = ContentSafetyEngine.isExplicitTrigger("1xbet online jua betting")
    assertTrue("Expected 'jua / 1xbet' to trigger block", isJuaTriggered)

    val (isDanceTriggered, _, _) = ContentSafetyEngine.isExplicitTrigger("hot item song dance video")
    assertTrue("Expected 'hot dance' to trigger block", isDanceTriggered)
  }

  @Test
  fun `detects all 4 system bypass vectors - uninstall, dns off, accessibility off, admin deactivate`() {
    // 1. Uninstall attempt
    assertNotNull(ContentSafetyEngine.detectSystemBypass("settings app info uninstall pureshield"))
    assertNotNull(ContentSafetyEngine.detectSystemBypass("force stop pureshield clear storage"))

    // 2. DNS change / off attempt
    assertNotNull(ContentSafetyEngine.detectSystemBypass("turn off private dns in settings"))
    assertNotNull(ContentSafetyEngine.detectSystemBypass("private dns mode off"))

    // 3. Accessibility off attempt
    assertNotNull(ContentSafetyEngine.detectSystemBypass("accessibility stop pureshield service"))
    assertNotNull(ContentSafetyEngine.detectSystemBypass("turn off pureshield accessibility"))

    // 4. Device Admin deactivation attempt
    assertNotNull(ContentSafetyEngine.detectSystemBypass("deactivate this device admin app pureshield"))
    assertNotNull(ContentSafetyEngine.detectSystemBypass("device administrator deactivate"))

    // Safe regular queries are not flagged as bypass
    assertNull(ContentSafetyEngine.detectSystemBypass("normal web article on science"))
  }

  @Test
  fun `safe content is passed by safety engine`() {
    val safeResult = ContentSafetyEngine.inspectContent("https://wikipedia.org/science")
    assertFalse(safeResult.isBlocked)
    assertEquals(RiskLevel.SAFE, safeResult.riskLevel)
  }
}
