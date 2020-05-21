package com.atlassian.migration.datacenter.core.aws.region

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure
import com.atlassian.sal.api.pluginsettings.PluginSettings
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import software.amazon.awssdk.regions.Region
import java.util.*
import java.util.function.Supplier

@ExtendWith(MockitoExtension::class)
internal class PluginSettingsRegionManagerTest {
    var pluginSettingsRegionManager: PluginSettingsRegionManager? = null

    @Mock
    private val globalInfrastructure: GlobalInfrastructure? = null

    @Mock
    private val pluginSettingsFactory: PluginSettingsFactory? = null

    @Mock
    private val pluginSettings: PluginSettings? = null
    private val pluginSettingsRegionKey = PluginSettingsRegionManager.AWS_REGION_PLUGIN_STORAGE_KEY + PluginSettingsRegionManager.REGION_PLUGIN_STORAGE_SUFFIX

    @BeforeEach
    fun setUp() {
        Mockito.`when`(pluginSettingsFactory!!.createGlobalSettings()).thenReturn(pluginSettings)
        pluginSettingsRegionManager = PluginSettingsRegionManager(Supplier { pluginSettingsFactory!! }, globalInfrastructure!!)
    }

    @Test
    fun shouldGetRegionFromPluginSettingsWhenKeyExists() {
        Mockito.`when`(pluginSettings!![pluginSettingsRegionKey]).thenReturn("area-51")
        val region = pluginSettingsRegionManager!!.region
        Assertions.assertEquals("area-51", region)
    }

    @Test
    fun shouldDefaultToUsEast1aRegionFromPluginSettingsWhenKeyDoesNotExists() {
        Mockito.`when`(pluginSettings!![pluginSettingsRegionKey]).thenReturn("")
        val region = pluginSettingsRegionManager!!.region
        Assertions.assertEquals(Region.US_EAST_1.toString(), region)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreValidRegion() {
        val validRegion = "area-52"
        Mockito.`when`(globalInfrastructure!!.regions).thenReturn(object : ArrayList<String?>() {
            init {
                add("area-50")
                add("area-51")
                add("area-52")
            }
        })
        pluginSettingsRegionManager!!.storeRegion(validRegion)
        Mockito.verify(pluginSettings)!!.put(pluginSettingsRegionKey, validRegion)
    }

    @Test
    fun shouldThrowExceptionWhenTryingToStoreAnInvalidRegion() {
        val invalidRegion = "area-53"
        Mockito.`when`(globalInfrastructure!!.regions).thenReturn(ArrayList())
        Assertions.assertThrows(InvalidAWSRegionException::class.java) { pluginSettingsRegionManager!!.storeRegion(invalidRegion) }
        Mockito.verify(pluginSettings, Mockito.never())!!.put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }
}