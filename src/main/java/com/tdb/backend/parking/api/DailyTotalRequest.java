package com.tdb.backend.parking.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DailyTotalRequest(@NotNull List<@NotNull @Valid VisitRequest> visits) {}
