package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.Identifier;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
public record EmployeeId(long value) implements Identifier {
}
