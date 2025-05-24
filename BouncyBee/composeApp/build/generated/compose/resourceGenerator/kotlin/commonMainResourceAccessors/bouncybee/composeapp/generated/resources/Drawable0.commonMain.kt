@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package bouncybee.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainDrawable0 {
  public val background: DrawableResource by 
      lazy { init_background() }

  public val bee_sprite: DrawableResource by 
      lazy { init_bee_sprite() }

  public val compose_multiplatform: DrawableResource by 
      lazy { init_compose_multiplatform() }

  public val moving_background: DrawableResource by 
      lazy { init_moving_background() }

  public val pipe: DrawableResource by 
      lazy { init_pipe() }

  public val pipe_cap: DrawableResource by 
      lazy { init_pipe_cap() }
}

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("background", CommonMainDrawable0.background)
  map.put("bee_sprite", CommonMainDrawable0.bee_sprite)
  map.put("compose_multiplatform", CommonMainDrawable0.compose_multiplatform)
  map.put("moving_background", CommonMainDrawable0.moving_background)
  map.put("pipe", CommonMainDrawable0.pipe)
  map.put("pipe_cap", CommonMainDrawable0.pipe_cap)
}

internal val Res.drawable.background: DrawableResource
  get() = CommonMainDrawable0.background

private fun init_background(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:background",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/bouncybee.composeapp.generated.resources/drawable/background.png", -1, -1),
    )
)

internal val Res.drawable.bee_sprite: DrawableResource
  get() = CommonMainDrawable0.bee_sprite

private fun init_bee_sprite(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:bee_sprite",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/bouncybee.composeapp.generated.resources/drawable/bee_sprite.png", -1, -1),
    )
)

internal val Res.drawable.compose_multiplatform: DrawableResource
  get() = CommonMainDrawable0.compose_multiplatform

private fun init_compose_multiplatform(): DrawableResource =
    org.jetbrains.compose.resources.DrawableResource(
  "drawable:compose_multiplatform",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/bouncybee.composeapp.generated.resources/drawable/compose-multiplatform.xml", -1, -1),
    )
)

internal val Res.drawable.moving_background: DrawableResource
  get() = CommonMainDrawable0.moving_background

private fun init_moving_background(): DrawableResource =
    org.jetbrains.compose.resources.DrawableResource(
  "drawable:moving_background",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/bouncybee.composeapp.generated.resources/drawable/moving_background.png", -1, -1),
    )
)

internal val Res.drawable.pipe: DrawableResource
  get() = CommonMainDrawable0.pipe

private fun init_pipe(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:pipe",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/bouncybee.composeapp.generated.resources/drawable/pipe.png", -1, -1),
    )
)

internal val Res.drawable.pipe_cap: DrawableResource
  get() = CommonMainDrawable0.pipe_cap

private fun init_pipe_cap(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:pipe_cap",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/bouncybee.composeapp.generated.resources/drawable/pipe_cap.png", -1, -1),
    )
)
