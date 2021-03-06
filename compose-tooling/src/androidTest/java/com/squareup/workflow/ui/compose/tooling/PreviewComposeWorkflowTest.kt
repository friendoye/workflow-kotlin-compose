/*
 * Copyright 2020 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("TestFunctionName", "PrivatePropertyName")

package com.squareup.workflow.ui.compose.tooling

import androidx.compose.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.size
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertIsNotDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithText
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.squareup.workflow.Workflow
import com.squareup.workflow.ui.ViewEnvironmentKey
import com.squareup.workflow.ui.compose.WorkflowRendering
import com.squareup.workflow.ui.compose.composed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Duplicate of [PreviewViewFactoryTest] but for [com.squareup.workflow.ui.compose.ComposeWorkflow].
 */
@RunWith(AndroidJUnit4::class)
class PreviewComposeWorkflowTest {

  @Rule @JvmField val composeRule = createComposeRule()

  @Test fun singleChild() {
    composeRule.setContent {
      ParentWithOneChildPreview()
    }

    onNodeWithText("one").assertIsDisplayed()
    onNodeWithText("two").assertIsDisplayed()
  }

  @Test fun twoChildren() {
    composeRule.setContent {
      ParentWithTwoChildrenPreview()
    }

    onNodeWithText("one").assertIsDisplayed()
    onNodeWithText("two").assertIsDisplayed()
    onNodeWithText("three").assertIsDisplayed()
  }

  @Test fun modifierIsApplied() {
    composeRule.setContent {
      ParentWithModifier()
    }

    // The view factory will be rendered with size (0,0), so it should be reported as not displayed.
    onNodeWithText("one").assertIsNotDisplayed()
    onNodeWithText("two").assertIsNotDisplayed()
  }

  @Test fun placeholderModifierIsApplied() {
    composeRule.setContent {
      ParentWithPlaceholderModifier()
    }

    // The child will be rendered with size (0,0), so it should be reported as not displayed.
    onNodeWithText("one").assertIsDisplayed()
    onNodeWithText("two").assertIsNotDisplayed()
  }

  @Test fun customViewEnvironment() {
    composeRule.setContent {
      ParentConsumesCustomKeyPreview()
    }

    onNodeWithText("foo").assertIsDisplayed()
  }

  private val ParentWithOneChild =
    Workflow.composed<Pair<String, String>, Nothing> { props, _, environment ->
      Column {
        Text(props.first)
        WorkflowRendering(props.second, environment)
      }
    }

  @Preview @Composable private fun ParentWithOneChildPreview() {
    ParentWithOneChild.preview(Pair("one", "two"))
  }

  private val ParentWithTwoChildren =
    Workflow.composed<Triple<String, String, String>, Nothing> { props, _, environment ->
      Column {
        WorkflowRendering(rendering = props.first, viewEnvironment = environment)
        Text(props.second)
        WorkflowRendering(rendering = props.third, viewEnvironment = environment)
      }
    }

  @Preview @Composable private fun ParentWithTwoChildrenPreview() {
    ParentWithTwoChildren.preview(Triple("one", "two", "three"))
  }

  @Preview @Composable private fun ParentWithModifier() {
    ParentWithOneChild.preview(
        Pair("one", "two"),
        modifier = Modifier.size(0.dp)
    )
  }

  @Preview @Composable private fun ParentWithPlaceholderModifier() {
    ParentWithOneChild.preview(
        Pair("one", "two"),
        placeholderModifier = Modifier.size(0.dp)
    )
  }

  object TestEnvironmentKey : ViewEnvironmentKey<String>(String::class) {
    override val default: String get() = error("Not specified")
  }

  private val ParentConsumesCustomKey = Workflow.composed<Unit, Nothing> { _, _, environment ->
    Text(environment[TestEnvironmentKey])
  }

  @Preview @Composable private fun ParentConsumesCustomKeyPreview() {
    ParentConsumesCustomKey.preview(Unit) {
      it + (TestEnvironmentKey to "foo")
    }
  }
}
