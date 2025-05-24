@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package bouncybee.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainFont0 {
  public val chewy_regular: FontResource by 
      lazy { init_chewy_regular() }
}

@InternalResourceApi
internal fun _collectCommonMainFont0Resources(map: MutableMap<String, FontResource>) {
  map.put("chewy_regular", CommonMainFont0.chewy_regular)
}

internal val Res.font.chewy_regular: FontResource
  get() = CommonMainFont0.chewy_regular

private fun init_chewy_regular(): FontResource = org.jetbrains.compose.resources.FontResource(
  "font:chewy_regular",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/bouncybee.composeapp.generated.resources/font/chewy_regular.ttf", -1, -1),
    )
)
