@file:OptIn(ExperimentalUuidApi::class)

package com.example.observabilityapp.data

import com.example.domain.models.IncidentTracker
import com.example.domain.models.Metadata
import com.example.domain.util.EIncidentSeverity
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun provideFakeIncidentTracker(severity: EIncidentSeverity, message: String, metadata: List<Metadata>) : IncidentTracker{
  return IncidentTracker(
    id = Uuid.random().toString(),
    errorCode = Random.nextInt(100, 999),
    message = message,
    severity = severity,
    pkScreen = "",
    timestamp = System.currentTimeMillis(),
    metadata = metadata
  )
}