package com.example.pekomon.minesweeper.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasLongClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.example.pekomon.minesweeper.game.Cell
import com.example.pekomon.minesweeper.game.CellState
import com.example.pekomon.minesweeper.game.GameStatus
import com.example.pekomon.minesweeper.ui.theme.MinesweeperTheme
import kotlin.test.Test

class CellAccessibilityTest {
    @Test
    fun hiddenCell_hasHiddenDescription_andClickActions() = runComposeUiTest {
        setContent {
            MinesweeperTheme(useDarkTheme = false) {
                CellView(
                    cell =
                        Cell(
                            x = 0,
                            y = 0,
                            isMine = false,
                            adjacentMines = 0,
                            state = CellState.HIDDEN,
                        ),
                    onReveal = {},
                    onToggleFlag = {},
                    boardStatus = GameStatus.IN_PROGRESS,
                    modifier = Modifier.testTag("cell"),
                )
            }
        }

        onNodeWithTag("cell")
            .assertContentDescriptionEquals("Hidden cell")
            .assertHasClickAction()
            .assertHasLongClickAction()
    }

    @Test
    fun flaggedCell_hasFlaggedDescription_andLongClickAction() = runComposeUiTest {
        setContent {
            MinesweeperTheme(useDarkTheme = false) {
                CellView(
                    cell =
                        Cell(
                            x = 0,
                            y = 0,
                            isMine = false,
                            adjacentMines = 0,
                            state = CellState.FLAGGED,
                        ),
                    onReveal = {},
                    onToggleFlag = {},
                    boardStatus = GameStatus.IN_PROGRESS,
                    modifier = Modifier.testTag("flagged"),
                )
            }
        }

        onNodeWithTag("flagged")
            .assertContentDescriptionEquals("Flagged cell")
            .assertHasLongClickAction()
            .assertHasNoClickAction()
    }

    @Test
    fun cell_meetsMinimumTouchTarget() = runComposeUiTest {
        setContent {
            MinesweeperTheme(useDarkTheme = false) {
                CellView(
                    cell =
                        Cell(
                            x = 0,
                            y = 0,
                            isMine = false,
                            adjacentMines = 3,
                            state = CellState.HIDDEN,
                        ),
                    onReveal = {},
                    onToggleFlag = {},
                    boardStatus = GameStatus.IN_PROGRESS,
                    modifier = Modifier.testTag("sizing"),
                )
            }
        }

        onNodeWithTag("sizing")
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
    }
}
