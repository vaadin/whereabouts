package com.example.whereabouts.projects;

import com.example.whereabouts.common.Identifier;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
public record ProjectId(long value) implements Identifier {
}