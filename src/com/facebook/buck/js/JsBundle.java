/*
 * Copyright 2017-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.js;

import com.facebook.buck.model.BuildTargets;
import com.facebook.buck.rules.AbstractBuildRule;
import com.facebook.buck.rules.AddToRuleKey;
import com.facebook.buck.rules.BuildContext;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildableContext;
import com.facebook.buck.rules.ExplicitBuildTargetSourcePath;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.shell.WorkerTool;
import com.facebook.buck.step.Step;
import com.facebook.buck.step.fs.RmStep;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.nio.file.Path;

public class JsBundle extends AbstractBuildRule {

  @AddToRuleKey
  private final String bundleName;

  @AddToRuleKey
  private final ImmutableSet<String> entryPoints;

  @AddToRuleKey
  private final ImmutableSortedSet<SourcePath> libraries;

  @AddToRuleKey
  private final WorkerTool worker;

  protected JsBundle(
      BuildRuleParams params,
      ImmutableSortedSet<SourcePath> libraries,
      ImmutableSet<String> entryPoints,
      String bundleName,
      WorkerTool worker) {
    super(params);
    this.bundleName = bundleName;
    this.entryPoints = entryPoints;
    this.libraries = libraries;
    this.worker = worker;
  }


  @Override
  public ImmutableList<Step> getBuildSteps(
      BuildContext context,
      BuildableContext buildableContext) {
    final SourcePathResolver sourcePathResolver = context.getSourcePathResolver();
    final Path outputPath = sourcePathResolver.getAbsolutePath(getSourcePathToOutput());

    String jobArgs = String.format(
        "bundle %s --out %s %s",
        JsUtil.resolveMapJoin(libraries, sourcePathResolver, p -> "--lib " + p),
        outputPath,
        String.join(" ", entryPoints));
    return ImmutableList.of(
        new RmStep(
            getProjectFilesystem(),
            outputPath),
        JsUtil.workerShellStep(
            worker,
            jobArgs,
            getBuildTarget(),
            sourcePathResolver,
            getProjectFilesystem()));
  }

  @Override
  public SourcePath getSourcePathToOutput() {
    return new ExplicitBuildTargetSourcePath(
        getBuildTarget(),
        BuildTargets.getGenPath(
            getProjectFilesystem(),
            getBuildTarget(),
            "%s/" + bundleName));
  }
}
