package org.p8499.quant.analysis.dayPolicy.common

import org.p8499.quant.analysis.dayPolicy.Snapshot
import org.p8499.quant.analysis.dayPolicy.Status

val <T : Status> Snapshot<T>.value get() = cash + positions.value

val <T : Status> Snapshot<T>.positionRate get() = positions.value / value

val <T : Status> List<Snapshot<T>>.positionRate get() = sumOf(Snapshot<T>::positionRate) / size
