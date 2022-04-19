package org.p8499.quant.analysis.dayPolicy.common

import org.p8499.quant.analysis.dayPolicy.SnapshotPosition

val SnapshotPosition.value get() = price * volume

val SnapshotPosition.costValue get() = cost * volume

val SnapshotPosition.pl get() = (price - cost) * volume

val SnapshotPosition.plPercent get() = price / cost

val List<SnapshotPosition>.value get() = sumOf(SnapshotPosition::value)

val List<SnapshotPosition>.costValue get() = sumOf(SnapshotPosition::costValue)

val List<SnapshotPosition>.pl get() = sumOf(SnapshotPosition::pl)

val List<SnapshotPosition>.plPercent get() = sumOf(SnapshotPosition::pl) / sumOf(SnapshotPosition::costValue)
