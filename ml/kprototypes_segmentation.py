#!/usr/bin/env python3
"""Small dependency-free K-Prototypes runner used by the Spring backend.

Input is JSON on stdin:
{
  "target": "BUILDING",
  "k": 4,
  "maxIterations": 50,
  "records": [
    {"id": 1, "numeric": {"insuredValue": 100000}, "categorical": {"type": "RESIDENTIAL"}}
  ]
}

Output is JSON on stdout with assignments and cluster summaries.
"""

from __future__ import annotations

import json
import math
import sys
from collections import Counter, defaultdict
from typing import Any


def as_float(value: Any) -> float:
    if value is None:
        return 0.0
    try:
        number = float(value)
        if math.isnan(number) or math.isinf(number):
            return 0.0
        return number
    except (TypeError, ValueError):
        return 0.0


def normalize_records(records: list[dict[str, Any]]) -> tuple[list[str], list[str], dict[int, list[float]], dict[int, list[str]]]:
    numeric_keys = sorted({key for record in records for key in (record.get("numeric") or {}).keys()})
    categorical_keys = sorted({key for record in records for key in (record.get("categorical") or {}).keys()})

    raw_numeric: dict[int, list[float]] = {}
    categorical: dict[int, list[str]] = {}
    for index, record in enumerate(records):
        numeric_values = record.get("numeric") or {}
        categorical_values = record.get("categorical") or {}
        raw_numeric[index] = [as_float(numeric_values.get(key)) for key in numeric_keys]
        categorical[index] = [str(categorical_values.get(key, "UNKNOWN") or "UNKNOWN") for key in categorical_keys]

    if not numeric_keys:
        return numeric_keys, categorical_keys, raw_numeric, categorical

    means = []
    stds = []
    for col in range(len(numeric_keys)):
        values = [raw_numeric[row][col] for row in raw_numeric]
        mean = sum(values) / len(values)
        variance = sum((value - mean) ** 2 for value in values) / len(values)
        std = math.sqrt(variance) or 1.0
        means.append(mean)
        stds.append(std)

    normalized = {}
    for row, values in raw_numeric.items():
        normalized[row] = [(value - means[col]) / stds[col] for col, value in enumerate(values)]

    return numeric_keys, categorical_keys, normalized, categorical


def mixed_distance(
    numeric_values: list[float],
    categorical_values: list[str],
    numeric_centroid: list[float],
    categorical_centroid: list[str],
    gamma: float,
) -> float:
    numeric_distance = sum((value - numeric_centroid[index]) ** 2 for index, value in enumerate(numeric_values))
    categorical_distance = sum(1 for index, value in enumerate(categorical_values) if value != categorical_centroid[index])
    return numeric_distance + gamma * categorical_distance


def choose_initial_centroids(
    k: int,
    numeric: dict[int, list[float]],
    categorical: dict[int, list[str]],
    gamma: float,
) -> list[int]:
    indices = sorted(numeric.keys())
    if not indices:
        return []

    chosen = [indices[0]]
    while len(chosen) < k and len(chosen) < len(indices):
        best_index = None
        best_distance = -1.0
        for index in indices:
            if index in chosen:
                continue
            nearest = min(
                mixed_distance(numeric[index], categorical[index], numeric[c], categorical[c], gamma)
                for c in chosen
            )
            if nearest > best_distance:
                best_distance = nearest
                best_index = index
        chosen.append(best_index if best_index is not None else indices[len(chosen)])
    return chosen


def recompute_centroids(
    assignments: dict[int, int],
    k: int,
    numeric: dict[int, list[float]],
    categorical: dict[int, list[str]],
    old_numeric_centroids: list[list[float]],
    old_categorical_centroids: list[list[str]],
) -> tuple[list[list[float]], list[list[str]]]:
    numeric_centroids = []
    categorical_centroids = []
    numeric_width = len(next(iter(numeric.values()), []))
    categorical_width = len(next(iter(categorical.values()), []))

    for cluster_id in range(k):
        members = [index for index, assigned in assignments.items() if assigned == cluster_id]
        if not members:
            numeric_centroids.append(old_numeric_centroids[cluster_id])
            categorical_centroids.append(old_categorical_centroids[cluster_id])
            continue

        numeric_centroids.append([
            sum(numeric[index][col] for index in members) / len(members)
            for col in range(numeric_width)
        ])
        categorical_centroids.append([
            Counter(categorical[index][col] for index in members).most_common(1)[0][0]
            for col in range(categorical_width)
        ])

    return numeric_centroids, categorical_centroids


def kprototypes(records: list[dict[str, Any]], k: int, max_iterations: int) -> dict[str, Any]:
    if not records:
        return {"assignments": [], "clusters": []}

    k = max(1, min(k, len(records)))
    numeric_keys, categorical_keys, numeric, categorical = normalize_records(records)
    gamma = 1.0

    initial_indices = choose_initial_centroids(k, numeric, categorical, gamma)
    numeric_centroids = [list(numeric[index]) for index in initial_indices]
    categorical_centroids = [list(categorical[index]) for index in initial_indices]
    assignments: dict[int, int] = {}

    for _ in range(max_iterations):
        changed = False
        for index in numeric:
            distances = [
                mixed_distance(numeric[index], categorical[index], numeric_centroids[cluster_id], categorical_centroids[cluster_id], gamma)
                for cluster_id in range(k)
            ]
            cluster_id = min(range(k), key=lambda item: distances[item])
            if assignments.get(index) != cluster_id:
                assignments[index] = cluster_id
                changed = True

        new_numeric_centroids, new_categorical_centroids = recompute_centroids(
            assignments, k, numeric, categorical, numeric_centroids, categorical_centroids
        )
        numeric_centroids, categorical_centroids = new_numeric_centroids, new_categorical_centroids
        if not changed:
            break

    cluster_members: dict[int, list[int]] = defaultdict(list)
    for index, cluster_id in assignments.items():
        cluster_members[cluster_id].append(index)

    assignments_output = []
    clusters_output = []
    for index, record in enumerate(records):
        cluster_id = assignments[index]
        distance = mixed_distance(numeric[index], categorical[index], numeric_centroids[cluster_id], categorical_centroids[cluster_id], gamma)
        assignments_output.append({
            "entityId": record["id"],
            "clusterId": cluster_id,
            "distance": round(distance, 6),
        })

    for cluster_id in range(k):
        members = cluster_members.get(cluster_id, [])
        numeric_averages = {}
        categorical_modes = {}
        if members:
            for key in numeric_keys:
                numeric_averages[key] = sum(as_float(records[index].get("numeric", {}).get(key)) for index in members) / len(members)
            for key in categorical_keys:
                categorical_modes[key] = Counter(
                    str((records[index].get("categorical") or {}).get(key, "UNKNOWN") or "UNKNOWN")
                    for index in members
                ).most_common(1)[0][0]
        clusters_output.append({
            "clusterId": cluster_id,
            "label": f"Cluster {cluster_id}",
            "size": len(members),
            "numericAverages": {key: round(value, 4) for key, value in numeric_averages.items()},
            "categoricalModes": categorical_modes,
        })

    return {"assignments": assignments_output, "clusters": clusters_output}


def main() -> int:
    payload = json.load(sys.stdin)
    records = payload.get("records") or []
    k = int(payload.get("k") or 4)
    max_iterations = int(payload.get("maxIterations") or 50)

    result = kprototypes(records, k, max_iterations)
    result["target"] = payload.get("target")
    result["algorithm"] = "K_PROTOTYPES"
    json.dump(result, sys.stdout)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
