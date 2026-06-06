@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.inrotate.analytics.ai.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
data class AiFeatureInsightsResponse(
    val model: AiAnalyticsModelInfo,
    val dataset: AiAnalyticsDataset,
    val featureImportance: AiFeatureImportanceBlock,
    val factors: AiFactorsBlock,
    val charts: AiChartsBlock,
)

@Serializable
data class AiAnalyticsModelInfo(
    @JsonNames("model_version")
    val modelVersion: String,
    @JsonNames("model_type")
    val modelType: String? = null,
    @JsonNames("task_type")
    val taskType: String? = null,
    @JsonNames("trained_at")
    val trainedAt: String? = null,
    val metrics: Map<String, JsonElement> = emptyMap(),
    @JsonNames("baseline_metrics")
    val baselineMetrics: Map<String, JsonElement> = emptyMap(),
    val warnings: List<String> = emptyList(),
)

@Serializable
data class AiAnalyticsDataset(
    @JsonNames("events_count")
    val eventsCount: Int,
    @JsonNames("scale_distribution")
    val scaleDistribution: List<AiScaleDistributionItem> = emptyList(),
    @JsonNames("participants_buckets")
    val participantsBuckets: List<AiParticipantsBucketItem> = emptyList(),
)

@Serializable
data class AiScaleDistributionItem(
    val scale: String,
    val label: String,
    @JsonNames("participants_range")
    val participantsRange: String,
    val count: Int,
    val percent: Double,
)

@Serializable
data class AiParticipantsBucketItem(
    val bucket: String,
    val from: Int? = null,
    val to: Int? = null,
    val count: Int,
    val percent: Double,
)

@Serializable
data class AiFeatureImportanceBlock(
    @JsonNames("top_transformed_features")
    val topTransformedFeatures: List<AiFeatureImportanceItem> = emptyList(),
    @JsonNames("grouped_features")
    val groupedFeatures: List<AiFeatureImportanceItem> = emptyList(),
)

@Serializable
data class AiFeatureImportanceItem(
    val feature: String,
    @JsonNames("display_name")
    val displayName: String,
    val importance: Double,
    val group: String? = null,
)

@Serializable
data class AiFactorsBlock(
    @JsonNames("by_main_type")
    val byMainType: List<AiFactorStatsItem> = emptyList(),
    @JsonNames("by_organization_type")
    val byOrganizationType: List<AiFactorStatsItem> = emptyList(),
    @JsonNames("by_level")
    val byLevel: List<AiFactorStatsItem> = emptyList(),
    @JsonNames("by_organization_role")
    val byOrganizationRole: List<AiFactorStatsItem> = emptyList(),
    @JsonNames("by_month")
    val byMonth: List<AiMonthFactorStatsItem> = emptyList(),
    @JsonNames("by_keyword")
    val byKeyword: List<AiFactorStatsItem> = emptyList(),
)

@Serializable
data class AiFactorStatsItem(
    val code: String,
    val label: String,
    val count: Int,
    @JsonNames("mean_participants")
    val meanParticipants: Double? = null,
    @JsonNames("median_participants")
    val medianParticipants: Double? = null,
    @JsonNames("mass_count")
    val massCount: Int? = null,
    @JsonNames("mass_share")
    val massShare: Double,
    @JsonNames("large_or_mass_share")
    val largeOrMassShare: Double,
    val percent: Double? = null,
)

@Serializable
data class AiMonthFactorStatsItem(
    val month: Int,
    val label: String,
    val count: Int,
    @JsonNames("mean_participants")
    val meanParticipants: Double? = null,
    @JsonNames("median_participants")
    val medianParticipants: Double? = null,
    @JsonNames("mass_share")
    val massShare: Double,
    @JsonNames("large_or_mass_share")
    val largeOrMassShare: Double,
    @JsonNames("scale_distribution")
    val scaleDistribution: Map<String, Double> = emptyMap(),
)

@Serializable
data class AiChartsBlock(
    @JsonNames("scale_distribution")
    val scaleDistribution: AiChartData? = null,
    @JsonNames("top_feature_importance")
    val topFeatureImportance: AiChartData? = null,
    @JsonNames("grouped_feature_importance")
    val groupedFeatureImportance: AiChartData? = null,
    @JsonNames("mass_share_by_type")
    val massShareByType: AiChartData? = null,
    @JsonNames("mass_share_by_organization_type")
    val massShareByOrganizationType: AiChartData? = null,
    @JsonNames("monthly_scale_distribution")
    val monthlyScaleDistribution: AiChartData? = null,
    @JsonNames("keyword_impact")
    val keywordImpact: AiChartData? = null,
)

@Serializable
data class AiChartData(
    val title: String,
    val type: String,
    val items: List<AiChartItem> = emptyList(),
)

@Serializable
data class AiChartItem(
    val label: String,
    val value: Double? = null,
    val percent: Double? = null,
    val group: String? = null,
    val code: String? = null,
)
