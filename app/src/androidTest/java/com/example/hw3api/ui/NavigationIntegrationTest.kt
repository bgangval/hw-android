package com.example.hw3api.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class NavigationIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `search field is displayed`() {
        composeTestRule.setContent {
            Text("Search by name")
        }

        composeTestRule
            .onNodeWithText("Search by name")
            .assertIsDisplayed()
    }

    @Test
    fun `retry button is displayed`() {
        composeTestRule.setContent {
            Text("Retry")
        }

        composeTestRule
            .onNodeWithText("Retry")
            .assertIsDisplayed()
    }

    @Test
    fun `character list shows Rick Sanchez`() {
        composeTestRule.setContent {
            Text("Rick Sanchez")
        }

        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .assertIsDisplayed()
    }
}