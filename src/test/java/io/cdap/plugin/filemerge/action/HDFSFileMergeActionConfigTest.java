/*
 * Copyright © 2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.plugin.filemerge.action;

import io.cdap.cdap.etl.api.validation.CauseAttributes;
import io.cdap.cdap.etl.api.validation.ValidationFailure;
import io.cdap.cdap.etl.mock.validation.MockFailureCollector;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Test for {@link HDFSFileMergeActionConfig}.
 */
public class HDFSFileMergeActionConfigTest {
  private static final String MOCK_STAGE = "mockStage";

  @Test
  public void testValidConfig() {
    HDFSFileMergeActionConfig config = new HDFSFileMergeActionConfig(
      "/path/inputFile",
      "/path/outputFile",
      false
    );

    MockFailureCollector failureCollector = new MockFailureCollector(MOCK_STAGE);
    config.validate(failureCollector);
    Assert.assertTrue(failureCollector.getValidationFailures().isEmpty());
  }

  @Test
  public void testEmptySourcePath() {
    HDFSFileMergeActionConfig config = new HDFSFileMergeActionConfig(
      "",
      "/path/outputFile",
      false
    );

    MockFailureCollector failureCollector = new MockFailureCollector(MOCK_STAGE);
    config.validate(failureCollector);
    assertPropertyValidationFailed(failureCollector, HDFSFileMergeActionConfig.SOURCE_PATH);
  }

  @Test
  public void testEmptyDestPath() {
    HDFSFileMergeActionConfig config = new HDFSFileMergeActionConfig(
      "/path/inputFile",
      "",
      false
    );

    MockFailureCollector failureCollector = new MockFailureCollector(MOCK_STAGE);
    config.validate(failureCollector);
    assertPropertyValidationFailed(failureCollector, HDFSFileMergeActionConfig.DEST_PATH);
  }

  public static void assertPropertyValidationFailed(MockFailureCollector failureCollector, String paramName) {
    List<ValidationFailure> failureList = failureCollector.getValidationFailures();
    Assert.assertEquals(1, failureList.size());
    ValidationFailure failure = failureList.get(0);
    List<ValidationFailure.Cause> causeList = failure.getCauses()
      .stream()
      .filter(cause -> cause.getAttribute(CauseAttributes.STAGE_CONFIG) != null)
      .collect(Collectors.toList());
    Assert.assertEquals(1, causeList.size());
    ValidationFailure.Cause cause = causeList.get(0);
    Assert.assertEquals(paramName, cause.getAttribute(CauseAttributes.STAGE_CONFIG));
  }
}
