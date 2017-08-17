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

package com.facebook.buck.jvm.java;

import com.facebook.buck.io.ProjectFilesystem;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.util.MoreCollectors;
import com.facebook.buck.util.immutables.BuckStyleImmutable;
import com.google.common.collect.ImmutableSortedSet;
import java.nio.file.Path;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@BuckStyleImmutable
abstract class AbstractCompilerParameters {
  @Value.Default
  public ImmutableSortedSet<Path> getSourceFilePaths() {
    return ImmutableSortedSet.of();
  }

  @Value.Default
  public ImmutableSortedSet<Path> getClasspathEntries() {
    return ImmutableSortedSet.of();
  }

  public abstract Path getOutputDirectory();

  public abstract Optional<Path> getGeneratedCodeDirectory();

  public abstract Optional<Path> getWorkingDirectory();

  public abstract Optional<Path> getDepFilePath();

  public abstract Path getPathToSourcesList();

  public abstract static class Builder {
    public CompilerParameters.Builder setSourceFileSourcePaths(
        ImmutableSortedSet<SourcePath> srcs,
        ProjectFilesystem projectFilesystem,
        SourcePathResolver resolver) {
      ImmutableSortedSet<Path> javaSrcs =
          srcs.stream()
              .map(src -> projectFilesystem.relativize(resolver.getAbsolutePath(src)))
              .collect(MoreCollectors.toImmutableSortedSet());
      return ((CompilerParameters.Builder) this).setSourceFilePaths(javaSrcs);
    }

    public CompilerParameters.Builder setClasspathEntriesSourcePaths(
        ImmutableSortedSet<SourcePath> compileTimeClasspathSourcePaths,
        SourcePathResolver resolver) {
      ImmutableSortedSet<Path> compileTimeClasspathPaths =
          compileTimeClasspathSourcePaths
              .stream()
              .map(resolver::getAbsolutePath)
              .collect(MoreCollectors.toImmutableSortedSet());
      return ((CompilerParameters.Builder) this).setClasspathEntries(compileTimeClasspathPaths);
    }
  }
}
