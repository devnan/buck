/*
 * Copyright 2018-present Facebook, Inc.
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

package com.facebook.buck.rules.modern.config;

import com.facebook.buck.core.config.BuckConfig;
import com.facebook.buck.core.config.ConfigView;
import com.facebook.buck.core.util.immutables.BuckStyleTuple;
import com.facebook.buck.core.util.log.Logger;
import org.immutables.value.Value;

/** Various configuration for ModernBuildRule behavior. */
@Value.Immutable
@BuckStyleTuple
abstract class AbstractModernBuildRuleConfig
    implements ConfigView<BuckConfig>, ModernBuildRuleStrategyConfig {
  private final Logger LOG = Logger.get(AbstractModernBuildRuleConfig.class);

  public static final String SECTION = "modern_build_rule";

  @Value.Derived
  public ModernBuildRuleStrategyConfig getDefaultStrategyConfig() {
    return new ModernBuildRuleStrategyConfigFromSection(getDelegate(), SECTION);
  }

  @Override
  public ModernBuildRuleBuildStrategy getBuildStrategy() {
    ModernBuildRuleBuildStrategy strategy = getDefaultStrategyConfig().getBuildStrategy();
    if (ModernBuildRuleBuildStrategy.NONE == strategy && isRemoteExecutionExperimentEnabled()) {
      LOG.warn("Remote Execution experiment is enabled for the current user.");
      return ModernBuildRuleBuildStrategy.HYBRID_LOCAL;
    }

    return getDefaultStrategyConfig().getBuildStrategy();
  }

  @Override
  public HybridLocalBuildStrategyConfig getHybridLocalConfig() {
    return getDefaultStrategyConfig().getHybridLocalConfig();
  }

  private boolean isRemoteExecutionExperimentEnabled() {
    return getDelegate().getBooleanValue("experiments", "remote_execution_beta_test", false);
  }
}
